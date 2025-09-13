package com.iseem_backend.application.mapper;

import com.iseem_backend.application.DTO.request.CustomFieldRequest;
import com.iseem_backend.application.DTO.request.StudentRequest;
import com.iseem_backend.application.DTO.response.CustomFieldResponse;
import com.iseem_backend.application.DTO.response.StudentResponse;
import com.iseem_backend.application.model.CustomField;
import com.iseem_backend.application.model.Student;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    public Student toEntity(StudentRequest request) {
        if (request == null) return null;

        Student student = Student.builder()
                .matricule(request.getMatricule())
                .dateNaissance(request.getDateNaissance())
                .lieuNaissance(request.getLieuNaissance())
                .sexe(request.getSexe())
                .nationalite(request.getNationalite())
                .adresse(request.getAdresse())
                .ville(request.getVille())
                .situationFamiliale(request.getSituationFamiliale())
                .niveau(request.getNiveau())
                .groupe(request.getGroupe())
                .anneeAcademique(request.getAnneeAcademique())
                .statut(request.getStatut())
                .bourse(request.getBourse())
                .handicap(request.getHandicap())
                .build();

        if (request.getCustomFields() != null) {
            student.setCustomFields(toEntityCustomFields(request.getCustomFields(), student));
        }

        return student;
    }

    public StudentResponse toDto(Student student) {
        if (student == null) return null;

        return StudentResponse.builder()
                .idStudent(student.getUserId())
                .email(student.getUser().getEmail())
                .nom(student.getUser().getNom())
                .prenom(student.getUser().getPrenom())
                .telephone(student.getUser().getTelephone())
                .image(student.getUser().getImage())
                .matricule(student.getMatricule())
                .dateNaissance(student.getDateNaissance())
                .lieuNaissance(student.getLieuNaissance())
                .sexe(student.getSexe())
                .nationalite(student.getNationalite())
                .adresse(student.getAdresse())
                .ville(student.getVille())
                .situationFamiliale(student.getSituationFamiliale())
                .niveau(student.getNiveau())
                .groupe(student.getGroupe())
                .anneeAcademique(student.getAnneeAcademique())
                .statut(student.getStatut())
                .bourse(student.getBourse())
                .handicap(student.getHandicap())
                .customFields(toDtoCustomFields(student.getCustomFields()))
                .build();
    }

    public void updateEntityFromRequest(StudentRequest request, Student student) {
        if (request == null || student == null) return;

        student.setMatricule(request.getMatricule());
        student.setDateNaissance(request.getDateNaissance());
        student.setLieuNaissance(request.getLieuNaissance());
        student.setSexe(request.getSexe());
        student.setNationalite(request.getNationalite());
        student.setAdresse(request.getAdresse());
        student.setVille(request.getVille());
        student.setSituationFamiliale(request.getSituationFamiliale());
        student.setNiveau(request.getNiveau());
        student.setGroupe(request.getGroupe());
        student.setAnneeAcademique(request.getAnneeAcademique());
        student.setStatut(request.getStatut());
        student.setBourse(request.getBourse());
        student.setHandicap(request.getHandicap());

        if (request.getCustomFields() != null) {
            student.getCustomFields().clear();
            student.getCustomFields().addAll(toEntityCustomFields(request.getCustomFields(), student));
        }
    }

    private Set<CustomField> toEntityCustomFields(Set<CustomFieldRequest> requests, Student student) {
        if (requests == null) return new HashSet<>();
        return requests.stream()
                .map(req -> CustomField.builder()
                        .fieldName(req.getFieldName())
                        .fieldValue(req.getFieldValue())
                        .student(student)
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<CustomFieldResponse> toDtoCustomFields(Set<CustomField> fields) {
        if (fields == null) return new HashSet<>();
        return fields.stream()
                .map(field -> CustomFieldResponse.builder()
                        .id(field.getId())
                        .fieldName(field.getFieldName())
                        .fieldValue(field.getFieldValue())
                        .build())
                .collect(Collectors.toSet());
    }
}
