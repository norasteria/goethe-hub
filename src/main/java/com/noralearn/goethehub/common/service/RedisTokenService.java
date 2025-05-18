package com.noralearn.goethehub.common.service;

import com.noralearn.goethehub.auth.enums.TokenType;
import com.noralearn.goethehub.common.helper.JwtHelper;
import com.noralearn.goethehub.common.helper.TokenHasherHelper;
import com.noralearn.goethehub.common.redis.AccessTokenRedisRepository;
import com.noralearn.goethehub.common.redis.RefreshTokenRedisRepository;
import com.noralearn.goethehub.common.redis.ResetPasswordTokenRedisRepository;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

  private final AccessTokenRedisRepository accessTokenRedisRepository;
  private final RefreshTokenRedisRepository refreshTokenRedisRepository;
  private final ResetPasswordTokenRedisRepository resetPasswordTokenRedisRepository;

  private final JwtHelper jwtHelper;

  public void revokeAccessToken(String token) {
    final String hashedToken = TokenHasherHelper.hashToken(token);
    final long ttl = this.jwtHelper.getExpirationTTL(token, TokenType.USER_TOKEN);

    accessTokenRedisRepository.save(hashedToken, "revoked", ttl);
  }

  public boolean isAccessTokenRevoked(String token) {
    final String hashedToken = TokenHasherHelper.hashToken(token);

    return accessTokenRedisRepository.hasKey(hashedToken);
  }

  public void storeRefreshToken(String token) {
    final String hashedToken = TokenHasherHelper.hashToken(token);
    final String userId = this.getUserIdRefreshToken(token);
    final long ttl = this.jwtHelper.getExpirationTTL(token, TokenType.REFRESH_TOKEN);

    refreshTokenRedisRepository.save(UUID.fromString(userId), hashedToken, ttl);
  }

  public boolean isValidRefreshToken(String token) {
    final String hashedToken = TokenHasherHelper.hashToken(token);
    final String userId = this.getUserIdRefreshToken(token);
    final String storedToken = refreshTokenRedisRepository.find(UUID.fromString(userId));

    return hashedToken.equals(storedToken);
  }

  public void deleteRefreshToken(UUID userId) {
    refreshTokenRedisRepository.delete(userId);
  }

  public void storeResetPasswordToken(String resetToken, UUID userId) {
    final Duration RESET_PASSWORD_TOKEN_TTL = Duration.ofMinutes(15);

    resetPasswordTokenRedisRepository.save(resetToken, userId, RESET_PASSWORD_TOKEN_TTL);
  }

  public  UUID getUserIdByResetToken(String resetToken) {
    return resetPasswordTokenRedisRepository.find(resetToken);
  }

  public void deleteResetToken(String resetToken) {
    resetPasswordTokenRedisRepository.delete(resetToken);
  }

  private String getUserIdRefreshToken(String token) {
    return this.jwtHelper
        .extractClaim(token, TokenType.REFRESH_TOKEN)
        .getSubject();
  }
}
