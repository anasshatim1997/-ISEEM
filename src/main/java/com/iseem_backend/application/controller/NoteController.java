package com.iseem_backend.application.controller;

import com.iseem_backend.application.DTO.request.BulkNoteRequest;
import com.iseem_backend.application.DTO.request.NoteRequest;
import com.iseem_backend.application.DTO.response.BulletinResponse;
import com.iseem_backend.application.DTO.response.NoteResponse;
import com.iseem_backend.application.enums.TypeNote;
import com.iseem_backend.application.service.NoteService;
import com.iseem_backend.application.utils.handler.GlobalResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/notes")
@RequiredArgsConstructor
@Tag(name = "Administration des Notes", description = "Endpoints d'administration pour la gestion des notes")
public class NoteController {

    private final NoteService noteService;

    @Operation(summary = "Ajouter une note", description = "Ajouter une note pour un étudiant dans un module")
    @PostMapping
    public ResponseEntity<?> ajouterNote(@RequestBody NoteRequest request) {
        NoteResponse response = noteService.ajouterNote(request, request.getEnseignantId());
        return GlobalResponseHandler.success(response, "Note ajoutée avec succès");
    }

    @Operation(summary = "Ajouter des notes en masse", description = "Ajouter plusieurs notes en une seule requête")
    @PostMapping("/bulk")
    public ResponseEntity<?> ajouterNotesEnMasse(@RequestBody BulkNoteRequest request) {
        List<NoteResponse> responses = noteService.ajouterNotesEnMasse(request, request.getEnseignantId());
        return GlobalResponseHandler.success(responses, "Notes ajoutées en masse");
    }

    @Operation(summary = "Modifier une note", description = "Modifier une note existante")
    @PutMapping("/{noteId}")
    public ResponseEntity<?> modifierNote(@Parameter(description = "ID de la note") @PathVariable UUID noteId,
                                          @RequestBody NoteRequest request) {
        NoteResponse response = noteService.modifierNote(noteId, request, request.getEnseignantId());
        return GlobalResponseHandler.success(response, "Note modifiée avec succès");
    }

    @Operation(summary = "Supprimer une note", description = "Supprimer une note existante")
    @DeleteMapping("/{noteId}")
    public ResponseEntity<?> supprimerNote(@Parameter(description = "ID de la note") @PathVariable UUID noteId,
                                           @RequestParam UUID enseignantId) {
        noteService.supprimerNote(noteId, enseignantId);
        return GlobalResponseHandler.success(null, "Note supprimée avec succès");
    }

    @Operation(summary = "Toutes les notes d'un étudiant", description = "Récupérer toutes les notes d'un étudiant (vue admin)")
    @GetMapping("/etudiant/{studentId}")
    public ResponseEntity<?> getNotesEtudiant(@Parameter(description = "ID de l'étudiant") @PathVariable UUID studentId,
                                              @Parameter(description = "Année scolaire") @RequestParam String anneeScolaire) {
        List<NoteResponse> responses = noteService.obtenirNotesParEtudiant(studentId, anneeScolaire);
        return GlobalResponseHandler.success(responses, "Notes de l'étudiant récupérées");
    }

    @Operation(summary = "Toutes les notes d'un module", description = "Récupérer toutes les notes d'un module (vue admin)")
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<?> getNotesModule(@Parameter(description = "ID du module") @PathVariable UUID moduleId,
                                            @Parameter(description = "Année scolaire") @RequestParam String anneeScolaire) {
        List<NoteResponse> responses = noteService.obtenirNotesParModule(moduleId, anneeScolaire);
        return GlobalResponseHandler.success(responses, "Notes du module récupérées");
    }

    @Operation(summary = "Toutes les notes d'un enseignant", description = "Récupérer toutes les notes saisies par un enseignant")
    @GetMapping("/enseignant/{enseignantId}")
    public ResponseEntity<?> getNotesEnseignant(@Parameter(description = "ID de l'enseignant") @PathVariable UUID enseignantId,
                                                @Parameter(description = "Année scolaire") @RequestParam String anneeScolaire) {
        List<NoteResponse> responses = noteService.obtenirNotesParEnseignant(enseignantId, anneeScolaire);
        return GlobalResponseHandler.success(responses, "Notes de l'enseignant récupérées");
    }

    @Operation(summary = "Bulletin officiel", description = "Générer le bulletin officiel d'un étudiant")
    @GetMapping("/bulletin/{studentId}")
    public ResponseEntity<?> getBulletinOfficiel(@Parameter(description = "ID de l'étudiant") @PathVariable UUID studentId,
                                                 @Parameter(description = "Année scolaire") @RequestParam String anneeScolaire,
                                                 @Parameter(description = "Type d'évaluation") @RequestParam TypeNote typeEvaluation) {
        BulletinResponse response = noteService.genererBulletin(studentId, anneeScolaire, typeEvaluation);
        return GlobalResponseHandler.success(response, "Bulletin officiel généré");
    }

    @Operation(summary = "Exporter bulletin officiel PDF", description = "Exporter le bulletin officiel au format PDF")
    @GetMapping("/bulletin/{studentId}/pdf")
    public ResponseEntity<byte[]> exportBulletinOfficielPDF(@Parameter(description = "ID de l'étudiant") @PathVariable UUID studentId,
                                                            @Parameter(description = "Année scolaire") @RequestParam String anneeScolaire,
                                                            @Parameter(description = "Type d'évaluation") @RequestParam TypeNote typeEvaluation) {
        byte[] pdfContent = noteService.exporterBulletinPDF(studentId, anneeScolaire, typeEvaluation);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "bulletin_officiel_" + studentId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }
}