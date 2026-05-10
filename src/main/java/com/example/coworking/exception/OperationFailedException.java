package com.example.coworking.exception;

import org.springframework.http.HttpStatus;

public class OperationFailedException extends BasicException {

  public OperationFailedException(ErrorCode errorCode) {
    super(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
