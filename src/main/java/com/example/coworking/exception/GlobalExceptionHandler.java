package com.example.coworking.exception;

import jakarta.persistence.Basic;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(BasicException exception,
      HttpServletRequest request) {
    return buildResponse(exception, request, exception.getHttpStatus());
  }

  public ResponseEntity<ErrorResponse> buildResponse(
      BasicException appException,
      HttpServletRequest request,
      HttpStatus status
  ) {
    appException.log();
    return ResponseEntity.status(status).body(
        new ErrorResponse(
            appException.getErrorCode().getCode(),
            appException.getErrorCode().getMessage(),
            status.value(),
            request.getRequestURI()
        )
    );
  }
}

