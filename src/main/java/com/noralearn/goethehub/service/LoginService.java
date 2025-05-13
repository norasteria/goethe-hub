package com.noralearn.goethehub.service;

import com.noralearn.goethehub.bean.AuthToken;
import com.noralearn.goethehub.dto.request.LoginRequestDTO;
import com.noralearn.goethehub.dto.response.LoginResponseDTO;
import com.noralearn.goethehub.enums.AuthActivityStatus;
import com.noralearn.goethehub.exception.auth.AuthenticationException;
import com.noralearn.goethehub.helper.JwtHelper;
import com.noralearn.goethehub.helper.RequestHeaderHelper;
import com.noralearn.goethehub.helper.UserAgentHelper;
import com.noralearn.goethehub.model.LoginActivity;
import com.noralearn.goethehub.model.User;
import com.noralearn.goethehub.repository.LoginActivityRepository;
import com.noralearn.goethehub.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtHelper jwtHelper;
  private final RedisTokenService redisTokenService;

  private final UsersRepository usersRepository;
  private final LoginActivityRepository loginActivityRepository;
  private final UserAgentHelper userAgentHelper;

  private String ipAddress;
  private String deviceType;

  public LoginResponseDTO login(LoginRequestDTO requestDTO, HttpServletRequest servletRequest) {
    String rawUserAgent = RequestHeaderHelper.extractUserAgent(servletRequest);
    this.deviceType = this.userAgentHelper.buildDeviceTypeByUserAgent(rawUserAgent);
    this.ipAddress = this.getIpAddress(servletRequest);

    User selectedUser = this.validateLogin(requestDTO);

    final AuthToken accessToken = this.jwtHelper.generateAccessToken(selectedUser);
    final AuthToken refreshToken = this.jwtHelper.generateRefreshToken(selectedUser);

    this.redisTokenService.storeRefreshToken(refreshToken.getToken());

    return LoginResponseDTO.builder()
        .accessToken(accessToken.getToken())
        .accessTokenExpiredAt(accessToken.getExpiredAt())
        .refreshToken(refreshToken.getToken())
        .refreshTokenExpiredAt(refreshToken.getExpiredAt())
        .user(LoginResponseDTO.UserDetail.builder()
            .id(selectedUser.getId())
            .email(selectedUser.getEmail())
            .build())
        .build();
  }

  private User validateLogin(LoginRequestDTO loginRequestDTO) {
    final User selectedUser = usersRepository
        .findByEmailAndIsActive(loginRequestDTO.getEmail(), true)
        .orElseThrow(() -> {
          this.saveLoginActivity(AuthActivityStatus.LOGIN_FAILED, null);
          return new AuthenticationException("No email found.");
        });

    if (!selectedUser.isActive()) {
      // TODO: need to handle proper reactivation
      throw new AuthenticationException("Can't login non-active account.");
    }

    final int countTodayFailedTodayLogin = this.loginActivityRepository.countFailedLoginAttemptsToday(
        selectedUser.getId(),
        AuthActivityStatus.LOGIN_FAILED.name()
    );

    if (countTodayFailedTodayLogin > 2) {
      this.saveLoginActivity(AuthActivityStatus.LOGIN_SUSPENDED, selectedUser);
      selectedUser.setActive(false);
      this.usersRepository.save(selectedUser);
      throw new AuthenticationException("This account has been suspended.");
    }

    final boolean isPasswordMatch = this.passwordEncoder.matches(
        loginRequestDTO.getPassword(),
        selectedUser.getPassword()
    );

    if (!isPasswordMatch) {
      this.saveLoginActivity(AuthActivityStatus.LOGIN_FAILED, selectedUser);
      throw new AuthenticationException();
    }

    this.saveLoginActivity(AuthActivityStatus.LOGIN_SUCCESS, selectedUser);

    return selectedUser;
  }

  private void saveLoginActivity(AuthActivityStatus authActivityStatus, User selectedUser){
    final LoginActivity loginActivity = LoginActivity.builder()
        .status(authActivityStatus)
        .deviceType(this.deviceType)
        .ipAddress(this.ipAddress)
        .user(selectedUser)
        .build();

    this.loginActivityRepository.save(loginActivity);
  }

  private String getIpAddress(HttpServletRequest servletRequest){
    return servletRequest.getRemoteAddr();
  }
}
