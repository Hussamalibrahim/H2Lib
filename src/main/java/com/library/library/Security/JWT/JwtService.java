package com.library.library.Security.JWT;

import com.library.library.Model.UserCredentials;
import com.library.library.Repository.UserCredentialsRepository;
import com.library.library.Security.UserPrincipal;
import com.library.library.Security.UserPrincipalImp;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    // Generate token with extra claims
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

    // Validate token (expiration + signature)
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
            return !isTokenExpired(token) && StringUtils.hasText(extractUsername(token));
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

    public String extractUsername(String token) {
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

    public Collection<? extends GrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        @SuppressWarnings("unchecked")
        List<String> authorities = claims.get("authorities", List.class);
        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    // Build UserPrincipal from token
    public UserPrincipalImp getPrincipalFromToken(String token) {
        Claims claims = extractAllClaims(token);
        String email = claims.getSubject();

        Optional<UserCredentials> optionalUser = userCredentialsRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            log.warn("JWT contains unknown email: {}", email);
            return null;  // skip setting authentication
        }

        UserPrincipalImp principal = new UserPrincipalImp(optionalUser.get());
        principal.setJwtToken(token);
        return principal;
    }


    // Key for signing and validating
    private Key getSignInKey() {
        byte[] keyBytes = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
