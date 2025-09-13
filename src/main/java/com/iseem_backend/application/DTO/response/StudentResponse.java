package com.iseem_backend.application.DTO.response;

import com.iseem_backend.application.enums.Niveau;
import com.iseem_backend.application.enums.Sex;
import com.iseem_backend.application.enums.Statut;
import com.iseem_backend.application.enums.YesOrNo;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private UUID idStudent;
    private String email;
    private String nom;
    private String prenom;
    private String telephone;
    private String image;
    private String matricule;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private Sex sexe;
    private String nationalite;
    private String adresse;
    private String ville;
    private String situationFamiliale;
    private Niveau niveau;
    private String groupe;
    private String anneeAcademique;
    private Statut statut;
    private YesOrNo bourse;
    private YesOrNo handicap;

    private Set<CustomFieldResponse> customFields;
}
