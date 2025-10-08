package com.iseem_backend.application.controller;

import com.iseem_backend.application.DTO.request.StudentRequest;
import com.iseem_backend.application.DTO.response.StudentResponse;
import com.iseem_backend.application.model.Diplome;
import com.iseem_backend.application.service.StudentService;
import com.iseem_backend.application.utils.handler.GlobalResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;

@RestController
@RequestMapping("/api/v1/admin/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gestion des étudiants", description = "APIs pour gérer les étudiants, diplômes, import/export et cartes scolaires")
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Ajouter les données d'un étudiant à un utilisateur existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Étudiant ajouté avec succès"),
            @ApiResponse(responseCode = "400", description = "Entrée invalide", content = @Content)
    })
    @PostMapping("/{userId}")
    public ResponseEntity<?> ajouterEtudiant(@PathVariable UUID userId,
                                             @Valid @RequestBody StudentRequest studentRequest) {
        StudentResponse response = studentService.ajouterEtudiant(userId, studentRequest);
        return GlobalResponseHandler.success(response, "Étudiant ajouté avec succès");
    }

    @Operation(summary = "Mettre à jour un étudiant existant")
    @PutMapping("/{id}")
    public ResponseEntity<?> modifierEtudiant(@PathVariable UUID id,
                                              @Valid @RequestBody StudentRequest studentRequest) {
        StudentResponse response = studentService.modifierEtudiant(id, studentRequest);
        return GlobalResponseHandler.success(response, "Étudiant mis à jour avec succès");
    }

    @Operation(summary = "Supprimer un étudiant par ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerEtudiant(@PathVariable UUID id) {
        studentService.supprimerEtudiant(id);
        return GlobalResponseHandler.success(null, "Étudiant supprimé avec succès");
    }

    @Operation(summary = "Consulter le profil d'un étudiant par ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> consulterProfil(@PathVariable UUID id) {
        StudentResponse response = studentService.consulterProfil(id);
        return GlobalResponseHandler.success(response, "Profil de l'étudiant récupéré avec succès");
    }

    @Operation(summary = "Récupérer un étudiant par matricule")
    @GetMapping("/matricule/{matricule}")
    public ResponseEntity<?> obtenirEtudiantParMatricule(@PathVariable String matricule) {
        StudentResponse response = studentService.obtenirEtudiantParMatricule(matricule);
        return GlobalResponseHandler.success(response, "Étudiant récupéré avec succès");
    }

    @Operation(summary = "Récupérer tous les étudiants avec pagination")
    @GetMapping
    public ResponseEntity<?> obtenirTousLesEtudiants(Pageable pageable) {
        Page<StudentResponse> response = studentService.obtenirTousLesEtudiants(pageable);
        return GlobalResponseHandler.success(response, "Étudiants récupérés avec succès");
    }

    @Operation(summary = "Importer des étudiants depuis un fichier Excel")
    @PostMapping("/import")
    public ResponseEntity<?> importerEtudiants(@RequestParam("file") MultipartFile fichierExcel) {
        try {
            Map<String, Object> result = studentService.importerEtudiants(fichierExcel);
            return GlobalResponseHandler.success(result, "Import des étudiants terminé");
        } catch (Exception e) {
            log.error("Erreur lors de l'import des étudiants depuis le fichier Excel", e);
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage() + " - Cause : " +
                    (e.getCause() != null ? e.getCause().getMessage() : "Inconnue"));
        }
    }

    @Operation(summary = "Exporter les étudiants en fichier Excel")
    @GetMapping("/all/export")
    public ResponseEntity<byte[]> exporterEtudiants() {
        byte[] excelData = studentService.exporterEtudiants();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=etudiants.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelData);
    }

    @Operation(summary = "Générer les cartes scolaires des étudiants en PDF")
    @PostMapping("/cards")
    public ResponseEntity<byte[]> genererCartesScolaires(@RequestBody List<UUID> idsEtudiants) {
        byte[] pdfData = studentService.genererCartesScolaires(idsEtudiants);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=cartes_etudiants.pdf")
                .body(pdfData);
    }

    @Operation(summary = "Ajouter un diplôme à un étudiant")
    @PostMapping("/{id}/diplomes")
    public ResponseEntity<?> ajouterDiplome(@PathVariable UUID id, @RequestBody Diplome diplome) {
        studentService.ajouterDiplome(id, diplome);
        return GlobalResponseHandler.success(null, "Diplôme ajouté avec succès");
    }

    @Operation(summary = "Supprimer un diplôme d'un étudiant")
    @DeleteMapping("/{id}/diplomes/{diplomeId}")
    public ResponseEntity<?> supprimerDiplome(@PathVariable UUID id, @PathVariable UUID diplomeId) {
        studentService.supprimerDiplome(id, diplomeId);
        return GlobalResponseHandler.success(null, "Diplôme supprimé avec succès");
    }

    @Operation(summary = "Consulter tous les diplômes d'un étudiant")
    @GetMapping("/{id}/diplomes")
    public ResponseEntity<?> consulterDiplomes(@PathVariable UUID id) {
        List<Diplome> diplomes = studentService.consulterDiplomes(id);
        return GlobalResponseHandler.success(diplomes, "Diplômes récupérés avec succès");
    }

    @Operation(summary = "Rechercher des étudiants par nom, prénom et matricule")
    @GetMapping("/search")
    public ResponseEntity<?> rechercherEtudiants(@RequestParam String nom,
                                                 @RequestParam String prenom,
                                                 @RequestParam String matricule) {
        List<StudentResponse> etudiants = studentService.rechercherEtudiants(nom, prenom, matricule);
        return GlobalResponseHandler.success(etudiants, "Étudiants récupérés avec succès");
    }

    @Operation(summary = "Récupérer la liste des étudiants pour un enseignant")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<?> obtenirEtudiantsPourEnseignant(@PathVariable UUID teacherId) {
        List<StudentResponse> students = studentService.getStudentsForTeacher(teacherId);
        return GlobalResponseHandler.success(students, "Étudiants de l'enseignant récupérés avec succès");
    }
}
