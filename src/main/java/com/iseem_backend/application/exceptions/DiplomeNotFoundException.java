package com.iseem_backend.application.exceptions;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class DiplomeNotFoundException extends ApiException {
    public DiplomeNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "DIPLOME_NOT_FOUND");
    }

    public DiplomeNotFoundException(UUID id) {
        this("Diplôme introuvable avec ID: " + (id == null ? "null" : id.toString()));
    }
}
