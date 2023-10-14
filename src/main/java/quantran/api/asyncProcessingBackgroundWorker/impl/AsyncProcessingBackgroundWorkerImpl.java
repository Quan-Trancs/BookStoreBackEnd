package quantran.api.asyncProcessingBackgroundWorker.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import quantran.api.asyncProcessingBackgroundWorker.AsyncProcessingBackgroundWorker;
import quantran.api.asyncProcessingBackgroundWorker.task.Task;
import quantran.api.service.TaskService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
@Service
public class AsyncProcessingBackgroundWorkerImpl implements AsyncProcessingBackgroundWorker {
    private final TaskService taskService;
    private final ExecutorService executorService;
    private static final int WORKERSNUM = 2;
    private Queue<Task> requestQueue = new ConcurrentLinkedQueue<>();
    private static Queue<String> resultQueue = new ConcurrentLinkedQueue<>();
    private final Object LOCK = new Object();
    private boolean workersStarted = false;

    public void addToRequestQueue(Task task) {
        requestQueue.add(task);
        if (!workersStarted) {
            startWorkers();
        }
    }
    public static void addToResultQueue(String status) {
        resultQueue.add(status);
    }
    public Task getFromRequestQueue() {
        return requestQueue.poll();
    }
    public String getFromResultQueue() {
        return resultQueue.poll();
    }

    /*public AsyncProcessingBackgroundWorkerImpl() {
        //requestQueue = new ConcurrentLinkedQueue<>();
        executor = Executors.newFixedThreadPool(WORKERSNUM);
    }*/
    public AsyncProcessingBackgroundWorkerImpl(TaskService taskService) {
        this.executorService = Executors.newFixedThreadPool(WORKERSNUM);
        this.taskService = taskService;
    }
    public void startWorkers() {
        for (int i = 0; i < WORKERSNUM; i++) {
            executorService.execute(() -> {
                while (true) {
                    Task task;
                    synchronized (LOCK) {
                        task = getFromRequestQueue();
                        while (task == null) {
                            // No task available, wait for notification
                            waiting(1000);
                        }
                    }

                    if (task != null) {
                        taskService.runTask(task); // Execute the task
                    }
                }
            });
        }
        workersStarted = true;
    }

    public void shutdown() {
        executorService.shutdown();
    }

    // Simulate command execution with a delay
    private void waiting(int time) {
        try {
            Thread.sleep(time); // Simulate a 1-second command execution
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}