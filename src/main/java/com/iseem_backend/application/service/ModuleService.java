package com.iseem_backend.application.service;

import com.iseem_backend.application.DTO.request.ModuleRequest;
import com.iseem_backend.application.DTO.response.ModuleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ModuleService {
    ModuleResponse creerModule(ModuleRequest request);
    ModuleResponse modifierModule(UUID id, ModuleRequest request);
    void supprimerModule(UUID id);
    ModuleResponse obtenirModuleParId(UUID id);
    List<ModuleResponse> obtenirTousLesModules();
    Page<ModuleResponse> obtenirModulesAvecPagination(Pageable pageable);
    List<ModuleResponse> rechercherModules(String nom);
    List<ModuleResponse> obtenirModulesParEnseignant(UUID enseignantId);
    List<ModuleResponse> obtenirModulesParDiplome(UUID diplomeId);
    ModuleResponse assignerEnseignant(UUID moduleId, UUID enseignantId);
    ModuleResponse retirerEnseignant(UUID moduleId);
    ModuleResponse assignerEtudiant(UUID moduleId, UUID studentId);
    ModuleResponse retirerEtudiant(UUID moduleId, UUID studentId);
    List<ModuleResponse> obtenirModulesSansEnseignant();
    long compterEtudiantsParModule(UUID moduleId);
}