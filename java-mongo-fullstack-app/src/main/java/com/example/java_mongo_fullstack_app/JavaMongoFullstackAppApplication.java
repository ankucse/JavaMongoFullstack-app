package com.example.java_mongo_fullstack_app;

import com.example.java_mongo_fullstack_app.entity.User;
import com.example.java_mongo_fullstack_app.repository.UserRepository;
import com.example.java_mongo_fullstack_app.service.AuthService;
import com.example.java_mongo_fullstack_app.service.EmployeeService;
import com.example.java_mongo_fullstack_app.model.Employee;
import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Collections;

@SpringBootApplication
public class JavaMongoFullstackAppApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(JavaMongoFullstackAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner createInitialUsers(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService, EmployeeService employeeService) {
        return args -> {
            if (userRepository.count() == 0) {
                System.out.println("No users found. Creating initial ADMIN and USER accounts...");

                // Create an ADMIN user
                authService.registerUser("Admin User", "admin@example.com", "adminpass", "ADMIN");
                System.out.println("Admin user created: admin@example.com / adminpass");

                // Create a regular USER
                authService.registerUser("Regular User", "user@example.com", "userpass", "USER");
                System.out.println("Regular user created: user@example.com / userpass");

                // Temporarily impersonate an ADMIN to safely scaffold the initial Employee profiles
                SecurityContext originalContext = SecurityContextHolder.getContext();
                try {
                    SecurityContext tempContext = SecurityContextHolder.createEmptyContext();
                    tempContext.setAuthentication(new UsernamePasswordAuthenticationToken("system_seeder", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))));
                    SecurityContextHolder.setContext(tempContext);

                    // Ensure an Employee profile exists for the admin user
                    Employee adminProfile = new Employee();
                    adminProfile.setFirstName("Admin");
                    adminProfile.setLastName("User");
                    adminProfile.setEmail("admin@example.com");
                    adminProfile.setDepartment("Management");
                    adminProfile.setRole("Administrator");
                    adminProfile.setStatus(EmployeeStatus.ACTIVE);
                    employeeService.saveEmployeeModel(adminProfile);

                    // Ensure an Employee profile exists for the regular user
                    Employee userProfile = new Employee();
                    userProfile.setFirstName("Regular");
                    userProfile.setLastName("User");
                    userProfile.setEmail("user@example.com");
                    userProfile.setDepartment("IT");
                    userProfile.setRole("Developer");
                    userProfile.setStatus(EmployeeStatus.ACTIVE);
                    employeeService.saveEmployeeModel(userProfile);
                } finally {
                    SecurityContextHolder.setContext(originalContext); // Clean up context
                }
            }
        };
    }
}
