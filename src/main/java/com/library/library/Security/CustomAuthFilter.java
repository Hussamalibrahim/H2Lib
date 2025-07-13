package com.library.library.Security;

import com.library.library.Exception.infrastructure.AccountLockedException;
import com.library.library.Security.Interfaces.LoginAttemptTracker;
import com.library.library.Security.JWT.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtService jwtService;

    @Autowired
    private LoginAttemptTracker attemptService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (isPublicEndpoint(request)) {
            log.warn("\n\n\n\n\n the request is illegal\n\n\n\n\n\n");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = getJwtFromRequest(request);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String email = jwtService.extractUsername(jwt);

            // Check if account is blocked before processing
            if (attemptService.isAccountLocked(email)) {
                Instant lockedUntil = Instant.now().plus(LoginAttemptService.LOCK_DURATION);
                throw new AccountLockedException("Account locked", lockedUntil);
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.validateToken(jwt, email)) {  // Modified to only validate token
                    attemptService.loginSucceeded(email);

                    // Create basic authentication without UserDetails
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            jwtService.extractAuthorities(jwt)  // Extract authorities from JWT
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }  catch (Exception e) {
            handleAuthenticationError(response, e, jwt);
            return;
        }

        logger.info("JWT JwtAuthenticationFilter work superficially");
        filterChain.doFilter(request, response);
    }

    // to split the bearer from authorization header
    private String getJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length()).trim();
            // Basic validation of token structure
            if (token.split("\\.").length != 3) {
                log.warn("Invalid JWT structure in Authorization header");
                return null;
            }
            return token;
        }
        return null;
    }

    private void handleAuthenticationError(HttpServletResponse response, Exception e, String jwt) throws IOException {
        switch (e) {
            case JwtException jwtException -> {
                log.error("JWT processing error: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            }
            case BadCredentialsException badCredentialsException -> {
                String username = null;
                try {
                    username = jwtService.extractUsername(jwt);
                } catch (Exception ex) {
                    log.warn("Could not extract username from invalid JWT");
                }

                if (username != null) {
                    attemptService.loginFailed(username);
                    int remaining = attemptService.getRemainingAttempts(username);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                            "Invalid credentials. Remaining attempts: " + remaining);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
                }
            }
            case AccountLockedException accountLockedException ->
                    response.sendError(423, "Account locked until " + accountLockedException.getLockedUntil());
            case null, default -> {
                log.error("Authentication error", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            }
        }
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getServletPath();
        log.info("the current path is :{}",path);
        return path.startsWith("/login-back") ||
                path.startsWith("/register-back") ||
                path.startsWith("/settings") ||
                path.startsWith("/oauth2") ||
                path.startsWith("/error") ||
                path.startsWith("/images");
    }
}