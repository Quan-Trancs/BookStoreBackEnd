package quantran.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import quantran.api.entity.BookType;
import quantran.api.dto.AsyncTaskRequest;
import quantran.api.dto.AsyncTaskResponseDto;
import quantran.api.dto.BookRequestDto;
import quantran.api.dto.BookDetailDto;
import quantran.api.model.BookModel;
import quantran.api.model.UserModel;
import quantran.api.page.Paginate;
import quantran.api.service.AsyncTaskService;
import quantran.api.service.BookService;
import quantran.api.asyncProcessingWorkAcceptor.AsyncProcessingWorkAcceptor;
import quantran.api.asyncProcessingBackgroundWorker.impl.AsyncProcessingBackgroundWorkerImpl;
import quantran.api.asyncProcessingBackgroundWorker.task.Task;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
@Validated
public class RestBookController {
    
    private final BookService bookService;
    private final AsyncTaskService asyncTaskService;
    private final AsyncProcessingWorkAcceptor asyncProcessingWorkAcceptor;
    private final AsyncProcessingBackgroundWorkerImpl asyncProcessingBackgroundWorkerImpl;
    
    /**
     * GET /api/v1/books - Get all books with pagination and search
     */
    @GetMapping
    public ResponseEntity<Paginate<BookModel>> getBooks(
            @RequestParam(defaultValue = "") String searchName,
            @RequestParam(defaultValue = "") String searchAuthor,
            @RequestParam(defaultValue = "") String searchId,
            @RequestParam(required = false) String searchGenre,
            @RequestParam(required = false) String searchPublisher,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Getting books - search: name={}, author={}, id={}, genre={}, publisher={}, page={}, size={}", 
                searchName, searchAuthor, searchId, searchGenre, searchPublisher, page, size);
        
        Paginate<BookModel> result = bookService.getBook(searchName, searchAuthor, searchId, searchGenre, searchPublisher, page, size);
        return ResponseEntity.ok(result);
    }
    
    /**
     * GET /api/v1/books/{id} - Get a specific book by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailDto> getBook(@PathVariable String id) {
        log.info("Getting book by ID: {}", id);
        
        return bookService.getBookById(id)
                .map(book -> ResponseEntity.ok(book))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * POST /api/v1/books - Create a new book
     */
    @PostMapping
    public ResponseEntity<BookModel> createBook(@Valid @RequestBody BookRequestDto bookRequest) {
        log.info("Creating new book: {}", bookRequest.getId());
        
        BookModel bookModel = new BookModel(
            bookRequest.getId(),
            bookRequest.getTitle(),
            bookRequest.getAuthor(),
            bookRequest.getPrice().toString(),
            bookRequest.getBookType()
        );
        
        bookService.addBook(bookModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookModel);
    }
    
    /**
     * PUT /api/v1/books/{id} - Update a book (synchronous)
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookModel> updateBook(
            @PathVariable String id,
            @Valid @RequestBody BookRequestDto bookRequest) {
        
        log.info("Updating book: {}", id);
        
        // Ensure the ID in the path matches the request body
        if (!id.equals(bookRequest.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        BookModel bookModel = new BookModel(
            bookRequest.getId(),
            bookRequest.getTitle(),
            bookRequest.getAuthor(),
            bookRequest.getPrice().toString(),
            bookRequest.getBookType()
        );
        
        bookService.updateBook(bookModel);
        return ResponseEntity.ok(bookModel);
    }
    
    /**
     * PATCH /api/v1/books/{id} - Update a book asynchronously
     */
    @PatchMapping("/{id}")
    public ResponseEntity<AsyncTaskResponseDto> updateBookAsync(
            @RequestHeader @NotBlank(message = "User name is required") String userName,
            @RequestHeader @NotBlank(message = "User key is required") String userKey,
            @PathVariable String id,
            @Valid @RequestBody BookRequestDto bookRequest) {
        
        log.info("Updating book asynchronously: {}", id);
        
        // Validate user authentication
        UserModel userModel = new UserModel(userName, userKey);
        String[] status = asyncProcessingWorkAcceptor.workAcceptor(userModel);
        if ("404".equals(status[0])) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Ensure the ID in the path matches the request body
        if (!id.equals(bookRequest.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        BookModel bookModel = new BookModel(
            bookRequest.getId(),
            bookRequest.getTitle(),
            bookRequest.getAuthor(),
            bookRequest.getPrice().toString(),
            bookRequest.getBookType()
        );
        
        // Submit task for async processing
        AsyncTaskRequest task = asyncTaskService.submitTask("update_book", bookModel, userName);
        
        // Add task to background worker queue
        Task backgroundTask = new Task("update", task.getTaskId(), bookModel);
        asyncProcessingBackgroundWorkerImpl.addToRequestQueue(backgroundTask);
        
        AsyncTaskResponseDto response = AsyncTaskResponseDto.fromAsyncTaskRequest(task);
        return ResponseEntity.accepted().body(response);
    }
    
    /**
     * DELETE /api/v1/books/{id} - Delete a book
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        log.info("Deleting book: {}", id);
        
        bookService.delBook(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /api/v1/books/types - Get all book types
     */
    @GetMapping("/types")
    public ResponseEntity<List<BookType>> getBookTypes() {
        log.info("Getting book types");
        
        List<BookType> bookTypes = bookService.getBookType();
        return ResponseEntity.ok(bookTypes);
    }
    
    /**
     * POST /api/v1/books/upload - Upload books from CSV
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadBooks(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Uploading books from file: {}", file.getOriginalFilename());
        
        bookService.uploadBook(file);
        return ResponseEntity.ok("Books uploaded successfully");
    }
    
    /**
     * GET /api/v1/books/download - Download books as CSV
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadBooks() throws IOException {
        log.info("Downloading books as CSV");
        
        return bookService.downloadBook();
    }
    
    /**
     * GET /api/v1/books/tasks/{taskId} - Get task status
     */
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<AsyncTaskResponseDto> getTaskStatus(@PathVariable String taskId) {
        log.info("Getting task status: {}", taskId);
        
        return asyncTaskService.getTaskStatus(taskId)
                .map(task -> ResponseEntity.ok(AsyncTaskResponseDto.fromAsyncTaskRequest(task)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * DELETE /api/v1/books/tasks/{taskId} - Cancel a task
     */
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<String> cancelTask(
            @PathVariable String taskId,
            @RequestParam String userId) {
        
        log.info("Cancelling task: {} for user: {}", taskId, userId);
        
        boolean cancelled = asyncTaskService.cancelTask(taskId, userId);
        
        if (cancelled) {
            return ResponseEntity.ok("Task cancelled successfully");
        } else {
            return ResponseEntity.badRequest().body("Task could not be cancelled");
        }
    }
} 