package com.example.java_mongo_fullstack_app.service;

import com.example.java_mongo_fullstack_app.entity.User;
import com.example.java_mongo_fullstack_app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> registerUser(String name, String email, String password, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return Optional.empty(); // User with this email already exists
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password)); // Encode password
        newUser.setRole(role);
        newUser.setEnabled(true); // Enable user by default

        return Optional.of(userRepository.save(newUser));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
