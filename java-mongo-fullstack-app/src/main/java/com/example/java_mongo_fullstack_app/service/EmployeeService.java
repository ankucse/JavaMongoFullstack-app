package com.example.java_mongo_fullstack_app.service;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.example.java_mongo_fullstack_app.model.EmploymentType;

import java.util.List;

public interface EmployeeService {

    EmployeeDto createEmployee(EmployeeDto employeeDto);

    List<EmployeeDto> getAllEmployees();

    EmployeeDto getEmployeeById(String id);

    EmployeeDto updateEmployee(String id, EmployeeDto employeeDto);

    void deleteEmployee(String id);

    List<EmployeeDto> searchEmployeesByName(String name);

    EmployeeDto searchEmployeeByEmail(String email);

    List<EmployeeDto> filterEmployeesByDepartment(String department);

    List<EmployeeDto> filterEmployeesByStatus(EmployeeStatus status);

    List<EmployeeDto> filterEmployeesByEmploymentType(EmploymentType employmentType);
}
