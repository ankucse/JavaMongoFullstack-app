package com.example.java_mongo_fullstack_app.service.impl;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.example.java_mongo_fullstack_app.exception.ResourceNotFoundException;
import com.example.java_mongo_fullstack_app.exception.ValidationException;
import com.example.java_mongo_fullstack_app.model.Employee;
import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.example.java_mongo_fullstack_app.model.EmploymentType;
import com.example.java_mongo_fullstack_app.repository.EmployeeRepository;
import com.example.java_mongo_fullstack_app.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        log.info("START: createEmployee - Request received: {}", employeeDto);
        try {
            // Check for duplicate email
            Optional<Employee> existingEmployee = employeeRepository.findByEmail(employeeDto.getEmail());
            if (existingEmployee.isPresent()) {
                log.warn("Validation failed: Email already exists - {}", employeeDto.getEmail());
                throw new ValidationException("Email already exists");
            }

            Employee employee = mapToEntity(employeeDto);
            Employee savedEmployee = employeeRepository.save(employee);
            EmployeeDto savedEmployeeDto = mapToDto(savedEmployee);
            log.info("END: createEmployee - Response sent: {}", savedEmployeeDto);
            return savedEmployeeDto;
        } catch (ValidationException ex) {
             throw ex;
        } catch (Exception ex) {
            log.error("ERROR: createEmployee - Exception occurred: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to create employee", ex);
        }
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        log.info("START: getAllEmployees - Request received");
        try {
            List<Employee> employees = employeeRepository.findAll();
            List<EmployeeDto> employeeDtos = employees.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
            log.info("END: getAllEmployees - Response sent, count: {}", employeeDtos.size());
            return employeeDtos;
        } catch (Exception ex) {
            log.error("ERROR: getAllEmployees - Exception occurred: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to fetch employees", ex);
        }
    }

    @Override
    public EmployeeDto getEmployeeById(String id) {
        log.info("START: getEmployeeById - Request received for ID: {}", id);
        try {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
            EmployeeDto employeeDto = mapToDto(employee);
            log.info("END: getEmployeeById - Response sent: {}", employeeDto);
            return employeeDto;
        } catch (ResourceNotFoundException ex) {
            log.warn("ERROR: getEmployeeById - {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("ERROR: getEmployeeById - Exception occurred: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to fetch employee", ex);
        }
    }

    @Override
    public EmployeeDto updateEmployee(String id, EmployeeDto employeeDto) {
        log.info("START: updateEmployee - Request received for ID: {}, Data: {}", id, employeeDto);
        try {
            Employee existingEmployee = employeeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

            // Check if email is being changed to an existing one
            if (!existingEmployee.getEmail().equals(employeeDto.getEmail())) {
                Optional<Employee> emailCheck = employeeRepository.findByEmail(employeeDto.getEmail());
                if (emailCheck.isPresent()) {
                    log.warn("Validation failed: Email already exists - {}", employeeDto.getEmail());
                    throw new ValidationException("Email already exists");
                }
            }

            // Update fields
            existingEmployee.setFirstName(employeeDto.getFirstName());
            existingEmployee.setLastName(employeeDto.getLastName());
            existingEmployee.setEmail(employeeDto.getEmail());
            existingEmployee.setPhoneNumber(employeeDto.getPhoneNumber());
            existingEmployee.setDepartment(employeeDto.getDepartment());
            existingEmployee.setDesignation(employeeDto.getDesignation());
            existingEmployee.setRole(employeeDto.getRole());
            existingEmployee.setEmploymentType(employeeDto.getEmploymentType());
            existingEmployee.setDateOfJoining(employeeDto.getDateOfJoining());
            existingEmployee.setExperienceYears(employeeDto.getExperienceYears());
            existingEmployee.setSalary(employeeDto.getSalary());
            existingEmployee.setBonus(employeeDto.getBonus());
            if (employeeDto.getCurrency() != null) existingEmployee.setCurrency(employeeDto.getCurrency());
            existingEmployee.setStatus(employeeDto.getStatus());
            existingEmployee.setManagerName(employeeDto.getManagerName());
            existingEmployee.setWorkLocation(employeeDto.getWorkLocation());
            existingEmployee.setProfileImageUrl(employeeDto.getProfileImageUrl());
            existingEmployee.setNotes(employeeDto.getNotes());

            Employee updatedEmployee = employeeRepository.save(existingEmployee);
            EmployeeDto updatedEmployeeDto = mapToDto(updatedEmployee);
            log.info("END: updateEmployee - Response sent: {}", updatedEmployeeDto);
            return updatedEmployeeDto;
        } catch (ResourceNotFoundException | ValidationException ex) {
             throw ex;
        } catch (Exception ex) {
            log.error("ERROR: updateEmployee - Exception occurred: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to update employee", ex);
        }
    }

    @Override
    public void deleteEmployee(String id) {
        log.info("START: deleteEmployee - Request received for ID: {}", id);
        try {
            Employee existingEmployee = employeeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
            employeeRepository.delete(existingEmployee);
            log.info("END: deleteEmployee - Successfully deleted ID: {}", id);
        } catch (ResourceNotFoundException ex) {
            log.warn("ERROR: deleteEmployee - {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("ERROR: deleteEmployee - Exception occurred: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to delete employee", ex);
        }
    }

    @Override
    public List<EmployeeDto> searchEmployeesByName(String name) {
        log.info("START: searchEmployeesByName - Name: {}", name);
        try {
            List<Employee> employees = employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
            List<EmployeeDto> result = employees.stream().map(this::mapToDto).collect(Collectors.toList());
            log.info("END: searchEmployeesByName - Found {} employees", result.size());
            return result;
        } catch (Exception ex) {
             log.error("ERROR: searchEmployeesByName - Exception occurred: {}", ex.getMessage(), ex);
             throw new RuntimeException("Failed to search employees", ex);
        }
    }

    @Override
    public EmployeeDto searchEmployeeByEmail(String email) {
         log.info("START: searchEmployeeByEmail - Email: {}", email);
         try {
             Employee employee = employeeRepository.findByEmail(email)
                     .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + email));
             EmployeeDto result = mapToDto(employee);
             log.info("END: searchEmployeeByEmail - Found");
             return result;
         } catch (ResourceNotFoundException ex) {
              throw ex;
         } catch (Exception ex) {
             log.error("ERROR: searchEmployeeByEmail - Exception occurred: {}", ex.getMessage(), ex);
             throw new RuntimeException("Failed to search employee", ex);
         }
    }

    @Override
    public List<EmployeeDto> filterEmployeesByDepartment(String department) {
        log.info("START: filterEmployeesByDepartment - Department: {}", department);
        try {
            List<Employee> employees = employeeRepository.findByDepartment(department);
            List<EmployeeDto> result = employees.stream().map(this::mapToDto).collect(Collectors.toList());
            log.info("END: filterEmployeesByDepartment - Found {} employees", result.size());
            return result;
        } catch (Exception ex) {
             log.error("ERROR: filterEmployeesByDepartment - Exception occurred: {}", ex.getMessage(), ex);
             throw new RuntimeException("Failed to filter employees", ex);
        }
    }

    @Override
    public List<EmployeeDto> filterEmployeesByStatus(EmployeeStatus status) {
         log.info("START: filterEmployeesByStatus - Status: {}", status);
         try {
             List<Employee> employees = employeeRepository.findByStatus(status);
             List<EmployeeDto> result = employees.stream().map(this::mapToDto).collect(Collectors.toList());
             log.info("END: filterEmployeesByStatus - Found {} employees", result.size());
             return result;
         } catch (Exception ex) {
              log.error("ERROR: filterEmployeesByStatus - Exception occurred: {}", ex.getMessage(), ex);
              throw new RuntimeException("Failed to filter employees", ex);
         }
    }

    @Override
    public List<EmployeeDto> filterEmployeesByEmploymentType(EmploymentType employmentType) {
        log.info("START: filterEmployeesByEmploymentType - Type: {}", employmentType);
        try {
            List<Employee> employees = employeeRepository.findByEmploymentType(employmentType);
            List<EmployeeDto> result = employees.stream().map(this::mapToDto).collect(Collectors.toList());
            log.info("END: filterEmployeesByEmploymentType - Found {} employees", result.size());
            return result;
        } catch (Exception ex) {
             log.error("ERROR: filterEmployeesByEmploymentType - Exception occurred: {}", ex.getMessage(), ex);
             throw new RuntimeException("Failed to filter employees", ex);
        }
    }


    private Employee mapToEntity(EmployeeDto dto) {
        if (dto == null) {
            return null;
        }
        return Employee.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .department(dto.getDepartment())
                .designation(dto.getDesignation())
                .role(dto.getRole())
                .employmentType(dto.getEmploymentType())
                .dateOfJoining(dto.getDateOfJoining())
                .experienceYears(dto.getExperienceYears())
                .salary(dto.getSalary())
                .bonus(dto.getBonus())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "INR")
                .status(dto.getStatus())
                .managerName(dto.getManagerName())
                .workLocation(dto.getWorkLocation())
                .profileImageUrl(dto.getProfileImageUrl())
                .notes(dto.getNotes())
                .build();
    }

    private EmployeeDto mapToDto(Employee entity) {
        if (entity == null) {
            return null;
        }
        return EmployeeDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .department(entity.getDepartment())
                .designation(entity.getDesignation())
                .role(entity.getRole())
                .employmentType(entity.getEmploymentType())
                .dateOfJoining(entity.getDateOfJoining())
                .experienceYears(entity.getExperienceYears())
                .salary(entity.getSalary())
                .bonus(entity.getBonus())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .managerName(entity.getManagerName())
                .workLocation(entity.getWorkLocation())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .profileImageUrl(entity.getProfileImageUrl())
                .notes(entity.getNotes())
                .build();
    }
}
