package com.poc.microservices.user.authentication.service.helper;

import com.poc.microservices.JwtHelper;
import io.jsonwebtoken.JwtException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class JwtLocalHelper {
    @Value("${jwt.expirationMinutes: 60}")
    private long expirationMinutes;

    public String generateToken(String role, String secretKey) {
        try {
            return new JwtHelper(expirationMinutes).generateToken(role, secretKey);
        } catch (JwtException e) {
            throw new JwtException("Invalid or expired token", e);
        }
    }

    public String getRoleFromToken(String token, String secretKey) {
        return new JwtHelper(null).getRoleFromToken(token, secretKey);
    }
}