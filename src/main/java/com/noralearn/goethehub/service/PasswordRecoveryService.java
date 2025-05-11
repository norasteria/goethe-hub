package com.noralearn.goethehub.service;

import com.noralearn.goethehub.dto.request.ForgotPasswordRequestDTO;
import com.noralearn.goethehub.exception.DataNotFoundException;
import com.noralearn.goethehub.model.User;
import com.noralearn.goethehub.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordRecoveryService {

  private final UsersRepository usersRepository;

  private final RedisTokenService redisTokenService;

  public void forgotPassword(ForgotPasswordRequestDTO requestDTO){
    String resetToken = RandomStringUtils.secure().next(10, true, true);

    User user = this.usersRepository.findByEmailAndIsActive(requestDTO.getEmail(), true)
        .orElseThrow(() -> new DataNotFoundException("No email found."));

    this.redisTokenService.storeResetPasswordToken(resetToken, user.getId());

    // Mimicking send link of reset password to user's email
    log.info("Send reset link with attaching this reset token: {}", resetToken);
  }
}
