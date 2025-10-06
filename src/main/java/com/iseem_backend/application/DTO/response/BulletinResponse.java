package com.iseem_backend.application.DTO.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulletinResponse {
    private UUID studentId;
    private String studentNom;
    private String studentPrenom;
    private String matricule;
    private String niveau;
    private String anneeScolaire;
    private String typeEvaluation;
    private List<NoteModuleResponse> notes;
    private BigDecimal moyenneGenerale;
    private String mention;
    private String professeurResponsable;
}