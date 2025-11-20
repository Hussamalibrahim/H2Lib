package com.library.library.security.securityFilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.library.security.JWT.JwtService;
import com.library.library.security.UserPrincipalImp;
import com.library.library.security.interfaces.LoginAttemptTracker;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

import static com.library.library.Utils.JsonRead.getJsonBody;

@Slf4j
public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtService jwtService;
    private final TokenBasedRememberMeServices rememberMeServices;
    private final LoginAttemptTracker loginAttemptService;

    public JsonUsernamePasswordAuthenticationFilter(AuthenticationManager authManager,
                                                    JwtService jwtService,
                                                    TokenBasedRememberMeServices rememberMeServices,
                                                    LoginAttemptTracker loginAttemptService) {
        super.setAuthenticationManager(authManager);
        this.jwtService = jwtService;
        this.rememberMeServices = rememberMeServices;
        this.setFilterProcessesUrl("/login-back");
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            try {
                Map<String, String> creeds = objectMapper.readValue(
                        request.getInputStream(), new TypeReference<>() {}
                );

                String username = creeds.getOrDefault("email", "").trim();
                String password = creeds.getOrDefault("password", "");
                if (password != null) password = password.trim();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, password);

                setDetails(request, authToken);
                return this.getAuthenticationManager().authenticate(authToken);

            } catch (IOException e) {
                throw new RuntimeException("Failed to parse authentication request body", e);
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

        Map<String, String> body = getJsonBody(request);
        String email = body.get("email");

        if (loginAttemptService != null) {
            loginAttemptService.loginSucceeded(email);
        }

        String token = jwtService.generateToken(principal);
        principal.setJwtToken(token);

        setAuthCookies(request, response, token);

        rememberMeServices.loginSuccess(request, response, authResult);

        Map<String, Object> payload = Map.of(
                "success", true,
                "token", token,
                "redirectUrl", "/"
        );
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), payload);
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {

        Map<String, String> body = getJsonBody(request);
        String email = body.get("email");

        if (email != null && loginAttemptService != null) {
            loginAttemptService.loginFailed(email);
        }

        Map<String, Object> payload = Map.of(
                "success", false,
                "message", failed.getMessage(),
                "remainingAttempts",
                email != null ? Objects.requireNonNull(loginAttemptService).getRemainingAttempts(email) : null, "lockedUntil",
                Objects.requireNonNull(email != null ? loginAttemptService.getLockedUntil(email).orElse(null) : null)
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), payload);
    }

    private void setAuthCookies(HttpServletRequest request, HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(request.isSecure())
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
