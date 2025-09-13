package com.iseem_backend.application.DTO.response;

import com.iseem_backend.application.enums.Role;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    UUID idUser ;
    String email ;
    Role role ;
    String nom;
    String prenom ;
    String telephone ;
    String image ;
}
