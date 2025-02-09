package com.noralearn.usermanagment.service;

import com.noralearn.usermanagment.dto.request.RegisterRequestDTO;
import com.noralearn.usermanagment.dto.response.UserDTO;
import com.noralearn.usermanagment.exception.DataNotFoundException;
import com.noralearn.usermanagment.exception.ValidationException;
import com.noralearn.usermanagment.model.Role;
import com.noralearn.usermanagment.model.User;
import com.noralearn.usermanagment.repository.RolesRepository;
import com.noralearn.usermanagment.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {
  private final BCryptPasswordEncoder passwordEncoder;

  private final UsersRepository userRepository;
  private final RolesRepository rolesRepository;

  public UserDTO registerUser(RegisterRequestDTO registerRequestDTO, String roleCodename){
    boolean isValidPassword = registerRequestDTO.getConfirmPassword().equals(registerRequestDTO.getPassword());

    if(!isValidPassword) throw new ValidationException("mismatch with confirm password");

    Role selectedRole = this.findRole(roleCodename);
    User userToSave = this.mapToUserModel(registerRequestDTO, selectedRole);

    this.userRepository.save(userToSave);

    return this.mapToUserDTO(userToSave, selectedRole);
  }

  private Role findRole(String roleCodename){
    String roleName = switch (roleCodename) {
      case "admin" -> "ADMINISTRATOR";
      case "teacher" -> "TEACHER_STAFF";
      case "student" -> "STUDENT_FREE";
      default -> null;
    };

    return this.rolesRepository
        .findByCodename(roleName)
        .orElseThrow(() -> new DataNotFoundException("Related role is not found"));
  }

  private User mapToUserModel(RegisterRequestDTO registerRequestDTO, Role selectedRole){
    return User.builder()
        .fullName(registerRequestDTO.getFullName())
        .email(registerRequestDTO.getEmail())
        .isActive(true)
        .password(this.passwordEncoder.encode(registerRequestDTO.getPassword()))
        .role(selectedRole)
        .build();
  }

  private UserDTO mapToUserDTO(User user, Role selectedRole){
    return UserDTO.builder()
        .id(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .isActive(user.isActive())
        .roleName(selectedRole.getCodename())
        .build();
  }

}
