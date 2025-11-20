package com.library.library.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.library.Utils.HtmlUtils;
import com.library.library.exception.infrastructure.AccountLockedException;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class AuthFailureHandler implements AuthenticationFailureHandler, AuthenticationEntryPoint {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {


        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));


        log.warn("Authentication failure for [{}]: {}", request.getRemoteAddr(), exception.getClass().getSimpleName(), exception);

        if (isAjax) {
            Map<String, Object> payload = Map.of(
                    "success", false,
                    "error", "Authentication failed"
            );

            response.setContentType("application/json");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            mapper.writeValue(response.getWriter(), payload);
            response.getWriter().flush();
            return;
        }

        String userMessage;
        if (exception instanceof AccountLockedException locked) {
            userMessage = "Account locked" + locked.getMessage();
        } else if (exception instanceof OAuth2AuthenticationException) {
            userMessage = "OAuth authentication failed";
        } else {
            userMessage = "Authentication failed";
        }
        String encodedMessage = URLEncoder.encode(userMessage, StandardCharsets.UTF_8);
        redirectStrategy.sendRedirect(request, response, "/login?error=" + encodedMessage);
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String safeMessage = HtmlUtils.sanitize(authException.getMessage());
        String encodedMessage = URLEncoder.encode("Authentication failed: " + safeMessage, StandardCharsets.UTF_8);
        redirectStrategy.sendRedirect(request, response, "/login?error=" + encodedMessage);
    }
}
