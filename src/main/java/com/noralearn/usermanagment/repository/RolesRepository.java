package com.noralearn.usermanagment.repository;

import com.noralearn.usermanagment.model.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface RolesRepository extends CrudRepository<Role, UUID> {

  Optional<Role> findByCodename(String codename);
}
