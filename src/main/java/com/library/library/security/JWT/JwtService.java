package com.library.library.security.JWT;

import com.library.library.model.UserCredentials;
import com.library.library.repository.UserCredentialsRepository;
import com.library.library.security.UserPrincipal;
import com.library.library.security.UserPrincipalImp;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final UserCredentialsRepository userCredentialsRepository;

    public JwtService(JwtProperties jwtProperties, UserCredentialsRepository userCredentialsRepository) {
        this.jwtProperties = jwtProperties;
        this.userCredentialsRepository = userCredentialsRepository;
    }

    public String generateToken(UserPrincipal userPrincipal) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("email", userPrincipal.getEmail());
        extraClaims.put("authorities", userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userPrincipal.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpiration()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
            return !isTokenExpired(token) && StringUtils.hasText(extractSubject(token));
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public UserPrincipal getPrincipalFromToken(String token) {
        Claims claims = extractAllClaims(token);
        String email = claims.getSubject();

        Optional<UserCredentials> optionalUser = userCredentialsRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            log.warn("JWT contains unknown email: {}", email);
            return null;
        }

        UserPrincipal principal = new UserPrincipalImp(optionalUser.get());
        principal.setJwtToken(token);
        return principal;
    }

    private Key getSignInKey() {
        byte[] keyBytes = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
