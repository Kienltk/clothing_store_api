package com.clothingstore.clothing_store_api.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private Key SECRET_KEY;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.accessTokenExpirationMs:3600000}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refreshTokenExpirationMs:604800000}")
    private long refreshTokenExpirationMs;

    @PostConstruct
    private void initSecretKey() {
        if (secretKey == null || secretKey.trim().isEmpty() || secretKey.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET_KEY must be at least 32 characters long and not null/empty");
        }
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, String role) {
        if (username == null || username.trim().isEmpty() || role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and role must not be null or empty");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String generateRefreshToken(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty");
        }
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(SECRET_KEY)
                .compact();
    }

    public boolean isTokenValid(String token, String username) {
        if (token == null || token.trim().isEmpty() || username == null || username.trim().isEmpty()) {
            return false;
        }
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token must not be null or empty");
        }
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    private boolean isTokenExpired(String token) {
        if (token == null || token.trim().isEmpty()) {
            return true;
        }
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public Map<String, String> generateTokenPair(String username, String role) {
        if (username == null || username.trim().isEmpty() || role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and role must not be null or empty");
        }
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", generateToken(username, role));
        tokens.put("refresh_token", generateRefreshToken(username));
        return tokens;
    }
}
