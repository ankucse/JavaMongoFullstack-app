package com.example.java_mongo_fullstack_app.auth;

import com.example.java_mongo_fullstack_app.entity.User;
import com.example.java_mongo_fullstack_app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Attempting to load user from database by email: '{}'", email);
        
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            logger.warn("Authentication failed: No user found in database with email: '{}'", email);
            throw new UsernameNotFoundException("No user found with email: " + email);
        }
        
        User user = userOptional.get();
        logger.info("User found in DB. Email: '{}', Role: '{}', Enabled: {}", user.getEmail(), user.getRole(), user.isEnabled());
        
        // Fix the double-prefix bug: ensure role strictly starts with exactly one "ROLE_"
        String cleanRole = user.getRole().replace("ROLE_", "");
        String finalRole = "ROLE_" + cleanRole;
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority(finalRole))
        );
    }
}
