package com.noralearn.usermanagment.helper;

import com.noralearn.usermanagment.bean.AuthToken;
import com.noralearn.usermanagment.bean.IAuthenticable;
import com.noralearn.usermanagment.enums.TokenType;
import com.noralearn.usermanagment.exception.auth.AuthenticationException;
import com.noralearn.usermanagment.exception.auth.ExpiredTokenException;
import com.noralearn.usermanagment.exception.auth.InvalidSignatureException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtHelper {

  private final Duration ACCESS_TOKEN_EXPIRY_TIME = Duration.ofMinutes(15);

  private final Duration REFRESH_TOKEN_EXPIRY_TIME = Duration.ofHours(1);

  private final String tokenIssuer;

  private final SecretKey accessTokenSecret;

  private final SecretKey refreshTokenSecret;

  public JwtHelper(
      @Value("${security.token.issuer}") String tokenIssuer,
      @Value("${security.token.access-secret}") String accessSecretKey,
      @Value("${security.token.refresh-secret}") String refreshSecretKey
  ){
    this.tokenIssuer = tokenIssuer;
    this.accessTokenSecret = Keys.hmacShaKeyFor(accessSecretKey.getBytes(StandardCharsets.UTF_8));
    this.refreshTokenSecret = Keys.hmacShaKeyFor(refreshSecretKey.getBytes(StandardCharsets.UTF_8));
  }

  public AuthToken generateAccessToken(IAuthenticable authEntity){
    return this.generateToken(
        authEntity,
        ACCESS_TOKEN_EXPIRY_TIME,
        this.accessTokenSecret,
        TokenType.USER_TOKEN
    );
  }

  public AuthToken generateRefreshToken(IAuthenticable authEntity){
    return this.generateToken(
        authEntity,
        REFRESH_TOKEN_EXPIRY_TIME,
        this.refreshTokenSecret,
        TokenType.REFRESH_TOKEN
    );
  }

  public Claims extractClaim(String token, TokenType tokenType){
    try {
      SecretKey secretKey = this.getSecretKey(tokenType);

      return Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch(ExpiredJwtException ex){
      throw new ExpiredTokenException();
    } catch (SignatureException ex){
      throw new InvalidSignatureException();
    } catch (Exception ex){
      throw new AuthenticationException(ex.getMessage());
    }
  }

  private AuthToken generateToken(IAuthenticable authEntity, Duration duration, SecretKey tokenSecret, TokenType tokenType){
  final ZonedDateTime now = ZonedDateTime.now();
  final ZonedDateTime expiredTime = now.plus(duration);

  final String token = Jwts.builder()
      .issuer(this.tokenIssuer)
      .subject(authEntity.getId().toString())
      .claim("email", authEntity.getEmail())
      .claim("role", authEntity.getRole().getCodename())
      .claim("token_type", tokenType)
      .issuedAt(Date.from(now.toInstant()))
      .expiration(Date.from(expiredTime.toInstant()))
      .signWith(tokenSecret, SIG.HS256)
      .compact();

  log.info("GENERATED TOKEN: %s".formatted(token));

  return AuthToken.builder()
      .token(token)
      .expiredAt(expiredTime)
      .build();
  }


  private SecretKey getSecretKey(TokenType tokenType){
   return switch (tokenType){
      case USER_TOKEN -> this.accessTokenSecret;
      case REFRESH_TOKEN -> this.refreshTokenSecret;
   };
  }

}
