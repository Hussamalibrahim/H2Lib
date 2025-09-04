package com.library.library.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.library.security.interfaces.LoginAttemptTracker;
import com.library.library.security.JWT.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import java.io.IOException;
import java.util.Map;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JwtService jwtService;

    private final TokenBasedRememberMeServices rememberMeServices;

    private final LoginAttemptTracker loginAttemptService;



    public JsonUsernamePasswordAuthenticationFilter(AuthenticationManager authManager,
                                                    JwtService jwtService,
                                                    TokenBasedRememberMeServices rememberMeServices, LoginAttemptTracker loginAttemptService) {
        super.setAuthenticationManager(authManager);
        this.jwtService = jwtService;
        this.rememberMeServices = rememberMeServices;
        this.setFilterProcessesUrl("/login-back");
        this.loginAttemptService=loginAttemptService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            try {
                Map<String, String> creds = objectMapper.readValue(request.getInputStream(), Map.class);

                String username = creds.get("email");
                String password = creds.get("password");

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, password);

                setDetails(request, authToken);
                return this.getAuthenticationManager().authenticate(authToken);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return super.attemptAuthentication(request, response);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        UserPrincipalImp principal = (UserPrincipalImp) authResult.getPrincipal();
        String email = principal.getEmail();

        if (loginAttemptService != null) {
            loginAttemptService.loginSucceeded(email);
        }

        String token = jwtService.generateToken(principal);
        principal.setJwtToken(token);

        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        response.addCookie(cookie);

        rememberMeServices.loginSuccess(request, response, authResult);

        response.setContentType("application/json");
        response.getWriter().write("{\"success\": true, \"token\": \"" + token + "\", \"redirectUrl\": \"/\"}");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        String email = request.getParameter("email");
        if (email != null && loginAttemptService != null) {
            loginAttemptService.loginFailed(email);
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"success\": false, \"message\": \"" + failed.getMessage() + "\"}");
    }

}
