package com.noralearn.usermanagment.helper;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class RequestHeaderHelper {

  public static String extractAccessToken(HttpServletRequest servletRequest) {
    final String HEADER_AUTHORIZATION = "Authorization";
    final String TOKEN_PREFIX = "Bearer ";

    final String authHeader = servletRequest.getHeader(HEADER_AUTHORIZATION);

    if (StringUtils.hasText(authHeader) && authHeader.startsWith(TOKEN_PREFIX)) {
      return authHeader.substring(TOKEN_PREFIX.length());
    }

    return null;
  }
}
