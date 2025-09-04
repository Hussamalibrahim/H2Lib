package com.library.library.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "book_list")
@Entity
public class BookList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "list_id")
    private Long id;

    @NotBlank(message = "List name is required")
    @Size(max = 30, message = "List name must be less than 30 characters")
    @Column(name = "name", nullable = false)
    private String name;


    @Temporal(TemporalType.DATE)
    @Column(name = "created_date", nullable = false, updatable = false)
    private Date createdDate;

    @Column(name = "is_default", nullable = false,columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDefault;

    @ManyToOne
    private Users user;


    @OneToMany(mappedBy = "bookList", orphanRemoval = true)
    private Set<Book> books = new LinkedHashSet<>();

    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
    }

}
