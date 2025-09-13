package com.iseem_backend.application.service.impl;

import com.iseem_backend.application.DTO.request.FormationRequest;
import com.iseem_backend.application.DTO.response.FormationResponse;
import com.iseem_backend.application.exceptions.EnseignantNotFoundException;
import com.iseem_backend.application.exceptions.FormationNotFoundException;
import com.iseem_backend.application.mapper.FormationMapper;
import com.iseem_backend.application.model.Enseignant;
import com.iseem_backend.application.model.EmploiDuTemps;
import com.iseem_backend.application.model.Formation;
import com.iseem_backend.application.repository.EnseignantRepository;
import com.iseem_backend.application.repository.FormationRepository;
import com.iseem_backend.application.service.FormationService;
import com.iseem_backend.application.utils.EmploiDuTempsGenerator;
import com.iseem_backend.application.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FormationServiceImpl implements FormationService {

    private final FormationRepository formationRepository;
    private final EnseignantRepository enseignantRepository;
    private final FormationMapper formationMapper;

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public FormationResponse ajouterFormation(FormationRequest request) {
        Formation formation = formationMapper.toEntity(request);

        if (request.getEnseignantsIds() != null && !request.getEnseignantsIds().isEmpty()) {
            Set<Enseignant> enseignants = request.getEnseignantsIds().stream()
                    .map(id -> enseignantRepository.findById(id)
                            .orElseThrow(() -> new EnseignantNotFoundException(id)))
                    .limit(2)
                    .collect(Collectors.toSet());
            formation.setEnseignants(enseignants);
        }

        formation = formationRepository.save(formation);
        return formationMapper.toDto(formation);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public FormationResponse modifierFormation(UUID idFormation, FormationRequest request) {
        Formation formation = formationRepository.findById(idFormation)
                .orElseThrow(() -> new FormationNotFoundException(idFormation));

        formationMapper.updateEntityFromRequest(request, formation);

        if (request.getEnseignantsIds() != null && !request.getEnseignantsIds().isEmpty()) {
            Set<Enseignant> enseignants = request.getEnseignantsIds().stream()
                    .map(id -> enseignantRepository.findById(id)
                            .orElseThrow(() -> new EnseignantNotFoundException(id)))
                    .limit(2)
                    .collect(Collectors.toSet());
            formation.setEnseignants(enseignants);
        }

        formation = formationRepository.save(formation);
        return formationMapper.toDto(formation);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public void supprimerFormation(UUID idFormation) {
        Formation formation = formationRepository.findById(idFormation)
                .orElseThrow(() -> new FormationNotFoundException(idFormation));
        formationRepository.delete(formation);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION') or hasRole('ENSEIGNANT')")
    @Transactional(readOnly = true)
    public FormationResponse obtenirFormationParId(UUID idFormation) {
        Formation formation = formationRepository.findById(idFormation)
                .orElseThrow(() -> new FormationNotFoundException(idFormation));
        return formationMapper.toDto(formation);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION') or hasRole('ENSEIGNANT')")
    @Transactional(readOnly = true)
    public Page<FormationResponse> obtenirToutesFormations(Pageable pageable) {
        return formationRepository.findAll(pageable)
                .map(formationMapper::toDto);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public void assignerEnseignants(UUID idFormation, List<UUID> enseignantsIds) {
        Formation formation = formationRepository.findById(idFormation)
                .orElseThrow(() -> new FormationNotFoundException(idFormation));

        Set<Enseignant> enseignants = enseignantsIds.stream()
                .map(id -> enseignantRepository.findById(id)
                        .orElseThrow(() -> new EnseignantNotFoundException(id)))
                .limit(2)
                .collect(Collectors.toSet());

        formation.setEnseignants(enseignants);
        formationRepository.save(formation);
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public byte[] exporterFormations() {
        try {
            List<Formation> formations = formationRepository.findAll();
            return ExcelUtils.exportFormations(formations);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'export des formations", e);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public void importerFormations(MultipartFile fichierExcel) {
        try {
            List<Formation> formations = ExcelUtils.importFormations(fichierExcel);
            formationRepository.saveAll(formations);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'import des formations", e);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMINISTRATION') or hasRole('ENSEIGNANT')")
    @Transactional(readOnly = true)
    public byte[] genererEmploiDuTemps(UUID idFormation) {
        Formation formation = formationRepository.findById(idFormation)
                .orElseThrow(() -> new FormationNotFoundException(idFormation));
        EmploiDuTemps emploi = formation.getEmploiDuTemps();
        return EmploiDuTempsGenerator.generatePDF(emploi);
    }
}
