package com.example.coworking.exception;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

  private String errorCode;
  private String message;
  private int status;
  private String path;
  private LocalDateTime timestamp;

  public ErrorResponse(String errorCode, String message, int status, String path) {
    this.errorCode = errorCode;
    this.message = message;
    this.status = status;
    this.path = path;
    this.timestamp = LocalDateTime.now();
  }

}
