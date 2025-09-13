package com.iseem_backend.application.model;

import com.iseem_backend.application.enums.StatusEnseignant;
import com.iseem_backend.application.utils.TimeSlot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "enseignants")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enseignant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID enseignantId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotNull
    private String specialite;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateEmbauche;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEnseignant statusEnseignant;

    private Duration heuresTravail;

    @Embedded
    @NotNull
    private TimeSlot horaire;

    @OneToMany(mappedBy = "enseignant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Module> modules;

    @ManyToMany(mappedBy = "professeurs", fetch = FetchType.LAZY)
    private Set<Diplome> diplomes;


    @OneToMany(mappedBy = "enseignant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CustomField> customFields = new HashSet<>();

}