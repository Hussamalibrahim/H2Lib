package com.library.library.model;


import com.library.library.model.enumerations.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_certificate")
public class UserCredentials {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Users user;

    //FIXME i think in could be null if user sign in with OAuth2
    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = true)
    private String email;// Nullable for OAuth2 users

    //it could be null if the user sign in with oauth2
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(name = "password", nullable = true)
    private String password; // Nullable for OAuth2 users


    @Column(name = "enabled", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean enabled = true;

    @Column(name = "expired", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean expired;

    @Column(name = "email_verified", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean emailVerified = false;

//    @Column(name = "credentials_expired", columnDefinition = "BOOLEAN DEFAULT FALSE")
//    private Boolean credentialsExpired;

    @Column(name = "locked", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean locked = false;


    @Column(name = "locked_at")
    private Instant lockedAt;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;


    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;

    @ElementCollection
    @CollectionTable(name = "user_providers", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "provider")
    private Set<String> providers
            = new HashSet<>();


    public boolean isPermanentlyLocked() {
        return Boolean.TRUE.equals(locked);
    }

    public boolean isAccountExpired() {
        return Boolean.TRUE.equals(expired);
    }

    public void addProvider(String provider) {
        this.providers.add(provider);
    }

    public boolean hasProvider(String provider) {
        return this.providers.contains(provider);
    }

    public boolean isOAuth2User() {
        return !this.providers.isEmpty();
    }
}
