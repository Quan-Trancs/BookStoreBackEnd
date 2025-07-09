package quantran.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import quantran.api.BookType.BookType;
import quantran.api.business.BookBusiness;
import quantran.api.model.BookModel;
import quantran.api.page.Paginate;
import quantran.api.service.BookService;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Log4j2
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookBusiness bookBusiness;
    @Override
    public ResponseEntity<byte[]> downloadBook() throws IOException {
        log.info("Start downloadBook()");

        String downloadBookContent = bookBusiness.downloadBook();

        ByteArrayOutputStream bookBaos = new ByteArrayOutputStream();
        try(ZipOutputStream bookZos = new ZipOutputStream(bookBaos)){

            ZipEntry bookZipEntry = new ZipEntry("bookList.csv");
            bookZos.putNextEntry(bookZipEntry);

            byte[] downloadBookBytes = downloadBookContent.getBytes();
            bookZos.write(downloadBookBytes);

            bookZos.closeEntry();
        }

        byte[] bookZipBytes = bookBaos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "bookList.zip");
        log.info("End downloadBook()");
        return ResponseEntity.ok()
                .headers(headers)
                .body(bookZipBytes);
    }
    @Override
    public void uploadBook(MultipartFile bookFile) throws IOException {
        log.info("Start uploadBook()");
        List<BookModel> bookList = new ArrayList<>();
        
        try (BufferedReader bookReader = new BufferedReader(new InputStreamReader(bookFile.getInputStream()))) {
            String line;
            int lineNumber = 0;
            
            while ((line = bookReader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] data = line.split(",");
                    
                    // Validate CSV format - should have exactly 5 columns
                    if (data.length != 5) {
                        log.warn("Skipping line {}: Invalid CSV format. Expected 5 columns, found {}", lineNumber, data.length);
                        continue;
                    }
                    
                    // Validate that required fields are not empty
                    if (data[0].trim().isEmpty() || data[1].trim().isEmpty() || 
                        data[2].trim().isEmpty() || data[3].trim().isEmpty() || data[4].trim().isEmpty()) {
                        log.warn("Skipping line {}: Empty required fields", lineNumber);
                        continue;
                    }
                    
                    BookModel bookModel = new BookModel(
                        data[0].trim(), // id
                        data[1].trim(), // name
                        data[2].trim(), // author
                        data[4].trim(), // price
                        data[3].trim()  // bookType
                    );
                    bookList.add(bookModel);
                    
                } catch (Exception e) {
                    log.error("Error processing line {}: {}", lineNumber, e.getMessage());
                }
            }
        }
        
        if (!bookList.isEmpty()) {
            bookBusiness.uploadBook(bookList);
        } else {
            log.warn("No valid books found in uploaded file");
        }
        
        log.info("End uploadBook()");
    }
    @Override
    public Paginate getBook(String searchName, String searchAuthor, String searchId, int page, int pageSize) {
        log.info("Start getBook()");

        Paginate paginate = bookBusiness.getBook(searchId, searchName, searchAuthor, page, pageSize);

        log.info("End getBook()");
        return paginate;
    }
    @Override
    public List<BookType> getBookType() {
        log.info("Start getBookType()");

        List<BookType> bookTypes = bookBusiness.getBookType();

        log.info("End getBookType()");
        return bookTypes;
    }
    @Override
    public void addBook(BookModel bookModel) {
        log.info("Start addBook()");
        bookBusiness.addBook(bookModel);
        log.info("End addBook()");
    }
    @Override
    public void delBook(String delId) {
        log.info("Start delBook()");
        bookBusiness.delBook(delId);
        log.info("End delBook()");
    }
    @Override
    public void updateBook(BookModel bookModel) {
        log.info("Start updateBook()");
        bookBusiness.updateBook(bookModel);
        log.info("End updateBook()");
    }
}
