package com.library.library.security;

import com.library.library.security.JWT.JwtService;
import com.library.library.Utils.TypeFiles;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.net.URI;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public AuthSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtService.generateToken(principal);
        principal.setJwtToken(jwt);

        setAuthCookies(request, response, jwt);

        boolean isAjax = (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"))
                || "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (isAjax) {
            // Return structured JSON
            Map<String, Object> payload = Map.of(
                    "success", true,
                    "token", jwt,
                    "redirectUrl", "/"
            );
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            mapper.writeValue(response.getWriter(), payload);
            response.getWriter().flush();
            return;
        }

        handleRedirect(request, response);
    }

    private void updateUserFromOAuthAttributes(UserPrincipal principal) {
        if (principal == null) return;
        Map<String, Object> attributes = principal.getAttributes();
        if (attributes == null || attributes.isEmpty()) return;

        log.debug("OAuth attributes keys: {}", attributes.keySet());

        principal.getUsersCredentials().setEmailVerified(true);

        if (principal.getUsers() != null) {
            if (principal.getUsers().getDisplayName() == null) {
                Object nameObj = attributes.get("name");
                if (nameObj instanceof String name && !name.isBlank()) {
                    principal.getUsers().setDisplayName(name);
                }
            }

            if (principal.getUsers().getImageUrl() == null) {
                Object imageUrlObj = attributes.get("picture") != null ? attributes.get("picture") : attributes.get("avatar_url");
                if (imageUrlObj instanceof String imageUrl && !imageUrl.isBlank()) {
                    principal.getUsers().setImageUrl(imageUrl);
                    principal.getUsers().setImageContentType(TypeFiles.getFileExtension(imageUrl));
                }
            }
        }
    }

    private void setAuthCookies(HttpServletRequest request, HttpServletResponse response, String jwt) {
        ResponseCookie cookie = ResponseCookie.from("token", jwt)
                .httpOnly(true)
                .secure(request.isSecure())
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void handleRedirect(HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        String redirectUrl = "/";

        HttpSession session = request.getSession(false);
        if (session != null) {
            Object saved = session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
            if (saved instanceof DefaultSavedRequest savedRequest) {
                String savedUrl = savedRequest.getRedirectUrl();
                String safe = sanitizeRedirectTarget(savedUrl);
                if (safe != null) {
                    redirectUrl = safe;
                }
            }
        }

        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }

    /**
     * Ensure redirect targets are internal (prevent open-redirect).
     * Returns path-only string (starting with "/") or null when not allowed.
     */
    private String sanitizeRedirectTarget(String candidate) {
        if (candidate == null || candidate.isBlank()) return null;

        try {
            URI uri = URI.create(candidate);

            if (uri.isAbsolute()) return null; // Only allow relative URLs

            String path = uri.getPath();
            if (path == null || !path.startsWith("/")) return null;

            if (path.contains("\n") || path.contains("\r")) return null; // basic sanitation

            String query = uri.getQuery();
            return (query == null || query.isEmpty()) ? path : path + "?" + query;

        } catch (Exception e) {
            log.debug("Invalid saved redirect URL: {}", candidate);
            return null;
        }
    }
}
