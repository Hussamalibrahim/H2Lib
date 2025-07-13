package com.library.library.Exception.Dto;

public class ValidationError extends RuntimeException {
    public ValidationError(String message) {
        super(message);
    }
}
