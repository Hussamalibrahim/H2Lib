package com.library.library.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {
    private static final Logger log = LoggerFactory.getLogger(LogoutService.class);

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logging out user: {}",
                request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous");

        SecurityContextHolder.clearContext();

        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        // Delete JWT cookie
        deleteCookie("token", request, response);
        deleteCookie("remember-me-cookie", request, response);

        log.info("Logout completed, JWT cookie removed");
    }


    private void deleteCookie(String name, HttpServletRequest request, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(request.isSecure())
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
