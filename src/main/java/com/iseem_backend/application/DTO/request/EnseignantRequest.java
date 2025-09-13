package com.iseem_backend.application.DTO.request;

import com.iseem_backend.application.enums.StatusEnseignant;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
public class EnseignantRequest {
    private UUID userId;
    private String specialite;
    private LocalDate dateEmbauche;
    private StatusEnseignant statusEnseignant;
    private Duration heuresTravail;
    private HoraireRequest horaire;
    private Set<UUID> moduleIds;
    private Set<UUID> diplomeIds;
    private Set<CustomFieldRequest> customFields;
}
