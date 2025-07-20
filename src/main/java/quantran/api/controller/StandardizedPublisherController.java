package quantran.api.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import quantran.api.dto.PublisherRequestDto;
import quantran.api.dto.PublisherResponseDto;
import quantran.api.dto.BookResponseDto;
import quantran.api.page.Paginate;
import quantran.api.service.StandardizedPublisherService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Optional;

/**
 * Standardized PublisherController with RESTful endpoints and consistent naming conventions.
 * This controller provides standardized CRUD operations and query methods for publishers.
 */
@RestController
@RequestMapping("/api/v1/publishers")
@Validated
@Log4j2
public class StandardizedPublisherController {

    @Autowired
    private StandardizedPublisherService publisherService;

    // Standardized CRUD Operations

    /**
     * Create a new publisher.
     * 
     * @param request The publisher creation request
     * @return The created publisher response
     */
    @PostMapping
    public ResponseEntity<PublisherResponseDto> createPublisher(@Valid @RequestBody PublisherRequestDto request) {
        log.info("Creating publisher with name: {}", request.getName());
        
        try {
            PublisherResponseDto createdPublisher = publisherService.createPublisher(request);
            log.info("Successfully created publisher with ID: {}", createdPublisher.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPublisher);
        } catch (Exception e) {
            log.error("Error creating publisher with name: {}", request.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find a publisher by its ID.
     * 
     * @param id The publisher ID
     * @return The publisher response if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<PublisherResponseDto> findPublisherById(
            @PathVariable @Min(value = 1, message = "Publisher ID must be positive") Long id) {
        log.info("Finding publisher by ID: {}", id);
        
        try {
            Optional<PublisherResponseDto> publisher = publisherService.findPublisherById(id);
            if (publisher.isPresent()) {
                log.info("Found publisher with ID: {}", id);
                return ResponseEntity.ok(publisher.get());
            } else {
                log.warn("Publisher not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error finding publisher with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find publishers with search criteria.
     * 
     * @param name Publisher name filter
     * @param country Country filter
     * @param city City filter
     * @param isActive Active status filter
     * @param page Page number (0-based)
     * @param size Page size
     * @return Paginated list of publisher responses
     */
    @GetMapping
    public ResponseEntity<Paginate<PublisherResponseDto>> findPublishers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be non-negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be positive") int size) {
        
        log.info("Finding publishers with filters - name: {}, country: {}, city: {}, isActive: {}, page: {}, size: {}", 
                name, country, city, isActive, page, size);
        
        try {
            Paginate<PublisherResponseDto> publishers = publisherService.findPublishers(name, country, city, isActive, page, size);
            log.info("Found {} publishers", publishers.getTotal());
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error finding publishers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing publisher.
     * 
     * @param id The publisher ID
     * @param request The publisher update request
     * @return The updated publisher response
     */
    @PutMapping("/{id}")
    public ResponseEntity<PublisherResponseDto> updatePublisher(
            @PathVariable @Min(value = 1, message = "Publisher ID must be positive") Long id,
            @Valid @RequestBody PublisherRequestDto request) {
        
        log.info("Updating publisher with ID: {}", id);
        
        try {
            PublisherResponseDto updatedPublisher = publisherService.updatePublisher(id, request);
            log.info("Successfully updated publisher with ID: {}", id);
            return ResponseEntity.ok(updatedPublisher);
        } catch (Exception e) {
            log.error("Error updating publisher with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a publisher by its ID.
     * 
     * @param id The publisher ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(
            @PathVariable @Min(value = 1, message = "Publisher ID must be positive") Long id) {
        
        log.info("Deleting publisher with ID: {}", id);
        
        try {
            publisherService.deletePublisher(id);
            log.info("Successfully deleted publisher with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting publisher with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Specialized Query Endpoints

    /**
     * Find a publisher by name.
     * 
     * @param name The publisher name
     * @return The publisher response if found
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<PublisherResponseDto> findPublisherByName(
            @PathVariable @Pattern(regexp = "^[A-Za-z\\s\\-']+$", message = "Invalid publisher name format") String name) {
        
        log.info("Finding publisher by name: {}", name);
        
        try {
            Optional<PublisherResponseDto> publisher = publisherService.findPublisherByName(name);
            if (publisher.isPresent()) {
                log.info("Found publisher with name: {}", name);
                return ResponseEntity.ok(publisher.get());
            } else {
                log.warn("Publisher not found with name: {}", name);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error finding publisher with name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find publishers by country.
     * 
     * @param country The country
     * @return List of publisher responses
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<List<PublisherResponseDto>> findPublishersByCountry(
            @PathVariable @Pattern(regexp = "^[A-Za-z\\s\\-']+$", message = "Invalid country format") String country) {
        
        log.info("Finding publishers by country: {}", country);
        
        try {
            List<PublisherResponseDto> publishers = publisherService.findPublishersByCountry(country);
            log.info("Found {} publishers for country: {}", publishers.size(), country);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error finding publishers by country: {}", country, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find publishers by city.
     * 
     * @param city The city
     * @return List of publisher responses
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<PublisherResponseDto>> findPublishersByCity(
            @PathVariable @Pattern(regexp = "^[A-Za-z\\s\\-']+$", message = "Invalid city format") String city) {
        
        log.info("Finding publishers by city: {}", city);
        
        try {
            List<PublisherResponseDto> publishers = publisherService.findPublishersByCity(city);
            log.info("Found {} publishers for city: {}", publishers.size(), city);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error finding publishers by city: {}", city, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find publishers by founded year.
     * 
     * @param foundedYear The founded year
     * @return List of publisher responses
     */
    @GetMapping("/founded-year/{foundedYear}")
    public ResponseEntity<List<PublisherResponseDto>> findPublishersByFoundedYear(
            @PathVariable @Min(value = 1800, message = "Founded year must be at least 1800") Integer foundedYear) {
        
        log.info("Finding publishers by founded year: {}", foundedYear);
        
        try {
            List<PublisherResponseDto> publishers = publisherService.findPublishersByFoundedYear(foundedYear);
            log.info("Found {} publishers for founded year: {}", publishers.size(), foundedYear);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error finding publishers by founded year: {}", foundedYear, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find publishers founded before a specific year.
     * 
     * @param year The year
     * @return List of publisher responses
     */
    @GetMapping("/founded-before/{year}")
    public ResponseEntity<List<PublisherResponseDto>> findPublishersFoundedBefore(
            @PathVariable @Min(value = 1800, message = "Year must be at least 1800") Integer year) {
        
        log.info("Finding publishers founded before year: {}", year);
        
        try {
            List<PublisherResponseDto> publishers = publisherService.findPublishersFoundedBefore(year);
            log.info("Found {} publishers founded before year: {}", publishers.size(), year);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error finding publishers founded before year: {}", year, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find publishers founded after a specific year.
     * 
     * @param year The year
     * @return List of publisher responses
     */
    @GetMapping("/founded-after/{year}")
    public ResponseEntity<List<PublisherResponseDto>> findPublishersFoundedAfter(
            @PathVariable @Min(value = 1800, message = "Year must be at least 1800") Integer year) {
        
        log.info("Finding publishers founded after year: {}", year);
        
        try {
            List<PublisherResponseDto> publishers = publisherService.findPublishersFoundedAfter(year);
            log.info("Found {} publishers founded after year: {}", publishers.size(), year);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error finding publishers founded after year: {}", year, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find publishers by book genre.
     * 
     * @param genreName The genre name
     * @return List of publisher responses
     */
    @GetMapping("/genre/{genreName}")
    public ResponseEntity<List<PublisherResponseDto>> findPublishersByBookGenre(
            @PathVariable @Pattern(regexp = "^[A-Za-z\\s\\-']+$", message = "Invalid genre name format") String genreName) {
        
        log.info("Finding publishers by book genre: {}", genreName);
        
        try {
            List<PublisherResponseDto> publishers = publisherService.findPublishersByBookGenre(genreName);
            log.info("Found {} publishers for book genre: {}", publishers.size(), genreName);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error finding publishers by book genre: {}", genreName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find publishers by book author.
     * 
     * @param authorName The author name
     * @return List of publisher responses
     */
    @GetMapping("/author/{authorName}")
    public ResponseEntity<List<PublisherResponseDto>> findPublishersByBookAuthor(
            @PathVariable @Pattern(regexp = "^[A-Za-z\\s\\-']+$", message = "Invalid author name format") String authorName) {
        
        log.info("Finding publishers by book author: {}", authorName);
        
        try {
            List<PublisherResponseDto> publishers = publisherService.findPublishersByBookAuthor(authorName);
            log.info("Found {} publishers for book author: {}", publishers.size(), authorName);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            log.error("Error finding publishers by book author: {}", authorName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Find books by publisher ID.
     * 
     * @param publisherId The publisher ID
     * @return List of book responses
     */
    @GetMapping("/{publisherId}/books")
    public ResponseEntity<List<BookResponseDto>> findBooksByPublisher(
            @PathVariable @Min(value = 1, message = "Publisher ID must be positive") Long publisherId) {
        
        log.info("Finding books by publisher ID: {}", publisherId);
        
        try {
            List<BookResponseDto> books = publisherService.findBooksByPublisher(publisherId);
            log.info("Found {} books for publisher ID: {}", books.size(), publisherId);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            log.error("Error finding books by publisher ID: {}", publisherId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 