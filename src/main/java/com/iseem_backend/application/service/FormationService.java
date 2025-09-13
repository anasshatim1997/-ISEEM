package com.iseem_backend.application.service;

import com.iseem_backend.application.DTO.request.FormationRequest;
import com.iseem_backend.application.DTO.response.FormationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FormationService {

    FormationResponse ajouterFormation(FormationRequest request);

    FormationResponse modifierFormation(UUID idFormation, FormationRequest request);

    void supprimerFormation(UUID idFormation);

    FormationResponse obtenirFormationParId(UUID idFormation);

    Page<FormationResponse> obtenirToutesFormations(Pageable pageable);

    void assignerEnseignants(UUID idFormation, List<UUID> enseignantsIds);

    byte[] exporterFormations();

    void importerFormations(MultipartFile fichierExcel);

    byte[] genererEmploiDuTemps(UUID idFormation);
}
