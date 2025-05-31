package com.poc.microservices.user.authentication.service;

import lombok.RequiredArgsConstructor;
import com.poc.microservices.user.authentication.model.dto.UserDTO;
import com.poc.microservices.user.authentication.model.entity.UserRole;
import com.poc.microservices.user.authentication.model.entity.User;
import com.poc.microservices.user.authentication.repository.UserRepository;
import com.poc.microservices.user.authentication.service.helper.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {
    private final JwtHelper jwtHelper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserDTO saveUser(UserDTO userDTO) {
        String password = passwordEncoder.encode(userDTO.getPassword());
        User user = new User(null, userDTO.getUsername(), password, UserRole.valueOf(userDTO.getRole()));
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }

    public String authenticateUser(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return jwtHelper.generateToken(user.getUserRole().name()); // Return JWT token
            }
        }
        return null;
    }

    public UserDTO findByUsername(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        return new UserDTO(byUsername.orElse(new User()));
    }
}