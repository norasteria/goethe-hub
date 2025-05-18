package com.noralearn.goethehub.user.repository;

import com.noralearn.goethehub.user.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<User, UUID> {

  Optional<User> findByIdAndIsActive(UUID userId, boolean isActive);

  Optional<User> findByEmailAndIsActive(String email, boolean isActive);
}
