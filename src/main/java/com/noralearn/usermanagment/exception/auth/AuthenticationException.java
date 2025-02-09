package com.noralearn.usermanagment.exception.auth;

import com.noralearn.usermanagment.exception.CustomException;
import org.springframework.http.HttpStatus;


public class AuthenticationException extends CustomException {

  public AuthenticationException() {
    super(HttpStatus.UNAUTHORIZED, "authentication-failure", "Auth Failed");
  }

  public AuthenticationException(String message) {
    super(HttpStatus.UNAUTHORIZED, "authentication-failure", message);
  }

}
