package com.iseem_backend.application.DTO.request;

import com.iseem_backend.application.enums.ModeFormation;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormationRequest {

    @NotNull
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
}
