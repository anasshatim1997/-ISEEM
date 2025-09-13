package com.iseem_backend.application.mapper;

import com.iseem_backend.application.DTO.request.DiplomeRequest;
import com.iseem_backend.application.DTO.response.DiplomeResponse;
import com.iseem_backend.application.model.Diplome;
import com.iseem_backend.application.model.Enseignant;
import com.iseem_backend.application.model.Student;
import com.iseem_backend.application.repository.EnseignantRepository;
import com.iseem_backend.application.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DiplomeMapper {

    private final EnseignantRepository enseignantRepository;
    private final StudentRepository studentRepository;

    public Diplome toEntity(DiplomeRequest request) {
        Diplome diplome = new Diplome();
        diplome.setTypeDiplome(request.getTypeDiplome());
        diplome.setCustomDiplomeLabel(request.getCustomDiplomeLabel());
        diplome.setNiveau(request.getNiveau());
        diplome.setNomDiplome(request.getNomDiplome());
        diplome.setAnneeObtention(request.getAnneeObtention());
        diplome.setMention(request.getMention());
        diplome.setDateDelivrance(request.getDateDelivrance());
        diplome.setModeRemise(request.getModeRemise());
        diplome.setCommentaire(request.getCommentaire());

        if (request.getProfesseursIds() != null) {
            Set<Enseignant> professeurs = request.getProfesseursIds().stream()
                    .map(id -> enseignantRepository.findById(id).orElse(null))
                    .collect(Collectors.toSet());
            diplome.setProfesseurs(professeurs);
        }

        if (request.getStudentId() != null) {
            Student student = studentRepository.findById(request.getStudentId()).orElse(null);
            diplome.setStudent(student);
        }

        return diplome;
    }

    public DiplomeResponse toDto(Diplome diplome) {
        return DiplomeResponse.builder()
                .idDiplome(diplome.getIdDiplome())
                .typeDiplome(diplome.getTypeDiplome())
                .customDiplomeLabel(diplome.getCustomDiplomeLabel())
                .niveau(diplome.getNiveau())
                .nomDiplome(diplome.getNomDiplome())
                .anneeObtention(diplome.getAnneeObtention())
                .estValide(diplome.isEstValide())
                .mention(diplome.getMention())
                .dateDelivrance(diplome.getDateDelivrance())
                .signatureAdminId(diplome.getSignatureAdmin() != null ? diplome.getSignatureAdmin().getUserId() : null)
                .qrCodeUrl(diplome.getQrCodeUrl())
                .commentaire(diplome.getCommentaire())
                .modeRemise(diplome.getModeRemise())
                .professeursIds(diplome.getProfesseurs() != null ?
                        diplome.getProfesseurs().stream().map(Enseignant::getEnseignantId).collect(Collectors.toSet()) : null)
                .studentId(diplome.getStudent() != null ? diplome.getStudent().getUserId() : null)
                .build();
    }
    public void updateEntityFromRequest(DiplomeRequest request, Diplome diplome) {
        if (request.getTypeDiplome() != null) diplome.setTypeDiplome(request.getTypeDiplome());
        if (request.getCustomDiplomeLabel() != null) diplome.setCustomDiplomeLabel(request.getCustomDiplomeLabel());
        if (request.getNiveau() != null) diplome.setNiveau(request.getNiveau());
        if (request.getNomDiplome() != null) diplome.setNomDiplome(request.getNomDiplome());
        if (request.getAnneeObtention() != null) diplome.setAnneeObtention(request.getAnneeObtention());
        if (request.getMention() != null) diplome.setMention(request.getMention());
        if (request.getDateDelivrance() != null) diplome.setDateDelivrance(request.getDateDelivrance());
        if (request.getModeRemise() != null) diplome.setModeRemise(request.getModeRemise());
        if (request.getCommentaire() != null) diplome.setCommentaire(request.getCommentaire());

        if (request.getProfesseursIds() != null) {
            Set<Enseignant> professeurs = request.getProfesseursIds().stream()
                    .map(id -> enseignantRepository.findById(id).orElse(null))
                    .collect(Collectors.toSet());
            diplome.setProfesseurs(professeurs);
        }

        if (request.getStudentId() != null) {
            Student student = studentRepository.findById(request.getStudentId()).orElse(null);
            diplome.setStudent(student);
        }
    }

}
