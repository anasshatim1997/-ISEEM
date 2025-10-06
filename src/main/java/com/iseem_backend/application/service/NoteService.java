package com.iseem_backend.application.service;

import com.iseem_backend.application.DTO.request.BulkNoteRequest;
import com.iseem_backend.application.DTO.request.NoteRequest;
import com.iseem_backend.application.DTO.response.BulletinResponse;
import com.iseem_backend.application.DTO.response.NoteResponse;
import com.iseem_backend.application.enums.TypeNote;

import java.util.List;
import java.util.UUID;

public interface NoteService {
    NoteResponse ajouterNote(NoteRequest request, UUID enseignantId);
    List<NoteResponse> ajouterNotesEnMasse(BulkNoteRequest request, UUID enseignantId);
    NoteResponse modifierNote(UUID noteId, NoteRequest request, UUID enseignantId);
    void supprimerNote(UUID noteId, UUID enseignantId);
    List<NoteResponse> obtenirNotesParModule(UUID moduleId, String anneeScolaire);
    List<NoteResponse> obtenirNotesParEtudiant(UUID studentId, String anneeScolaire);
    List<NoteResponse> obtenirNotesParEnseignant(UUID enseignantId, String anneeScolaire);
    BulletinResponse genererBulletin(UUID studentId, String anneeScolaire, TypeNote typeEvaluation);
    byte[] exporterBulletinPDF(UUID studentId, String anneeScolaire, TypeNote typeEvaluation);
}