package com.noralearn.goethehub.auth.exception;

import com.noralearn.goethehub.common.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ExpiredTokenException extends CustomException {

  public ExpiredTokenException(){
    super(HttpStatus.UNAUTHORIZED, "token-expired", "Expired access token");
  }
}
