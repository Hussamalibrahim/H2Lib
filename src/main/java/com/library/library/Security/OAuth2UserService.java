package com.library.library.Security;

import com.library.library.Model.UserCredentials;
import com.library.library.Service.UserCredentialsService;
import com.library.library.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import com.library.library.Model.Users;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
@Slf4j
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserCredentialsService userCredentialsService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPrincipal userPrincipal;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("\n\n\n");
        log.info("OAuth2UserRequest: {}", userRequest);
        log.info("Access token: {}", userRequest.getAccessToken().getTokenValue());
        log.info("Additional parameters: {}", userRequest.getAdditionalParameters());
        System.out.println("\n\n\n");

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // For GitHub, we need to fetch email separately
        if ("github".equals(registrationId)) {
            attributes = fetchGitHubUserDetails(userRequest.getAccessToken().getTokenValue());
        }

        return processOAuth2User(registrationId, attributes);
    }

    private Map<String, Object> fetchGitHubUserDetails(String accessToken) throws OAuth2AuthenticationException {
        try {
            // Get primary email from GitHub
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "token " + accessToken);

            // First get user emails
            ResponseEntity<List<Map<String, Object>>> emailsResponse = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {
                    }
            );

            // Find primary email
            String email = null;
            if (emailsResponse.getBody() != null) {
                email = emailsResponse.getBody().stream()
                        .filter(e -> Boolean.TRUE.equals(e.get("primary")))
                        .map(e -> (String) e.get("email"))
                        .findFirst()
                        .orElseThrow(() -> new OAuth2AuthenticationException("No primary email found"));
            }

            // Get user details
            ResponseEntity<Map<String, Object>> userResponse = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {
                    }
            );

            Map<String, Object> userDetails = userResponse.getBody();
            if (userDetails != null) {
                userDetails.put("email", email);
            }
            return userDetails;

        } catch (Exception e) {
            throw new OAuth2AuthenticationException("Failed to fetch GitHub user details");
        }
    }

    private OAuth2User processOAuth2User(String provider, Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<UserCredentials> credentialsOptional = userCredentialsService.findByEmail(email);

        if (credentialsOptional.isPresent()) {
            UserCredentials credentials = credentialsOptional.get();
            if (!credentials.hasProvider(provider)) {
                credentials.addProvider(provider);
                userCredentialsService.save(credentials);
            }
            return userPrincipal.create(credentials, attributes);
        } else {
            return registerNewOAuthUser(provider, email, attributes);
        }
    }

    private OAuth2User registerNewOAuthUser(String provider, String email, Map<String, Object> attributes) {
        Users user = new Users();
        user.setDisplayName((String) attributes.get("name"));

        // Handle profile picture
        if ("google".equals(provider)) {
            user.setImageUrl((String) attributes.get("picture"));
        } else if ("github".equals(provider)) {
            user.setImageUrl((String) attributes.get("avatar_url"));
        }

        Users savedUser = userService.save(user);

        UserCredentials credentials = new UserCredentials();
        credentials.setUser(savedUser);
        credentials.setEmail(email);
        credentials.setEnabled(true);
        credentials.setEmailVerified(true);
        credentials.addProvider(provider);

        UserCredentials savedCredentials = userCredentialsService.save(credentials);
        return userPrincipal.create(savedCredentials, attributes);
    }
}