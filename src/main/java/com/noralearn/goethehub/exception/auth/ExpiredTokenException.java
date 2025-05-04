package com.noralearn.goethehub.exception.auth;

import com.noralearn.goethehub.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ExpiredTokenException extends CustomException {

  public ExpiredTokenException(){
    super(HttpStatus.UNAUTHORIZED, "token-expired", "Expired access token");
  }
}
