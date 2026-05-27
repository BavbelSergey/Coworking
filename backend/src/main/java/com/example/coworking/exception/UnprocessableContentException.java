package com.example.coworking.exception;

import org.springframework.http.HttpStatus;

public class UnprocessableContentException extends BasicException {

  public UnprocessableContentException(ErrorCode errorCode) {
    super(errorCode, HttpStatus.UNPROCESSABLE_ENTITY);
  }

}
