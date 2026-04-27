package com.example.coworking.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BasicException {

  public NotFoundException(ErrorCode errorCode) {
    super(errorCode, HttpStatus.NOT_FOUND);
  }
}
