package com.noralearn.goethehub.repository.redis.impl;

import com.noralearn.goethehub.repository.redis.ResetPasswordTokenRedisRepository;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ResetPasswordTokenRedisRepositoryImpl implements ResetPasswordTokenRedisRepository {

  private final String RESET_PASSWORD_TOKEN_PREFIX = "geothehub:reset_password:%s";

  private final RedisTemplate<String, String> redisTemplate;


  @Override
  public void save(String resetToken, UUID userId, Duration ttl) {
    redisTemplate.opsForValue().set(RESET_PASSWORD_TOKEN_PREFIX.formatted(resetToken), userId.toString(), ttl);
  }

  @Override
  public UUID find(String resetToken) {
    String cachedUserId = redisTemplate.opsForValue().get(RESET_PASSWORD_TOKEN_PREFIX.formatted(resetToken));

    return UUID.fromString(cachedUserId);
  }

  @Override
  public void delete(String resetToken) {
    redisTemplate.delete(RESET_PASSWORD_TOKEN_PREFIX.formatted(resetToken));
  }
}
