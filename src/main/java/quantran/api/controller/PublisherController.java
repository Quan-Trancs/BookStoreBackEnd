package quantran.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import quantran.api.dto.BookDetailDto;
import quantran.api.entity.Publisher;
import quantran.api.page.Paginate;
import quantran.api.service.PublisherService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
@Log4j2
@Validated
public class PublisherController {

    private final PublisherService publisherService;

    /**
     * Get all publishers with pagination and search
     */
    @GetMapping
    public ResponseEntity<Paginate> getPublishers(
            @RequestParam(required = false) String searchName,
            @RequestParam(required = false) String searchCountry,
            @RequestParam(required = false) String searchCity,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("Getting publishers with search: name={}, country={}, city={}, active={}, page={}, size={}", 
                searchName, searchCountry, searchCity, isActive, page, pageSize);
        
        try {
            Paginate result = publisherService.getPublishers(searchName, searchCountry, searchCity, isActive, page, pageSize);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error getting publishers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get publisher by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getPublisherById(@PathVariable Long id) {
        log.info("Getting publisher by ID: {}", id);
        
        try {
            Optional<Publisher> publisher = publisherService.getPublisherById(id);
            return publisher.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting publisher by ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new publisher
     */
    @PostMapping
    public ResponseEntity<Publisher> createPublisher(@Valid @RequestBody Publisher publisher) {
        log.info("Creating new publisher: {}", publisher.getName());
        
        try {
            Publisher createdPublisher = publisherService.createPublisher(publisher);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPublisher);
        } catch (RuntimeException e) {
            log.error("Error creating publisher: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating publisher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing publisher
     */
    @PutMapping("/{id}")
    public ResponseEntity<Publisher> updatePublisher(@PathVariable Long id, @Valid @RequestBody Publisher publisher) {
        log.info("Updating publisher with ID: {}", id);
        
        try {
            Publisher updatedPublisher = publisherService.updatePublisher(id, publisher);
            return ResponseEntity.ok(updatedPublisher);
        } catch (RuntimeException e) {
            log.error("Error updating publisher: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating publisher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a publisher
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        log.info("Deleting publisher with ID: {}", id);
        
        try {
            publisherService.deletePublisher(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting publisher: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error deleting publisher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get publishers by country
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Publisher>> getPublishersByCountry(@PathVariable String country) {
        log.info("Getting publishers by country: {}", country);
        
        try {
            List<Publisher> publishers = publisherService.getPublishersByCountry(country);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error getting publishers by country: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get publishers by city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Publisher>> getPublishersByCity(@PathVariable String city) {
        log.info("Getting publishers by city: {}", city);
        
        try {
            List<Publisher> publishers = publisherService.getPublishersByCity(city);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error getting publishers by city: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get publishers by founded year
     */
    @GetMapping("/founded-year/{year}")
    public ResponseEntity<List<Publisher>> getPublishersByFoundedYear(@PathVariable Integer year) {
        log.info("Getting publishers by founded year: {}", year);
        
        try {
            List<Publisher> publishers = publisherService.getPublishersByFoundedYear(year);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error getting publishers by founded year: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get publishers founded before a specific year
     */
    @GetMapping("/founded-before/{year}")
    public ResponseEntity<List<Publisher>> getPublishersFoundedBefore(@PathVariable Integer year) {
        log.info("Getting publishers founded before: {}", year);
        
        try {
            List<Publisher> publishers = publisherService.getPublishersFoundedBefore(year);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error getting publishers founded before: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get publishers founded after a specific year
     */
    @GetMapping("/founded-after/{year}")
    public ResponseEntity<List<Publisher>> getPublishersFoundedAfter(@PathVariable Integer year) {
        log.info("Getting publishers founded after: {}", year);
        
        try {
            List<Publisher> publishers = publisherService.getPublishersFoundedAfter(year);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error getting publishers founded after: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get top publishers by book count
     */
    @GetMapping("/top")
    public ResponseEntity<List<Publisher>> getTopPublishersByBookCount(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Getting top publishers by book count, limit: {}", limit);
        
        try {
            List<Publisher> publishers = publisherService.getTopPublishersByBookCount(limit);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error getting top publishers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get publishers by book genre
     */
    @GetMapping("/by-genre/{genreName}")
    public ResponseEntity<List<Publisher>> getPublishersByBookGenre(@PathVariable String genreName) {
        log.info("Getting publishers by book genre: {}", genreName);
        
        try {
            List<Publisher> publishers = publisherService.getPublishersByBookGenre(genreName);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error getting publishers by book genre: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get publishers by book author
     */
    @GetMapping("/by-author/{authorName}")
    public ResponseEntity<List<Publisher>> getPublishersByBookAuthor(@PathVariable String authorName) {
        log.info("Getting publishers by book author: {}", authorName);
        
        try {
            List<Publisher> publishers = publisherService.getPublishersByBookAuthor(authorName);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error getting publishers by book author: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get books by publisher
     */
    @GetMapping("/{id}/books")
    public ResponseEntity<List<BookDetailDto>> getBooksByPublisher(@PathVariable Long id) {
        log.info("Getting books by publisher ID: {}", id);
        
        try {
            List<BookDetailDto> books = publisherService.getBooksByPublisher(id);
            return ResponseEntity.ok(books);
        } catch (RuntimeException e) {
            log.error("Error getting books by publisher: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error getting books by publisher: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get total publisher count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalPublisherCount() {
        log.info("Getting total publisher count");
        
        try {
            long count = publisherService.getTotalPublisherCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error getting total publisher count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 