package com.iseem_backend.application.model;

import com.iseem_backend.application.enums.ModeFormation;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "formations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Formation {

    @Id
    @GeneratedValue
    private UUID idFormation;

    private String nom;

    private Integer duree;

    private BigDecimal cout;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "formation_enseignants",
            joinColumns = @JoinColumn(name = "formation_id"),
            inverseJoinColumns = @JoinColumn(name = "enseignant_id")
    )
    private Set<Enseignant> enseignants;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "emploi_du_temps_id")
    private EmploiDuTemps emploiDuTemps;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String anneeFormation;

    private Boolean estActive;

    @Enumerated(EnumType.STRING)
    private ModeFormation modeFormation;

    private String niveauAcces;

    private Integer capaciteMax;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "formation_enseignants",
            joinColumns = @JoinColumn(name = "formation_id"),
            inverseJoinColumns = @JoinColumn(name = "enseignant_id")
    )
    private Set<Enseignant> professeurs;


    public String getNomProfesseurs() {
        if (professeurs == null || professeurs.isEmpty()) return "";
        return professeurs.stream()
                .map(p -> p.getUser() != null ? p.getUser().getNom() : "Unknown")
                .collect(Collectors.joining(", "));
    }


}
