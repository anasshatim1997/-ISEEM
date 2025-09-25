package com.iseem_backend.application.service;

import com.iseem_backend.application.DTO.request.EnseignantRequest;
import com.iseem_backend.application.DTO.response.EnseignantResponse;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EnseignantService {
    EnseignantResponse ajouter(EnseignantRequest request);
    EnseignantResponse modifier(UUID id, EnseignantRequest request);
    void supprimer(UUID id);
    EnseignantResponse obtenirParId(UUID id);
    List<EnseignantResponse> obtenirTous();
    Page<EnseignantResponse> obtenirTousAvecPagination(Pageable pageable);
    List<EnseignantResponse> obtenirParSpecialite(String specialite);
    List<EnseignantResponse> obtenirParStatut(String statut);
    EnseignantResponse assignerDiplome(UUID enseignantId, UUID diplomeId);
    EnseignantResponse assignerModule(UUID enseignantId, UUID moduleId);
    EnseignantResponse retirerDiplome(UUID enseignantId, UUID diplomeId);
    EnseignantResponse retirerModule(UUID enseignantId, UUID moduleId);

}
