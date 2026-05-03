package com.example.coworking.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    log.error("Validation error at {}: {}",
        request.getRequestURI(),
        ex.getMessage()
    );

    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
      String fieldName = fieldError.getField();
      String errorMessage = fieldError.getDefaultMessage();
      assert errorMessage != null;
      fieldErrors.merge(fieldName, errorMessage, (old, newMsg) -> old + "; " + newMsg);
    });

    String message = "Validation error, wrong fields " + fieldErrors.keySet();

    ErrorResponse response = new ErrorResponse(
        ErrorCode.BAD_REQUEST.name(),
        message,
        HttpStatus.BAD_REQUEST.value(),
        request.getRequestURI()
    );
    response.setFieldErrors(fieldErrors);

    return ResponseEntity.badRequest().body(response);
  }
}
