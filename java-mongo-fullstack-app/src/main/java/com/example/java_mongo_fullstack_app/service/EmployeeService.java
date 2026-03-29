package com.example.java_mongo_fullstack_app.service;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.example.java_mongo_fullstack_app.model.Employee;
import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.example.java_mongo_fullstack_app.model.EmploymentType;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    
    // Existing methods used by EmployeeController (using EmployeeDto)
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

    // Methods used by Auth and Dashboard (using Employee model)
    List<Employee> findAllEmployeeModels();
    Optional<Employee> findEmployeeModelById(String id);
    Employee saveEmployeeModel(Employee employee);
    Optional<Employee> findModelByEmail(String email);
}
