package com.poc.microservices.user.authentication.controller;

import com.poc.microservices.user.authentication.model.dto.UserDTO;
import com.poc.microservices.user.authentication.model.entity.User;
import com.poc.microservices.user.authentication.model.entity.UserRole;
import com.poc.microservices.user.authentication.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class) // Enables Spring test features
@WebMvcTest(UserController.class) // Focus only on UserController
@ContextConfiguration(classes = {TestSecurityConfig.class})
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;


    // Test registration endpoint
    @Test
    void testRegisterUser() throws Exception {
        String requestBody = """
                    {
                        "username": "testUser",
                        "password": "securePass",
                        "role": "EMPLOYER"
                    }
                """;

        when(userService.saveUser(Mockito.any(UserDTO.class))).thenReturn(new UserDTO(new User(1L, "testUser",
                "securePass", UserRole.EMPLOYER))); // Mock service response

        mvc.perform(post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON) // Use correct MediaType
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.username").value("testUser")); // Verify response
    }

    // Test authentication endpoint
    @Test
    void testLogin() throws Exception {
        when(userService.authenticateUser("testUser", "securePass")).thenReturn("mockJWT");

        mvc.perform(post("/users/login")
                        .with(csrf())
                        .param("username", "testUser")
                        .param("password", "securePass"))
                .andExpect(status().isOk())
                .andExpect(content().string("mockJWT"));
    }

    // Test employer authorization (with mock token)
    @Test
    void testGetEmployer_Authorized() throws Exception {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.isAuthenticated()).thenReturn(true);
        when(mockAuth.getAuthorities()).thenAnswer(invocation -> Collections.singletonList(new SimpleGrantedAuthority("ROLE_EMPLOYER")));
        SecurityContextHolder.getContext().setAuthentication(mockAuth);

        mvc.perform(get("/users/employer"))
                .andExpect(status().isOk())
                .andExpect(content().string("Test employer"));
    }

    // Test employer authorization failure (missing JWT)
    @Test
    void testGetEmployer_Unauthorized() throws Exception {
        mvc.perform(get("/users/employer"))
                .andExpect(status().isForbidden()); // Expect HTTP 403
    }
}