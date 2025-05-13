package com.noralearn.goethehub.repository.redis.impl;

import com.noralearn.goethehub.repository.redis.AccessTokenRedisRepository;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccessTokenRedisRepositoryImpl implements AccessTokenRedisRepository {

  private final String ACCESS_TOKEN_PREFIX = "geothehub:access_token:%s";

  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void save(String hashedToken, String value, Duration ttl) {
    redisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX.formatted(hashedToken), value, ttl);
  }

  @Override
  public void save(String hashedToken, String value, long ttl) {
    redisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX.formatted(hashedToken), value, ttl, TimeUnit.SECONDS);
  }

  @Override
  public boolean hasKey(String hashedToken) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(ACCESS_TOKEN_PREFIX.formatted(hashedToken)));
  }
}
