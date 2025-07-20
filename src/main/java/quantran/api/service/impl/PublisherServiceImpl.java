package quantran.api.service.impl;

import quantran.api.dto.BookDetailDto;
import quantran.api.entity.Publisher;
import quantran.api.page.Paginate;
import quantran.api.repository.PublisherRepository;
import quantran.api.service.PublisherService;
import quantran.api.service.AbstractBaseService;
import quantran.api.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PublisherServiceImpl extends AbstractBaseService<Publisher, Long, PublisherRepository> implements PublisherService {

    @Autowired
    public PublisherServiceImpl(PublisherRepository publisherRepository) {
        super(publisherRepository);
    }

    @Override
    public Paginate<Publisher> getPublishers(String searchName, String searchCountry, String searchCity, Boolean isActive, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Publisher> publisherPage = repository.findPublishersWithSearch(searchName, searchCountry, searchCity, isActive, pageable);
        
        // Convert to generic Paginate with Publisher entities
        return new Paginate<>(publisherPage.getContent(), (int) publisherPage.getTotalElements());
    }

    @Override
    public Optional<Publisher> getPublisherByName(String name) {
        return repository.findByNameIgnoreCase(name);
    }

    @Override
    protected void validateBeforeCreate(Publisher publisher) {
        ValidationUtil.validateNameDoesNotExist(
            publisher.getName(),
            repository.findByNameIgnoreCase(publisher.getName()),
            "Publisher"
        );
    }

    @Override
    protected void validateBeforeDelete(Publisher publisher) {
        ValidationUtil.validateEntityCanBeDeleted(
            !publisher.getBooks().isEmpty(),
            "Publisher",
            "books"
        );
    }

    @Override
    protected void updateEntityFields(Publisher target, Publisher source) {
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setCountry(source.getCountry());
        target.setCity(source.getCity());
        target.setWebsite(source.getWebsite());
        target.setFoundedYear(source.getFoundedYear());
    }

    @Override
    protected Long getEntityId(Publisher entity) {
        return entity.getId();
    }

    @Override
    protected String getEntityTypeName() {
        return "Publisher";
    }

    @Override
    public List<Publisher> getPublishersByCountry(String country) {
        return repository.findByCountryIgnoreCase(country);
    }

    @Override
    public List<Publisher> getPublishersByCity(String city) {
        return repository.findByCityIgnoreCase(city);
    }

    @Override
    public List<Publisher> getPublishersByFoundedYear(Integer foundedYear) {
        return repository.findByFoundedYear(foundedYear);
    }

    @Override
    public List<Publisher> getPublishersFoundedBefore(Integer year) {
        return repository.findByFoundedYearBefore(year);
    }

    @Override
    public List<Publisher> getPublishersFoundedAfter(Integer year) {
        return repository.findByFoundedYearAfter(year);
    }

    @Override
    public List<Publisher> getTopPublishersByBookCount(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return repository.findTopPublishersByBookCount(pageable);
    }

    @Override
    public List<Publisher> getPublishersByBookGenre(String genreName) {
        return repository.findByBookGenre(genreName);
    }

    @Override
    public List<Publisher> getPublishersByBookAuthor(String authorName) {
        return repository.findByBookAuthor(authorName);
    }

    @Override
    public List<BookDetailDto> getBooksByPublisher(Long publisherId) {
        Publisher publisher = ValidationUtil.validateEntityExists(
            repository.findById(publisherId), publisherId, "Publisher"
        );
        
        return publisher.getBooks().stream()
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
                    .publisher(BookDetailDto.PublisherDto.builder()
                        .id(publisher.getId())
                        .name(publisher.getName())
                        .description(publisher.getDescription())
                        .country(publisher.getCountry())
                        .city(publisher.getCity())
                        .website(publisher.getWebsite())
                        .foundedYear(publisher.getFoundedYear())
                        .bookCount(publisher.getBookCount())
                        .build())
                    .createdAt(book.getCreatedAt())
                    .updatedAt(book.getUpdatedAt())
                    .build())
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalPublisherCount() {
        return repository.getTotalPublisherCount();
    }
} 