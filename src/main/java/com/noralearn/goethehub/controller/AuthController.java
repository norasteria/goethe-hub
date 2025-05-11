package com.noralearn.goethehub.controller;

import com.noralearn.goethehub.dto.request.ForgotPasswordRequestDTO;
import com.noralearn.goethehub.dto.request.LoginRequestDTO;
import com.noralearn.goethehub.dto.request.RefreshTokenRequestDTO;
import com.noralearn.goethehub.dto.request.RegisterRequestDTO;
import com.noralearn.goethehub.dto.request.ResetPasswordDTO;
import com.noralearn.goethehub.dto.response.LoginResponseDTO;
import com.noralearn.goethehub.dto.response.MessageResponseDTO;
import com.noralearn.goethehub.factory.ApiResponseFactory;
import com.noralearn.goethehub.dto.response.UserDTO;
import com.noralearn.goethehub.service.PasswordRecoveryService;
import com.noralearn.goethehub.service.LoginService;
import com.noralearn.goethehub.service.LogoutService;
import com.noralearn.goethehub.service.RefreshTokenService;
import com.noralearn.goethehub.service.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final RegistrationService registrationService;

  private final LoginService loginService;
  private final RefreshTokenService refreshTokenService;
  private final LogoutService logoutService;
  private final PasswordRecoveryService passwordRecoveryService;


 @PostMapping("/register/{role}") // role should be "admin"/"teacher"/"student"
 @ResponseStatus(value = HttpStatus.OK)
  public ApiResponseFactory<UserDTO> registerUser(@Valid @RequestBody RegisterRequestDTO requestDTO, @PathVariable String role){
    UserDTO savedUser = this.registrationService.registerUser(requestDTO, role);

    return ApiResponseFactory.success(savedUser);
  }

  @PostMapping("/login")
  @ResponseStatus(value = HttpStatus.OK)
  public ApiResponseFactory<LoginResponseDTO> login(
      @Valid @RequestBody LoginRequestDTO loginRequestDTO,
      HttpServletRequest request
  ){
   return ApiResponseFactory.success(this.loginService.login(loginRequestDTO, request));
  }

  @PostMapping("/refresh-token")
  @ResponseStatus(value = HttpStatus.OK)
  public ApiResponseFactory<LoginResponseDTO> refreshToken(
      @Valid @RequestBody RefreshTokenRequestDTO requestDTO
  ) {
   return ApiResponseFactory.success(this.refreshTokenService.refreshToken(requestDTO));
  }

  @PostMapping("/logout")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void logout(HttpServletRequest request) {
   this.logoutService.logout(request);
  }

  @PostMapping("/forgot-password")
  public ApiResponseFactory<MessageResponseDTO> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequestDTO requestDTO
  ) {
  this.passwordRecoveryService.forgotPassword(requestDTO);

   return ApiResponseFactory.success(
       MessageResponseDTO.builder()
       .message("Reset password link is ready, please check your email.")
       .build());
  }

  @PostMapping("/reset-password")
  public ApiResponseFactory<MessageResponseDTO> resetPassword(
      @Valid @RequestBody ResetPasswordDTO requestDTO,
      HttpServletRequest servletRequest
  ) {
  this.passwordRecoveryService.resetPassword(requestDTO, servletRequest);

   return ApiResponseFactory.success(
       MessageResponseDTO.builder()
           .message("The related password has updated. Please do re-login.")
           .build()
   );
  }
}
