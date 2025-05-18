package com.noralearn.goethehub.auth.service;

import com.noralearn.goethehub.auth.enums.AuthActivityStatus;
import com.noralearn.goethehub.auth.enums.TokenType;
import com.noralearn.goethehub.common.service.RedisTokenService;
import com.noralearn.goethehub.common.helper.JwtHelper;
import com.noralearn.goethehub.common.helper.RequestHeaderHelper;
import com.noralearn.goethehub.common.helper.UserAgentHelper;
import com.noralearn.goethehub.auth.model.LoginActivity;
import com.noralearn.goethehub.user.model.User;
import com.noralearn.goethehub.auth.repository.LoginActivityRepository;
import com.noralearn.goethehub.user.repository.UsersRepository;
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
  private final UserAgentHelper userAgentHelper;

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
    String deviceType = this.userAgentHelper.buildDeviceTypeByUserAgent(userAgent);

    final LoginActivity loginActivity = LoginActivity.builder()
        .status(AuthActivityStatus.LOGOUT)
        .deviceType(deviceType)
        .ipAddress(servletRequest.getRemoteAddr())
        .user(user)
        .build();

    this.loginActivityRepository.save(loginActivity);
  }
}
