package com.iseem_backend.application.controller;

import com.iseem_backend.application.DTO.request.EnseignantRequest;
import com.iseem_backend.application.DTO.response.EnseignantResponse;
import com.iseem_backend.application.service.EnseignantService;
import com.iseem_backend.application.utils.handler.GlobalResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/enseignants")
@RequiredArgsConstructor
@Tag(name = "Gestion des Enseignants", description = "Endpoints pour gérer les enseignants")
public class EnseignantController {

    private final EnseignantService enseignantService;

    @Operation(summary = "Ajouter un enseignant", description = "Créer un nouveau profil enseignant avec les informations personnelles et professionnelles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enseignant créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    @PostMapping
    public ResponseEntity<?> ajouter(@RequestBody EnseignantRequest request) {
        EnseignantResponse response = enseignantService.ajouter(request);
        return GlobalResponseHandler.success(response, "Enseignant ajouté");
    }

    @Operation(summary = "Modifier un enseignant", description = "Mettre à jour les informations d'un enseignant existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enseignant modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Enseignant introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> modifier(@Parameter(description = "ID de l'enseignant") @PathVariable UUID id,
                                      @RequestBody EnseignantRequest request) {
        EnseignantResponse response = enseignantService.modifier(id, request);
        return GlobalResponseHandler.success(response, "Enseignant modifié");
    }

    @Operation(summary = "Supprimer un enseignant", description = "Supprimer définitivement un enseignant du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enseignant supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Enseignant introuvable")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@Parameter(description = "ID de l'enseignant") @PathVariable UUID id) {
        enseignantService.supprimer(id);
        return GlobalResponseHandler.success(null, "Enseignant supprimé");
    }

    @Operation(summary = "Obtenir un enseignant par ID", description = "Récupérer les détails complets d'un enseignant spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enseignant trouvé"),
            @ApiResponse(responseCode = "404", description = "Enseignant introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenirParId(@Parameter(description = "ID de l'enseignant") @PathVariable UUID id) {
        EnseignantResponse response = enseignantService.obtenirParId(id);
        return GlobalResponseHandler.success(response, "Enseignant récupéré");
    }

    @Operation(summary = "Lister tous les enseignants", description = "Récupérer la liste complète de tous les enseignants")
    @ApiResponse(responseCode = "200", description = "Liste des enseignants récupérée")
    @GetMapping
    public ResponseEntity<?> obtenirTous() {
        List<EnseignantResponse> response = enseignantService.obtenirTous();
        return GlobalResponseHandler.success(response, "Liste des enseignants récupérée");
    }

    @Operation(summary = "Lister les enseignants avec pagination", description = "Récupérer les enseignants par pages avec tri et filtrage")
    @ApiResponse(responseCode = "200", description = "Page d'enseignants récupérée")
    @GetMapping("/paginated")
    public ResponseEntity<?> obtenirTousAvecPagination(Pageable pageable) {
        Page<EnseignantResponse> response = enseignantService.obtenirTousAvecPagination(pageable);
        return GlobalResponseHandler.success(response, "Page d'enseignants récupérée");
    }

    @Operation(summary = "Filtrer par spécialité", description = "Récupérer les enseignants d'une spécialité donnée")
    @ApiResponse(responseCode = "200", description = "Enseignants filtrés par spécialité")
    @GetMapping("/specialite/{specialite}")
    public ResponseEntity<?> obtenirParSpecialite(@Parameter(description = "Spécialité recherchée") @PathVariable String specialite) {
        List<EnseignantResponse> response = enseignantService.obtenirParSpecialite(specialite);
        return GlobalResponseHandler.success(response, "Enseignants filtrés par spécialité");
    }

    @Operation(summary = "Filtrer par statut", description = "Récupérer les enseignants selon leur statut professionnel")
    @ApiResponse(responseCode = "200", description = "Enseignants filtrés par statut")
    @GetMapping("/statut/{statut}")
    public ResponseEntity<?> obtenirParStatut(@Parameter(description = "Statut recherché (ACTIF, INACTIF, CONGE)") @PathVariable String statut) {
        List<EnseignantResponse> response = enseignantService.obtenirParStatut(statut);
        return GlobalResponseHandler.success(response, "Enseignants filtrés par statut");
    }

    @Operation(summary = "Assigner un diplôme", description = "Associer un diplôme à un enseignant pour certifier sa qualification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diplôme assigné avec succès"),
            @ApiResponse(responseCode = "404", description = "Enseignant ou diplôme introuvable"),
            @ApiResponse(responseCode = "409", description = "Diplôme déjà assigné")
    })
    @PostMapping("/{id}/diplomes/{diplomeId}")
    public ResponseEntity<?> assignerDiplome(
            @Parameter(description = "ID de l'enseignant") @PathVariable UUID id,
            @Parameter(description = "ID du diplôme") @PathVariable UUID diplomeId) {
        EnseignantResponse response = enseignantService.assignerDiplome(id, diplomeId);
        return GlobalResponseHandler.success(response, "Diplôme assigné");
    }

    @Operation(summary = "Assigner un module", description = "Affecter un module d'enseignement à un enseignant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Module assigné avec succès"),
            @ApiResponse(responseCode = "404", description = "Enseignant ou module introuvable"),
            @ApiResponse(responseCode = "409", description = "Module déjà assigné")
    })
    @PostMapping("/{id}/modules/{moduleId}")
    public ResponseEntity<?> assignerModule(
            @Parameter(description = "ID de l'enseignant") @PathVariable UUID id,
            @Parameter(description = "ID du module") @PathVariable UUID moduleId) {
        EnseignantResponse response = enseignantService.assignerModule(id, moduleId);
        return GlobalResponseHandler.success(response, "Module assigné");
    }

    @Operation(summary = "Retirer un diplôme", description = "Supprimer l'association entre un enseignant et un diplôme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diplôme retiré avec succès"),
            @ApiResponse(responseCode = "404", description = "Enseignant ou diplôme introuvable")
    })
    @DeleteMapping("/{id}/diplomes/{diplomeId}")
    public ResponseEntity<?> retirerDiplome(
            @Parameter(description = "ID de l'enseignant") @PathVariable UUID id,
            @Parameter(description = "ID du diplôme") @PathVariable UUID diplomeId) {
        EnseignantResponse response = enseignantService.retirerDiplome(id, diplomeId);
        return GlobalResponseHandler.success(response, "Diplôme retiré");
    }

    @Operation(summary = "Retirer un module", description = "Supprimer l'affectation d'un module à un enseignant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Module retiré avec succès"),
            @ApiResponse(responseCode = "404", description = "Enseignant ou module introuvable")
    })
    @DeleteMapping("/{id}/modules/{moduleId}")
    public ResponseEntity<?> retirerModule(
            @Parameter(description = "ID de l'enseignant") @PathVariable UUID id,
            @Parameter(description = "ID du module") @PathVariable UUID moduleId) {
        EnseignantResponse response = enseignantService.retirerModule(id, moduleId);
        return GlobalResponseHandler.success(response, "Module retiré");
    }
}