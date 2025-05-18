package com.noralearn.goethehub.auth.exception;

import com.noralearn.goethehub.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidSignatureException extends CustomException {

  public InvalidSignatureException() {
    super(HttpStatus.UNAUTHORIZED, "invalid-signature", "Invalid token signature.");
  }
}
