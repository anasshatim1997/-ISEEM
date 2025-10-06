package com.iseem_backend.application.exceptions;

public class AcademicYearNotFoundException extends RuntimeException {
    public AcademicYearNotFoundException(String message) {
        super(message);
    }
}