package com.example.java_mongo_fullstack_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employees")
public class Employee {

    @Id
    private String id;

    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;
    private String phoneNumber;

    private String department;
    private String designation;
    private String role;
    private EmploymentType employmentType;
    private LocalDate dateOfJoining;
    private Integer experienceYears;

    private Double salary;
    private Double bonus;
    private String currency;

    private EmployeeStatus status;
    private String managerName;
    private String workLocation;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String profileImageUrl;
    private String notes;
}
