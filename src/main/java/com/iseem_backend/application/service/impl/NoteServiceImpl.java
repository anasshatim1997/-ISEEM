package com.iseem_backend.application.service.impl;

import com.iseem_backend.application.DTO.request.BulkNoteRequest;
import com.iseem_backend.application.DTO.request.NoteRequest;
import com.iseem_backend.application.DTO.response.BulletinResponse;
import com.iseem_backend.application.DTO.response.NoteModuleResponse;
import com.iseem_backend.application.DTO.response.NoteResponse;
import com.iseem_backend.application.enums.TypeNote;
import com.iseem_backend.application.model.*;
import com.iseem_backend.application.model.Module;
import com.iseem_backend.application.repository.*;
import com.iseem_backend.application.service.NoteService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final StudentRepository studentRepository;
    private final ModuleRepository moduleRepository;
    private final EnseignantRepository enseignantRepository;

    @Override
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ADMINISTRATION')")
    public NoteResponse ajouterNote(NoteRequest request, UUID enseignantId) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));

        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module introuvable"));

        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));

        if (!module.getEnseignant().equals(enseignant)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à noter ce module");
        }

        Optional<Note> existingNote = noteRepository.findByStudentAndModuleAndTypeAndAnnee(
                request.getStudentId(), request.getModuleId(), request.getTypeNote(), request.getAnneeScolaire());

        if (existingNote.isPresent()) {
            throw new RuntimeException("Une note existe déjà pour cet étudiant dans ce module pour ce type d'évaluation");
        }

        Note note = Note.builder()
                .student(student)
                .module(module)
                .typeNote(request.getTypeNote())
                .valeur(request.getValeur())
                .anneeScolaire(request.getAnneeScolaire())
                .saisiePar(enseignant.getUser())
                .build();

        Note savedNote = noteRepository.save(note);
        return mapToResponse(savedNote);
    }

    @Override
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ADMINISTRATION')")
    public List<NoteResponse> ajouterNotesEnMasse(BulkNoteRequest request, UUID enseignantId) {
        List<NoteResponse> responses = new ArrayList<>();

        for (NoteRequest noteRequest : request.getNotes()) {
            try {
                NoteResponse response = ajouterNote(noteRequest, enseignantId);
                responses.add(response);
            } catch (Exception e) {
                log.error("Erreur lors de l'ajout de la note pour l'étudiant {}: {}",
                        noteRequest.getStudentId(), e.getMessage());
            }
        }

        return responses;
    }

    @Override
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ADMINISTRATION')")
    public NoteResponse modifierNote(UUID noteId, NoteRequest request, UUID enseignantId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note introuvable"));

        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));

        if (!note.getModule().getEnseignant().equals(enseignant)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier cette note");
        }

        note.setValeur(request.getValeur());
        note.setTypeNote(request.getTypeNote());
        note.setAnneeScolaire(request.getAnneeScolaire());

        Note savedNote = noteRepository.save(note);
        return mapToResponse(savedNote);
    }

    @Override
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ADMINISTRATION')")
    public void supprimerNote(UUID noteId, UUID enseignantId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note introuvable"));

        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));

        if (!note.getModule().getEnseignant().equals(enseignant)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer cette note");
        }

        noteRepository.delete(note);
    }

    @Override
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ADMINISTRATION')")
    @Transactional(readOnly = true)
    public List<NoteResponse> obtenirNotesParModule(UUID moduleId, String anneeScolaire) {
        List<Note> notes = noteRepository.findByModuleWithDetails(moduleId, anneeScolaire);
        return notes.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ETUDIANT') or hasRole('ENSEIGNANT') or hasRole('ADMINISTRATION')")
    @Transactional(readOnly = true)
    public List<NoteResponse> obtenirNotesParEtudiant(UUID studentId, String anneeScolaire) {
        List<Note> notes = noteRepository.findByStudentWithDetails(studentId, anneeScolaire);
        return notes.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ADMINISTRATION')")
    @Transactional(readOnly = true)
    public List<NoteResponse> obtenirNotesParEnseignant(UUID enseignantId, String anneeScolaire) {
        List<Note> notes = noteRepository.findByEnseignantAndAnneeScolaire(enseignantId, anneeScolaire);
        return notes.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ETUDIANT') or hasRole('ENSEIGNANT') or hasRole('ADMINISTRATION')")
    @Transactional(readOnly = true)
    public BulletinResponse genererBulletin(UUID studentId, String anneeScolaire, TypeNote typeEvaluation) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));

        List<Note> notes = noteRepository.findByStudentWithDetails(studentId, anneeScolaire);

        Map<UUID, List<Note>> notesByModule = notes.stream()
                .collect(Collectors.groupingBy(note -> note.getModule().getIdModule()));

        List<NoteModuleResponse> moduleNotes = new ArrayList<>();
        BigDecimal sommeCoefficients = BigDecimal.ZERO;
        BigDecimal sommePonderee = BigDecimal.ZERO;

        for (Map.Entry<UUID, List<Note>> entry : notesByModule.entrySet()) {
            List<Note> moduleNotesList = entry.getValue();
            Module module = moduleNotesList.get(0).getModule();

            NoteModuleResponse moduleResponse = NoteModuleResponse.builder()
                    .moduleId(module.getIdModule())
                    .moduleNom(module.getNom())
                    .coefficient(module.getCoefficient())
                    .build();

            for (Note note : moduleNotesList) {
                switch (note.getTypeNote()) {
                    case C1 -> moduleResponse.setNoteC1(note.getValeur());
                    case C2 -> moduleResponse.setNoteC2(note.getValeur());
                    case EXAMEN_TH -> moduleResponse.setNoteExamenTh(note.getValeur());
                    case EXAMEN_PR -> moduleResponse.setNoteExamenPr(note.getValeur());
                }
            }

            BigDecimal moyenneModule = calculerMoyenneModule(moduleResponse, typeEvaluation);
            moduleResponse.setMoyenneModule(moyenneModule);

            if (moyenneModule != null && module.getCoefficient() != null) {
                BigDecimal moyennePonderee = moyenneModule.multiply(module.getCoefficient());
                moduleResponse.setMoyennePonderee(moyennePonderee);

                sommeCoefficients = sommeCoefficients.add(module.getCoefficient());
                sommePonderee = sommePonderee.add(moyennePonderee);
            }

            moduleNotes.add(moduleResponse);
        }

        BigDecimal moyenneGenerale = sommeCoefficients.compareTo(BigDecimal.ZERO) > 0
                ? sommePonderee.divide(sommeCoefficients, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        String professeurResponsable = "";
        if (!notes.isEmpty() && notes.get(0).getModule().getEnseignant() != null) {
            Enseignant enseignant = notes.get(0).getModule().getEnseignant();
            professeurResponsable = enseignant.getUser().getNom() + " " + enseignant.getUser().getPrenom();
        }

        return BulletinResponse.builder()
                .studentId(studentId)
                .studentNom(student.getUser().getNom())
                .studentPrenom(student.getUser().getPrenom())
                .matricule(student.getMatricule())
                .niveau(student.getNiveau() != null ? student.getNiveau().name() : "")
                .anneeScolaire(anneeScolaire)
                .typeEvaluation(typeEvaluation.getLibelle())
                .notes(moduleNotes)
                .moyenneGenerale(moyenneGenerale)
                .mention(calculerMention(moyenneGenerale))
                .professeurResponsable(professeurResponsable)
                .build();
    }

    @Override
    @PreAuthorize("hasRole('ETUDIANT') or hasRole('ENSEIGNANT') or hasRole('ADMINISTRATION')")
    public byte[] exporterBulletinPDF(UUID studentId, String anneeScolaire, TypeNote typeEvaluation) {
        BulletinResponse bulletin = genererBulletin(studentId, anneeScolaire, typeEvaluation);
        return generatePDF(bulletin);
    }

    private BigDecimal calculerMoyenneModule(NoteModuleResponse module, TypeNote typeEvaluation) {
        switch (typeEvaluation) {
            case C1 -> {
                return module.getNoteC1();
            }
            case C2 -> {
                return module.getNoteC2();
            }
            case EXAMEN_TH -> {
                if (module.getNoteC1() != null && module.getNoteC2() != null && module.getNoteExamenTh() != null) {
                    BigDecimal controles = module.getNoteC1().add(module.getNoteC2()).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                    return controles.add(module.getNoteExamenTh()).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                }
                return module.getNoteExamenTh();
            }
            case EXAMEN_PR -> {
                return module.getNoteExamenPr();
            }
            default -> {
                return null;
            }
        }
    }

    private String calculerMention(BigDecimal moyenne) {
        if (moyenne == null) return "Non calculée";
        if (moyenne.compareTo(BigDecimal.valueOf(16)) >= 0) return "Très Bien";
        if (moyenne.compareTo(BigDecimal.valueOf(14)) >= 0) return "Bien";
        if (moyenne.compareTo(BigDecimal.valueOf(12)) >= 0) return "Assez Bien";
        if (moyenne.compareTo(BigDecimal.valueOf(10)) >= 0) return "Passable";
        return "Insuffisant";
    }

    private byte[] generatePDF(BulletinResponse bulletin) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);

            Paragraph title = new Paragraph("BULLETIN SCOLAIRE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            Paragraph infosGenerales = new Paragraph();
            infosGenerales.add(new Chunk("Année Scolaire: ", boldFont));
            infosGenerales.add(new Chunk(bulletin.getAnneeScolaire() + "\n", normalFont));
            infosGenerales.add(new Chunk("Type d'évaluation: ", boldFont));
            infosGenerales.add(new Chunk(bulletin.getTypeEvaluation() + "\n", normalFont));
            infosGenerales.setSpacingAfter(15);
            document.add(infosGenerales);

            PdfPTable studentTable = new PdfPTable(2);
            studentTable.setWidthPercentage(100);
            studentTable.setSpacingAfter(20);

            addCell(studentTable, "Nom et Prénom:", boldFont);
            addCell(studentTable, bulletin.getStudentNom() + " " + bulletin.getStudentPrenom(), normalFont);

            addCell(studentTable, "N° Inscription:", boldFont);
            addCell(studentTable, bulletin.getMatricule(), normalFont);

            addCell(studentTable, "Niveau:", boldFont);
            addCell(studentTable, bulletin.getNiveau(), normalFont);

            document.add(studentTable);

            PdfPTable notesTable = new PdfPTable(7);
            notesTable.setWidthPercentage(100);
            notesTable.setWidths(new float[]{3f, 1.2f, 1.2f, 1.2f, 1.2f, 1.2f, 1.5f});
            notesTable.setSpacingAfter(15);

            addHeaderCell(notesTable, "Matière", headerFont);
            addHeaderCell(notesTable, "Coef.", headerFont);
            addHeaderCell(notesTable, "C1", headerFont);
            addHeaderCell(notesTable, "C2", headerFont);
            addHeaderCell(notesTable, "Ex. Th", headerFont);
            addHeaderCell(notesTable, "Ex. Pr", headerFont);
            addHeaderCell(notesTable, "Moyenne", headerFont);

            for (NoteModuleResponse note : bulletin.getNotes()) {
                addCell(notesTable, note.getModuleNom(), normalFont);
                addCell(notesTable, note.getCoefficient() != null ? String.valueOf(note.getCoefficient()) : "-", normalFont);
                addCell(notesTable, note.getNoteC1() != null ? String.valueOf(note.getNoteC1()) : "-", normalFont);
                addCell(notesTable, note.getNoteC2() != null ? String.valueOf(note.getNoteC2()) : "-", normalFont);
                addCell(notesTable, note.getNoteExamenTh() != null ? String.valueOf(note.getNoteExamenTh()) : "-", normalFont);
                addCell(notesTable, note.getNoteExamenPr() != null ? String.valueOf(note.getNoteExamenPr()) : "-", normalFont);
                addCell(notesTable, note.getMoyenneModule() != null ? String.valueOf(note.getMoyenneModule()) : "-", normalFont);
            }

            document.add(notesTable);

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(60);
            summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            summaryTable.setSpacingAfter(20);

            addCell(summaryTable, "Moyenne Générale:", boldFont);
            addCell(summaryTable, bulletin.getMoyenneGenerale() != null ? bulletin.getMoyenneGenerale() + " / 20" : "-", boldFont);

            addCell(summaryTable, "Mention:", boldFont);
            addCell(summaryTable, bulletin.getMention(), boldFont);

            document.add(summaryTable);

            Paragraph prof = new Paragraph();
            prof.add(new Chunk("Professeur Responsable: ", boldFont));
            prof.add(new Chunk(bulletin.getProfesseurResponsable() != null && !bulletin.getProfesseurResponsable().isEmpty()
                    ? bulletin.getProfesseurResponsable() : "Non assigné", normalFont));
            prof.setSpacingAfter(30);
            document.add(prof);

            Paragraph signature = new Paragraph("Signature: ____________________", normalFont);
            signature.setAlignment(Element.ALIGN_RIGHT);
            document.add(signature);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF: ", e);
            throw new RuntimeException("Erreur lors de la génération du bulletin PDF", e);
        }
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(200, 200, 200));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private NoteResponse mapToResponse(Note note) {
        return NoteResponse.builder()
                .idNote(note.getIdNote())
                .studentId(note.getStudent().getUserId())
                .studentNom(note.getStudent().getUser().getNom())
                .studentPrenom(note.getStudent().getUser().getPrenom())
                .matricule(note.getStudent().getMatricule())
                .moduleId(note.getModule().getIdModule())
                .moduleNom(note.getModule().getNom())
                .typeNote(note.getTypeNote())
                .valeur(note.getValeur())
                .anneeScolaire(note.getAnneeScolaire())
                .dateCreation(note.getDateCreation())
                .dateModification(note.getDateModification())
                .saisiePar(note.getSaisiePar() != null ? note.getSaisiePar().getEmail() : "")
                .build();
    }
}