package com.poc.microservices.user.authentication.repository;

import com.poc.microservices.user.authentication.model.entity.User;
import com.poc.microservices.user.authentication.model.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("securePass");
        user.setUserRole(UserRole.EMPLOYEE);
        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByUsername("testUser");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUsername());
    }
}
