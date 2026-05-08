package com.example.coworking.utils;

import com.example.coworking.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AsyncTaskExecutor {

  public static final int TASK_EXECUTION_TIME_MILLIS = 10000;

  private final AsyncTaskStorage asyncTaskStorage;

  @Async("taskExecutor")
  public void executeTask(String taskId) {
    log.info("Task {} started", taskId);

    try {
      log.debug("Task {} executing", taskId);
      asyncTaskStorage.updateTask(taskId, TaskStatus.IN_PROGRESS);

      Thread.sleep(TASK_EXECUTION_TIME_MILLIS);

      asyncTaskStorage.updateTask(taskId, TaskStatus.DONE);
      log.debug("Task {} finished successfully", taskId);

    } catch (Exception ex) {

      log.error("Error! Task {} interrupted", taskId);
      asyncTaskStorage.updateTask(taskId, TaskStatus.FAILED);
      Thread.currentThread().interrupt();
    }
  }
}
