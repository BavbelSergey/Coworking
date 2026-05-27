package com.example.coworking.utils;

import com.example.coworking.exception.ErrorCode;
import com.example.coworking.exception.NotFoundException;
import com.example.coworking.model.AsyncTask;
import com.example.coworking.model.TaskStatus;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class AsyncTaskStorage {

  private final Map<String, AsyncTask> tasks = new ConcurrentHashMap<>();

  public AsyncTask create() {

    String taskId = UUID.randomUUID().toString();

    AsyncTask asyncTask = new AsyncTask(taskId);

    tasks.put(taskId, asyncTask);

    return tasks.get(taskId);
  }

  public Optional<AsyncTask> get(String taskId) {
    return Optional.ofNullable(tasks.get(taskId));
  }

  public void updateTask(String taskId, TaskStatus status) {

    AsyncTask asyncTask = tasks.get(taskId);

    if (asyncTask == null) {
      throw new NotFoundException(
          ErrorCode.TASK_NOT_FOUND);
    }

    asyncTask.setStatus(status);
    tasks.put(taskId, asyncTask);
  }
}
