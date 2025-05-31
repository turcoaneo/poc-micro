package com.poc.microservices.user.authentication.controller;

import com.poc.microservices.user.authentication.model.dto.UserDTO;
import com.poc.microservices.user.authentication.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

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

        ResponseEntity<String> token = userController.login("user", "password");

        assertEquals("mockJWT", token.getBody());
        verify(userService).authenticateUser("user", "password");
    }


    // Test for registerUser()
    @Test
    void testRegisterUser() {
        UserDTO mockUser = getUserDTO();
        when(userService.saveUser(any(UserDTO.class))).thenReturn(mockUser);

        ResponseEntity<UserDTO> userDTOResponseEntity = userController.registerUser(mockUser);
        UserDTO result = userDTOResponseEntity.getBody();

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