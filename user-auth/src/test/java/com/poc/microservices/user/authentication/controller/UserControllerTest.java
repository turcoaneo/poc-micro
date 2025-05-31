package com.poc.microservices.user.authentication.controller;

import com.poc.microservices.user.authentication.model.dto.UserDTO;
import com.poc.microservices.user.authentication.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
    }

    // Test for login()
    @Test
    void testLogin() {
        when(userService.authenticateUser("user", "password")).thenReturn("mockJWT");

        String token = userController.login("user", "password");

        assertEquals("mockJWT", token);
        verify(userService).authenticateUser("user", "password");
    }


    // Test for registerUser()
    @Test
    void testRegisterUser() {
        UserDTO mockUser = getUserDTO();
        when(userService.saveUser(any(UserDTO.class))).thenReturn(mockUser);

        UserDTO result = userController.registerUser(mockUser);

        assertNotNull(result);
        assertEquals("user", result.getUsername());
        verify(userService).saveUser(any(UserDTO.class));
    }

    private static UserDTO getUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("user");
        userDTO.setPassword("password");
        return userDTO;
    }
}