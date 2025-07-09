package quantran.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quantran.api.entity.Author;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    /**
     * Find authors by name (case-insensitive)
     */
    List<Author> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find author by exact name
     */
    Optional<Author> findByNameIgnoreCase(String name);
    
    /**
     * Find authors by country
     */
    List<Author> findByCountryIgnoreCase(String country);
    
    /**
     * Find living authors
     */
    @Query("SELECT a FROM Author a WHERE a.deathDate IS NULL")
    List<Author> findLivingAuthors();
    
    /**
     * Find authors by birth year range
     */
    @Query("SELECT a FROM Author a WHERE YEAR(a.birthDate) BETWEEN :startYear AND :endYear")
    List<Author> findByBirthYearRange(@Param("startYear") int startYear, @Param("endYear") int endYear);
    
    /**
     * Find authors with book count greater than specified
     */
    @Query("SELECT a FROM Author a WHERE SIZE(a.books) > :minBookCount")
    List<Author> findByBookCountGreaterThan(@Param("minBookCount") int minBookCount);
    
    /**
     * Find authors by active status
     */
    List<Author> findByIsActive(Boolean isActive);
    
    /**
     * Search authors with pagination
     */
    @Query("SELECT a FROM Author a WHERE " +
           "(:name IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:country IS NULL OR LOWER(a.country) LIKE LOWER(CONCAT('%', :country, '%'))) AND " +
           "(:isAlive IS NULL OR (a.deathDate IS NULL) = :isAlive)")
    Page<Author> findAuthorsWithSearch(
            @Param("name") String name,
            @Param("country") String country,
            @Param("isAlive") Boolean isAlive,
            Pageable pageable);
    
    /**
     * Find authors by book genre
     */
    @Query("SELECT DISTINCT a FROM Author a JOIN a.books b JOIN b.genres g WHERE g.name = :genreName")
    List<Author> findByBookGenre(@Param("genreName") String genreName);
    
    /**
     * Find authors by book publisher
     */
    @Query("SELECT DISTINCT a FROM Author a JOIN a.books b WHERE b.publisher.name = :publisherName")
    List<Author> findByBookPublisher(@Param("publisherName") String publisherName);
    
    /**
     * Get total count of authors
     */
    @Query("SELECT COUNT(a) FROM Author a")
    long getTotalAuthorCount();
    
    /**
     * Get authors with most books
     */
    @Query("SELECT a FROM Author a ORDER BY SIZE(a.books) DESC")
    List<Author> findTopAuthorsByBookCount(Pageable pageable);
} 