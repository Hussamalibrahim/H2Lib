package com.library.library.Model.Dto;

import com.library.library.Model.Enumerations.Role;
import com.library.library.Model.Users;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Set;


@Data
@RequiredArgsConstructor
public class UserCredentialsDto {

    private Long id;

    private Users user;


    @Email(message = "Invalid email format")
    @Column(name = "email")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private Boolean enabled;

    private Boolean emailVerified;

    private Boolean locked = false;

    private Instant lockedAt;

    private Instant passwordChangedAt;

    private Set<String> providers;

    private Role role;

}
