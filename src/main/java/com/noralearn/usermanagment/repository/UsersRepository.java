package com.noralearn.usermanagment.repository;

import com.noralearn.usermanagment.model.Users;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<Users, UUID> {

}
