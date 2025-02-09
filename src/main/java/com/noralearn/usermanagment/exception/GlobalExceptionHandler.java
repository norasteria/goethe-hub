package com.noralearn.usermanagment.exception;

import com.noralearn.usermanagment.factory.ApiResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResponseFactory<?>> handleCustomException(CustomException exception){
    log.error(exception.getMessage(), exception);
    return ResponseEntity
        .status(exception.getStatus())
        .body(ApiResponseFactory.error(exception.getCode(), exception.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseFactory<?>> handleGenericException(Exception ex){
    log.error(String.valueOf(ex.getMessage()), ex);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponseFactory.error("general-error", ex.getMessage()));
  }
}
