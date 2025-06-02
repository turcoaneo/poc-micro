package com.poc.microservices;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // Initializes mocks automatically
public class JwtHelperTest {

    @InjectMocks
    private JwtHelper jwtHelper;

    private final String secretKey = "someUsefulLargeEnoughSecretKeyToBeAtLeast256Bits";

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGenerateToken_Valid() {
        String token = jwtHelper.generateToken("EMPLOYER", secretKey);
        assertNotNull(token); // Token must not be null
    }

    @Test
    void testGetRoleFromValidToken() {
        String token = jwtHelper.generateToken("ADMIN", secretKey);
        String role = jwtHelper.getRoleFromToken(token, secretKey);

        assertEquals("ADMIN", role); // Token should extract the correct role
    }

    @Test
    void testExpiredToken_ThrowsException() {
        Instant issuedAt = Instant.now().minus(10, ChronoUnit.MINUTES); // Simulate an expired token
        Instant expiration = issuedAt.plus(1, ChronoUnit.MINUTES);

        String expiredToken = Jwts.builder()
                .claims().subject("USER").and()
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(this.getTestSigningKey(), Jwts.SIG.HS256)
                .compact();

        JwtException exception = assertThrows(JwtException.class, () -> jwtHelper.getRoleFromToken(expiredToken, secretKey));
        assertEquals("Invalid or expired token", exception.getMessage());
    }

    private SecretKey getTestSigningKey() {
        return Keys.hmacShaKeyFor("someUsefulLargeEnoughSecretKeyToBeAtLeast256Bits".getBytes());
    }


    @Test
    void testInvalidToken_ThrowsException() {
        JwtException exception = assertThrows(JwtException.class, () -> jwtHelper.getRoleFromToken("fakeToken", secretKey));
        assertEquals("Invalid or expired token", exception.getMessage());
    }
}