package com.iseem_backend.application.model;

import com.iseem_backend.application.enums.EtatScolarite;
import com.iseem_backend.application.enums.ModePaiement;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "paiment")
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Paiment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idPaiment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private Diplome formation;

    private BigDecimal coutScolarite;
    private BigDecimal montantPaye = BigDecimal.ZERO;
    private BigDecimal resteAPayer = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;

    @Enumerated(EnumType.STRING)
    private EtatScolarite etatScolarite;

    private LocalDate dateDernierPaiement;
    private String commentaire;
    private String numeroRecu;

    private String fichierRecu;
    private String scanRecu;

    private LocalDate dateEmissionRecu;
    private String emetteurRecu;

}
