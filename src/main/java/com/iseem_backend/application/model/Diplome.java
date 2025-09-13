package com.iseem_backend.application.model;

import com.iseem_backend.application.enums.Mention;
import com.iseem_backend.application.enums.ModeRemise;
import com.iseem_backend.application.enums.TypeDiplome;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "diplome")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Diplome {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idDiplome;

    @Enumerated(EnumType.STRING)
    private TypeDiplome typeDiplome;

    private String customDiplomeLabel;

    @Column(columnDefinition = "TEXT")
    private String niveau;

    @OneToMany(mappedBy = "diplome", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Module> modules;

    private String nomDiplome;

    private Integer anneeObtention;

    private boolean estValide;

    @Enumerated(EnumType.STRING)
    private Mention mention;

    private LocalDate dateDelivrance;

    @ManyToOne
    @JoinColumn(name = "signature_admin_id")
    private User signatureAdmin;

    private String qrCodeUrl;

    @Lob
    private byte[] fichierDiplome;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Enumerated(EnumType.STRING)
    private ModeRemise modeRemise;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "diplome_enseigant",
            joinColumns = @JoinColumn(name = "id_diplome"),
            inverseJoinColumns = @JoinColumn(name = "id_enseignant")
    )
    private Set<Enseignant> professeurs;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
}
