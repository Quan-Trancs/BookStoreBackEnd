package quantran.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import quantran.api.dto.BookDetailDto;
import quantran.api.entity.BookType;
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
import quantran.api.dto.AsyncTaskRequest;
import quantran.api.service.AsyncTaskService;

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
    private final AsyncTaskService asyncTaskService;
    @GetMapping(UrlConstant.LIST)
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<Paginate> list( @RequestParam (defaultValue = "") String searchName, @RequestParam (defaultValue = "") String searchAuthor, @RequestParam (defaultValue = "") String searchId, @RequestParam (defaultValue = "0") String page, @RequestParam (defaultValue = "5") String pageSize) {
        log.info("Start list()");

        Paginate result = bookService.getBook(searchName, searchAuthor, searchId, null, null, parseInt(page), parseInt(pageSize) );

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
        ResponseEntity<byte[]> result = bookService.downloadBook();
        log.info("End downloadBook()");
        return result;
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
    public ResponseEntity<AsyncTaskRequest> updateBook(
            @RequestHeader @NotBlank(message = "User name is required") String name, 
            @RequestHeader @NotBlank(message = "User key is required") String key, 
            @RequestParam @NotBlank(message = "Book ID is required") String updateId, 
            @RequestParam @NotBlank(message = "Book name is required") String updateName, 
            @RequestParam @NotBlank(message = "Book type is required") String updateType, 
            @RequestParam @NotBlank(message = "Author is required") String updateAuthor, 
            @RequestParam @NotBlank(message = "Price is required") String updatePrice) {
        
        log.info("Start updateBook() - ID: {}", updateId);
        
        BookModel bookModel = new BookModel(updateId.trim(), updateName, updateAuthor, updatePrice, updateType);
        UserModel userModel = new UserModel(name, key);
        
        // Validate book model
        Set<ConstraintViolation<BookModel>> violations = validator.validate(bookModel);
        if(!violations.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        
        try {
            // Validate user authentication
            String[] status = asyncProcessingWorkAcceptor.workAcceptor(userModel);
            if ("404".equals(status[0])) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Submit task for async processing
            AsyncTaskRequest task = asyncTaskService.submitTask("update_book", bookModel, name);
            
            // Add task to background worker queue
            Task backgroundTask = new Task("update", task.getTaskId(), bookModel);
            asyncProcessingBackgroundWorkerImpl.addToRequestQueue(backgroundTask);
            
            log.info("End updateBook(), task submitted - taskId: {}", task.getTaskId());
            return ResponseEntity.accepted().body(task);
            
        } catch (Exception e) {
            log.error("Error submitting update task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Enhanced book management endpoints
    @GetMapping("/detail/{id}")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<BookDetailDto> getBookById(@PathVariable String id) {
        log.info("Getting book detail by ID: {}", id);
        
        try {
            return bookService.getBookById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting book by ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/isbn/{isbn}")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<BookDetailDto> getBookByIsbn(@PathVariable String isbn) {
        log.info("Getting book by ISBN: {}", isbn);
        
        try {
            return bookService.getBookByIsbn(isbn)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting book by ISBN {}: {}", isbn, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/author/{authorId}")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<List<BookDetailDto>> getBooksByAuthor(@PathVariable Long authorId) {
        log.info("Getting books by author ID: {}", authorId);
        
        try {
            List<BookDetailDto> books = bookService.getBooksByAuthor(authorId);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            log.error("Error getting books by author: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/genre/{genreId}")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<List<BookDetailDto>> getBooksByGenre(@PathVariable String genreId) {
        log.info("Getting books by genre ID: {}", genreId);
        
        try {
            List<BookDetailDto> books = bookService.getBooksByGenre(genreId);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            log.error("Error getting books by genre: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/publisher/{publisherId}")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<List<BookDetailDto>> getBooksByPublisher(@PathVariable Long publisherId) {
        log.info("Getting books by publisher ID: {}", publisherId);
        
        try {
            List<BookDetailDto> books = bookService.getBooksByPublisher(publisherId);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            log.error("Error getting books by publisher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/low-stock")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<List<BookDetailDto>> getBooksWithLowStock() {
        log.info("Getting books with low stock");
        
        try {
            List<BookDetailDto> books = bookService.getBooksWithLowStock();
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            log.error("Error getting books with low stock: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/out-of-stock")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<List<BookDetailDto>> getOutOfStockBooks() {
        log.info("Getting out of stock books");
        
        try {
            List<BookDetailDto> books = bookService.getOutOfStockBooks();
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            log.error("Error getting out of stock books: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/discount")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<List<BookDetailDto>> getBooksWithDiscount() {
        log.info("Getting books with discount");
        
        try {
            List<BookDetailDto> books = bookService.getBooksWithDiscount();
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            log.error("Error getting books with discount: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/publication-year/{year}")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<List<BookDetailDto>> getBooksByPublicationYear(@PathVariable int year) {
        log.info("Getting books by publication year: {}", year);
        
        try {
            List<BookDetailDto> books = bookService.getBooksByPublicationYear(year);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            log.error("Error getting books by publication year: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/language/{language}")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<List<BookDetailDto>> getBooksByLanguage(@PathVariable String language) {
        log.info("Getting books by language: {}", language);
        
        try {
            List<BookDetailDto> books = bookService.getBooksByLanguage(language);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            log.error("Error getting books by language: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/format/{format}")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<List<BookDetailDto>> getBooksByFormat(@PathVariable String format) {
        log.info("Getting books by format: {}", format);
        
        try {
            List<BookDetailDto> books = bookService.getBooksByFormat(format);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            log.error("Error getting books by format: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/price-range")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<List<BookDetailDto>> getBooksByPriceRange(
            @RequestParam java.math.BigDecimal minPrice,
            @RequestParam java.math.BigDecimal maxPrice) {
        log.info("Getting books by price range: {} - {}", minPrice, maxPrice);
        
        try {
            List<BookDetailDto> books = bookService.getBooksByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            log.error("Error getting books by price range: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Inventory management endpoints
    @PostMapping("/{bookId}/stock")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<String> updateStock(
            @PathVariable String bookId,
            @RequestParam Integer quantity) {
        log.info("Updating stock for book {}: {}", bookId, quantity);
        
        try {
            bookService.updateStock(bookId, quantity);
            return ResponseEntity.ok("Stock updated successfully");
        } catch (RuntimeException e) {
            log.error("Error updating stock: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating stock: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{bookId}/reserve")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<String> reserveBook(
            @PathVariable String bookId,
            @RequestParam Integer quantity) {
        log.info("Reserving book {}: {}", bookId, quantity);
        
        try {
            bookService.reserveBook(bookId, quantity);
            return ResponseEntity.ok("Book reserved successfully");
        } catch (RuntimeException e) {
            log.error("Error reserving book: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error reserving book: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{bookId}/release")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<String> releaseBook(
            @PathVariable String bookId,
            @RequestParam Integer quantity) {
        log.info("Releasing book {}: {}", bookId, quantity);
        
        try {
            bookService.releaseBook(bookId, quantity);
            return ResponseEntity.ok("Book released successfully");
        } catch (RuntimeException e) {
            log.error("Error releasing book: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error releasing book: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{bookId}/available")
    @CrossOrigin(origins = UrlConstant.BOOKFE)
    public ResponseEntity<Boolean> isBookAvailable(
            @PathVariable String bookId,
            @RequestParam Integer quantity) {
        log.info("Checking availability for book {}: {}", bookId, quantity);
        
        try {
            boolean available = bookService.isBookAvailable(bookId, quantity);
            return ResponseEntity.ok(available);
        } catch (RuntimeException e) {
            log.error("Error checking book availability: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error checking book availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
