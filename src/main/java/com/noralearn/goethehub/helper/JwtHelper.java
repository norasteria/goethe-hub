package com.noralearn.goethehub.helper;

import com.noralearn.goethehub.bean.AuthToken;
import com.noralearn.goethehub.bean.IAuthenticable;
import com.noralearn.goethehub.enums.TokenType;
import com.noralearn.goethehub.exception.auth.AuthenticationException;
import com.noralearn.goethehub.exception.auth.ExpiredTokenException;
import com.noralearn.goethehub.exception.auth.InvalidSignatureException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
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
  ) {
    this.tokenIssuer = tokenIssuer;
    this.accessTokenSecret = Keys.hmacShaKeyFor(accessSecretKey.getBytes(StandardCharsets.UTF_8));
    this.refreshTokenSecret = Keys.hmacShaKeyFor(refreshSecretKey.getBytes(StandardCharsets.UTF_8));
  }

  public AuthToken generateAccessToken(IAuthenticable authEntity) {
    final ZonedDateTime now = ZonedDateTime.now();
    final ZonedDateTime expiredTime = now.plus(ACCESS_TOKEN_EXPIRY_TIME);

    final String token = Jwts.builder()
        .issuer(this.tokenIssuer)
        .subject(authEntity.getId().toString())
        .claim("email", authEntity.getEmail())
        .claim("role", authEntity.getRole().getCodename())
        .claim("token_type", TokenType.USER_TOKEN)
        .issuedAt(Date.from(now.toInstant()))
        .expiration(Date.from(expiredTime.toInstant()))
        .signWith(this.accessTokenSecret, SIG.HS256)
        .compact();

    return AuthToken.builder()
        .token(token)
        .expiredAt(expiredTime)
        .build();
  }

  public AuthToken generateRefreshToken(IAuthenticable authEntity) {
    final ZonedDateTime now = ZonedDateTime.now();
    final ZonedDateTime expiredTime = now.plus(REFRESH_TOKEN_EXPIRY_TIME);

    final String token = Jwts.builder()
        .issuer(this.tokenIssuer)
        .subject(authEntity.getId().toString())
        .claim("token_type", TokenType.REFRESH_TOKEN)
        .issuedAt(Date.from(now.toInstant()))
        .expiration(Date.from(expiredTime.toInstant()))
        .signWith(this.refreshTokenSecret, SIG.HS256)
        .compact();

    return AuthToken.builder()
        .token(token)
        .expiredAt(expiredTime)
        .build();
  }

  public Claims extractClaim(String token, TokenType tokenType) {
    try {
      SecretKey secretKey = this.getSecretKey(tokenType);

      return Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (ExpiredJwtException ex) {
      throw new ExpiredTokenException();
    } catch (SignatureException | MalformedJwtException ex) {
      throw new InvalidSignatureException();
    } catch (Exception ex) {
      throw new AuthenticationException(ex.getMessage());
    }
  }

  public long getExpirationTTL(String token, TokenType tokenType) {
    Claims claims = this.extractClaim(token, tokenType);
    Date expirationDate = claims.getExpiration();
    long ttl = (expirationDate.getTime() - Instant.now().toEpochMilli()) / 1000;

    return Math.max(ttl, 0);
  }

  private SecretKey getSecretKey(TokenType tokenType) {
   return switch (tokenType){
      case USER_TOKEN -> this.accessTokenSecret;
      case REFRESH_TOKEN -> this.refreshTokenSecret;
   };
  }
}
