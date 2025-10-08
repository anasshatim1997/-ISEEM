package com.iseem_backend.application.DTO.response;


import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    @NotEmpty(message = "First name cannot be empty")
    private String prenom;

    @NotEmpty(message = "Last name cannot be empty")
    private String nom;

    @NotBlank(message = "Telephone cannot be blank")
    private String telephone;

    @NotBlank(message = "Email cannot be empty")
    @Email
    private String email;

    @Nullable
    private String image;
}
