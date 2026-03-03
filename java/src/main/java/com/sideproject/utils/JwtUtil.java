package com.sideproject.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessExpirationMillis;
    private final long refreshExpirationMillis;

    private JwtUtil(Builder builder) {
        this.accessKey = builder.accessKey;
        this.refreshKey = builder.refreshKey;
        this.accessExpirationMillis = builder.accessExpirationMillis;
        this.refreshExpirationMillis = builder.refreshExpirationMillis;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, accessKey, accessExpirationMillis);
    }

    public String generateRefreshToken(String subject) {
        return generateToken(subject, Map.of(), refreshKey, refreshExpirationMillis);
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accessKey);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshKey);
    }

    public String extractSubjectFromAccessToken(String token) {
        return parseClaims(token, accessKey).getSubject();
    }

    public String extractSubjectFromRefreshToken(String token) {
        return parseClaims(token, refreshKey).getSubject();
    }

    private String generateToken(String subject, Map<String, Object> claims, SecretKey key, long expirationMillis) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMillis);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    private boolean validateToken(String token, SecretKey key) {
        try {
            parseClaims(token, key);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Claims parseClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static class Builder {
        private SecretKey accessKey;
        private SecretKey refreshKey;
        private long accessExpirationMillis;
        private long refreshExpirationMillis;

        public Builder accessKey(SecretKey accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public Builder refreshKey(SecretKey refreshKey) {
            this.refreshKey = refreshKey;
            return this;
        }

        public Builder accessExpirationMillis(long accessExpirationMillis) {
            this.accessExpirationMillis = accessExpirationMillis;
            return this;
        }

        public Builder refreshExpirationMillis(long refreshExpirationMillis) {
            this.refreshExpirationMillis = refreshExpirationMillis;
            return this;
        }

        public JwtUtil build() {
            if (accessKey == null || refreshKey == null) {
                throw new IllegalStateException("accessKey and refreshKey are required");
            }
            if (accessExpirationMillis <= 0 || refreshExpirationMillis <= 0) {
                throw new IllegalStateException("Token expiration must be greater than 0");
            }
            return new JwtUtil(this);
        }
    }
}

