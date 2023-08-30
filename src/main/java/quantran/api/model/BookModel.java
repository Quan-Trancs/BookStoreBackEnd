package quantran.api.model;

import quantran.api.entity.BookEntity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class BookModel {
    @NotNull(message = "book's id is required")
    @NotEmpty(message = "book's id is required")
    private String id;
    private String name;
    private String author;
    @NotNull(message = "book's price is required")
    @NotEmpty(message = "book's price is required")
    @Pattern(regexp = "(?i)^[1-9]\\d*(vnd)$", message = "invalid price input")
    private String price;
    @NotNull(message = "book's type is required")
    @NotEmpty(message = "book's type is required")
    private String bookType;

    public BookModel(String id, String name, String author, String price, String bookType) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.bookType = bookType;
    }
    public BookModel(BookEntity bookEntity) {
        this.id = bookEntity.getId();
        this.name = bookEntity.getName();
        this.author = bookEntity.getAuthor();
        this.price = String.format("%.0f VND", bookEntity.getPrice() * 23000);
        this.bookType = bookEntity.getBookType().getBookType();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBookType() { return bookType; }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

}
