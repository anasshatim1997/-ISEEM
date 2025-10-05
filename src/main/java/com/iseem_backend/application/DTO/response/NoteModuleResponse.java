package com.iseem_backend.application.DTO.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteModuleResponse {
    private UUID moduleId;
    private String moduleNom;
    private BigDecimal coefficient;
    private BigDecimal noteC1;
    private BigDecimal noteC2;
    private BigDecimal noteExamenTh;
    private BigDecimal noteExamenPr;
    private BigDecimal moyenneModule;
    private BigDecimal moyennePonderee;
}
