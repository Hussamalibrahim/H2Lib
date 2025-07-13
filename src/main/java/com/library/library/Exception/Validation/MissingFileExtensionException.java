package com.library.library.Exception.Validation;

public class MissingFileExtensionException extends RuntimeException {
    public MissingFileExtensionException() {
        super("File must have an extension (e.g., '.pdf')");
    }
}
