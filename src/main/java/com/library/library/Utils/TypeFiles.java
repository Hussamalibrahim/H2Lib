package com.library.library.Utils;

import com.library.library.Exception.Validation.MissingFileExtensionException;

import java.util.Optional;


public class TypeFiles {

    public static String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElseThrow(MissingFileExtensionException::new);
    }

    public static String getMimeType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "epub" -> "application/epub+zip";
            case "mobi" -> "application/x-mobipocket-ebook";
            case "webp" -> "image/webp";
            case "jpeg", "jpg" -> "image/jpeg";
            case "png" -> "image/png";
            case "avif" -> "image/avif";
            default -> "application/octet-stream";
        };
    }

}
