package com.noralearn.usermanagment.controller;

import com.noralearn.usermanagment.dto.request.RegisterRequestDTO;
import com.noralearn.usermanagment.dto.response.ApiResponse;
import com.noralearn.usermanagment.dto.response.UserDTO;
import com.noralearn.usermanagment.enums.ResponseStatusEnum;
import com.noralearn.usermanagment.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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


 @PostMapping("/register")
 @ResponseStatus(value = HttpStatus.OK)
  public ApiResponse<UserDTO> registerUser(@Valid @RequestBody RegisterRequestDTO requestDTO){
    UserDTO savedUser = this.registrationService.registerUser(requestDTO);

    return new ApiResponse<UserDTO>(ResponseStatusEnum.success, savedUser);
  }

}
