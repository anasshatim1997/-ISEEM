package com.iseem_backend.application.model;

import com.iseem_backend.application.enums.Niveau;
import com.iseem_backend.application.enums.Sex;
import com.iseem_backend.application.enums.Statut;
import com.iseem_backend.application.enums.YesOrNo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "students")
public class Student {

    @Id
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true)
    @NotNull
    private String matricule;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateNaissance;

    private String lieuNaissance;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Sex sexe;

    @NotNull
    private String nationalite;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @NotNull
    private String ville;

    @NotNull
    private String situationFamiliale;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Niveau niveau;

    @NotNull
    private String groupe;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private String anneeAcademique;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Statut statut;

    @NotNull
    @Enumerated(EnumType.STRING)
    private YesOrNo bourse;

    @NotNull
    @Enumerated(EnumType.STRING)
    private YesOrNo handicap;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Absence> absences;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_module",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "module_id")
    )
    private Set<Module> modules;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Diplome> diplomes;


    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CustomField> customFields = new HashSet<>();

}