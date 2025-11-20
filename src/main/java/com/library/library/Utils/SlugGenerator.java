package com.library.library.Utils;

// this class to make url-key for authors and books
public class SlugGenerator{

    public static String createKeyWithId(String title, Long id) {
        String cleanTitle = title.toLowerCase()
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        return cleanTitle + "-" + id;
    }
    public static String createKey(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    public static Long extractIdFromKey(String Key) {
        try {
            String[] parts = Key.split("-");
            return Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid book key format");
        }
    }
}