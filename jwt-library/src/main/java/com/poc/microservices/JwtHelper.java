package com.poc.microservices;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public class JwtHelper {
    private final Long expirationMinutes;

    public JwtHelper(Long expirationMinutes) {
        this.expirationMinutes = Objects.requireNonNullElse(expirationMinutes, 60L);
    }

    public String generateToken(String role, String secretKeyValue) {
        return Jwts.builder()
                .claims().subject(role).and()
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(Duration.ofMinutes(expirationMinutes))))
                .signWith(getSigningKey(secretKeyValue), Jwts.SIG.HS256)
                .compact();
    }

    public String getRoleFromToken(String token, String secretKeyValue) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey(secretKeyValue))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Check if token is expired
            if (claims.getExpiration().before(new Date())) {
                throw new JwtException("Token has expired"); // Or handle it gracefully
            }

            return claims.getSubject(); // Extract role if valid
        } catch (JwtException e) {
            throw new JwtException("Invalid or expired token", e);
        }
    }

    private static SecretKey getSigningKey(String secretKeyValue) {
        return Keys.hmacShaKeyFor(secretKeyValue.getBytes());
    }
}