package com.noralearn.usermanagment.exception;

import org.springframework.http.HttpStatus;

public class  DataNotFoundException extends CustomException {

  public DataNotFoundException(String message) {
    super(HttpStatus.NOT_FOUND, "data-not-found", message);
  }
}
