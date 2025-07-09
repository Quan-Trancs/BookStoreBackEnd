package quantran.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quantran.api.BookType.BookType;

@Repository
public interface BookTypeRepository extends JpaRepository<BookType, String> {
    //void deleteById(String id);
}
