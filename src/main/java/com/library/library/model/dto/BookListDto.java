package com.library.library.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@RequiredArgsConstructor
public class BookListDto {
    private Long id;

    @NotBlank(message = "List name is required")
    @Size(max = 30, message = "List name must be less than 30 characters")
    private String name;

    private Date createdDate;

    private Boolean isDefault;

}
