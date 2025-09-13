package com.iseem_backend.application.DTO.request;

import com.iseem_backend.application.enums.Mention;
import com.iseem_backend.application.enums.ModeRemise;
import com.iseem_backend.application.enums.TypeDiplome;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiplomeRequest {

    private TypeDiplome typeDiplome;
    private String customDiplomeLabel;
    private String niveau;
    private String nomDiplome;
    private Integer anneeObtention;
    private Mention mention;
    private LocalDate dateDelivrance;
    private ModeRemise modeRemise;
    private String commentaire;
    private Set<UUID> professeursIds;
    private UUID studentId;
}
