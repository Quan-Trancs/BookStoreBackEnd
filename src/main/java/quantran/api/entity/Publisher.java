package quantran.api.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "publishers", indexes = {
    @Index(name = "idx_publisher_name", columnList = "name"),
    @Index(name = "idx_publisher_country", columnList = "country"),
    @Index(name = "idx_publisher_city", columnList = "city"), // For city filter
    @Index(name = "idx_publisher_founded_year", columnList = "founded_year") // For founded year queries
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publisher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 255)
    @NotBlank(message = "Publisher name is required")
    @Size(min = 1, max = 255, message = "Publisher name must be between 1 and 255 characters")
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "country", length = 100)
    private String country;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "website", length = 500)
    private String website;
    
    @Column(name = "email", length = 255)
    private String email;
    
    @Column(name = "phone", length = 50)
    private String phone;
    
    @Column(name = "founded_year")
    private Integer foundedYear;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL)
    private List<BookEntity> books = new ArrayList<>();
    
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
    
    public boolean isEstablished() {
        return foundedYear != null && foundedYear > 0;
    }
} 