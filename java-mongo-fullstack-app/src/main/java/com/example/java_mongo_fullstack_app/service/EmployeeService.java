package com.example.java_mongo_fullstack_app.service;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {

    EmployeeDto createEmployee(EmployeeDto employeeDto);

    List<EmployeeDto> getAllEmployees();

    EmployeeDto getEmployeeById(String id);

    EmployeeDto updateEmployee(String id, EmployeeDto employeeDto);

    void deleteEmployee(String id);
}
