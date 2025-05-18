package com.noralearn.goethehub.common.repository.redis.impl;

import com.noralearn.goethehub.common.repository.redis.RefreshTokenRedisRepository;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepositoryImpl implements RefreshTokenRedisRepository {

  private final String REFRESH_TOKEN_PREFIX = "geothehub:refresh:%s";

  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void save(UUID userId, String value, Duration ttl) {
    redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX.formatted(userId), value, ttl);
  }

  @Override
  public void save(UUID userId, String value, long ttl) {
    redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX.formatted(userId), value, ttl, TimeUnit.SECONDS);
  }

  @Override
  public String find(UUID userId) {
    return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX.formatted(userId));
  }

  @Override
  public void delete(UUID userId) {
    redisTemplate.delete(REFRESH_TOKEN_PREFIX.formatted(userId));
  }
}
