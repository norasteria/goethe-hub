package com.noralearn.goethehub.auth.bean;

import com.noralearn.goethehub.acl.model.Role;
import java.util.UUID;

public interface IAuthenticable {

  UUID getId();

  String getEmail();

  String getPassword();

  Role getRole();

}
