package quantran.api.entity;

import quantran.api.BookType.BookType;
import quantran.api.model.BookModel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "book")
public class BookEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "author")
    private String author;
    @Column(name = "price")
    private double price;
    @ManyToOne()
    @JoinColumn(name = "bookType", referencedColumnName = "bookType")
    private BookType bookType;

    public BookEntity() {}
    public BookEntity(BookModel bookModel) {
        this.id = bookModel.getId();
        this.name = bookModel.getName();
        this.author = bookModel.getAuthor();
        this.price = parsePrice(bookModel.getPrice());
        BookType newBookType = new BookType(bookModel.getBookType());
        this.bookType = newBookType;
    }
    public BookEntity(String id, String name, String author, BookType bookType, double price) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.bookType = bookType;
        this.price = price;
    }
    public BookEntity(String id, String name, String author, String bookType, double price) {
        this.id = id;
        this.name = name;
        this.author = author;
        BookType newBookType = new BookType(bookType);
        this.bookType = newBookType;
        this.price = price;
    }

    private double parsePrice(String priceString) {
        try {
            // Remove "VND" suffix and parse the number
            String numericPart = priceString.replaceAll("(?i)vnd$", "").trim();
            BigDecimal price = new BigDecimal(numericPart);
            // Convert from VND to USD (assuming 23000 VND = 1 USD)
            BigDecimal usdPrice = price.divide(BigDecimal.valueOf(23000), 2, RoundingMode.HALF_UP);
            return usdPrice.doubleValue();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price format: " + priceString);
        }
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

    public BookType getBookType() {
        return bookType;
    }

    public void setBookType(BookType bookType) {
        this.bookType = bookType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
