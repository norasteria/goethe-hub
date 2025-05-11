package com.noralearn.goethehub.repository;

import com.noralearn.goethehub.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<User, UUID> {

  Optional<User> findByIdAndIsActive(UUID userId, boolean isActive);

  Optional<User> findByEmailAndIsActive(String email, boolean isActive);
}
