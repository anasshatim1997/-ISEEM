package com.iseem_backend.application.exceptions;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class EnseignantNotFoundException extends ApiException {
    public EnseignantNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "ENSEIGNANT_NOT_FOUND");
    }

    public EnseignantNotFoundException(UUID id) {
        this("Enseignant introuvable avec ID: " + (id == null ? "null" : id.toString()));
    }
}
