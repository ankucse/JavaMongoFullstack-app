package com.example.java_mongo_fullstack_app;

import com.example.java_mongo_fullstack_app.entity.User;
import com.example.java_mongo_fullstack_app.repository.UserRepository;
import com.example.java_mongo_fullstack_app.service.AuthService;
import com.vaadin.flow.component.page.AppShellConfigurator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class JavaMongoFullstackAppApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(JavaMongoFullstackAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner createInitialUsers(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        return args -> {
            if (userRepository.count() == 0) {
                System.out.println("No users found. Creating initial ADMIN and USER accounts...");

                // Create an ADMIN user
                authService.registerUser("Admin User", "admin@example.com", "adminpass", "ADMIN");
                System.out.println("Admin user created: admin@example.com / adminpass");

                // Create a regular USER
                authService.registerUser("Regular User", "user@example.com", "userpass", "USER");
                System.out.println("Regular user created: user@example.com / userpass");

                // Create an employee record for the regular user to see in their dashboard
                // This assumes you have an EmployeeService and EmployeeRepository
                // You'll need to inject and use your actual EmployeeService here
                // For demonstration, I'll add a placeholder if EmployeeService was injected
                // If you have an existing EmployeeService, you'd use it like this:
                // EmployeeService employeeService = context.getBean(EmployeeService.class);
                // employeeService.saveEmployee(new Employee("Regular", "User", "user@example.com", "IT"));
            }
        };
    }
}
