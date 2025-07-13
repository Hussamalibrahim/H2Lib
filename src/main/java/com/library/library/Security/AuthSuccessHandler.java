package com.library.library.Security;

import com.library.library.Security.JWT.JwtService;
import com.library.library.Utils.TypeFiles;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtService jwtService;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("\n\n\nuser come AuthSuccessHandler\nn\n\n\n");

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // Update user details from OAuth attributes if available
        if (principal.getAttributes() != null) {
            updateUserFromOAuthAttributes(principal);
        }

        String jwt = jwtService.generateToken(principal);
        principal.setJwtToken(jwt);

        setAuthCookies(request, response, jwt);
        setAuthHeaders(response, jwt);
        handleRedirect(request, response);
    }

    private void updateUserFromOAuthAttributes(UserPrincipal principal) {
        Map<String, Object> attributes = principal.getAttributes();

        if (principal.getUsers() != null) {
            // Update display name if not set
            if (principal.getUsers().getDisplayName() == null) {
                String name = (String) attributes.get("name");
                if (name != null) {
                    principal.getUsers().setDisplayName(name);
                }
            }

            // Update avatar if not set
            if (principal.getUsers().getImageUrl() == null) {
                String imageUrl = (String) attributes.get("picture");
                if (imageUrl == null) {
                    imageUrl = (String) attributes.get("avatar_url");
                }
                if (imageUrl != null) {
                    principal.getUsers().setImageUrl(imageUrl);
                    principal.getUsers().setImageContentType(TypeFiles.getFileExtension(imageUrl));
                }
            }
        }
    }

    private void setAuthCookies(HttpServletRequest request,
                                HttpServletResponse response,
                                String jwt) {
        ResponseCookie cookie = ResponseCookie.from("token", jwt)
                .httpOnly(true)
                .secure(request.isSecure())
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void setAuthHeaders(HttpServletResponse response, String jwt) {
        response.addHeader("Authorization", "Bearer " + jwt);
    }

    private void handleRedirect(HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        String redirectUrl = "/"; // Default redirect

        // Check for saved request
        HttpSession session = request.getSession(false);
        if (session != null) {
            DefaultSavedRequest savedRequest = (DefaultSavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
            if (savedRequest != null && !savedRequest.getRequestURL().contains("/login")) {
                redirectUrl = savedRequest.getRequestURL();
            }
        }

        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}