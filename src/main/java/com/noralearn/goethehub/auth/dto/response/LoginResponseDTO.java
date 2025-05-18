package com.noralearn.goethehub.auth.dto.response;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class LoginResponseDTO {

  private String accessToken;

  private ZonedDateTime accessTokenExpiredAt;

  private String refreshToken;

  private ZonedDateTime refreshTokenExpiredAt;

  private UserDetail user;

  @Setter
  @Getter
  @Builder
  public static class UserDetail {

    private UUID id;

    private String email;
  }
}
