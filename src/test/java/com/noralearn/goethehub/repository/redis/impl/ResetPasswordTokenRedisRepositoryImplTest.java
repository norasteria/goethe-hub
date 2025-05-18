package com.noralearn.goethehub.repository.redis.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.noralearn.goethehub.common.redis.impl.ResetPasswordTokenRedisRepositoryImpl;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class ResetPasswordTokenRedisRepositoryImplTest {

  private final String RESET_PASSWORD_TOKEN_PREFIX = "geothehub:reset_password:%s";

  @Mock
  private RedisTemplate<String, String> redisTemplate;

  @Mock
  private ValueOperations<String, String> valueOperations;

  @InjectMocks
  private ResetPasswordTokenRedisRepositoryImpl resetPasswordTokenRedisRepository;

  @Test
  void save_ttlInDuration() {
    final String key = "dummy_reset_token";
    final UUID userId = UUID.randomUUID();
    final Duration ttl = Duration.ofSeconds(1);

    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    resetPasswordTokenRedisRepository.save(key, userId, ttl);

    Mockito.verify(valueOperations).set(RESET_PASSWORD_TOKEN_PREFIX.formatted(key), userId.toString(), ttl);
  }

  @Test
  void find() {
    final String dummyResetToken = "dummy_reset_token";
    final String mockCachedUserId = "99c44f1c-0064-4b00-842c-41d97fe81c39";

    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    Mockito.when(valueOperations.get(RESET_PASSWORD_TOKEN_PREFIX.formatted(dummyResetToken))).thenReturn(mockCachedUserId);

    UUID result = resetPasswordTokenRedisRepository.find(dummyResetToken);

    assertEquals(UUID.fromString(mockCachedUserId), result);
    Mockito.verify(valueOperations).get(RESET_PASSWORD_TOKEN_PREFIX.formatted(dummyResetToken));
  }

  @Test
  void delete() {
    final String dummyResetToken = "dummy_reset_token";

    resetPasswordTokenRedisRepository.delete(dummyResetToken);

    Mockito.verify(redisTemplate).delete(RESET_PASSWORD_TOKEN_PREFIX.formatted(dummyResetToken));
  }
}