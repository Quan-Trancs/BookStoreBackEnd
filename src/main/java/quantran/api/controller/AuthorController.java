package quantran.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import quantran.api.dto.BookDetailDto;
import quantran.api.entity.Author;
import quantran.api.page.Paginate;
import quantran.api.service.AuthorService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Log4j2
@Validated
public class AuthorController {

    private final AuthorService authorService;

    /**
     * Get all authors with pagination and search
     */
    @GetMapping
    public ResponseEntity<Paginate> getAuthors(
            @RequestParam(required = false) String searchName,
            @RequestParam(required = false) String searchCountry,
            @RequestParam(required = false) Boolean isAlive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("Getting authors with search: name={}, country={}, alive={}, page={}, size={}", 
                searchName, searchCountry, isAlive, page, pageSize);
        
        try {
            Paginate result = authorService.getAuthors(searchName, searchCountry, isAlive, page, pageSize);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error getting authors: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get author by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        log.info("Getting author by ID: {}", id);
        
        try {
            Optional<Author> author = authorService.getAuthorById(id);
            return author.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting author by ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new author
     */
    @PostMapping
    public ResponseEntity<Author> createAuthor(@Valid @RequestBody Author author) {
        log.info("Creating new author: {}", author.getName());
        
        try {
            Author createdAuthor = authorService.createAuthor(author);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAuthor);
        } catch (RuntimeException e) {
            log.error("Error creating author: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating author: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing author
     */
    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id, @Valid @RequestBody Author author) {
        log.info("Updating author with ID: {}", id);
        
        try {
            Author updatedAuthor = authorService.updateAuthor(id, author);
            return ResponseEntity.ok(updatedAuthor);
        } catch (RuntimeException e) {
            log.error("Error updating author: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating author: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete an author
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        log.info("Deleting author with ID: {}", id);
        
        try {
            authorService.deleteAuthor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting author: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error deleting author: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get authors by country
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Author>> getAuthorsByCountry(@PathVariable String country) {
        log.info("Getting authors by country: {}", country);
        
        try {
            List<Author> authors = authorService.getAuthorsByCountry(country);
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            log.error("Error getting authors by country: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get living authors
     */
    @GetMapping("/living")
    public ResponseEntity<List<Author>> getLivingAuthors() {
        log.info("Getting living authors");
        
        try {
            List<Author> authors = authorService.getLivingAuthors();
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            log.error("Error getting living authors: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get authors by birth year range
     */
    @GetMapping("/birth-year-range")
    public ResponseEntity<List<Author>> getAuthorsByBirthYearRange(
            @RequestParam int startYear,
            @RequestParam int endYear) {
        log.info("Getting authors by birth year range: {} - {}", startYear, endYear);
        
        try {
            List<Author> authors = authorService.getAuthorsByBirthYearRange(startYear, endYear);
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            log.error("Error getting authors by birth year range: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get top authors by book count
     */
    @GetMapping("/top")
    public ResponseEntity<List<Author>> getTopAuthorsByBookCount(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Getting top authors by book count, limit: {}", limit);
        
        try {
            List<Author> authors = authorService.getTopAuthorsByBookCount(limit);
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            log.error("Error getting top authors: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get authors by book genre
     */
    @GetMapping("/by-genre/{genreName}")
    public ResponseEntity<List<Author>> getAuthorsByBookGenre(@PathVariable String genreName) {
        log.info("Getting authors by book genre: {}", genreName);
        
        try {
            List<Author> authors = authorService.getAuthorsByBookGenre(genreName);
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            log.error("Error getting authors by book genre: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get authors by book publisher
     */
    @GetMapping("/by-publisher/{publisherName}")
    public ResponseEntity<List<Author>> getAuthorsByBookPublisher(@PathVariable String publisherName) {
        log.info("Getting authors by book publisher: {}", publisherName);
        
        try {
            List<Author> authors = authorService.getAuthorsByBookPublisher(publisherName);
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            log.error("Error getting authors by book publisher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get books by author
     */
    @GetMapping("/{id}/books")
    public ResponseEntity<List<BookDetailDto>> getBooksByAuthor(@PathVariable Long id) {
        log.info("Getting books by author ID: {}", id);
        
        try {
            List<BookDetailDto> books = authorService.getBooksByAuthor(id);
            return ResponseEntity.ok(books);
        } catch (RuntimeException e) {
            log.error("Error getting books by author: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error getting books by author: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get total author count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalAuthorCount() {
        log.info("Getting total author count");
        
        try {
            long count = authorService.getTotalAuthorCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error getting total author count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 