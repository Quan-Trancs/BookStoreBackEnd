package quantran.api.service;

import quantran.api.asyncProcessingBackgroundWorker.task.Task;
import quantran.api.model.BookModel;

public interface TaskService {
    void runTask(Task task);
}
