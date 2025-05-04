package com.noralearn.goethehub.repository;

import com.noralearn.goethehub.model.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface RolesRepository extends CrudRepository<Role, UUID> {

  Optional<Role> findByCodename(String codename);
}
