package com.iseem_backend.application.service;

import com.iseem_backend.application.DTO.request.EnseignantRequest;
import com.iseem_backend.application.DTO.response.EnseignantResponse;

import java.util.UUID;

public interface EnseignantService {
    EnseignantResponse ajouter(EnseignantRequest request);
    EnseignantResponse modifier(UUID enseignantId, EnseignantRequest request);
    void supprimer(UUID enseignantId);
    EnseignantResponse assignerDiplome(UUID enseignantId, UUID diplomeId);
    EnseignantResponse assignerModule(UUID enseignantId, UUID moduleId);

}
