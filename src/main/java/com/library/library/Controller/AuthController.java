package com.library.library.Controller;

import com.library.library.Model.Dto.LoginRequest;
import com.library.library.Model.Dto.RegistrationDto;
import com.library.library.Security.JWT.JwtService;
import com.library.library.Security.UserPrincipal;
import com.library.library.Service.UserCredentialsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserCredentialsService userCredentialsService;

    @GetMapping("/login")
    public String loginPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        // make it aop if user logged in
        // one condition is enough
        if (userPrincipal != null && userPrincipal.isOAuth2User()) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && userPrincipal.isOAuth2User()) {
            return "redirect:/";
        }
        return "register";
    }

    @GetMapping("/logout")
    public String logoutPage(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return "redirect:/";
        }
        return "logout";
    }

    @PostMapping(value = "/login-back", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginBack(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            if (loginRequest.getEmail() == null || loginRequest.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "fieldErrors", Map.of("email", "Email is required")
                ));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail().trim(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String jwt = jwtService.generateToken(userPrincipal);

            responseBody.put("success", true);
            responseBody.put("token", jwt);
            responseBody.put("redirectUrl", "/");
            return ResponseEntity.ok(responseBody);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Invalid email or password"
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Authentication failed"
            ));
        }
    }


    @PostMapping(value = "/register-back", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerBack(@RequestBody RegistrationDto registrationRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (registrationRequest.getEmail() == null || registrationRequest.getEmail().trim().isEmpty()) {
                response.put("success", false);
                response.put("fieldErrors", Map.of("email", "Email is required"));
                return ResponseEntity.badRequest().body(response);
            }
            if (!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())) {
                response.put("success", false);
                response.put("fieldErrors", Map.of("confirmError", "Passwords do not match"));
                return ResponseEntity.badRequest().body(response);
            }
            registrationRequest.setEmail(registrationRequest.getEmail().trim());
            if (registrationRequest.getName() != null) {
                registrationRequest.setName(registrationRequest.getName().trim());
            }
            userCredentialsService.create(registrationRequest);
            response.put("success", true);
            response.put("message", "Registration successful. Please login.");
            response.put("redirectUrl", "/login");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
