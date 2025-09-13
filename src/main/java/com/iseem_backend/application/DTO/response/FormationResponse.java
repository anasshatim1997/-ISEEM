package com.iseem_backend.application.DTO.response;

import com.iseem_backend.application.enums.ModeFormation;
import lombok.*;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormationResponse {

    private UUID idFormation;
    private String nom;
    private Integer duree;
    private BigDecimal cout;
    private Set<UUID> enseignantsIds;
    private String description;
    private Integer anneeFormation;
    private Boolean estActive;
    private ModeFormation modeFormation;
    private String niveauAcces;
    private Integer capaciteMax;
    private UUID emploiDuTempsId;
}
