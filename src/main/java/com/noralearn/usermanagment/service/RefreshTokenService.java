package com.noralearn.usermanagment.service;

import com.noralearn.usermanagment.bean.AuthToken;
import com.noralearn.usermanagment.dto.request.RefreshTokenRequestDTO;
import com.noralearn.usermanagment.dto.response.LoginResponseDTO;
import com.noralearn.usermanagment.enums.TokenType;
import com.noralearn.usermanagment.exception.auth.AuthenticationException;
import com.noralearn.usermanagment.helper.JwtHelper;
import com.noralearn.usermanagment.model.User;
import com.noralearn.usermanagment.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final UsersRepository usersRepository;

  private final JwtHelper jwtHelper;

  public LoginResponseDTO refreshToken(RefreshTokenRequestDTO requestDTO) {
    final Claims claims = this.jwtHelper.extractClaim(requestDTO.getRefreshToken(), TokenType.REFRESH_TOKEN);
    final UUID userId = UUID.fromString(claims.getSubject());

    final User selectedUser = this.usersRepository.findById(userId)
        .orElseThrow(() -> new AuthenticationException("Invalid refresh token."));

    final AuthToken accessToken = this.jwtHelper.generateAccessToken(selectedUser);
    final AuthToken refreshToken = this.jwtHelper.generateRefreshToken(selectedUser);

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
