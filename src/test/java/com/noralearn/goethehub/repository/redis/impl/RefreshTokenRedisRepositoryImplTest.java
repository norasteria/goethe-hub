package com.noralearn.goethehub.repository.redis.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.noralearn.goethehub.common.redis.impl.RefreshTokenRedisRepositoryImpl;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class RefreshTokenRedisRepositoryImplTest {

  private final String REFRESH_TOKEN_PREFIX = "geothehub:refresh:%s";

  @Mock
  private RedisTemplate<String, String> redisTemplate;

  @Mock
  private ValueOperations<String, String> valueOperations;

  @InjectMocks
  private RefreshTokenRedisRepositoryImpl refreshTokenRedisRepository;

  @BeforeEach
  void setUp() {}

  @Test
  void save_ttlInDuration() {
    final Duration ttl = Duration.ofSeconds(1);
    final UUID key = UUID.randomUUID();
    final String mockToken = "dummy_token";

    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    refreshTokenRedisRepository.save(key, mockToken, ttl);

    Mockito.verify(valueOperations).set(REFRESH_TOKEN_PREFIX.formatted(key), mockToken, ttl);
  }

  @Test
  void save_ttlInLong() {
    final long ttl = 60L;
    final UUID key = UUID.randomUUID();
    final String mockedToken = "dummy_token";

    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    refreshTokenRedisRepository.save(key, mockedToken, ttl);

    Mockito.verify(valueOperations).set(REFRESH_TOKEN_PREFIX.formatted(key), mockedToken, ttl, TimeUnit.SECONDS);
  }

  @Test
  void find() {
    final String mockHashToken = "dummy_hash_token";
    final UUID key = UUID.randomUUID();

    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    Mockito.when(valueOperations.get(REFRESH_TOKEN_PREFIX.formatted(key))).thenReturn(mockHashToken);

    String result = refreshTokenRedisRepository.find(key);

    assertEquals(result, mockHashToken);
    Mockito.verify(valueOperations).get(REFRESH_TOKEN_PREFIX.formatted(key));
  }

  @Test
  void delete() {
    final UUID key = UUID.randomUUID();

    refreshTokenRedisRepository.delete(key);

    Mockito.verify(redisTemplate).delete(REFRESH_TOKEN_PREFIX.formatted(key));
  }
}