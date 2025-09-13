package com.iseem_backend.application.controller;

import com.iseem_backend.application.DTO.request.FormationRequest;
import com.iseem_backend.application.DTO.response.FormationResponse;
import com.iseem_backend.application.service.FormationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/formations")
@RequiredArgsConstructor
@Tag(name = "Formations", description = "Gestion des formations")
public class FormationController {

    private final FormationService formationService;

    @Operation(summary = "Ajouter une formation")
    @PostMapping
    public ResponseEntity<FormationResponse> ajouterFormation(@RequestBody FormationRequest request) {
        FormationResponse response = formationService.ajouterFormation(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Modifier une formation")
    @PutMapping("/{id}")
    public ResponseEntity<FormationResponse> modifierFormation(
            @Parameter(description = "ID de la formation") @PathVariable UUID id,
            @RequestBody FormationRequest request) {
        FormationResponse response = formationService.modifierFormation(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Supprimer une formation")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerFormation(@Parameter(description = "ID de la formation") @PathVariable UUID id) {
        formationService.supprimerFormation(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtenir une formation par ID")
    @GetMapping("/{id}")
    public ResponseEntity<FormationResponse> obtenirFormationParId(@Parameter(description = "ID de la formation") @PathVariable UUID id) {
        FormationResponse response = formationService.obtenirFormationParId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lister toutes les formations avec pagination")
    @GetMapping
    public ResponseEntity<Page<FormationResponse>> obtenirToutesFormations(Pageable pageable) {
        Page<FormationResponse> page = formationService.obtenirToutesFormations(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Assigner des enseignants à une formation")
    @PostMapping("/{id}/assigner-enseignants")
    public ResponseEntity<Void> assignerEnseignants(
            @Parameter(description = "ID de la formation") @PathVariable UUID id,
            @RequestBody List<UUID> enseignantsIds) {
        formationService.assignerEnseignants(id, enseignantsIds);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Exporter toutes les formations en Excel")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exporterFormations() {
        byte[] fileContent = formationService.exporterFormations();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=formations.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileContent);
    }

    @Operation(summary = "Importer des formations depuis un fichier Excel")
    @PostMapping("/import")
    public ResponseEntity<Void> importerFormations(@RequestParam("file") MultipartFile file) {
        formationService.importerFormations(file);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Générer le PDF de l'emploi du temps d'une formation")
    @GetMapping("/{id}/emploi-du-temps")
    public ResponseEntity<byte[]> genererEmploiDuTemps(@Parameter(description = "ID de la formation") @PathVariable UUID id) {
        byte[] pdfContent = formationService.genererEmploiDuTemps(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=emploi_du_temps.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
}
