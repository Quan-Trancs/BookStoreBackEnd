package quantran.api.service;

import quantran.api.dto.BookDetailDto;
import quantran.api.entity.Author;
import quantran.api.page.Paginate;

import java.util.List;
import java.util.Optional;

public interface AuthorService extends BaseService<Author, Long> {
    
    /**
     * Get all authors with pagination and search
     */
    Paginate<Author> getAuthors(String searchName, String searchCountry, Boolean isAlive, int page, int pageSize);
    
    /**
     * Get author by name
     */
    Optional<Author> getAuthorByName(String name);
    
    /**
     * Get authors by country
     */
    List<Author> getAuthorsByCountry(String country);
    
    /**
     * Get living authors
     */
    List<Author> getLivingAuthors();
    
    /**
     * Get authors by birth year range
     */
    List<Author> getAuthorsByBirthYearRange(int startYear, int endYear);
    
    /**
     * Get authors with most books
     */
    List<Author> getTopAuthorsByBookCount(int limit);
    
    /**
     * Get authors by book genre
     */
    List<Author> getAuthorsByBookGenre(String genreName);
    
    /**
     * Get authors by book publisher
     */
    List<Author> getAuthorsByBookPublisher(String publisherName);
    
    /**
     * Get books by author
     */
    List<BookDetailDto> getBooksByAuthor(Long authorId);
    
    /**
     * Get total author count
     */
    long getTotalAuthorCount();
} 