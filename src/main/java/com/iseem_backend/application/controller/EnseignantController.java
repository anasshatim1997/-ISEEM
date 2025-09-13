package com.iseem_backend.application.controller;

import com.iseem_backend.application.DTO.request.EnseignantRequest;
import com.iseem_backend.application.DTO.response.EnseignantResponse;
import com.iseem_backend.application.service.EnseignantService;
import com.iseem_backend.application.utils.handler.GlobalResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/enseignants")
@RequiredArgsConstructor
public class EnseignantController {

    private final EnseignantService enseignantService;

    @PostMapping
    public ResponseEntity<?> ajouter(@RequestBody EnseignantRequest request) {
        EnseignantResponse response = enseignantService.ajouter(request);
        return GlobalResponseHandler.success(response, "Enseignant ajouté");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifier(@PathVariable UUID id, @RequestBody EnseignantRequest request) {
        EnseignantResponse response = enseignantService.modifier(id, request);
        return GlobalResponseHandler.success(response, "Enseignant modifié");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@PathVariable UUID id) {
        enseignantService.supprimer(id);
        return GlobalResponseHandler.success(null, "Enseignant supprimé");
    }

    @PostMapping("/{id}/diplomes/{diplomeId}")
    public ResponseEntity<?> assignerDiplome(@PathVariable UUID id, @PathVariable UUID diplomeId) {
        EnseignantResponse response = enseignantService.assignerDiplome(id, diplomeId);
        return GlobalResponseHandler.success(response, "Diplôme assigné");
    }

    @PostMapping("/{id}/modules/{moduleId}")
    public ResponseEntity<?> assignerModule(@PathVariable UUID id, @PathVariable UUID moduleId) {
        EnseignantResponse response = enseignantService.assignerModule(id, moduleId);
        return GlobalResponseHandler.success(response, "Module assigné");
    }
}
