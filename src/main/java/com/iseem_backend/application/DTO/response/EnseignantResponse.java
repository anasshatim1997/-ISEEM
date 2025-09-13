package com.iseem_backend.application.DTO.response;

import com.iseem_backend.application.enums.StatusEnseignant;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EnseignantResponse {
    private UUID enseignantId;
    private UUID userId;
    private String specialite;
    private LocalDate dateEmbauche;
    private StatusEnseignant statusEnseignant;
    private Duration heuresTravail;
    private HoraireResponse horaire;
    private Set<UUID> moduleIds;
    private Set<UUID> diplomeIds;
    private Set<CustomFieldResponse> customFields;
}