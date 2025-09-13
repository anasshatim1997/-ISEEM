package com.iseem_backend.application.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "emploi_slot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploiSlot {

    @Id
    @GeneratedValue
    private UUID id;

    private String jour;

    private LocalTime heureDebut;

    private LocalTime heureFin;

    private String module;
}
