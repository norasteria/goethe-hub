package com.noralearn.usermanagment.repository;

import com.noralearn.usermanagment.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  Optional<User> findByEmailAndIsActive(String email, boolean isActive);

}
