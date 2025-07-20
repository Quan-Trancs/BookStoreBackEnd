package quantran.api.entity;

/**
 * @deprecated This class is deprecated. Use {@link AuthorEntity} instead for consistency with naming conventions.
 * The AuthorEntity provides the same functionality with standardized naming.
 */
@Deprecated
public class Author extends AuthorEntity {
    
    public Author() {
        super();
    }
    
    public Author(Long id, String name, String biography, String country, String website, String email, Boolean isActive) {
        super(id, name, biography, null, null, country, website, email, isActive, null, null, null, null);
    }
    
    public Author(String name, String biography, String country) {
        super(null, name, biography, null, null, country, null, null, true, null, null, null, null);
    }
} 