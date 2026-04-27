package com.example.coworking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BasicException extends RuntimeException {

  private final ErrorCode errorCode;
  private final HttpStatus httpStatus;

  protected BasicException(ErrorCode errorCode, HttpStatus httpStatus) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.httpStatus = httpStatus;
  }

  public void log() {
    System.err.println("[ERROR] " + errorCode + ": " + getMessage());
  }
}
