package com.library.library.security;

import com.library.library.model.enumerations.Role;
import com.library.library.model.UserCredentials;
import com.library.library.model.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
@Component
public class UserPrincipalImp implements UserPrincipal {

    private final UserCredentials userCredentials;
    private String jwtToken;
    private final Map<String, Object> attributes;

    // Default constructor for framework
    public UserPrincipalImp() {
        this.userCredentials = null;
        this.attributes = null;
    }

    public UserPrincipalImp(UserCredentials user) {
        this.userCredentials = user;
        this.attributes = null;
    }

    public UserPrincipalImp(UserCredentials user, Map<String, Object> attributes) {
        this.userCredentials = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userCredentials.getRole().name()));
    }

    @Override
    public String getPassword() {
        return userCredentials != null ? userCredentials.getPassword() : null;
    }

    @Override
    public String getUsername() {
        return userCredentials != null ? userCredentials.getEmail() :
                attributes != null ? (String) attributes.get("email") : null;
    }


    @Override
    public Users getUsers() {
        return userCredentials != null ? userCredentials.getUser() : null;
    }

    @Override
    public UserCredentials getUsersCredentials() {
        return userCredentials;
    }

    @Override
    public boolean isAccountNonExpired() {
        return userCredentials != null && userCredentials.getRole() != Role.EXPIRED ||
                userCredentials != null && !userCredentials.isAccountExpired();
    }


    @Override
    public boolean isAccountNonLocked() {
        return userCredentials != null &&
                !Boolean.TRUE.equals(userCredentials.getLocked());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userCredentials != null && (
                userCredentials.getPasswordChangedAt() == null ||
                        userCredentials.getPasswordChangedAt()
                                .isAfter(Instant.now().minus(90, ChronoUnit.DAYS))
        );
    }

    @Override
    public boolean isEnabled() {
        return userCredentials != null &&
                Boolean.TRUE.equals(userCredentials.getEnabled());
    }


    @Override
    public String getName() {
        return userCredentials != null &&
                userCredentials.getUser() != null
                ? userCredentials.getUser().getDisplayName()
                : null;
    }


    public Long getId() {
        return userCredentials != null ? userCredentials.getId() : null;
    }

    public String getEmail() {
        return userCredentials != null ? userCredentials.getEmail() : null;
    }

    public Set<String> getProviders() {
        return userCredentials != null ? userCredentials.getProviders() : Set.of();
    }

    public boolean isOAuth2User() {
        return userCredentials != null && !userCredentials.getProviders().isEmpty();
    }

    @Override
    public String getJwtToken() {
        return jwtToken;
    }

    @Override
    public void setJwtToken(String token) {
        this.jwtToken = token;
    }

    public UserPrincipalImp create(UserCredentials userCredentials, Map<String, Object> attributes) {
        return new UserPrincipalImp(userCredentials,attributes);
    }
}
