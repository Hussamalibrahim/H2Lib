package com.library.library.exception.Dto;

public class ValidationError extends RuntimeException {
    public ValidationError(String message) {
        super(message);
    }
}
