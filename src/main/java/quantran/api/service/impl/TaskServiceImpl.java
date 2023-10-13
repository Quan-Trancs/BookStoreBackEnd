package quantran.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import quantran.api.asyncProcessingBackgroundWorker.impl.AsyncProcessingBackgroundWorkerImpl;
import quantran.api.asyncProcessingBackgroundWorker.task.Task;
import quantran.api.model.BookModel;
import quantran.api.service.BookService;
import quantran.api.service.TaskService;

@Service
@Log4j2
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final BookService bookService;
    @Override
    public void runTask(Task task) {
        log.info("Start runTask()");
        String request = task.getRequest();
        BookModel bookModel = task.getBookModel();
        String requestResult = requestCommand(request, bookModel);
        AsyncProcessingBackgroundWorkerImpl.addToResultQueue(requestResult);
        log.info("End runTask()");
    }

    public String requestCommand(String request, BookModel bookModel) {
        switch (request) {
            case "update":
                bookService.updateBook(bookModel);
                return "202";
            case "add":
                bookService.addBook(bookModel);
                return "202";
            default:
                System.out.println("404");
                return "404"; // You were missing a return statement here for the "404" case.
        }
    }

    private void waiting(int time) {
        try {
            Thread.sleep(time); // Simulate a 1-second command execution
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
