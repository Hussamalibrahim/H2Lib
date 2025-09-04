package com.library.library.exception.Dto;


import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ErrorResponseDto {
    private Instant timestamp;
    private int status;
    private String error;
    private String path;
    private String message;
    private Map<String, String> fieldErrors;
    private List<String> globalErrors;
    private Map<String, Object> details = new HashMap<>();

    // For API responses
    public static ErrorResponseDto create(HttpStatus status, String path, String message) {
        ErrorResponseDto dto = new ErrorResponseDto();
        dto.setTimestamp(Instant.now());
        dto.setStatus(status.value());
        dto.setError(status.getReasonPhrase());
        dto.setPath(path);
        dto.setMessage(message);
        return dto;
    }
    public ErrorResponseDto addDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }

    // For Thymeleaf views
    public void addToModel(Model model) {
        model.addAttribute("timestamp", timestamp);
        model.addAttribute("status", status);
        model.addAttribute("error", error);
        model.addAttribute("path", path);
        model.addAttribute("message", message);
        if (fieldErrors != null) {
            model.addAttribute("fieldErrors", fieldErrors);
        }
        if (globalErrors != null) {
            model.addAttribute("globalErrors", globalErrors);
        }
        if (!details.isEmpty()) {
            model.addAttribute("details", details);
        }
    }
}