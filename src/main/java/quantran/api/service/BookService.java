package quantran.api.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import quantran.api.BookType.BookType;
import quantran.api.model.BookModel;
import quantran.api.page.Paginate;

import java.io.IOException;
import java.util.List;

public interface BookService {
    Paginate getBook(String searchName, String searchAuthor, String searchId, int page, int pageSize);

    List<BookType> getBookType();

    void addBook(BookModel bookModel);
    void delBook(String delId);
    void updateBook(BookModel bookModel);
    void uploadBook(MultipartFile bookFile) throws IOException;
    ResponseEntity<byte[]> downloadBook() throws IOException;
}
