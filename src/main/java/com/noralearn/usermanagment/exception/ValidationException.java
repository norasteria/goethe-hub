package com.noralearn.usermanagment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "validation-failure")
public class ValidationException extends RuntimeException {

  public ValidationException (String message){
    super(message);
  }
}
