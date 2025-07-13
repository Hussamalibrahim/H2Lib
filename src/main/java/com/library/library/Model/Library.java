package com.library.library.Model;

import jakarta.persistence.*;
import com.library.library.Model.BookType;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "library")
public class Library {

    @Id
    @Column(name = "library_id")
    private short id = 1;

    //there is just one line on library_stats table
    @Min(value = 0, message = "Total users cannot be negative")
    @Column(name = "total_users", columnDefinition = "INTEGER DEFAULT 0")
    private Long totalUsers = 0L;

    @Min(value = 0, message = "Total books cannot be negative")
    @Column(name = "total_books", columnDefinition = "INTEGER DEFAULT 0")
    private Long totalBooks = 0L;

    @Min(value = 0, message = "Total downloads cannot be negative")
    @Column(name = "total_downloads", columnDefinition = "INTEGER DEFAULT 0")
    private Long totalDownloads = 0L;

    @Min(value = 0, message = "Total authors cannot be negative")
    @Column(name = "total_authors", columnDefinition = "INTEGER DEFAULT 0")
    private Long totalAuthors = 0L;

    @Min(value = 0, message = "Total ratings cannot be negative")
    @Column(name = "total_rating", columnDefinition = "INTEGER DEFAULT 0")
    private Long totalRating = 0L;

    // Constructor without ID for creating new instances
    public Library(long totalUsers, long totalBooks, long totalDownloads, long totalAuthors, long totalRating) {
        this.totalUsers = totalUsers;
        this.totalBooks = totalBooks;
        this.totalDownloads = totalDownloads;
        this.totalAuthors = totalAuthors;
        this.totalRating = totalRating;
    }
}