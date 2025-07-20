package quantran.api.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import quantran.api.entity.BookEntity;
import quantran.api.entity.BookType;
import quantran.api.model.BookModel;
import quantran.api.page.Paginate;
import quantran.api.dto.BookDetailDto;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface BookService extends BaseService<BookEntity, String> {
    
    // Basic CRUD operations inherited from BaseService
    // - getAll(int page, int size)
    // - getById(String id)
    // - create(BookEntity entity)
    // - update(String id, BookEntity entity)
    // - delete(String id)
    // - exists(String id)
    // - getTotalCount()
    
    // Specialized book operations
    Paginate<BookModel> getBook(String searchTitle, String searchAuthor, String searchId, String searchGenre, String searchPublisher, int page, int pageSize);

    List<BookType> getBookType();

    void addBook(BookModel bookModel);
    void delBook(String delId);
    void updateBook(BookModel bookModel);
    void uploadBook(MultipartFile bookFile) throws IOException;
    ResponseEntity<byte[]> downloadBook() throws IOException;
    
    // Enhanced book management methods
    Optional<BookDetailDto> getBookById(String id);
    Optional<BookDetailDto> getBookByIsbn(String isbn);
    List<BookDetailDto> getBooksByAuthor(Long authorId);
    List<BookDetailDto> getBooksByGenre(String genreId);
    List<BookDetailDto> getBooksByPublisher(Long publisherId);
    List<BookDetailDto> getBooksWithLowStock();
    List<BookDetailDto> getOutOfStockBooks();
    List<BookDetailDto> getBooksWithDiscount();
    List<BookDetailDto> getBooksByPublicationYear(int year);
    List<BookDetailDto> getBooksByLanguage(String language);
    List<BookDetailDto> getBooksByFormat(String format);
    List<BookDetailDto> getBooksByPriceRange(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
    
    // Inventory management
    void updateStock(String bookId, Integer quantity);
    void reserveBook(String bookId, Integer quantity);
    void releaseBook(String bookId, Integer quantity);
    boolean isBookAvailable(String bookId, Integer quantity);
}
