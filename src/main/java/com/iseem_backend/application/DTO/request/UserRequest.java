package com.iseem_backend.application.DTO.request;

import com.iseem_backend.application.enums.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    @Email
    @NotBlank
    private String email ;
    @NotBlank
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = "Password must be 8-20 characters long, contain at least one digit, one lowercase, one uppercase, and one special character.")
    private String password;
    @NotNull
    private Role role ;
    @NotBlank
    private String nom;
    @NotBlank
    private String prenom ;
    @NotBlank
    @Pattern(regexp = "^0[6-7]\\d{8}$", message = "Phone number must be 10 digits starting with 06 or 07")
    private String telephone;
    @Column
    private String image ;

}
