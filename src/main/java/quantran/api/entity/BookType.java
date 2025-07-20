package quantran.api.entity;

/**
 * @deprecated This class is deprecated. Use {@link BookTypeEntity} instead for consistency with naming conventions.
 * The BookTypeEntity provides the same functionality with standardized naming.
 */
@Deprecated
public class BookType extends BookTypeEntity {
    
    public BookType() {
        super();
    }
    
    public BookType(String id, String name, String description, String parentId, String ageRating, Boolean isActive, Integer sortOrder) {
        super(id, name, description, parentId, ageRating, isActive, sortOrder, null, null, null);
    }
    
    public BookType(String id, String name, String description) {
        super(id, name, description, null, null, true, null, null, null, null);
    }
} 