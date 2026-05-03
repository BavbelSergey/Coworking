package com.example.coworking.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException ex,
      HttpServletRequest request
  ) {
    log.error("Error {}! Invalid argument in request at {}: {}",
        HttpStatus.BAD_REQUEST.value(),
        request.getRequestURI(),
        ex.getMessage()
    );
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    ErrorResponse response = new ErrorResponse(
        ErrorCode.BAD_REQUEST.name(),
        errors.toString(),
        HttpStatus.BAD_REQUEST.value(),
        request.getRequestURI()
    );

    return ResponseEntity.badRequest().body(response);
  }
}

