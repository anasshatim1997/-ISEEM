package com.iseem_backend.application.exceptions;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ModuleNotFoundException extends ApiException {
    public ModuleNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "MODULE_NOT_FOUND");
    }

    public ModuleNotFoundException(UUID id) {
        this("Module introuvable avec ID: " + (id == null ? "null" : id.toString()));
    }
}