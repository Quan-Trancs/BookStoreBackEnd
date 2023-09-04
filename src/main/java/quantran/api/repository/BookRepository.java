package quantran.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quantran.api.entity.BookEntity;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, String> {
    //void deleteById(String id);
    List<BookEntity> findByNameContainsAndAuthorContainsAndIdContains(String searchId, String searchName, String searchAuthor);
    Page<BookEntity> findByNameContainsAndAuthorContainsAndIdContains(String searchName, String searchAuthor, String searchId, Pageable page);
    int countByNameContainsAndAuthorContainsAndIdContains(String searchName, String searchAuthor, String searchId);
}
