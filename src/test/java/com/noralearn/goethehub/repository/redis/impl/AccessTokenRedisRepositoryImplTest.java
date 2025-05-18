package com.noralearn.goethehub.repository.redis.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.noralearn.goethehub.common.redis.impl.AccessTokenRedisRepositoryImpl;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessTokenRedisRepositoryImplTest {

  @Mock
  private RedisTemplate<String, String> redisTemplate;

  @Mock
  private ValueOperations<String, String> valueOperations;

  @InjectMocks
  private AccessTokenRedisRepositoryImpl accessTokenRedisRepository;

  private final String ACCESS_TOKEN_PREFIX = "geothehub:access:%s";

  @BeforeEach
  void setUp() {}

  @Test
  void save_ttlInDuration() {
    final Duration ttl = Duration.ofSeconds(1);
    final String key = "test_redis_access_token";
    final String value = "token_value";

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    accessTokenRedisRepository.save(key, value, ttl);

    verify(valueOperations).set(ACCESS_TOKEN_PREFIX.formatted(key), value, ttl);
  }

  @Test
  void save_ttlInLong() {
    final long ttl = 60;
    final String key = "test_redis_access_token";
    final String value = "token_value";

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    accessTokenRedisRepository.save(key, value, ttl);

    verify(valueOperations).set(ACCESS_TOKEN_PREFIX.formatted(key), value, ttl, TimeUnit.SECONDS);
  }

  @Test
  void hasKey() {
    final String key = "test_exist";

    when(redisTemplate.hasKey(ACCESS_TOKEN_PREFIX.formatted(key))).thenReturn(true);

    boolean result = accessTokenRedisRepository.hasKey(key);

    assertTrue(result);
    verify(redisTemplate).hasKey(ACCESS_TOKEN_PREFIX.formatted(key));
  }
}