package com.example.coworking.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

  private String errorCode;
  private String message;
  private int status;
  private String path;
  private LocalDateTime timestamp;
  private String field;
  private String value;
  private Map<String, String> fieldErrors;

  public ErrorResponse(String errorCode, String message, int status, String path) {
    this.errorCode = errorCode;
    this.message = message;
    this.status = status;
    this.path = path;
    this.timestamp = LocalDateTime.now();
  }

}
