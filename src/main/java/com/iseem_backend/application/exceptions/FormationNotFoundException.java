package com.iseem_backend.application.exceptions;

import java.util.UUID;

public class FormationNotFoundException extends RuntimeException {
    public FormationNotFoundException(UUID id) {
        super("Formation not found with id: " + id);
    }
}
