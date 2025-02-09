package com.noralearn.usermanagment.config;


import com.noralearn.usermanagment.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(
    // Enable all annotations for specifying list of roles on a method
    securedEnabled = true, // Enable @Secured annotation
    jsr250Enabled = true // Enable @RoleAllowed annotation
)
public class SecurityConfig {

  private final JwtFilter jwtFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            // all /auth endpoints can be accessible
            .requestMatchers("/auth/**").permitAll()
            // all `/public` endpoints can be accessible without auth
            .requestMatchers("/public/**").permitAll()
            // allow accessing swagger-ui
            .requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/webjars/**"
            ).permitAll()
            .anyRequest().authenticated()) // other than, that need auth
        .sessionManagement(auth -> auth.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  /** Cors Config for production
  @Bean
  public CorsFilter corsFilter(){
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.setAllowCredentials(true); // allow cookies
    config.addAllowedOrigin("https://frontend-domain.com"); // allow specific FE domain address
    // allow specific methods
    corsConfig.addAllowedMethod("GET");
    corsConfig.addAllowedMethod("POST");
    corsConfig.addAllowedMethod("DELETE");
    corsConfig.addAllowedMethod("PUT");
    corsConfig.addAllowedMethod("PATCH");

    // allow all headers
    corsConfig.addAllowedHeader("*");

    // expose custom header
    corsConfig.addExposedHeader("Authorization");

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig); // apply cors config to all endpoints
    return new CorsFilter(source);
  } */
}
