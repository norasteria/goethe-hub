package com.noralearn.usermanagment.bean;

import com.noralearn.usermanagment.model.Role;
import java.util.UUID;

public interface IAuthenticable {

  UUID getId();

  String getEmail();

  String getPassword();

  Role getRole();

}
