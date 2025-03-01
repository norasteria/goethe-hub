package com.noralearn.usermanagment.repository;

import com.noralearn.usermanagment.enums.LoginStatus;
import com.noralearn.usermanagment.model.LoginActivity;
import java.time.ZonedDateTime;
import java.util.List;
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
