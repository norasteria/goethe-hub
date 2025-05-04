package com.noralearn.usermanagment.helper;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

public class RequestHeaderHelper {

  public static String extractAccessToken(HttpServletRequest servletRequest) {
    final String TOKEN_PREFIX = "Bearer ";

    final String authHeader = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if (StringUtils.hasText(authHeader) && authHeader.startsWith(TOKEN_PREFIX)) {
      return authHeader.substring(TOKEN_PREFIX.length());
    }

    return null;
  }

  public static String extractUserAgent(HttpServletRequest servletRequest){
    return servletRequest.getHeader(HttpHeaders.USER_AGENT);
  }
}
