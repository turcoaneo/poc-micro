package com.poc.microservices.user.authentication.service;

import com.poc.microservices.user.authentication.model.dto.UserDTO;
import com.poc.microservices.user.authentication.model.entity.User;
import com.poc.microservices.user.authentication.model.entity.UserRole;
import com.poc.microservices.user.authentication.repository.UserRepository;
import com.poc.microservices.user.authentication.service.helper.JwtLocalHelperUAM;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    private final JwtLocalHelperUAM jwtLocalHelperUAM;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO saveUser(UserDTO userDTO) {
        try {
            String password = passwordEncoder.encode(userDTO.getPassword());
            User user = new User(null, userDTO.getUsername(), password, UserRole.valueOf(userDTO.getRole()));
            User savedUser = userRepository.saveAndFlush(user);
            return new UserDTO(savedUser);
        } catch (RuntimeException runtimeException) {
            logger.error("Could not save ", runtimeException);
        }
        return null;
    }

    public String authenticateUser(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                final String keyName = "SECRET_KEY";
                final String secretKey = System.getenv(keyName) != null ? System.getenv(keyName) : System.getProperty(keyName);
                return jwtLocalHelperUAM.generateToken(user.getUserRole().name(), secretKey); // Return JWT token
            }
        }
        return null;
    }

    public UserDTO findByUsername(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        return new UserDTO(byUsername.orElse(new User()));
    }
}