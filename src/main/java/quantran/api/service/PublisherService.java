package quantran.api.service;

import quantran.api.dto.BookDetailDto;
import quantran.api.entity.Publisher;
import quantran.api.page.Paginate;

import java.util.List;
import java.util.Optional;

public interface PublisherService extends BaseService<Publisher, Long> {
    
    /**
     * Get all publishers with pagination and search
     */
    Paginate<Publisher> getPublishers(String searchName, String searchCountry, String searchCity, Boolean isActive, int page, int pageSize);
    
    /**
     * Get publisher by name
     */
    Optional<Publisher> getPublisherByName(String name);
    
    /**
     * Get publishers by country
     */
    List<Publisher> getPublishersByCountry(String country);
    
    /**
     * Get publishers by city
     */
    List<Publisher> getPublishersByCity(String city);
    
    /**
     * Get publishers by founded year
     */
    List<Publisher> getPublishersByFoundedYear(Integer foundedYear);
    
    /**
     * Get publishers founded before a specific year
     */
    List<Publisher> getPublishersFoundedBefore(Integer year);
    
    /**
     * Get publishers founded after a specific year
     */
    List<Publisher> getPublishersFoundedAfter(Integer year);
    
    /**
     * Get top publishers by book count
     */
    List<Publisher> getTopPublishersByBookCount(int limit);
    
    /**
     * Get publishers by book genre
     */
    List<Publisher> getPublishersByBookGenre(String genreName);
    
    /**
     * Get publishers by book author
     */
    List<Publisher> getPublishersByBookAuthor(String authorName);
    
    /**
     * Get books by publisher
     */
    List<BookDetailDto> getBooksByPublisher(Long publisherId);
    
    /**
     * Get total publisher count
     */
    long getTotalPublisherCount();
} 