package quantran.api.BookType;

import quantran.api.entity.BookEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "bookType")
public class BookType {
    @Id
    @Column(name = "bookType")
    private String bookType;

    @OneToMany(mappedBy = "bookType")
    private List<BookEntity> bookEntities;

    public BookType() {}
    public BookType(String bookType) {
        this.bookType = bookType;
    }
    public String getBookType() {
        return bookType;
    }

    public void setBookType(String secretField) {
        this.bookType = bookType;
    }
}
