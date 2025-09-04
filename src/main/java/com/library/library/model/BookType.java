package com.library.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "book_types")
@Getter @Setter
public class BookType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_types_id")
    private Long id;

    @NotBlank(message = "Type name is required")
    @Size(max = 30, message = "Type name must be less than 50 characters")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase with hyphens")
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Min(value = 0, message = "Book count cannot be negative")
    @Column(name = "book_count", columnDefinition = "INT DEFAULT 0")
    private int bookCount = 0;

    @ManyToMany(mappedBy = "types")
    private Set<Book> books = new HashSet<>();

}