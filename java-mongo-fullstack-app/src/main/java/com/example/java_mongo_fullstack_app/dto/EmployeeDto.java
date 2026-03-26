package com.example.java_mongo_fullstack_app.dto;

import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.example.java_mongo_fullstack_app.model.EmploymentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    private String id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String department;
    private String designation;
    private String role;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotNull(message = "Date of joining is required")
    private LocalDate dateOfJoining;

    private Integer experienceYears;

    @Positive(message = "Salary must be positive")
    private Double salary;

    private Double bonus;

    @Builder.Default
    private String currency = "INR";

    @NotNull(message = "Status is required")
    private EmployeeStatus status;

    private String managerName;
    private String workLocation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String profileImageUrl;
    private String notes;
}
