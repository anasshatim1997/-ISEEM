package com.iseem_backend.application.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandeAuthentification {
    private String email;
    private String motDePasse;
}