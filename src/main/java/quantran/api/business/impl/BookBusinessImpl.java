package quantran.api.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import quantran.api.repository.BookRepository;
import quantran.api.repository.BookTypeRepository;
import quantran.api.BookType.BookType;
import quantran.api.business.BookBusiness;
import quantran.api.entity.BookEntity;
import quantran.api.model.BookModel;
import quantran.api.page.Paginate;
import quantran.api.repository.BookRepository;
import quantran.api.repository.BookTypeRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log4j2
@RequiredArgsConstructor
public class BookBusinessImpl implements BookBusiness {
    private final BookRepository bookRepository;
    private final BookTypeRepository bookTypeRepository;
    @Override
    public List<BookType> getBookType() {
        log.info("Start getBookType()");
        List<BookType> bookTypes = bookTypeRepository.findAll();
        log.info("End getBook()");
        return bookTypes;
    }
    @Override
    public Paginate getBook(String searchId, String searchName, String searchAuthor, int page, int pageSize) {
        log.info("Start getBook()");
        int totalRecord = bookRepository.countByNameContainsAndAuthorContainsAndIdContains(searchName, searchAuthor, searchId);
        int total = (totalRecord + pageSize - 1) / pageSize;
        Pageable currentPage = PageRequest.of(page, pageSize);
        Page<BookEntity> bookEntitiesPage = (bookRepository.findByNameContainsAndAuthorContainsAndIdContains(searchName, searchAuthor, searchId, currentPage));
        List<BookEntity> bookEntities = bookEntitiesPage.getContent();
        //List<BookEntity> bookEntities = (bookRepository.findByIdContainsAndNameContainsAndAuthorContains(searchName, searchAuthor, searchId));
        Stream<BookModel> bookModelStream = bookEntities.stream().map(bookEntity -> new BookModel(bookEntity));
        List<BookModel> bookModels =  bookModelStream.collect(Collectors.toList());
        Paginate paginate = new Paginate(bookModels, total);
        log.info("End getBook()");
        return paginate;
    }
    @Override
    public String downloadBook() {
        log.info("Start downloadBook()");
        List<BookEntity> bookEntities = bookRepository.findAll();
        Stream<BookModel> bookModelStream = bookEntities.stream().map(bookEntity -> new BookModel(bookEntity));
        List<BookModel> bookModels =  bookModelStream.collect(Collectors.toList());
        log.info("End downloadBook()");
        return bookModels.stream().map(BookModel -> BookModel.getId() + "," + BookModel.getName() + "," + BookModel.getAuthor() + "," + BookModel.getBookType() + "," + BookModel.getPrice()).collect(Collectors.joining(System.lineSeparator()));
    }
    @Override
    public void uploadBook(List<BookModel> bookModels) {
        log.info("Start uploadBook()");
        Stream<BookEntity> bookEntityStream = bookModels.stream().map(bookModel -> new BookEntity(bookModel));
        List<BookEntity> bookEntities = bookEntityStream.collect(Collectors.toList());
        bookRepository.saveAll(bookEntities);
        log.info("End uploadBook()");
    }
    @Override
    public void addBook(BookModel bookModel) {
        log.info("Start addBook()");
        if (!bookRepository.existsById(bookModel.getId())) {
            bookRepository.save(new BookEntity(bookModel));
        }
        log.info("End addBook()");
    }
    @Override
    public void delBook(String delId) {
        log.info("Start delBook()");
        bookRepository.deleteById(delId);
        log.info("End delBook()");
    }
    @Override
    public void updateBook(BookModel bookModel) {
        log.info("Start updateBook()");
        if (bookRepository.existsById(bookModel.getId())) {
            bookRepository.save(new BookEntity(bookModel));
        }
        log.info("End updateBook()");
    }
}
