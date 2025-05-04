package com.noralearn.goethehub.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

  private UUID id;

  private String fullName;

  private String email;

  private boolean isActive;

  private String roleName;
}
