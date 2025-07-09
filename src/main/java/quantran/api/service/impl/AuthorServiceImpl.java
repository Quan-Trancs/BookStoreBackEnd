package quantran.api.service.impl;

import quantran.api.dto.BookDetailDto;
import quantran.api.entity.Author;
import quantran.api.page.Paginate;
import quantran.api.repository.AuthorRepository;
import quantran.api.service.AuthorService;
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
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public Paginate<Author> getAuthors(String searchName, String searchCountry, Boolean isAlive, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Author> authorPage = authorRepository.findAuthorsWithSearch(searchName, searchCountry, isAlive, pageable);
        
        // Convert to generic Paginate with Author entities
        return new Paginate<>(authorPage.getContent(), (int) authorPage.getTotalElements());
    }

    @Override
    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    @Override
    public Optional<Author> getAuthorByName(String name) {
        return authorRepository.findByNameIgnoreCase(name);
    }

    @Override
    public Author createAuthor(Author author) {
        // Check if author with same name already exists
        Optional<Author> existingAuthor = authorRepository.findByNameIgnoreCase(author.getName());
        if (existingAuthor.isPresent()) {
            throw new RuntimeException("Author with name '" + author.getName() + "' already exists");
        }
        return authorRepository.save(author);
    }

    @Override
    public Author updateAuthor(Long id, Author authorDetails) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
        
        author.setName(authorDetails.getName());
        author.setBiography(authorDetails.getBiography());
        author.setBirthDate(authorDetails.getBirthDate());
        author.setCountry(authorDetails.getCountry());
        
        return authorRepository.save(author);
    }

    @Override
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
        
        // Check if author has any books before deleting
        if (!author.getBooks().isEmpty()) {
            throw new RuntimeException("Cannot delete author with existing books. Remove books first.");
        }
        
        authorRepository.delete(author);
    }

    @Override
    public List<Author> getAuthorsByCountry(String country) {
        return authorRepository.findByCountryIgnoreCase(country);
    }

    @Override
    public List<Author> getLivingAuthors() {
        return authorRepository.findLivingAuthors();
    }

    @Override
    public List<Author> getAuthorsByBirthYearRange(int startYear, int endYear) {
        return authorRepository.findByBirthYearRange(startYear, endYear);
    }

    @Override
    public List<Author> getTopAuthorsByBookCount(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return authorRepository.findTopAuthorsByBookCount(pageable);
    }

    @Override
    public List<Author> getAuthorsByBookGenre(String genreName) {
        return authorRepository.findByBookGenre(genreName);
    }

    @Override
    public List<Author> getAuthorsByBookPublisher(String publisherName) {
        return authorRepository.findByBookPublisher(publisherName);
    }

    @Override
    public List<BookDetailDto> getBooksByAuthor(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));
        
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
        return authorRepository.getTotalAuthorCount();
    }
} 