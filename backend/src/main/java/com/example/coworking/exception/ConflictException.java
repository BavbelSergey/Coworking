package com.example.coworking.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BasicException {

  public ConflictException(ErrorCode errorCode) {
    super(errorCode, HttpStatus.CONFLICT);
  }
}
