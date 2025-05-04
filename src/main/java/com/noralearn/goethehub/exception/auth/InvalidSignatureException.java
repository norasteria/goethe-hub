package com.noralearn.goethehub.exception.auth;

import com.noralearn.goethehub.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidSignatureException extends CustomException {

  public InvalidSignatureException() {
    super(HttpStatus.UNAUTHORIZED, "invalid-signature", "Invalid token signature.");
  }
}
