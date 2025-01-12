package com.noralearn.usermanagment.service;

import com.noralearn.usermanagment.dto.request.RegisterRequestDTO;
import com.noralearn.usermanagment.dto.response.UserDTO;
import com.noralearn.usermanagment.exception.ValidationException;
import com.noralearn.usermanagment.model.Users;
import com.noralearn.usermanagment.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {
  private final BCryptPasswordEncoder passwordEncoder;

  private final UsersRepository userRepository;

  public UserDTO registerUser(RegisterRequestDTO registerRequestDTO){
    boolean isValidPassword = registerRequestDTO.getConfirmPassword().equals(registerRequestDTO.getPassword());

    if(!isValidPassword) throw new ValidationException("mismatch with confirm password");

    Users userToSave = this.mapToUserModel(registerRequestDTO);

    this.userRepository.save(userToSave);

    return this.mapToUserDTO(userToSave);
  }

  private Users mapToUserModel(RegisterRequestDTO registerRequestDTO){
    return Users.builder()
        .fullName(registerRequestDTO.getFullName())
        .email(registerRequestDTO.getEmail())
        .isActive(true)
        .password(this.passwordEncoder.encode(registerRequestDTO.getPassword()))
        .build();
  }

  private UserDTO mapToUserDTO(Users user){
    return UserDTO.builder()
        .id(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .isActive(user.isActive())
        .build();
  }

}
