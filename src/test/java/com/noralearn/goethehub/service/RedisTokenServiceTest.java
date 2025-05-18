package com.noralearn.goethehub.service;

import static org.junit.jupiter.api.Assertions.*;

import com.noralearn.goethehub.auth.enums.TokenType;
import com.noralearn.goethehub.common.service.RedisTokenService;
import com.noralearn.goethehub.common.helper.JwtHelper;
import com.noralearn.goethehub.common.helper.TokenHasherHelper;
import com.noralearn.goethehub.common.redis.AccessTokenRedisRepository;
import com.noralearn.goethehub.common.redis.RefreshTokenRedisRepository;
import com.noralearn.goethehub.common.redis.ResetPasswordTokenRedisRepository;
import io.jsonwebtoken.Claims;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RedisTokenServiceTest {

  @Mock
  private Claims mockClaims;

  @Mock
  private AccessTokenRedisRepository accessTokenRedisRepository;

  @Mock
  private RefreshTokenRedisRepository refreshTokenRedisRepository;

  @Mock
  private ResetPasswordTokenRedisRepository resetPasswordTokenRedisRepository;

  @Mock
  private JwtHelper jwtHelper;

  @InjectMocks
  private RedisTokenService redisTokenService;

  @BeforeEach
  void setUp() {
  }

  @Test
  void revokeAccessToken() {
    final String token = "dummy-token";
    final String mockHashToken = "dummy-hashed-token";
    final long ttl = 60L;

    // Mock static helper
    try (MockedStatic<TokenHasherHelper> mockedHasher = Mockito.mockStatic(TokenHasherHelper.class)) {
      mockedHasher.when(() -> TokenHasherHelper.hashToken(token)).thenReturn(mockHashToken);
      Mockito.when(jwtHelper.getExpirationTTL(token, TokenType.USER_TOKEN)).thenReturn(ttl);

      // Act call revokeAccessToken
      redisTokenService.revokeAccessToken(token);

      Mockito.verify(accessTokenRedisRepository).save(mockHashToken, "revoked", ttl);
    }
  }

  @Test
  void isAccessTokenRevoked() {
    final String token = "dummy-token";
    final String mockHashToken = "dummy-hash-token";

    // Mock static helper
    try (MockedStatic<TokenHasherHelper> mockHasher = Mockito.mockStatic(TokenHasherHelper.class)) {
      mockHasher.when(() -> TokenHasherHelper.hashToken(token)).thenReturn(mockHashToken);
      Mockito.when(accessTokenRedisRepository.hasKey(mockHashToken)).thenReturn(true);

      boolean result = redisTokenService.isAccessTokenRevoked(token);
      assertTrue(result);
    }
  }

  @Test
  void storeRefreshToken() {
    final String token = "dummy-token";
    final String mockHashedToken = "dummy-hash-token";
    final String mockTokenSubject = "e5a23750-a13b-44e5-9f44-796fb80ebc3f";
    final long ttl = 60L;

    // Mock static helper
    try (MockedStatic<TokenHasherHelper> mockHasher = Mockito.mockStatic(TokenHasherHelper.class)) {
      mockHasher.when(() -> TokenHasherHelper.hashToken(token)).thenReturn(mockHashedToken);
      Mockito.when(jwtHelper.extractClaim(token, TokenType.REFRESH_TOKEN)).thenReturn(mockClaims);
      Mockito.when(mockClaims.getSubject()).thenReturn(mockTokenSubject);
      Mockito.when(jwtHelper.getExpirationTTL(token, TokenType.REFRESH_TOKEN)).thenReturn(ttl);

      // Act
      redisTokenService.storeRefreshToken(token);

      Mockito.verify(refreshTokenRedisRepository).save(UUID.fromString(mockTokenSubject), mockHashedToken, ttl);
    }
  }

  @Test
  void isValidResetToken_true() {
    final String token = "dummy-token";
    final String mockTokenSubject = "e5a23750-a13b-44e5-9f44-796fb80ebc3f";
    final String mockHashedToken = "dummy-hashed-token";
    final String mockCachedToken = "dummy-hashed-token";

    // Mock static helper
    try (MockedStatic<TokenHasherHelper> mockHasher = Mockito.mockStatic(TokenHasherHelper.class)) {
      mockHasher.when(() -> TokenHasherHelper.hashToken(token)).thenReturn(mockHashedToken);
      // Mock get claim subject
      Mockito.when(jwtHelper.extractClaim(token, TokenType.REFRESH_TOKEN)).thenReturn(mockClaims);
      Mockito.when(mockClaims.getSubject()).thenReturn(mockTokenSubject);

      Mockito.when(refreshTokenRedisRepository.find(UUID.fromString(mockTokenSubject))).thenReturn(mockCachedToken);

      boolean result = redisTokenService.isValidRefreshToken(token);

      assertTrue(result);
      Mockito.verify(refreshTokenRedisRepository).find(UUID.fromString(mockTokenSubject));
    }
  }

  @Test
  void isValidResetToken_false() {
    final String token = "dummy-token";
    final String mockTokenSubject = "e5a23750-a13b-44e5-9f44-796fb80ebc3f";
    final String mockHashedToken = "dummy-hashed-token";
    final String mockCachedToken = "other-dummy-hashed-token";

    // Mock static helper
    try (MockedStatic<TokenHasherHelper> mockHasher = Mockito.mockStatic(TokenHasherHelper.class)) {
      mockHasher.when(() -> TokenHasherHelper.hashToken(token)).thenReturn(mockHashedToken);
      // Mock get claim subject
      Mockito.when(jwtHelper.extractClaim(token, TokenType.REFRESH_TOKEN)).thenReturn(mockClaims);
      Mockito.when(mockClaims.getSubject()).thenReturn(mockTokenSubject);

      Mockito.when(refreshTokenRedisRepository.find(UUID.fromString(mockTokenSubject))).thenReturn(mockCachedToken);

      boolean result = redisTokenService.isValidRefreshToken(token);

      assertFalse(result);
      Mockito.verify(refreshTokenRedisRepository).find(UUID.fromString(mockTokenSubject));
    }
  }

  @Test
  void deleteRefreshToken() {
    final UUID userId = UUID.randomUUID();

    redisTokenService.deleteRefreshToken(userId);

    Mockito.verify(refreshTokenRedisRepository).delete(userId);
  }

  @Test
  void storeResetPasswordToken() {
    final String token = "dummy-token";
    final UUID userId = UUID.randomUUID();
    final Duration ttl = Duration.ofMinutes(15);

    redisTokenService.storeResetPasswordToken(token, userId);

    Mockito.verify(resetPasswordTokenRedisRepository).save(token, userId, ttl);
  }

  @Test
  void getUserIdByResetToken() {
    final String resetToken = "dummy-token";
    final UUID mockUserId = UUID.randomUUID();

    Mockito.when(resetPasswordTokenRedisRepository.find(resetToken)).thenReturn(mockUserId);

    UUID result = redisTokenService.getUserIdByResetToken(resetToken);

    assertEquals(result, mockUserId);
    Mockito.verify(resetPasswordTokenRedisRepository).find(resetToken);
  }

  @Test
  void deleteResetToken() {
    final String resetToken = "dummy-token";

    redisTokenService.deleteResetToken(resetToken);

    Mockito.verify(resetPasswordTokenRedisRepository).delete(resetToken);
  }
}
