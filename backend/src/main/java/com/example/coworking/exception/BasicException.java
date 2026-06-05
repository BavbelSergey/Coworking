package com.example.coworking.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
public class BasicException extends RuntimeException {

  private final ErrorCode errorCode;
  private final HttpStatus httpStatus;

  protected BasicException(ErrorCode errorCode, HttpStatus httpStatus) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.httpStatus = httpStatus;
  }
}
