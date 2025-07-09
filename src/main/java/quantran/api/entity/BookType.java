package quantran.api.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "book_types", indexes = {
    @Index(name = "idx_genre_name", columnList = "name"),
    @Index(name = "idx_genre_age_rating", columnList = "ageRating")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100, unique = true)
    @NotBlank(message = "Genre name is required")
    @Size(min = 1, max = 100, message = "Genre name must be between 1 and 100 characters")
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "age_rating", length = 20)
    private String ageRating; // G, PG, PG-13, R, etc.
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToMany(mappedBy = "genres")
    private Set<BookEntity> books = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public int getBookCount() {
        return books != null ? books.size() : 0;
    }
    
    public boolean isSuitableForChildren() {
        return "G".equals(ageRating) || "PG".equals(ageRating);
    }
} 