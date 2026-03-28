package com.example.java_mongo_fullstack_app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.java_mongo_fullstack_app.entity.User;
import com.example.java_mongo_fullstack_app.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed Admin User
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@example.com");
                        
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN"); 
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("Seeded Admin User: admin@example.com / admin123");
        }

        // Seed Standard User
        if (userRepository.findByEmail("user@example.com").isEmpty()) {
            User user = new User();
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            userRepository.save(user);
            System.out.println("Seeded Standard User: user@example.com / user123");
        }
    }
}