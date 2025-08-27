package com.library.library.Security;

import com.library.library.Exception.infrastructure.AccountLockedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private RememberMeProperties rememberMeProperties;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private CustomAuthFilter customAuthFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // HTTPS/SSL Enforcement
//                .requiresChannel(channel -> channel
//                        .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
//                        .requiresSecure()
//                )

                // Session Management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
                )

                // CSRF Protection
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/login-back", "/register-back",
                                "/login",
                                "/register",
                                "/favicon.ico",
                                "/login/oauth2/code/**", // Allow OAuth2 callback
                                "/oauth2/authorization/**" // Allow OAuth2 initiation
                                 )
                )

                // Authorization
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/favicon.ico",
                                "/login-back",
                                "/register-back",
                                "/error",
                                "/us/**",
                                "/css/**",
                                "settings",
                                "/js/**",
                                "/images/**",
                                "/h2lib/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // Exception Handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler((request, response, ex) ->
                                authenticationFailureHandler().onAuthenticationFailure(
                                        request,
                                        response,
                                        new AccountLockedException("Access denied", Instant.now().plus(30, ChronoUnit.MINUTES))
                                )
                        )
                )

                // Form Login
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler())
                        .permitAll()
                )

                // OAuth2 Login
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(user -> user.userService(auth2UserService()))
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler())
                )

                // Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                // Security Headers
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                                .preload(true)
                        )
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self' https:; " +
                                        "script-src 'self' https:; " +
                                        "style-src 'self' https:; " +
                                        "img-src 'self' data: https:; " +
                                        "font-src 'self' https: data:; " +
                                        "connect-src 'self' https:; " +
                                        "frame-ancestors 'self'; " +
                                        "upgrade-insecure-requests;")
                        )

                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // If the request is an AJAX request, then we return a JSON response.
                            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                                response.setContentType("application/json");
                                response.setStatus(HttpStatus.FORBIDDEN.value());
                                response.getWriter().write("{ \"error\": \"" + accessDeniedException.getMessage() + "\" }");
                            } else {
                                // Redirect to dedicated access denied page or to home with a flash attribute?
                                // We'll redirect to /access-denied
                                response.sendRedirect(request.getContextPath() + "/access-denied");
                            }
                        })
                )
                // Remember Me
                .rememberMe(remember -> remember
                        .rememberMeServices(rememberMeServices())
                        .key(rememberMeProperties.getSecretKey())
                        .tokenValiditySeconds(rememberMeProperties.getExpiration())
                )
                .addFilterBefore(customAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public TokenBasedRememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(
                rememberMeProperties.getSecretKey(),
                userDetailsService
        );
        rememberMe.setParameter("remember-me");
        rememberMe.setCookieName("remember-me-cookie");
        rememberMe.setTokenValiditySeconds(86400);
        rememberMe.setAlwaysRemember(false);
        return rememberMe;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthSuccessHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthFailureHandler();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthFailureHandler();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> auth2UserService() {
        return new com.library.library.Security.OAuth2UserService();
    }
}