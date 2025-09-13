package com.iseem_backend.application.service;

import com.iseem_backend.application.DTO.request.StudentRequest;
import com.iseem_backend.application.DTO.response.StudentResponse;
import com.iseem_backend.application.model.Diplome;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StudentService {

    StudentResponse ajouterEtudiant(UUID userId, StudentRequest request);

    StudentResponse modifierEtudiant(UUID id, StudentRequest studentRequest);

    void supprimerEtudiant(UUID id);

    StudentResponse consulterProfil(UUID id);

    StudentResponse obtenirEtudiantParMatricule(String matricule);

    Page<StudentResponse> obtenirTousLesEtudiants(Pageable pageable);

    Map<String, Object> importerEtudiants(MultipartFile fichierExcel);

    byte[] exporterEtudiants();

    byte[] genererCartesScolaires(List<UUID> idsEtudiants);

    void ajouterDiplome(UUID idEtudiant, Diplome diplome);

    void supprimerDiplome(UUID idEtudiant, UUID idDiplome);

    List<Diplome> consulterDiplomes(UUID idEtudiant);

    List<StudentResponse> rechercherEtudiants(String nom, String prenom, String matricule);

    List<StudentResponse> getStudentsForTeacher(UUID teacherId);

}