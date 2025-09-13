package com.iseem_backend.application.DTO.request;

import com.iseem_backend.application.enums.Niveau;
import com.iseem_backend.application.enums.Sex;
import com.iseem_backend.application.enums.Statut;
import com.iseem_backend.application.enums.YesOrNo;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequest {

    @NotNull
    private String matricule;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateNaissance;
    private String lieuNaissance;
    @NotNull
    private Sex sexe;
    @NotNull
    private String nationalite;
    private String adresse;
    @NotNull
    private String ville;
    @NotNull
    private String situationFamiliale;
    @NotNull
    private Niveau niveau;
    @NotNull
    private String groupe;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private String anneeAcademique;
    @NotNull
    private Statut statut;
    @NotNull
    private YesOrNo bourse;
    @NotNull
    private YesOrNo handicap;
    private Set<CustomFieldRequest> customFields;

}
