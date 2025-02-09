package com.noralearn.usermanagment.exception.auth;

import com.noralearn.usermanagment.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ExpiredTokenException extends CustomException {

  public ExpiredTokenException(){
    super(HttpStatus.UNAUTHORIZED, "token-expired", "Expired access token");
  }

}
