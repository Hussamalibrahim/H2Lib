package com.library.library.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "notifications_id")
    private Long id;

    @NotBlank(message = "Message is required")
    @Size(max = 500, message = "Message must be less than 500 characters")
    @Column(name = "message",nullable = false)
    private String massage;


    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_notifications", nullable = false, updatable = false)
    private LocalDate CreatedAt;

    // the book id could be url
    @NotBlank(message = "Slug is required")
    @Column(name = "target_id",nullable = false)
    private String targetId;

    //FIXME check if its read or not to give it specific color
    @Column(name="is_read")
    private Boolean isRead = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @PrePersist
    protected void onCreate() {
        CreatedAt = LocalDate.now();
    }

}
