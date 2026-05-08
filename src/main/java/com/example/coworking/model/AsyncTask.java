package com.example.coworking.model;

import com.example.coworking.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsyncTask {
  private String id;
  private TaskStatus status;

  public AsyncTask(String taskId) {
    this.status = TaskStatus.PENDING;
    this.id = taskId;
  }
}