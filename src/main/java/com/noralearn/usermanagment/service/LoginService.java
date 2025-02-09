package com.noralearn.usermanagment.service;

import com.noralearn.usermanagment.bean.AuthToken;
import com.noralearn.usermanagment.dto.request.LoginRequestDTO;
import com.noralearn.usermanagment.dto.response.LoginResponseDTO;
import com.noralearn.usermanagment.enums.LoginStatus;
import com.noralearn.usermanagment.exception.auth.AuthenticationException;
import com.noralearn.usermanagment.helper.JwtHelper;
import com.noralearn.usermanagment.model.LoginActivity;
import com.noralearn.usermanagment.model.User;
import com.noralearn.usermanagment.repository.LoginActivityRepository;
import com.noralearn.usermanagment.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtHelper jwtHelper;

  private final UsersRepository usersRepository;
  private final LoginActivityRepository loginActivityRepository;

  private String ipAddress;
  private String deviceType;

  public LoginResponseDTO login(LoginRequestDTO requestDTO, HttpServletRequest servletRequest){
    this.ipAddress = this.getIpAddress(servletRequest);
    this.deviceType = requestDTO.getDeviceType();

    User selectedUser = this.validateLogin(requestDTO, servletRequest);

    final AuthToken accessToken = this.jwtHelper.generateAccessToken(selectedUser);
    final AuthToken refreshToken = this.jwtHelper.generateRefreshToken(selectedUser);

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

  private User validateLogin(LoginRequestDTO loginRequestDTO,  HttpServletRequest servletRequest){
    User selectedUser = usersRepository
        .findByEmail(loginRequestDTO.getEmail())
        .orElseThrow(() -> {
          this.saveLoginActivity(LoginStatus.FAILED, null);
          return new AuthenticationException("No email found");
        });

    final boolean isPasswordMatch = this.passwordEncoder.matches(loginRequestDTO.getPassword(), selectedUser.getPassword());

    if (!isPasswordMatch){
      this.saveLoginActivity(LoginStatus.FAILED, selectedUser);
      throw new AuthenticationException();
    }

    this.saveLoginActivity(LoginStatus.SUCCESS, selectedUser);

    return selectedUser;
  }

  private void saveLoginActivity(LoginStatus loginStatus, User selectedUser){
    LoginActivity loginActivity = LoginActivity.builder()
        .status(loginStatus)
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
