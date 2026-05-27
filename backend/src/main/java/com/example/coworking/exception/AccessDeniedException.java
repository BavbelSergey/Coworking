package com.example.coworking.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException  extends BasicException {

  public AccessDeniedException(ErrorCode errorCode) {
    super(errorCode, HttpStatus.FORBIDDEN);
  }
}
