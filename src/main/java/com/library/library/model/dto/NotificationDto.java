package com.library.library.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class NotificationDto {
    private Long id;
    @NotBlank(message = "Message is required")
    @Size(max = 500, message = "Message must be less than 500 characters")
    private String massage;

    private LocalDate CreatedAt;
    @NotBlank(message = "Slug is required")
    private String targetId;

    private Boolean isRead;
}
