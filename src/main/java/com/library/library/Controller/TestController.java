//package com.library.library.Controller;
//
//import com.library.library.Exception.infrastructure.AccountLockedException;
//import com.library.library.Model.Dto.RegistrationDto;
//import com.library.library.Model.Dto.UserCredentialsDto;
//import com.library.library.Model.UserCredentials;
//import com.library.library.Security.JWT.JwtService;
//import com.library.library.Security.UserPrincipal;
//import com.library.library.Service.UserCredentialsService;
//import com.library.library.Service.UserService;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.Valid;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.time.Instant;
//import java.util.Map;
//
//@Slf4j
//@Controller
//@AllArgsConstructor
//public class AuthController {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//    @Autowired
//    private JwtService jwtService;
//
//    @Autowired
//    private UserCredentialsService userCredentialsService;
//
//    @Autowired
//    private UserService userService;
//
//
//    @GetMapping("/login")
//    public String loginPage() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
//            return "redirect:/";
//        }
//        return "login";
//    }
//
//
//    @GetMapping("/register")
//    public String registerPage() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
//            return "redirect:/";
//        }
//        return "register";
//    }
//
//    @GetMapping("/logout")
//    public String logoutPage() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null) {
//            return "redirect:/";
//        }
//        return "logout";
//    }
//
//    @PostMapping("/login-back")
//    public ResponseEntity<?> login(@RequestBody @Valid UserCredentialsDto loginRequest) {
//        final String loginEmail = loginRequest.getEmail();
//        log.info("Login attempt for email: {}", loginEmail);
//
//        try {
//            // Check if account exists first
//            UserCredentials credentials = userCredentialsService.findByEmail(loginEmail)
//                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
//
//            // Check if account is disabled
//            if (!Boolean.TRUE.equals(credentials.getEnabled())) {
//                log.warn("Login attempt for disabled account: {}", loginEmail);
//                throw new DisabledException("Account is disabled. Please contact support.");
//            }
//
//            // Check if account is locked
//            if (Boolean.TRUE.equals(credentials.getLocked())) {
//                Instant lockedUntil = userCredentialsService.getLockedUntil(loginEmail);
//                log.warn("Login attempt for locked account: {}", loginEmail);
//                throw new AccountLockedException("Account is temporarily locked", lockedUntil);
//            }
//
//            // Authenticate user
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            loginEmail,
//                            loginRequest.getPassword()
//                    )
//            );
//
//            // Authentication successful
//            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
//            String jwt = jwtService.generateToken(principal);
//            log.info("Successful login for user: {}", principal.getUsername());
//
//            // Reset login attempts on success
//            userCredentialsService.loginSucceeded(loginEmail);
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
//                    .body(Map.of(
//                            "status", "success",
//                            "redirectUrl", "/",
//                            "user", principal.getUsername(),
//                            "role", principal.getAuthorities()
//                    ));
//        } catch (DataAccessException e) {
//            log.error("Database error during registration", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of(
//                            "status", "error",
//                            "message", "Registration failed due to database error"
//                    ));
//        } catch (AccountLockedException e) {
//            return ResponseEntity.status(HttpStatus.LOCKED)
//                    .body(Map.of(
//                            "status", "error",
//                            "message", "Account is locked",
//                            "lockedUntil", e.getLockedUntil().toString()
//                    ));
//
//        } catch (DisabledException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of(
//                            "status", "error",
//                            "message", "Account is disabled. Please contact support."
//                    ));
//
//        } catch (BadCredentialsException e) {
//            log.warn("Bad credentials for {}", loginEmail);
//            userCredentialsService.loginFailed(loginEmail);
//            int remainingAttempts = userCredentialsService.getRemainingAttempts(loginEmail);
//
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of(
//                            "status", "error",
//                            "message", "Invalid email or password",
//                            "remainingAttempts", remainingAttempts
//                    ));
//
//        } catch (Exception e) {
//            log.error("Unexpected login error for {}: {}", loginEmail, e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of(
//                            "status", "error",
//                            "message", "An unexpected error occurred"
//                    ));
//        }
//    }
//
//    @PostMapping("/register-back")
//    public ResponseEntity<?> register(@RequestBody @Valid RegistrationDto registerRequest) {
//        log.info("going inside register-back");
//        log.info("Registration attempt - Name: {}, Email: {}",
//                registerRequest.getName(), registerRequest.getEmail());
//        log.debug("Password: {}, Confirm: {}",
//                registerRequest.getPassword(), registerRequest.getConfirmPassword());
//        try {
//            if (userCredentialsService.existsByEmail(registerRequest.getEmail())) {
//                return ResponseEntity.badRequest()
//                        .body(Map.of("status", "error", "message", "Email already exists"));
//            }
//            if (userService.existsByDisplayName(registerRequest.getName())) {
//                return ResponseEntity.badRequest()
//                        .body(Map.of("status", "error", "message", "Username already exists"));
//            }
//
//            UserCredentials savedUser = userCredentialsService.create(registerRequest);
//
//            log.info("the user that have sign in Role :{}",savedUser.getRole());
//            log.info("the user that have sign in Email :{}",savedUser.getEmail());
//            log.info("the user that have sign in Email :{}",savedUser.getPassword());
//
//
//            return ResponseEntity.ok()
//                    .body(Map.of(
//                            "status", "success",
//                            "redirectUrl", "/login",
//                            "message", "Registration successful"
//                    ));
//        } catch (DataAccessException e) {
//            log.error("Database error during registration", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of(
//                            "status", "error",
//                            "message", "Registration failed due to database error"
//                    ));
//        } catch (Exception e) {
//            log.error("Registration failed", e);
//            return ResponseEntity.badRequest()
//                    .body(Map.of(
//                            "status", "error",
//                            "message", e.getMessage()
//                    ));
//        }
//    }
//}
//
