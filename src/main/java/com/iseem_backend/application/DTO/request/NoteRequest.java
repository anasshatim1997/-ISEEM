package com.iseem_backend.application.DTO.request;

import com.iseem_backend.application.enums.TypeNote;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteRequest {
    @NotNull
    private UUID studentId;

    @NotNull
    private UUID moduleId;

    @NotNull
    private TypeNote typeNote;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "20.0")
    private BigDecimal valeur;

    @NotNull
    private UUID enseignantId;

    @NotNull
    private String anneeScolaire;
}