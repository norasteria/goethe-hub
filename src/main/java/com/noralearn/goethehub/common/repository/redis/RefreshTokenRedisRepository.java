package com.noralearn.goethehub.common.repository.redis;

import java.time.Duration;
import java.util.UUID;

public interface RefreshTokenRedisRepository {

  void save(UUID userId, String value, Duration ttl);

  void save(UUID userId, String value, long ttl);

  String find(UUID userId);

  void delete(UUID userId);
}
