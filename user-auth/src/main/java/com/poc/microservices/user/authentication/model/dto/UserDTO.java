package com.poc.microservices.user.authentication.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.poc.microservices.user.authentication.model.entity.User;

@Data
@NoArgsConstructor
public class UserDTO {
    private String username;
    private String password;
    private String role;

    // Constructor to convert User entity to DTO
    public UserDTO(User user) {
        if (user == null) {
            return;
        }
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getUserRole().name(); // Convert Enum to String
    }

}