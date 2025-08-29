package com.library.library.Security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class LogoutService {
    private static final Logger log = LoggerFactory.getLogger(LogoutService.class);

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("Logging out user: {}", request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous");

        SecurityContextHolder.clearContext();

        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        log.info("Logout completed, JWT cookie removed");
    }
}

