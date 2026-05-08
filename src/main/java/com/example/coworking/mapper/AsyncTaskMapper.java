package com.example.coworking.mapper;

import com.example.coworking.dto.AsyncTaskResponseDto;
import com.example.coworking.model.AsyncTask;
import org.springframework.stereotype.Component;

@Component
public class AsyncTaskMapper {

  public AsyncTaskResponseDto toDto(AsyncTask asyncTask) {
    AsyncTaskResponseDto dto = new AsyncTaskResponseDto();

    dto.setTaskStatus(asyncTask.getStatus());
    dto.setTaskId(asyncTask.getId());

    return dto;
  }
}