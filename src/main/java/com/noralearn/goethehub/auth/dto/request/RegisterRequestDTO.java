package com.noralearn.goethehub.auth.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO extends CreatePasswordRequestDTO {

  @NotBlank
  private String fullName;

  @Email
  @NotBlank
  private String email;
}
