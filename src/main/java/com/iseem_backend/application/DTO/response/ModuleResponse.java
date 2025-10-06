package com.iseem_backend.application.DTO.response;


import lombok.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleResponse {
    private UUID idModule;
    private String nom;
    private String moduleName;
    private BigDecimal coefficient;
    private String description;
    private Integer heuresTotal;
    private Integer heuresCours;
    private Integer heuresTD;
    private Integer heuresTP;
    private BigDecimal note;
    private UUID enseignantId;
    private String enseignantNom;
    private String enseignantPrenom;
    private UUID diplomeId;
    private String diplomeNom;
    private Set<UUID> studentIds;
    private Integer nombreEtudiants;
}
