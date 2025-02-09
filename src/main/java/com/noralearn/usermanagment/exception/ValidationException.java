package com.noralearn.usermanagment.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends CustomException {

  public ValidationException (String message){
    super(HttpStatus.BAD_REQUEST, "validation-failure", message);
  }
}
