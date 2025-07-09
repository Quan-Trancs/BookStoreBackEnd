package quantran.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDto {
    
    @NotBlank(message = "Book ID is required")
    @Size(min = 1, max = 50, message = "Book ID must be between 1 and 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Book ID can only contain letters, numbers, hyphens, and underscores")
    private String id;
    
    @NotBlank(message = "Book name is required")
    @Size(min = 1, max = 255, message = "Book name must be between 1 and 255 characters")
    private String name;
    
    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 255, message = "Author name must be between 1 and 255 characters")
    private String author;
    
    @NotBlank(message = "Price is required")
    @Pattern(regexp = "(?i)^[1-9]\\d*(vnd)$", message = "Price must be a positive number followed by 'VND'")
    private String price;
    
    @NotBlank(message = "Book type is required")
    @Size(min = 1, max = 100, message = "Book type must be between 1 and 100 characters")
    private String bookType;
} 