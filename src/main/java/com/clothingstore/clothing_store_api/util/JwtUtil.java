package com.clothingstore.clothing_store_api.util;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final Key SECRET_KEY;
    @Value("${jwt.accessTokenExpirationMs:3600000}")
    private  long accessTokenExpirationMs;

    @Value("${jwt.refreshTokenExpirationMs:604800000}")
    private  long refreshTokenExpirationMs;

    public JwtUtil() {
        Dotenv dotenv = Dotenv.configure().directory("./").ignoreIfMissing().load();
        String secret = dotenv.get("JWT_SECRET_KEY");
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET_KEY must be at least 32 characters long");
        }
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(SECRET_KEY)
                .compact();
    }
    private boolean isTokenExpired(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }
    public Map<String, String> generateTokenPair(String username, String role) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", generateToken(username, role));
        tokens.put("refresh_token", generateRefreshToken(username));
        return tokens;
    }
}