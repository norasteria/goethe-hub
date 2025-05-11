package com.noralearn.goethehub.exception.auth;

import com.noralearn.goethehub.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidResetTokenException extends CustomException {

  public InvalidResetTokenException(String message) {
    super(HttpStatus.BAD_REQUEST, "reset-password-invalid", message);
  }
}
