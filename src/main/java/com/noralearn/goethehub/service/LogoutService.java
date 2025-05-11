package com.noralearn.goethehub.service;

import com.noralearn.goethehub.enums.AuthActivityStatus;
import com.noralearn.goethehub.enums.TokenType;
import com.noralearn.goethehub.helper.JwtHelper;
import com.noralearn.goethehub.helper.RequestHeaderHelper;
import com.noralearn.goethehub.helper.UserAgentHelper;
import com.noralearn.goethehub.model.LoginActivity;
import com.noralearn.goethehub.model.User;
import com.noralearn.goethehub.repository.LoginActivityRepository;
import com.noralearn.goethehub.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService {

  private final UsersRepository usersRepository;
  private final LoginActivityRepository loginActivityRepository;

  private final RedisTokenService redisTokenService;
  private final JwtHelper jwtHelper;

  public void logout(HttpServletRequest servletRequest) {
    final String accessToken = RequestHeaderHelper.extractAccessToken(servletRequest);
    Claims tokenClaims = this.jwtHelper.extractClaim(accessToken, TokenType.USER_TOKEN);
    UUID userId = UUID.fromString(tokenClaims.getSubject());

    if (accessToken != null) {
      redisTokenService.revokeAccessToken(accessToken);
      redisTokenService.deleteRefreshToken(userId);
    }

    // Store logout activity
    this.usersRepository.findByIdAndIsActive(userId, true)
        .ifPresent(user -> this.saveLogoutActivity(servletRequest, user));
  }

  private void saveLogoutActivity(HttpServletRequest servletRequest, User user) {
    String userAgent = RequestHeaderHelper.extractUserAgent(servletRequest);
    String deviceType = UserAgentHelper.buildDeviceTypeByUserAgent(userAgent);

    final LoginActivity loginActivity = LoginActivity.builder()
        .status(AuthActivityStatus.LOGOUT)
        .deviceType(deviceType)
        .ipAddress(servletRequest.getRemoteAddr())
        .user(user)
        .build();

    this.loginActivityRepository.save(loginActivity);
  }
}
