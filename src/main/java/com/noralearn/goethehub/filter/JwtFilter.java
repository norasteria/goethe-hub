package com.noralearn.usermanagment.filter;

import com.noralearn.usermanagment.enums.TokenType;
import com.noralearn.usermanagment.exception.auth.AuthenticationException;
import com.noralearn.usermanagment.helper.JwtHelper;
import com.noralearn.usermanagment.helper.RequestHeaderHelper;
import com.noralearn.usermanagment.service.RedisTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtHelper jwtHelper;

  private final RedisTokenService redisTokenService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    String accessToken = RequestHeaderHelper.extractAccessToken(request);

    if (accessToken == null) {
      filterChain.doFilter(request, response);
      return;
    }

    if (redisTokenService.isAccessTokenRevoked(accessToken)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Toke has been revoked.");
    }

    Claims claims = jwtHelper.extractClaim(accessToken, TokenType.USER_TOKEN);
    String email= claims.get("email", String.class);
    String role = claims.get("role", String.class);

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        email, // Principal email,
        null, // credential null for JWT
        Collections.singletonList(new SimpleGrantedAuthority(role))
    );

    // set the Authentication object in the SecurityContext
    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }
}
