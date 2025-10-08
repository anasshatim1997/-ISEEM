package com.iseem_backend.application.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "modules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idModule;

    @Column(nullable = false)
    private String nom;

  

    @Column(precision = 3, scale = 1, nullable = false)
    private BigDecimal coefficient;

    private String description;

    private Integer heuresTotal;

    private Integer heuresCours;

    private Integer heuresTD;

    private Integer heuresTP;

    @Column(precision = 4, scale = 2)
    private BigDecimal note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_enseignant")
    private Enseignant enseignant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_diplome")
    private Diplome diplome;

    @ManyToMany(mappedBy = "modules", fetch = FetchType.LAZY)
    private Set<Student> students;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Absence> absences;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes;
}