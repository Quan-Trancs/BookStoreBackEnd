package quantran.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import quantran.api.BookType.BookType;
import quantran.api.asyncProcessingBackgroundWorker.impl.AsyncProcessingBackgroundWorkerImpl;
import quantran.api.asyncProcessingBackgroundWorker.task.Task;
import quantran.api.asyncProcessingWorkAcceptor.AsyncProcessingWorkAcceptor;
import quantran.api.common.UrlConstant;
import quantran.api.errorHandle.ErrorHandler;
import quantran.api.model.BookModel;
import quantran.api.model.UserModel;
import quantran.api.page.Paginate;
import quantran.api.service.BookService;
import quantran.api.service.TaskService;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.lang.Integer.parseInt;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping(UrlConstant.BOOK)
public class BookController {
    private final BookService bookService;
    private final TaskService taskService;
    private final ErrorHandler errorHandler;
    private final Validator validator;
    private final AsyncProcessingWorkAcceptor asyncProcessingWorkAcceptor;
    private final AsyncProcessingBackgroundWorkerImpl asyncProcessingBackgroundWorkerImpl;
    @GetMapping(UrlConstant.LIST)
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<Paginate> list( @RequestParam (defaultValue = "") String searchName, @RequestParam (defaultValue = "") String searchAuthor, @RequestParam (defaultValue = "") String searchId, @RequestParam (defaultValue = "0") String page, @RequestParam (defaultValue = "5") String pageSize) {
        log.info("Start list()");

        Paginate result = bookService.getBook(searchName, searchAuthor, searchId, parseInt(page), parseInt(pageSize) );

        log.info("End list()");
        return ResponseEntity.ok(result);
    }
    @GetMapping(UrlConstant.TYPE)
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<List<BookType>> getBookType() {
        log.info("Start list()");

        List<BookType> result = bookService.getBookType();

        log.info("End list()");
        return ResponseEntity.ok(result);
    }
    @GetMapping(UrlConstant.DOWNLOAD)
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<byte[]> downloadBook() throws IOException {
        log.info("Start downloadBook()");
        ResponseEntity<byte[]> downloadBook = bookService.downloadBook();
        log.info("End downloadBook()");
        return downloadBook;
    }
    @PostMapping(UrlConstant.UPLOAD)
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public void uploadBook(@RequestParam MultipartFile bookFile) throws IOException {
        log.info("Start uploadBook()");
        bookService.uploadBook(bookFile);
        log.info("End uploadBook()");
    }
    @PostMapping(UrlConstant.ADDBOOK)
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<String> addBook(
            @RequestParam @NotBlank(message = "Book ID is required") String addId, 
            @RequestParam @NotBlank(message = "Book name is required") String addName, 
            @RequestParam @NotBlank(message = "Book type is required") String addBookType,
            @RequestParam @NotBlank(message = "Author is required") String addAuthor, 
            @RequestParam @NotBlank(message = "Price is required") String addPrice) {
        BookModel bookModel = new BookModel(addId.trim(), addName, addAuthor, addPrice, addBookType);
        Set<ConstraintViolation<BookModel>> violations = validator.validate(bookModel);
        if(!violations.isEmpty()){
            return errorHandler.errorHandle(violations);
        }
        try {
            log.info("Start addBook()");
            bookService.addBook(bookModel);
            log.info("End addBook()");
            return ResponseEntity.ok("successfully added book");
        } catch (JpaObjectRetrievalFailureException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid BookType");
        }
    }
    @PostMapping(UrlConstant.DELBOOK)
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<String> delBook(@Valid @RequestParam("delId") @NotNull(message = "Delete ID is required!") @NotEmpty(message = "Delete ID is required") @NotBlank(message = "Invalid delete ID!") String delId) {
        try {
            log.info("Start delBook()");
            bookService.delBook(delId);
            log.info("End delBook()");
            return ResponseEntity.ok("successfully deleted book");
        } catch (EmptyResultDataAccessException e) {
            log.warn("Attempted to delete non-existent book with ID: {}", delId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Book with ID '" + delId + "' not found");
        }
    }
    @PostMapping(UrlConstant.UPDATEBOOK)
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<String> updateBook(
            @RequestHeader @NotBlank(message = "User name is required") String name, 
            @RequestHeader @NotBlank(message = "User key is required") String key, 
            @RequestParam @NotBlank(message = "Book ID is required") String updateId, 
            @RequestParam @NotBlank(message = "Book name is required") String updateName, 
            @RequestParam @NotBlank(message = "Book type is required") String updateType, 
            @RequestParam @NotBlank(message = "Author is required") String updateAuthor, 
            @RequestParam @NotBlank(message = "Price is required") String updatePrice) {
        BookModel bookModel = new BookModel(updateId.trim(), updateName, updateAuthor, updatePrice, updateType);
        UserModel userModel = new UserModel(name, key);
        Set<ConstraintViolation<BookModel>> violations = validator.validate(bookModel);
        if(!violations.isEmpty()){
            return errorHandler.errorHandle(violations);
        }
        try {
            String[] status = asyncProcessingWorkAcceptor.workAcceptor(userModel);
            if ("404".equals(status[0])) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Header");
            }
            Task task = new Task("update", status[1], bookModel);
            log.info("Start updateBook()");
            asyncProcessingBackgroundWorkerImpl.addToRequestQueue(task);
            log.info("End updateBook()");
            return ResponseEntity.ok(status[1] + " " + status[0]);
        } catch (JpaObjectRetrievalFailureException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid BookType");
        }
    }
}
