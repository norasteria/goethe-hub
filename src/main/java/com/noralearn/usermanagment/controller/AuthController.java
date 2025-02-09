package com.noralearn.usermanagment.controller;

import com.noralearn.usermanagment.dto.request.LoginRequestDTO;
import com.noralearn.usermanagment.dto.request.RegisterRequestDTO;
import com.noralearn.usermanagment.dto.response.LoginResponseDTO;
import com.noralearn.usermanagment.factory.ApiResponseFactory;
import com.noralearn.usermanagment.dto.response.UserDTO;
import com.noralearn.usermanagment.service.LoginService;
import com.noralearn.usermanagment.service.RegistrationService;
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


}
