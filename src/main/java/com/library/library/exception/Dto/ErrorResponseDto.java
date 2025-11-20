package com.library.library.exception.Dto;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.util.HtmlUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.library.library.Utils.HtmlUtils.sanitize;

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

    public static ErrorResponseDto create(HttpStatus status, String path, String message) {
        ErrorResponseDto dto = new ErrorResponseDto();
        dto.timestamp = Instant.now();
        dto.status = status.value();
        dto.error = sanitize(status.getReasonPhrase());
        dto.path = sanitize(path);
        dto.message = sanitize(message);
        return dto;
    }

}
