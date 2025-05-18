package com.noralearn.goethehub.auth.exception;

import com.noralearn.goethehub.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidResetTokenException extends CustomException {

  public InvalidResetTokenException(String message) {
    super(HttpStatus.BAD_REQUEST, "reset-password-invalid", message);
  }
}
