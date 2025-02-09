package com.noralearn.usermanagment.repository;

import com.noralearn.usermanagment.model.LoginActivity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface LoginActivityRepository extends CrudRepository<LoginActivity, UUID> {

  Optional<List<LoginActivity>> findByUserId(UUID userId);

}
