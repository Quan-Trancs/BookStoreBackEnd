package quantran.api.entity;

/**
 * @deprecated This class is deprecated. Use {@link PublisherEntity} instead for consistency with naming conventions.
 * The PublisherEntity provides the same functionality with standardized naming.
 */
@Deprecated
public class Publisher extends PublisherEntity {
    
    public Publisher() {
        super();
    }
    
    public Publisher(Long id, String name, String description, String country, String city, String website, String email, String phone, Integer foundedYear, Boolean isActive) {
        super(id, name, description, country, city, website, email, phone, foundedYear, isActive, null, null, null, null);
    }
    
    public Publisher(String name, String description, String country) {
        super(null, name, description, country, null, null, null, null, null, true, null, null, null, null);
    }
} 