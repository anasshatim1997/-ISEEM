package com.iseem_backend.application.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReponseAuthentification {
    private String token;
    private String email;
    private String role;
    private String userId;
}