package com.noralearn.goethehub.auth.service;

import com.noralearn.goethehub.auth.dto.request.ForgotPasswordRequestDTO;
import com.noralearn.goethehub.auth.dto.request.ResetPasswordDTO;
import com.noralearn.goethehub.auth.enums.AuthActivityStatus;
import com.noralearn.goethehub.common.exception.DataNotFoundException;
import com.noralearn.goethehub.auth.exception.InvalidResetTokenException;
import com.noralearn.goethehub.common.service.RedisTokenService;
import com.noralearn.goethehub.common.helper.RequestHeaderHelper;
import com.noralearn.goethehub.common.helper.UserAgentHelper;
import com.noralearn.goethehub.auth.model.LoginActivity;
import com.noralearn.goethehub.user.model.User;
import com.noralearn.goethehub.auth.repository.LoginActivityRepository;
import com.noralearn.goethehub.user.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordRecoveryService {

  private final BCryptPasswordEncoder passwordEncoder;

  private final UsersRepository usersRepository;
  private final LoginActivityRepository loginActivityRepository;

  private final RedisTokenService redisTokenService;
  private final UserAgentHelper userAgentHelper;

  public void forgotPassword(ForgotPasswordRequestDTO requestDTO){
    final String resetToken = RandomStringUtils.secure().next(10, true, true);

    final User user = this.usersRepository.findByEmailAndIsActive(requestDTO.getEmail(), true)
        .orElseThrow(() -> new DataNotFoundException("No email found."));

    this.redisTokenService.storeResetPasswordToken(resetToken, user.getId());

    // Mimicking send link of reset password to user's email
    log.info("Send reset link with attaching this reset token: {}", resetToken);
  }

  public void resetPassword(ResetPasswordDTO requestDTO, HttpServletRequest servletRequest) {
    final boolean isPasswordMatch = requestDTO.getConfirmPassword().equals(requestDTO.getPassword());

    if (!isPasswordMatch) throw new InvalidResetTokenException("Password doesn't match");

    final UUID userId = this.redisTokenService.getUserIdByResetToken(requestDTO.getResetToken());

    if (userId == null) throw new InvalidResetTokenException("Invalid reset token");

    User user = this.usersRepository.findByIdAndIsActive(userId, true)
        .orElseThrow(() -> new DataNotFoundException("User not found."));

    user.setPassword(this.passwordEncoder.encode(requestDTO.getPassword()));

    this.usersRepository.save(user);
    this.redisTokenService.deleteResetToken(requestDTO.getResetToken());
    this.logAuthActivity(user, servletRequest);
  }

  private void logAuthActivity(User user, HttpServletRequest servletRequest) {
    final String rawUserAgent = RequestHeaderHelper.extractUserAgent(servletRequest);
    final String deviceType = this.userAgentHelper.buildDeviceTypeByUserAgent(rawUserAgent);

    final LoginActivity loginActivity = LoginActivity.builder()
        .status(AuthActivityStatus.RESET_PASSWORD)
        .deviceType(deviceType)
        .ipAddress(servletRequest.getRemoteAddr())
        .user(user)
        .build();

    this.loginActivityRepository.save(loginActivity);
  }
}
