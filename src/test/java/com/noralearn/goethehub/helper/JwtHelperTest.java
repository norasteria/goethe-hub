package com.noralearn.goethehub.helper;

import static org.junit.jupiter.api.Assertions.*;

import com.noralearn.goethehub.bean.AuthToken;
import com.noralearn.goethehub.enums.TokenType;
import com.noralearn.goethehub.model.Role;
import com.noralearn.goethehub.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts.SIG;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtHelperTest {
  private JwtHelper jwtHelper;

  private final String accessTokenSecret = SIG.HS256.key().build().toString();

  private final String refreshTokenSecret = SIG.HS256.key().build().toString();

  @BeforeEach
  void setUp() {
    jwtHelper = new JwtHelper(
        "Goethe-Test",
        accessTokenSecret,
        refreshTokenSecret
    );
  }

  @Test
  void generateAndParseAccessToken() {
    final Role roleTest = Role.builder()
        .id(UUID.fromString("c7d66df1-1ca4-4875-9c57-04f014ca4d57"))
        .codename("STAFF")
        .build();
    final User userTest = User.builder()
        .id(UUID.fromString("9bb53677-908f-4733-829e-f29b989e0a09"))
        .email("nora@mail.com")
        .role(roleTest)
        .build();

    AuthToken accessToken = jwtHelper.generateAccessToken(userTest);
    Claims accessTokenClaims = jwtHelper.extractClaim(accessToken.getToken(), TokenType.USER_TOKEN);

    final ZonedDateTime now = ZonedDateTime.now();
    final ZonedDateTime expectedExpiredTime = now.plus(Duration.ofMinutes(15));

    assertEquals(accessTokenClaims.get("email", String.class), "nora@mail.com");
    assertEquals(accessTokenClaims.get("role", String.class), "STAFF");
    assertEquals(
        accessTokenClaims.getExpiration().toInstant().toEpochMilli(),
        expectedExpiredTime.toInstant().toEpochMilli(),
        1000 // 1s delay as tolerance of the time of generate token and make expectedExpiredTime
    );
  }

  @Test
  void generateAndParseRefreshToken() {
    final Role role = Role.builder()
        .id(UUID.fromString("1e586014-f71b-45f4-adbd-56b23b1c9338"))
        .codename("TEACHER")
        .build();
    final User testUser = User.builder()
        .id(UUID.fromString("7407a2f6-4c77-4926-9cf9-e48b2cd28112"))
        .email("nora2@mail.com")
        .role(role)
        .build();

    AuthToken refreshToken = jwtHelper.generateRefreshToken(testUser);
    Claims refreshTokenClaims = jwtHelper.extractClaim(refreshToken.getToken(), TokenType.REFRESH_TOKEN);

    assertEquals(refreshTokenClaims.get("email", String.class), "nora2@mail.com");
    assertEquals(refreshTokenClaims.get("role", String.class), "TEACHER");

    final ZonedDateTime now = ZonedDateTime.now();
    final ZonedDateTime expectedExpiredTime = now.plus(Duration.ofHours(1));

    assertEquals(
        refreshTokenClaims.getExpiration().toInstant().toEpochMilli(),
        expectedExpiredTime.toInstant().toEpochMilli(),
        1000 // 1s delay as tolerance after generate token and make expectedExpiredTime
    );

  }
}
