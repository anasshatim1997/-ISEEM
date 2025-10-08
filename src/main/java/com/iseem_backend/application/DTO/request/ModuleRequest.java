package com.iseem_backend.application.DTO.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleRequest {
    @NotBlank(message = "Le nom du module est obligatoire")
    private String nom;


    @NotNull(message = "Le coefficient est obligatoire")
    @DecimalMin(value = "0.5", message = "Le coefficient doit être supérieur à 0.5")
    private BigDecimal coefficient;

    private String description;

    private Integer heuresTotal;

    private Integer heuresCours;

    private Integer heuresTD;

    private Integer heuresTP;

    private UUID enseignantId;

    private UUID diplomeId;
}