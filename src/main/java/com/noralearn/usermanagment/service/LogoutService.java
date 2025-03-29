package com.noralearn.usermanagment.service;

import com.noralearn.usermanagment.helper.JwtHelper;
import com.noralearn.usermanagment.helper.RequestHeaderHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

  private final RedisTokenService redisTokenService;
  private final JwtHelper jwtHelper;

  public void logout(HttpServletRequest servletRequest) {
    final String accessToken = RequestHeaderHelper.extractAccessToken(servletRequest);

    if (accessToken != null) {
      redisTokenService.revokeAccessToken(accessToken);
      redisTokenService.deleteRefreshToken(accessToken);
    }
  }
}
