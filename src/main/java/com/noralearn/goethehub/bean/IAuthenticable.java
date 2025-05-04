package com.noralearn.goethehub.bean;

import com.noralearn.goethehub.model.Role;
import java.util.UUID;

public interface IAuthenticable {

  UUID getId();

  String getEmail();

  String getPassword();

  Role getRole();

}
