package com.noralearn.goethehub.repository;

import com.noralearn.goethehub.model.LoginActivity;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface LoginActivityRepository extends CrudRepository<LoginActivity, UUID> {

 @Query(value = "SELECT COUNT(*) FROM login_activities "
     + "WHERE user_id = :userId "
     + "AND status = :status "
     + "AND DATE(created_at) = CURRENT_DATE", nativeQuery = true)
  int countFailedLoginAttemptsToday(
      @Param("userId") UUID userId,
      @Param("status") String status);
}
