package com.poc.microservices.user.authentication.service;

import com.poc.microservices.user.authentication.model.entity.User;
import com.poc.microservices.user.authentication.model.entity.UserRole;
import com.poc.microservices.user.authentication.repository.UserRepository;
import com.poc.microservices.user.authentication.service.helper.JwtLocalHelperUAM;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Initializes mocks automatically
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy // Spy ensures real encryption behavior is used
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Spy
    JwtLocalHelperUAM jwtLocalHelperUAM;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        System.setProperty("SECRET_KEY", "someUsefulLargeEnoughSecretKeyToBeAtLeast256Bits");
        Assertions.assertNotNull(jwtLocalHelperUAM);
    }

    // Test authenticateUser() - Success Case
    @Test
    void testAuthenticateUser_Success() {
        User mockUser = new User(1L, "testUser", passwordEncoder.encode("correctPassword"), UserRole.ADMIN);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        String token = userService.authenticateUser("testUser", "correctPassword");

        assertNotNull(token, "Token should not be null on successful authentication.");
    }

    // Test authenticateUser() - Incorrect Password
    @Test
    void testAuthenticateUser_IncorrectPassword() {
        User mockUser = new User(1L, "testUser", passwordEncoder.encode("correctPassword"), UserRole.ADMIN);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        String token = userService.authenticateUser("testUser", "wrongPassword");

        assertNull(token, "Token should be null when authentication fails.");
    }

    // Test authenticateUser() - Non-Existent User
    @Test
    void testAuthenticateUser_UserNotFound() {
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        String token = userService.authenticateUser("nonExistentUser", "anyPassword");

        assertNull(token, "Token should be null when user does not exist.");
    }
}