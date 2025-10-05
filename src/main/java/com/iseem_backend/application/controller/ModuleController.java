package com.iseem_backend.application.controller;

import com.iseem_backend.application.DTO.request.ModuleRequest;
import com.iseem_backend.application.DTO.response.ModuleResponse;
import com.iseem_backend.application.service.ModuleService;
import com.iseem_backend.application.utils.handler.GlobalResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/modules")
@RequiredArgsConstructor
@Tag(name = "Gestion des Modules", description = "Endpoints pour gérer les modules d'enseignement")
public class ModuleController {

    private final ModuleService moduleService;

    @Operation(summary = "Créer un module", description = "Créer un nouveau module d'enseignement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Module créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Module déjà existant")
    })
    @PostMapping
    public ResponseEntity<?> creerModule(@Valid @RequestBody ModuleRequest request) {
        ModuleResponse response = moduleService.creerModule(request);
        return GlobalResponseHandler.success(response, "Module créé avec succès");
    }

    @Operation(summary = "Modifier un module", description = "Mettre à jour les informations d'un module")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Module modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Module introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> modifierModule(@Parameter(description = "ID du module") @PathVariable UUID id,
                                            @Valid @RequestBody ModuleRequest request) {
        ModuleResponse response = moduleService.modifierModule(id, request);
        return GlobalResponseHandler.success(response, "Module modifié avec succès");
    }

    @Operation(summary = "Supprimer un module", description = "Supprimer un module du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Module supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Module introuvable")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerModule(@Parameter(description = "ID du module") @PathVariable UUID id) {
        moduleService.supprimerModule(id);
        return GlobalResponseHandler.success(null, "Module supprimé avec succès");
    }

    @Operation(summary = "Obtenir un module", description = "Récupérer les détails d'un module par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Module trouvé"),
            @ApiResponse(responseCode = "404", description = "Module introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenirModule(@Parameter(description = "ID du module") @PathVariable UUID id) {
        ModuleResponse response = moduleService.obtenirModuleParId(id);
        return GlobalResponseHandler.success(response, "Module récupéré avec succès");
    }

    @Operation(summary = "Lister tous les modules", description = "Récupérer la liste complète des modules")
    @ApiResponse(responseCode = "200", description = "Liste des modules récupérée")
    @GetMapping
    public ResponseEntity<?> obtenirTousLesModules() {
        List<ModuleResponse> response = moduleService.obtenirTousLesModules();
        return GlobalResponseHandler.success(response, "Liste des modules récupérée");
    }

    @Operation(summary = "Modules avec pagination", description = "Récupérer les modules avec pagination et tri")
    @ApiResponse(responseCode = "200", description = "Page de modules récupérée")
    @GetMapping("/paginated")
    public ResponseEntity<?> obtenirModulesAvecPagination(Pageable pageable) {
        Page<ModuleResponse> response = moduleService.obtenirModulesAvecPagination(pageable);
        return GlobalResponseHandler.success(response, "Page de modules récupérée");
    }

    @Operation(summary = "Rechercher des modules", description = "Rechercher des modules par nom")
    @ApiResponse(responseCode = "200", description = "Modules trouvés")
    @GetMapping("/recherche")
    public ResponseEntity<?> rechercherModules(@Parameter(description = "Nom à rechercher") @RequestParam String nom) {
        List<ModuleResponse> response = moduleService.rechercherModules(nom);
        return GlobalResponseHandler.success(response, "Modules trouvés");
    }

    @Operation(summary = "Modules par enseignant", description = "Récupérer tous les modules d'un enseignant")
    @ApiResponse(responseCode = "200", description = "Modules de l'enseignant récupérés")
    @GetMapping("/enseignant/{enseignantId}")
    public ResponseEntity<?> obtenirModulesParEnseignant(@Parameter(description = "ID de l'enseignant") @PathVariable UUID enseignantId) {
        List<ModuleResponse> response = moduleService.obtenirModulesParEnseignant(enseignantId);
        return GlobalResponseHandler.success(response, "Modules de l'enseignant récupérés");
    }

    @Operation(summary = "Modules par diplôme", description = "Récupérer tous les modules d'un diplôme")
    @ApiResponse(responseCode = "200", description = "Modules du diplôme récupérés")
    @GetMapping("/diplome/{diplomeId}")
    public ResponseEntity<?> obtenirModulesParDiplome(@Parameter(description = "ID du diplôme") @PathVariable UUID diplomeId) {
        List<ModuleResponse> response = moduleService.obtenirModulesParDiplome(diplomeId);
        return GlobalResponseHandler.success(response, "Modules du diplôme récupérés");
    }

    @Operation(summary = "Assigner enseignant", description = "Assigner un enseignant à un module")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enseignant assigné avec succès"),
            @ApiResponse(responseCode = "404", description = "Module ou enseignant introuvable")
    })
    @PostMapping("/{moduleId}/enseignant/{enseignantId}")
    public ResponseEntity<?> assignerEnseignant(@Parameter(description = "ID du module") @PathVariable UUID moduleId,
                                                @Parameter(description = "ID de l'enseignant") @PathVariable UUID enseignantId) {
        ModuleResponse response = moduleService.assignerEnseignant(moduleId, enseignantId);
        return GlobalResponseHandler.success(response, "Enseignant assigné avec succès");
    }

    @Operation(summary = "Retirer enseignant", description = "Retirer l'enseignant d'un module")
    @ApiResponse(responseCode = "200", description = "Enseignant retiré avec succès")
    @DeleteMapping("/{moduleId}/enseignant")
    public ResponseEntity<?> retirerEnseignant(@Parameter(description = "ID du module") @PathVariable UUID moduleId) {
        ModuleResponse response = moduleService.retirerEnseignant(moduleId);
        return GlobalResponseHandler.success(response, "Enseignant retiré avec succès");
    }

    @Operation(summary = "Assigner étudiant", description = "Assigner un étudiant à un module")
    @ApiResponse(responseCode = "200", description = "Étudiant assigné avec succès")
    @PostMapping("/{moduleId}/etudiant/{studentId}")
    public ResponseEntity<?> assignerEtudiant(@Parameter(description = "ID du module") @PathVariable UUID moduleId,
                                              @Parameter(description = "ID de l'étudiant") @PathVariable UUID studentId) {
        ModuleResponse response = moduleService.assignerEtudiant(moduleId, studentId);
        return GlobalResponseHandler.success(response, "Étudiant assigné avec succès");
    }

    @Operation(summary = "Retirer étudiant", description = "Retirer un étudiant d'un module")
    @ApiResponse(responseCode = "200", description = "Étudiant retiré avec succès")
    @DeleteMapping("/{moduleId}/etudiant/{studentId}")
    public ResponseEntity<?> retirerEtudiant(@Parameter(description = "ID du module") @PathVariable UUID moduleId,
                                             @Parameter(description = "ID de l'étudiant") @PathVariable UUID studentId) {
        ModuleResponse response = moduleService.retirerEtudiant(moduleId, studentId);
        return GlobalResponseHandler.success(response, "Étudiant retiré avec succès");
    }

    @Operation(summary = "Modules sans enseignant", description = "Lister les modules qui n'ont pas d'enseignant assigné")
    @ApiResponse(responseCode = "200", description = "Modules sans enseignant récupérés")
    @GetMapping("/sans-enseignant")
    public ResponseEntity<?> obtenirModulesSansEnseignant() {
        List<ModuleResponse> response = moduleService.obtenirModulesSansEnseignant();
        return GlobalResponseHandler.success(response, "Modules sans enseignant récupérés");
    }

    @Operation(summary = "Compter étudiants", description = "Compter le nombre d'étudiants inscrits dans un module")
    @ApiResponse(responseCode = "200", description = "Nombre d'étudiants récupéré")
    @GetMapping("/{moduleId}/etudiants/count")
    public ResponseEntity<?> compterEtudiants(@Parameter(description = "ID du module") @PathVariable UUID moduleId) {
        long count = moduleService.compterEtudiantsParModule(moduleId);
        return GlobalResponseHandler.success(count, "Nombre d'étudiants récupéré");
    }
}