package com.library.library.Model.Dto;

import com.library.library.Model.Enumerations.PublishStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
public class BookDto {
    private Long id;

    @NotBlank(message = "Book title is required")
    @Size(min = 3, max = 50, message = "Book title must be 3-50 characters")
    private String bookTitle;

    @NotBlank(message = "Book key is required")
    @Pattern(regexp = "^[a-z0-9_-]{3,50}$",
            message = "Username key must be 3-50 chars (lowercase, numbers, _, -)")
    private String bookKey;

    @Pattern(regexp = "^(97([89]))?\\d{9}(\\d|X)$", message = "Invalid ISBN format")
    private String isbn;

    @Size(max = 30, message = "Publisher name must be less than 30 characters")
    private String publisherName;

    @Size(max = 30, message = "Publisher key must be less than 30 characters")
    private String publisherKey;

    @NotBlank(message = "Author name is required")
    @Size(max = 30, message = "Author name must be less than 30 characters")
    private String authorName;

    @NotBlank(message = "Author key is required")
    @Size(max = 30, message = "Author key must be less than 30 characters")
    private String authorKey;


    @Min(value = 850, message = "Publication year must be a valid year")
    @Max(value = 2100, message = "Publication year must be a valid year")
    private Integer publicationYear;

    @Min(value = 850, message = "Write year must be a valid year")
    @Max(value = 2100, message = "Write year must be a valid year")
    private Integer writeYear;

    @DecimalMin(value = "0.0", message = "Rating average cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating average cannot exceed 5.0")
    private Double ratingAvg;

    @Min(value = 0, message = "Rating count cannot be negative")
    @Column(name = "rating_number")
    private Long ratingNumber;

    @Min(value = 0, message = "Download count cannot be negative")
    private Long downloadingNumber;

    @Size(max = 512, message = "Google Drive ID must be less than 512 characters")
    private String imageGoogleDriveId;  // Stores the Google Drive file ID

    @Size(max = 512, message = "Direct link must be less than 512 characters")
    private String imageCoverDirectLink;

    @NotBlank(message = "Book file ID is required")
    private String bookFileId;
    @Positive(message = "Book size must be a positive number")
    private Double bookSize;
    @NotBlank(message = "File content type is required")
    private String fileContentType;

    private PublishStatus status;
    private Boolean isListed;
    @Min(value = 1, message = "Total pages must be at least 1")
    private Integer totalPages;
}
