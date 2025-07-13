package com.library.library.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "rating_table", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rating_id")
    private Long id;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column(name = "user_rating", nullable = false, updatable = false)
    private Integer rating;//5 stars


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_of_rating", updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateOfRating;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @PrePersist
    protected void onCreate() {
        dateOfRating = new Date();
    }
}
