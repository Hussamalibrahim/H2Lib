package com.library.library.security;

import com.library.library.model.UserCredentials;
import com.library.library.model.Users;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface UserPrincipal extends UserDetails, OAuth2User, Serializable {
    Long getId();

    String getEmail();

    Users getUsers();

     UserCredentials getUsersCredentials() ;

    // could be static
     UserPrincipalImp  create(UserCredentials user, Map<String, Object> attributes);

    Set<String> getProviders();

    boolean isOAuth2User();

    String getJwtToken();

    void setJwtToken(String token);
}