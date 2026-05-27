package com.example.coworking.service;

import com.example.coworking.dto.AsyncTaskResponseDto;
import com.example.coworking.exception.ErrorCode;
import com.example.coworking.exception.NotFoundException;
import com.example.coworking.mapper.AsyncTaskMapper;
import com.example.coworking.model.AsyncTask;
import com.example.coworking.utils.AsyncTaskExecutor;
import com.example.coworking.utils.AsyncTaskStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncTaskService {

  private final AsyncTaskStorage asyncTaskStorage;
  private final AsyncTaskMapper asyncTaskMapper;
  private final AsyncTaskExecutor asyncTaskExecutor;

  public AsyncTaskResponseDto startTask() {
    AsyncTask task = asyncTaskStorage.create();

    asyncTaskExecutor.executeTask(task.getId());

    return asyncTaskMapper.toDto(task);
  }

  public AsyncTaskResponseDto getById(String taskId) {
    log.debug("Trying to get {} task info", taskId);

    AsyncTask task = asyncTaskStorage.get(taskId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.TASK_NOT_FOUND));
    return asyncTaskMapper.toDto(task);
  }

}