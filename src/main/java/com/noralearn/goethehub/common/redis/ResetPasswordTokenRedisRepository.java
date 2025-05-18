package com.noralearn.goethehub.common.redis;

import java.time.Duration;
import java.util.UUID;

public interface ResetPasswordTokenRedisRepository {

  void save(String resetToken, UUID userId, Duration ttl);

  UUID find(String resetToken);

  void delete(String resetToken);
}
