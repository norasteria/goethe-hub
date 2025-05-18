package com.noralearn.goethehub.common.repository.redis;

import java.time.Duration;

public interface AccessTokenRedisRepository {

  void save(String hashedToken, String value, Duration ttl);

  void save(String hashedToken, String value, long ttl);

  boolean hasKey(String hashedToken);
}
