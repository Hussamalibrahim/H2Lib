package com.library.library.Model;

import com.library.library.Model.Enumerations.PublishStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @NotBlank(message = "Book title is required")
    @Size(min = 3, max = 50, message = "Book title must be 3-50 characters")
    @Column(name = "book_title", nullable = false, unique = true)
    private String bookTitle;

    @NotBlank(message = "Book key is required")
    @Pattern(regexp = "^[a-z0-9_-]{3,50}$",
            message = "Username key must be 3-50 chars (lowercase, numbers, _, -)")
    @Column(name = "book_key", nullable = false, unique = true)
    private String bookKey; // Format: "harry-potter-123" (title + id)

    @Pattern(regexp = "^(97([89]))?\\d{9}(\\d|X)$", message = "Invalid ISBN format")
    @Column(name = "isbn", unique = true)
    private String isbn;

    @Size(max = 30, message = "Publisher name must be less than 30 characters")
    @Column(name = "publisher_name")
    private String publisherName;

    @Size(max = 30, message = "Publisher key must be less than 30 characters")
    @Column(name = "publisher_key")
    private String publisherKey;

    @NotBlank(message = "Author name is required")
    @Size(max = 30, message = "Author name must be less than 30 characters")
    @Column(name = "author_name", nullable = false)
    private String authorName;

    @NotBlank(message = "Author key is required")
    @Size(max = 30, message = "Author key must be less than 30 characters")
    @Column(name = "author_key", nullable = false)
    private String authorKey;

    @PastOrPresent(message = "Publication year cannot be in the future")
    @Min(value = 850, message = "Publication year must be a valid year")
    @Max(value = 2100, message = "Publication year must be a valid year")
    @Column(name = "publication_year")
    private Integer publicationYear;

    @PastOrPresent(message = "Publication year cannot be in the future")
    @Min(value = 850, message = "Write year must be a valid year")
    @Max(value = 2100, message = "Write year must be a valid year")
    @Column(name = "write_year")
    private Integer writeYear;

    @DecimalMin(value = "0.0", message = "Rating average cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating average cannot exceed 5.0")
    @Column(name = "rating_avg")
    private Double ratingAvg = 0.0;

    @Min(value = 0, message = "Rating count cannot be negative")
    @Column(name = "rating_number")
    private Long ratingNumber = 0L;

    @Min(value = 0, message = "Download count cannot be negative")
    @Column(name = "downloading_number")
    private Long downloadingNumber = 0L;

    @NotBlank(message = "Book file ID is required")
    @Column(name = "book_file_id", nullable = false)
    private String bookFileId;

    @Positive(message = "Book size must be a positive number")
    @Column(name = "book_size", nullable = false)
    private Double bookSize;

    @NotBlank(message = "File content type is required")
    @Column(name = "book_content_type", nullable = false)
    private String fileContentType; // "pdf"

    @Size(max = 512, message = "Google Drive ID must be less than 512 characters")
    @Column(name = "image_google_drive_id", length = 100)
    private String imageGoogleDriveId;

    @Size(max = 512, message = "Direct link must be less than 512 characters")
    @Column(name = "image_cover_direct_link", length = 512)
    private String imageCoverDirectLink;

    @Enumerated(EnumType.STRING)
    @Column(name = "publish_status")
    private PublishStatus status = PublishStatus.PENDING;

    @Column(name = "is_listed", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isListed = false;

    @Min(value = 1, message = "Total pages must be at least 1")
    @Column(name = "total_pages")
    private Integer totalPages;

    // Relation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id")
    private Users uploader;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users authors ;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Rating> ratings ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id")
    private BookList bookList;

    @ManyToMany
    @JoinTable(
            name = "book_to_type",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    private Set<BookType> types = new HashSet<>();

    public void addType(BookType type) {
        this.types.add(type);
        type.getBooks().add(this);
    }

    public void removeType(BookType type) {
        this.types.remove(type);
        type.getBooks().remove(this);
    }


//    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Comment> comments = new ArrayList<>();

    @PrePersist
    protected void onCreate(){
        publicationYear = LocalDate.now().getYear();
    }
}