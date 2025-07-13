package com.library.library.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "downloads")
public class Downloads {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "downloads_id")
    private Long id;

    @PastOrPresent(message = "Publication year cannot be in the future")
    @Min(value = 850, message = "Write year must be a valid year")
    @Max(value = 2100, message = "Write year must be a valid year")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_of_download", nullable = false)
    private Date dateOfDownload;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @PrePersist
    protected void onCreate() {
        dateOfDownload = new Date();
    }

}
