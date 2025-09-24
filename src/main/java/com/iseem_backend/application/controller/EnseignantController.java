package com.iseem_backend.application.controller;

import com.iseem_backend.application.DTO.request.EnseignantRequest;
import com.iseem_backend.application.DTO.response.EnseignantResponse;
import com.iseem_backend.application.service.EnseignantService;
import com.iseem_backend.application.utils.handler.GlobalResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/enseignants")
@RequiredArgsConstructor
@Tag(name = "Gestion des Enseignants", description = "Endpoints pour gérer les enseignants")
public class EnseignantController {

    private final EnseignantService enseignantService;

    @Operation(summary = "Ajouter un enseignant")
    @PostMapping
    public ResponseEntity<?> ajouter(@RequestBody EnseignantRequest request) {
        EnseignantResponse response = enseignantService.ajouter(request);
        return GlobalResponseHandler.success(response, "Enseignant ajouté");
    }

    @Operation(summary = "Modifier un enseignant")
    @PutMapping("/{id}")
    public ResponseEntity<?> modifier(@Parameter(description = "ID de l'enseignant") @PathVariable UUID id,
                                      @RequestBody EnseignantRequest request) {
        EnseignantResponse response = enseignantService.modifier(id, request);
        return GlobalResponseHandler.success(response, "Enseignant modifié");
    }

    @Operation(summary = "Supprimer un enseignant")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@Parameter(description = "ID de l'enseignant") @PathVariable UUID id) {
        enseignantService.supprimer(id);
        return GlobalResponseHandler.success(null, "Enseignant supprimé");
    }

    @Operation(summary = "Assigner un diplôme à un enseignant")
    @PostMapping("/{id}/diplomes/{diplomeId}")
    public ResponseEntity<?> assignerDiplome(
            @Parameter(description = "ID de l'enseignant") @PathVariable UUID id,
            @Parameter(description = "ID du diplôme") @PathVariable UUID diplomeId) {
        EnseignantResponse response = enseignantService.assignerDiplome(id, diplomeId);
        return GlobalResponseHandler.success(response, "Diplôme assigné");
    }

    @Operation(summary = "Assigner un module à un enseignant")
    @PostMapping("/{id}/modules/{moduleId}")
    public ResponseEntity<?> assignerModule(
            @Parameter(description = "ID de l'enseignant") @PathVariable UUID id,
            @Parameter(description = "ID du module") @PathVariable UUID moduleId) {
        EnseignantResponse response = enseignantService.assignerModule(id, moduleId);
        return GlobalResponseHandler.success(response, "Module assigné");
    }
}
