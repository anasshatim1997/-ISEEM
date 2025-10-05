package com.iseem_backend.application.model;

import com.iseem_backend.application.enums.TypeNote;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    @NotNull
    private Module module;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private TypeNote typeNote;

    @DecimalMin(value = "0.0", message = "La note doit être positive")
    @DecimalMax(value = "20.0", message = "La note ne peut pas dépasser 20")
    @Column(precision = 4, scale = 2)
    private BigDecimal valeur;

    @Column(nullable = false)
    private String anneeScolaire;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saisie_par")
    private User saisiePar;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}