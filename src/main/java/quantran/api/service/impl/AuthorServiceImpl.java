package quantran.api.service.impl;

import quantran.api.dto.BookDetailDto;
import quantran.api.entity.Author;
import quantran.api.page.Paginate;
import quantran.api.repository.AuthorRepository;
import quantran.api.service.AuthorService;
import quantran.api.service.AbstractBaseService;
import quantran.api.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthorServiceImpl extends AbstractBaseService<Author, Long, AuthorRepository> implements AuthorService {

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository) {
        super(authorRepository);
    }

    @Override
    public Paginate<Author> getAuthors(String searchName, String searchCountry, Boolean isAlive, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Author> authorPage = repository.findAuthorsWithSearch(searchName, searchCountry, isAlive, pageable);
        
        // Convert to generic Paginate with Author entities
        return new Paginate<>(authorPage.getContent(), (int) authorPage.getTotalElements());
    }

    @Override
    public Optional<Author> getAuthorByName(String name) {
        return repository.findByNameIgnoreCase(name);
    }

    @Override
    protected void validateBeforeCreate(Author author) {
        ValidationUtil.validateNameDoesNotExist(
            author.getName(),
            repository.findByNameIgnoreCase(author.getName()),
            "Author"
        );
    }

    @Override
    protected void validateBeforeDelete(Author author) {
        ValidationUtil.validateEntityCanBeDeleted(
            !author.getBooks().isEmpty(),
            "Author",
            "books"
        );
    }

    @Override
    protected void updateEntityFields(Author target, Author source) {
        target.setName(source.getName());
        target.setBiography(source.getBiography());
        target.setBirthDate(source.getBirthDate());
        target.setCountry(source.getCountry());
    }

    @Override
    protected Long getEntityId(Author entity) {
        return entity.getId();
    }

    @Override
    protected String getEntityTypeName() {
        return "Author";
    }

    @Override
    public List<Author> getAuthorsByCountry(String country) {
        return repository.findByCountryIgnoreCase(country);
    }

    @Override
    public List<Author> getLivingAuthors() {
        return repository.findLivingAuthors();
    }

    @Override
    public List<Author> getAuthorsByBirthYearRange(int startYear, int endYear) {
        return repository.findByBirthYearRange(startYear, endYear);
    }

    @Override
    public List<Author> getTopAuthorsByBookCount(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return repository.findTopAuthorsByBookCount(pageable);
    }

    @Override
    public List<Author> getAuthorsByBookGenre(String genreName) {
        return repository.findByBookGenre(genreName);
    }

    @Override
    public List<Author> getAuthorsByBookPublisher(String publisherName) {
        return repository.findByBookPublisher(publisherName);
    }

    @Override
    public List<BookDetailDto> getBooksByAuthor(Long authorId) {
        Author author = ValidationUtil.validateEntityExists(
            repository.findById(authorId), authorId, "Author"
        );
        
        return author.getBooks().stream()
                .map(book -> BookDetailDto.builder()
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
                        .map(authorEntity -> BookDetailDto.AuthorDto.builder()
                            .id(authorEntity.getId())
                            .name(authorEntity.getName())
                            .biography(authorEntity.getBiography())
                            .country(authorEntity.getCountry())
                            .website(authorEntity.getWebsite())
                            .bookCount(authorEntity.getBookCount())
                            .build())
                        .collect(Collectors.toList()))
                    .genres(book.getGenres().stream()
                        .map(genreEntity -> BookDetailDto.GenreDto.builder()
                            .id(genreEntity.getId())
                            .name(genreEntity.getName())
                            .description(genreEntity.getDescription())
                            .ageRating(genreEntity.getAgeRating())
                            .bookCount(0) // BookType doesn't have getBookCount method
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
                    .build())
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalAuthorCount() {
        return repository.getTotalAuthorCount();
    }
} 