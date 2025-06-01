package com.poc.microservices.main.app.config.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@NoArgsConstructor
public class JwtHelper {
    public String getRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
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

    private static SecretKey getSigningKey() {
        final String keyName = "SECRET_KEY";
        final String secretKey = System.getenv(keyName) != null ? System.getenv(keyName) : System.getProperty(keyName);

        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("SECRET_KEY environment variable is not set!");
        }

        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}