package com.noralearn.usermanagment.exception.auth;

import com.noralearn.usermanagment.exception.CustomException;
import org.springframework.http.HttpStatus;

public class InvalidSignatureException extends CustomException {

  public InvalidSignatureException() {
    super(HttpStatus.UNAUTHORIZED, "invalid-signature", "Invalid token signature.");
  }
}
