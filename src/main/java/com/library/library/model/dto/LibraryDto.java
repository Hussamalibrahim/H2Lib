package com.library.library.model.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LibraryDto {
    private short id;
    @Min(value = 0, message = "Total users cannot be negative")
    private Long totalUsers ;
    @Min(value = 0, message = "Total books cannot be negative")
    private Long totalBooks ;
    @Min(value = 0, message = "Total downloads cannot be negative")
    private Long totalDownloads;
    @Min(value = 0, message = "Total authors cannot be negative")
    private Long totalAuthors;
    @Min(value = 0, message = "Total ratings cannot be negative")
    private Long totalRating ;

    public LibraryDto(Long totalUsers, Long totalBooks, Long totalDownloads, Long totalAuthors, Long totalRating) {
        this.totalUsers = totalUsers;
        this.totalBooks = totalBooks;
        this.totalDownloads = totalDownloads;
        this.totalAuthors = totalAuthors;
        this.totalRating = totalRating;
    }
}
