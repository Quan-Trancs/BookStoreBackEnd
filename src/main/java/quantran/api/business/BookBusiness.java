package quantran.api.business;

import quantran.api.entity.BookEntity;
import quantran.api.model.BookModel;
import quantran.api.page.Paginate;
import quantran.api.entity.BookType;

import java.util.List;

public interface BookBusiness {

    default List<BookType> getBookType() { return null; };

    default Paginate<BookModel> getBook(String searchId, String searchName, String searchAuthor, String searchGenre, String searchPublisher, int page, int pageSize) {
        return null;
    }
    default String downloadBook(){
        return null;
    };
    default void uploadBook(List<BookModel> bookModels){};
    default void addBook(BookModel bookModel) {}
    default void delBook(String delId) {}
    default void updateBook(BookModel bookModel) {}
}
