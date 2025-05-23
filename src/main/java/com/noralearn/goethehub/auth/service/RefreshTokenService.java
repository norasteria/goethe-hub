package com.noralearn.goethehub.auth.service;

import com.noralearn.goethehub.auth.bean.AuthToken;
import com.noralearn.goethehub.auth.dto.request.RefreshTokenRequestDTO;
import com.noralearn.goethehub.auth.dto.response.LoginResponseDTO;
import com.noralearn.goethehub.auth.enums.TokenType;
import com.noralearn.goethehub.auth.exception.AuthenticationException;
import com.noralearn.goethehub.common.service.RedisTokenService;
import com.noralearn.goethehub.common.helper.JwtHelper;
import com.noralearn.goethehub.user.model.User;
import com.noralearn.goethehub.user.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final UsersRepository usersRepository;

  private final JwtHelper jwtHelper;

  private final RedisTokenService redisTokenService;

  public LoginResponseDTO refreshToken(RefreshTokenRequestDTO requestDTO) {
    final Claims claims = this.jwtHelper.extractClaim(requestDTO.getRefreshToken(), TokenType.REFRESH_TOKEN);
    final UUID userId = UUID.fromString(claims.getSubject());

    if (!this.redisTokenService.isValidRefreshToken(requestDTO.getRefreshToken())) {
      throw new AuthenticationException("Invalid refresh token.");
    }

    final User selectedUser = this.usersRepository.findById(userId)
        .orElseThrow(() -> new AuthenticationException("Invalid refresh token."));

    final AuthToken accessToken = this.jwtHelper.generateAccessToken(selectedUser);
    final AuthToken refreshToken = this.jwtHelper.generateRefreshToken(selectedUser);

    redisTokenService.deleteRefreshToken(selectedUser.getId());
    redisTokenService.storeRefreshToken(refreshToken.getToken());

    return LoginResponseDTO.builder()
        .user(LoginResponseDTO.UserDetail.builder()
            .id(selectedUser.getId())
            .email(selectedUser.getEmail())
            .build())
        .accessToken(accessToken.getToken())
        .accessTokenExpiredAt(accessToken.getExpiredAt())
        .refreshToken(refreshToken.getToken())
        .refreshTokenExpiredAt(refreshToken.getExpiredAt())
        .build();
  }
}
