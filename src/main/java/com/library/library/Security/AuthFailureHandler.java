package com.library.library.Security;

import com.library.library.Exception.infrastructure.AccountLockedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

@Slf4j
@Component
public class AuthFailureHandler implements AuthenticationFailureHandler, AuthenticationEntryPoint {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        log.info("\n\n\nuser come here\nn\n\n\n");
        if (exception instanceof AccountLockedException lockedEx) {
            handleLockedAccount(request, response, lockedEx);
        } else if (exception instanceof OAuth2AuthenticationException oauthEx) {
            handleOAuthFailure(request, response, oauthEx);
        } else {
            handleGenericFailure(request, response, exception);
        }
    }

    private void handleLockedAccount(HttpServletRequest request,
                                     HttpServletResponse response,
                                     AccountLockedException exception) throws IOException {

        String encodedMessage = URLEncoder.encode(
                "Account locked until " + exception.getLockedUntil(),
                StandardCharsets.UTF_8
        );
        redirectStrategy.sendRedirect(request, response,
                "/login?error=" + encodedMessage + "&code=423");
    }

    private void handleOAuthFailure(HttpServletRequest request,
                                    HttpServletResponse response,
                                    OAuth2AuthenticationException exception) throws IOException {
        String errorMessage = determineOAuthErrorMessage(exception);
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        redirectStrategy.sendRedirect(request, response, "/login?error=" + encodedMessage);
    }

    private void handleGenericFailure(HttpServletRequest request,
                                      HttpServletResponse response,
                                      AuthenticationException exception) throws IOException {
        String errorMessage = "Authentication failed: " + exception.getMessage();
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        redirectStrategy.sendRedirect(request, response, "/login?error=" + encodedMessage);
    }

    private String determineOAuthErrorMessage(OAuth2AuthenticationException exception) {
        String errorCode = exception.getError().getErrorCode();
        return switch (errorCode) {
            case "access_denied" -> "You denied the access request";
            case "invalid_token" -> "Invalid authentication token";
            case "unauthorized_client" -> "Client not authorized";
            default -> "OAuth authentication failed: " + exception.getMessage();
        };
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        handleGenericFailure(request, response, authException);
    }
}