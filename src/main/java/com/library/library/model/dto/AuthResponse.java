package com.library.library.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class AuthResponse {
    private String message;
    private Long userId;
    private String username;
    private List<String> roles;

    public AuthResponse(String message, Long userId, String username, List<String> roles) {
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }
}