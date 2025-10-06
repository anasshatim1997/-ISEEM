package com.iseem_backend.application.DTO.response;

import com.iseem_backend.application.enums.TypeNote;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteResponse {
    private UUID idNote;
    private UUID studentId;
    private String studentNom;
    private String studentPrenom;
    private String matricule;
    private UUID moduleId;
    private String moduleNom;
    private TypeNote typeNote;
    private BigDecimal valeur;
    private String anneeScolaire;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private String saisiePar;
}