package com.iseem_backend.application.controller;

import com.iseem_backend.application.DTO.request.DiplomeRequest;
import com.iseem_backend.application.DTO.response.DiplomeResponse;
import com.iseem_backend.application.enums.TypeDiplome;
import com.iseem_backend.application.service.DiplomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/diplomes")
@RequiredArgsConstructor
@Tag(name = "Gestion des Diplômes", description = "Endpoints pour gérer les diplômes")
public class DiplomeController {

    private final DiplomeService diplomeService;

    @Operation(summary = "Créer un nouveau diplôme")
    @PostMapping
    public ResponseEntity<DiplomeResponse> creerDiplome(@RequestBody DiplomeRequest request) {
        return ResponseEntity.ok(diplomeService.creerDiplome(request));
    }

    @Operation(summary = "Modifier un diplôme existant")
    @PutMapping("/{id}")
    public ResponseEntity<DiplomeResponse> modifierDiplome(@PathVariable UUID id, @RequestBody DiplomeRequest request) {
        return ResponseEntity.ok(diplomeService.modifierDiplome(id, request));
    }

    @Operation(summary = "Valider un diplôme")
    @PostMapping("/{id}/valider")
    public ResponseEntity<Void> validerDiplome(@PathVariable UUID id) {
        diplomeService.validerDiplome(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Signer un diplôme")
    @PostMapping("/{id}/signer/{adminId}")
    public ResponseEntity<Void> signerDiplome(@PathVariable UUID id, @PathVariable UUID adminId) {
        diplomeService.signerDiplome(id, adminId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Supprimer un diplôme")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerDiplome(@PathVariable UUID id) {
        diplomeService.supprimerDiplome(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Obtenir un diplôme par son ID")
    @GetMapping("/{id}")
    public ResponseEntity<DiplomeResponse> obtenirDiplomeParId(@PathVariable UUID id) {
        return ResponseEntity.ok(diplomeService.obtenirDiplomeParId(id));
    }

    @Operation(summary = "Obtenir tous les diplômes")
    @GetMapping
    public ResponseEntity<List<DiplomeResponse>> obtenirTousDiplomes() {
        return ResponseEntity.ok(diplomeService.obtenirTousDiplomes());
    }

    @Operation(summary = "Filtrer les diplômes par type")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<DiplomeResponse>> filtrerDiplomesParType(@PathVariable TypeDiplome type) {
        return ResponseEntity.ok(diplomeService.filtrerDiplomesParType(type));
    }

    @Operation(summary = "Exporter tous les diplômes en fichier Excel")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exporterDiplomes() {
        byte[] data = diplomeService.exporterDiplomes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=diplomes.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @Operation(summary = "Importer des diplômes depuis un fichier Excel")
    @PostMapping("/import")
    public ResponseEntity<Void> importerDiplomes(@RequestParam("file") MultipartFile file) {
        diplomeService.importerDiplomes(file);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Générer un code QR pour un diplôme")
    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> genererQRCode(@PathVariable UUID id) {
        byte[] data = diplomeService.genererQRCode(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=qrcode.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(data);
    }

    @Operation(summary = "Générer un PDF pour un diplôme")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> genererPDF(@PathVariable UUID id) {
        byte[] data = diplomeService.genererPDF(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=diplome.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}
