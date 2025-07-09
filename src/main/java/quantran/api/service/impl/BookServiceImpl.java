package quantran.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import quantran.api.dto.BookDetailDto;
import quantran.api.entity.BookEntity;
import quantran.api.entity.BookType;
import quantran.api.business.BookBusiness;
import quantran.api.model.BookModel;
import quantran.api.page.Paginate;
import quantran.api.repository.BookRepository;
import quantran.api.service.BookService;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {
    private final BookBusiness bookBusiness;
    private final BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadBook() throws IOException {
        log.info("Start downloadBook()");
        
        String csvContent = bookBusiness.downloadBook();
        String header = "ID,Name,Author,BookType,Price\n";
        String fullContent = header + csvContent;
        
        // Create ZIP file with CSV
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry("books.csv");
            zos.putNextEntry(entry);
            zos.write(fullContent.getBytes());
            zos.closeEntry();
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "books.zip");
        
        log.info("End downloadBook()");
        return ResponseEntity.ok()
                .headers(headers)
                .body(baos.toByteArray());
    }

    @Override
    @Transactional
    public void uploadBook(MultipartFile bookFile) throws IOException {
        log.info("Start uploadBook()");
        List<BookModel> bookList = new ArrayList<>();
        
        try (BufferedReader bookReader = new BufferedReader(new InputStreamReader(bookFile.getInputStream()))) {
            String line;
            int lineNumber = 0;
            
            while ((line = bookReader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] data = line.split(",");
                    
                    // Validate CSV format - should have exactly 5 columns
                    if (data.length != 5) {
                        log.warn("Skipping line {}: Invalid CSV format. Expected 5 columns, found {}", lineNumber, data.length);
                        continue;
                    }
                    
                    // Validate that required fields are not empty
                    if (data[0].trim().isEmpty() || data[1].trim().isEmpty() || 
                        data[2].trim().isEmpty() || data[3].trim().isEmpty() || data[4].trim().isEmpty()) {
                        log.warn("Skipping line {}: Empty required fields", lineNumber);
                        continue;
                    }
                    
                    BookModel bookModel = new BookModel(
                        data[0].trim(), // id
                        data[1].trim(), // name
                        data[2].trim(), // author
                        data[4].trim(), // price
                        data[3].trim()  // bookType
                    );
                    bookList.add(bookModel);
                    
                } catch (Exception e) {
                    log.error("Error processing line {}: {}", lineNumber, e.getMessage());
                }
            }
        }
        
        if (!bookList.isEmpty()) {
            bookBusiness.uploadBook(bookList);
        } else {
            log.warn("No valid books found in uploaded file");
        }
        
        log.info("End uploadBook()");
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookType> getBookType() {
        log.info("Start getBookType()");

        List<BookType> bookTypes = bookBusiness.getBookType();

        log.info("End getBookType()");
        return bookTypes;
    }
    
    @Override
    @Transactional
    public void addBook(BookModel bookModel) {
        log.info("Start addBook()");
        bookBusiness.addBook(bookModel);
        log.info("End addBook()");
    }
    
    @Override
    @Transactional
    public void delBook(String delId) {
        log.info("Start delBook()");
        bookBusiness.delBook(delId);
        log.info("End delBook()");
    }
    
    @Override
    @Transactional
    public void updateBook(BookModel bookModel) {
        log.info("Start updateBook()");
        bookBusiness.updateBook(bookModel);
        log.info("End updateBook()");
    }

    // Enhanced book management methods
    @Override
    @Transactional(readOnly = true)
    public Paginate<BookModel> getBook(String searchTitle, String searchAuthor, String searchId, String searchGenre, String searchPublisher, int page, int pageSize) {
        log.info("Start getBook() with enhanced search");
        return bookBusiness.getBook(searchId, searchTitle, searchAuthor, searchGenre, searchPublisher, page, pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDetailDto> getBookById(String id) {
        return bookRepository.findById(id)
                .map(this::convertToBookDetailDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDetailDto> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(this::convertToBookDetailDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDetailDto> getBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId)
                .stream()
                .map(this::convertToBookDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDetailDto> getBooksByGenre(String genreId) {
        return bookRepository.findByGenreId(genreId)
                .stream()
                .map(this::convertToBookDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDetailDto> getBooksByPublisher(Long publisherId) {
        return bookRepository.findByPublisherId(publisherId)
                .stream()
                .map(this::convertToBookDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDetailDto> getBooksWithLowStock() {
        return bookRepository.findBooksWithLowStock()
                .stream()
                .map(this::convertToBookDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDetailDto> getOutOfStockBooks() {
        return bookRepository.findOutOfStockBooks()
                .stream()
                .map(this::convertToBookDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDetailDto> getBooksWithDiscount() {
        return bookRepository.findBooksWithDiscount()
                .stream()
                .map(this::convertToBookDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDetailDto> getBooksByPublicationYear(int year) {
        return bookRepository.findByPublicationYear(year)
                .stream()
                .map(this::convertToBookDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDetailDto> getBooksByLanguage(String language) {
        return bookRepository.findByLanguage(language)
                .stream()
                .map(this::convertToBookDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDetailDto> getBooksByFormat(String format) {
        return bookRepository.findByFormat(format)
                .stream()
                .map(this::convertToBookDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDetailDto> getBooksByPriceRange(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice) {
        return bookRepository.findByPriceBetween(minPrice, maxPrice)
                .stream()
                .map(this::convertToBookDetailDto)
                .collect(Collectors.toList());
    }

    // Inventory management methods
    @Override
    @Transactional
    public void updateStock(String bookId, Integer quantity) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        book.addStock(quantity);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void reserveBook(String bookId, Integer quantity) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        book.reserve(quantity);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void releaseBook(String bookId, Integer quantity) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        book.release(quantity);
        bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBookAvailable(String bookId, Integer quantity) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        return book.canReserve(quantity);
    }

    // Helper method to convert BookEntity to BookDetailDto
    private BookDetailDto convertToBookDetailDto(BookEntity book) {
        return BookDetailDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .subtitle(book.getSubtitle())
                .isbn(book.getIsbn())
                .isbn13(book.getIsbn13())
                .description(book.getDescription())
                .pageCount(book.getPageCount())
                .language(book.getLanguage())
                .publicationDate(book.getPublicationDate())
                .edition(book.getEdition())
                .format(book.getFormat())
                .price(book.getPrice())
                .originalPrice(book.getOriginalPrice())
                .discountedPrice(book.getDiscountedPrice())
                .discountPercentage(book.getDiscountPercentage())
                .stockQuantity(book.getStockQuantity())
                .availableQuantity(book.getAvailableQuantity())
                .reservedQuantity(book.getReservedQuantity())
                .reorderPoint(book.getReorderPoint())
                .maxStock(book.getMaxStock())
                .isLowStock(book.isLowStock())
                .isOutOfStock(book.isOutOfStock())
                .authors(book.getAuthors().stream()
                    .map(author -> BookDetailDto.AuthorDto.builder()
                        .id(author.getId())
                        .name(author.getName())
                        .biography(author.getBiography())
                        .country(author.getCountry())
                        .website(author.getWebsite())
                        .bookCount(author.getBookCount())
                        .build())
                    .collect(Collectors.toList()))
                .genres(book.getGenres().stream()
                    .map(genre -> BookDetailDto.GenreDto.builder()
                        .id(genre.getId())
                        .name(genre.getName())
                        .description(genre.getDescription())
                        .ageRating(genre.getAgeRating())
                        .bookCount(genre.getBookCount())
                        .build())
                    .collect(Collectors.toList()))
                .publisher(book.getPublisher() != null ? BookDetailDto.PublisherDto.builder()
                    .id(book.getPublisher().getId())
                    .name(book.getPublisher().getName())
                    .description(book.getPublisher().getDescription())
                    .country(book.getPublisher().getCountry())
                    .city(book.getPublisher().getCity())
                    .website(book.getPublisher().getWebsite())
                    .foundedYear(book.getPublisher().getFoundedYear())
                    .bookCount(book.getPublisher().getBookCount())
                    .build() : null)
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}
