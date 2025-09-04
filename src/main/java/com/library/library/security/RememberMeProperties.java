package com.library.library.security;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Component
@Getter
public class RememberMeProperties {
    @Value("${remember-me-secret}")
    private String secretKey;

    @Value("${remember-me-expiration}")
    private int expiration;

    @Setter
    private boolean alwaysRemember = false;
}
