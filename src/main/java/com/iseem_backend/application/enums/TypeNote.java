package com.iseem_backend.application.enums;

public enum TypeNote {
    C1("Contrôle 1"),
    C2("Contrôle 2"),
    EXAMEN_TH("Examen Théorique"),
    EXAMEN_PR("Examen Pratique"),
    RATTRAPAGE("Rattrapage");

    private final String libelle;

    TypeNote(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}