package com.library.library.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BookTypeDto {
    private Long id;

    @NotBlank(message = "Type name is required")
    @Size(max = 30, message = "Type name must be less than 50 characters")
    private String name;

    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase with hyphens")
    private String slug;

    @Min(value = 0, message = "Book count cannot be negative")
    private int bookCount = 0;

}
