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
import quantran.api.common.UrlConstant;
import quantran.api.errorHandle.ErrorHandler;
import quantran.api.model.BookModel;
import quantran.api.page.Paginate;
import quantran.api.service.BookService;

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
    private final ErrorHandler errorHandler;
    private final Validator validator;
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
    public ResponseEntity<String> addBook(@RequestParam String addId, @RequestParam String addName, @RequestParam String addBookType,@RequestParam String addAuthor, @RequestParam String addPrice) {
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
            StringBuilder errorMessage = new StringBuilder();
            // Iterate through constraint violations and append them to the error message
            errorMessage.append(HttpStatus.BAD_REQUEST).append(": \n");
            errorMessage.append("Invalid Book ID").append(". \n");
            // Return the error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }
    }
    @PostMapping(UrlConstant.UPDATEBOOK)
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<String> updateBook(@RequestParam String updateId, @RequestParam String updateName, @RequestParam String updateType, @RequestParam String updateAuthor, @RequestParam String updatePrice) {
        BookModel bookModel = new BookModel(updateId.trim(), updateName, updateAuthor, updatePrice, updateType);
        Set<ConstraintViolation<BookModel>> violations = validator.validate(bookModel);
        if(!violations.isEmpty()){
            return errorHandler.errorHandle(violations); //need to add handler
        }
        try {
            log.info("Start updateBook()");
            bookService.updateBook(bookModel);
            log.info("End updateBook()");
            return ResponseEntity.ok("successfully updated book");
        } catch (JpaObjectRetrievalFailureException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid BookType");
        }
    }
}
