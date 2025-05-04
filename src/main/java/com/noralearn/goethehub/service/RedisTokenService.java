package com.noralearn.goethehub.service;

import com.noralearn.goethehub.enums.TokenType;
import com.noralearn.goethehub.helper.JwtHelper;
import com.noralearn.goethehub.helper.TokenHasherHelper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

  private final String ACCESS_TOKEN_PREFIX = "revoked:access:%s";
  private final String REFRESH_TOKEN_PREFIX = "refresh:%s";

  private final RedisTemplate<String, String> redisTemplate;

  private final JwtHelper jwtHelper;

  public void revokeAccessToken(String token) {
    final String hashedToken = TokenHasherHelper.hashToken(token);
    final long ttl = this.jwtHelper.getExpirationTTL(token, TokenType.USER_TOKEN);

    redisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX.formatted(hashedToken), "revoked", ttl);
  }

  public boolean isAccessTokenRevoked(String token) {
    final String hashedToken = TokenHasherHelper.hashToken(token);

    return Boolean.TRUE.equals(redisTemplate.hasKey(ACCESS_TOKEN_PREFIX.formatted(hashedToken)));
  }

  public void storeRefreshToken(String token) {
    final String hashedToken = TokenHasherHelper.hashToken(token);
    final String userId = this.getUserIdRefreshToken(token);
    final long ttl = this.jwtHelper.getExpirationTTL(token, TokenType.REFRESH_TOKEN);

    redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX.formatted(userId), hashedToken, ttl);
  }

  public boolean isValidRefreshToken(String token) {
    final String hashedToken = TokenHasherHelper.hashToken(token);
    final String userId = this.getUserIdRefreshToken(token);
    final String storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX.formatted(userId));

    return hashedToken.equals(storedToken);
  }

  public void deleteRefreshToken(UUID userId) {
    redisTemplate.delete(REFRESH_TOKEN_PREFIX.formatted(userId));
  }

  public void deleteRefreshToken(String token) {
    String userId = this.getUserIdRefreshToken(token);
    redisTemplate.delete(REFRESH_TOKEN_PREFIX.formatted(userId));
  }

  private String getUserIdRefreshToken(String token) {
    return this.jwtHelper
        .extractClaim(token, TokenType.REFRESH_TOKEN)
        .getIssuer();
  }
}
