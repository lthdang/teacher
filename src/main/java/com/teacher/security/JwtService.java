package com.teacher.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed HS256 JWT for the given admin.
     *
     * @param adminId the admin UUID (used as the JWT subject)
     * @param email   the admin email (stored as a claim)
     * @return signed JWT string
     */
    public String generateToken(UUID adminId, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(adminId.toString())
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Parses and validates a JWT token.
     *
     * @param token the JWT string (without "Bearer " prefix)
     * @return the Claims payload if valid
     * @throws JwtException if the token is invalid or expired
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Returns the admin UUID (subject) from a raw JWT string.
     * Throws JwtException if invalid.
     */
    public UUID extractAdminId(String token) {
        return UUID.fromString(parseToken(token).getSubject());
    }

    /** Returns the token expiration in milliseconds (for response bodies). */
    public long getExpirationMs() {
        return expirationMs;
    }
}
