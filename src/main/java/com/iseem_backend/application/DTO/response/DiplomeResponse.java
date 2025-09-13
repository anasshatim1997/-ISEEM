package com.iseem_backend.application.DTO.response;

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
public class DiplomeResponse {

    private UUID idDiplome;
    private TypeDiplome typeDiplome;
    private String customDiplomeLabel;
    private String niveau;
    private String nomDiplome;
    private Integer anneeObtention;
    private boolean estValide;
    private Mention mention;
    private LocalDate dateDelivrance;
    private UUID signatureAdminId;
    private String qrCodeUrl;
    private String commentaire;
    private ModeRemise modeRemise;
    private Set<UUID> professeursIds;
    private UUID studentId;
}
