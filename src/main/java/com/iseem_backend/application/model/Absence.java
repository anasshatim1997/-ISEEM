package com.iseem_backend.application.model;

import com.iseem_backend.application.enums.AbsenceReason;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "absence")
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idAbsence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "id_module")
    private Module module;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private AbsenceReason reason;

    @Column(columnDefinition = "boolean default false")
    private boolean justified;

}
