package quantran.api.service.impl;

import quantran.api.dto.BookDetailDto;
import quantran.api.entity.Publisher;
import quantran.api.page.Paginate;
import quantran.api.repository.PublisherRepository;
import quantran.api.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PublisherServiceImpl implements PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;

    @Override
    @Cacheable(value = "publishers", key = "#searchName + '-' + #searchCountry + '-' + #searchCity + '-' + #isActive + '-' + #page + '-' + #pageSize")
    public Paginate<Publisher> getPublishers(String searchName, String searchCountry, String searchCity, Boolean isActive, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Publisher> publisherPage = publisherRepository.findPublishersWithSearch(searchName, searchCountry, searchCity, isActive, pageable);
        
        // Convert to generic Paginate with Publisher entities
        return new Paginate<>(publisherPage.getContent(), (int) publisherPage.getTotalElements());
    }

    @Override
    @Cacheable(value = "publisherDetails", key = "#id")
    public Optional<Publisher> getPublisherById(Long id) {
        return publisherRepository.findById(id);
    }

    @Override
    @Cacheable(value = "publisherDetailsByName", key = "#name")
    public Optional<Publisher> getPublisherByName(String name) {
        return publisherRepository.findByNameIgnoreCase(name);
    }

    @Override
    public Publisher createPublisher(Publisher publisher) {
        // Check if publisher with same name already exists
        Optional<Publisher> existingPublisher = publisherRepository.findByNameIgnoreCase(publisher.getName());
        if (existingPublisher.isPresent()) {
            throw new RuntimeException("Publisher with name '" + publisher.getName() + "' already exists");
        }
        return publisherRepository.save(publisher);
    }

    @Override
    public Publisher updatePublisher(Long id, Publisher publisherDetails) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + id));
        
        publisher.setName(publisherDetails.getName());
        publisher.setDescription(publisherDetails.getDescription());
        publisher.setCountry(publisherDetails.getCountry());
        publisher.setCity(publisherDetails.getCity());
        publisher.setWebsite(publisherDetails.getWebsite());
        publisher.setFoundedYear(publisherDetails.getFoundedYear());
        
        return publisherRepository.save(publisher);
    }

    @Override
    public void deletePublisher(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + id));
        
        // Check if publisher has any books before deleting
        if (!publisher.getBooks().isEmpty()) {
            throw new RuntimeException("Cannot delete publisher with existing books. Remove books first.");
        }
        
        publisherRepository.delete(publisher);
    }

    @Override
    @Cacheable(value = "publishersByCountry", key = "#country")
    public List<Publisher> getPublishersByCountry(String country) {
        return publisherRepository.findByCountryIgnoreCase(country);
    }

    @Override
    @Cacheable(value = "publishersByCity", key = "#city")
    public List<Publisher> getPublishersByCity(String city) {
        return publisherRepository.findByCityIgnoreCase(city);
    }

    @Override
    @Cacheable(value = "publishersByFoundedYear", key = "#foundedYear")
    public List<Publisher> getPublishersByFoundedYear(Integer foundedYear) {
        return publisherRepository.findByFoundedYear(foundedYear);
    }

    @Override
    @Cacheable(value = "publishersFoundedBefore", key = "#year")
    public List<Publisher> getPublishersFoundedBefore(Integer year) {
        return publisherRepository.findByFoundedYearBefore(year);
    }

    @Override
    @Cacheable(value = "publishersFoundedAfter", key = "#year")
    public List<Publisher> getPublishersFoundedAfter(Integer year) {
        return publisherRepository.findByFoundedYearAfter(year);
    }

    @Override
    @Cacheable(value = "topPublishersByBookCount", key = "#limit")
    public List<Publisher> getTopPublishersByBookCount(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return publisherRepository.findTopPublishersByBookCount(pageable);
    }

    @Override
    @Cacheable(value = "publishersByBookGenre", key = "#genreName")
    public List<Publisher> getPublishersByBookGenre(String genreName) {
        return publisherRepository.findByBookGenre(genreName);
    }

    @Override
    @Cacheable(value = "publishersByBookAuthor", key = "#authorName")
    public List<Publisher> getPublishersByBookAuthor(String authorName) {
        return publisherRepository.findByBookAuthor(authorName);
    }

    @Override
    public List<BookDetailDto> getBooksByPublisher(Long publisherId) {
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new RuntimeException("Publisher not found with id: " + publisherId));
        
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
        return publisherRepository.getTotalPublisherCount();
    }
} 