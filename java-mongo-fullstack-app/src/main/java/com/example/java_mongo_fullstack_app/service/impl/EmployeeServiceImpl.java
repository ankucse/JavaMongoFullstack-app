package com.example.java_mongo_fullstack_app.service.impl;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.example.java_mongo_fullstack_app.exception.ResourceNotFoundException;
import com.example.java_mongo_fullstack_app.model.Employee;
import com.example.java_mongo_fullstack_app.repository.EmployeeRepository;
import com.example.java_mongo_fullstack_app.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        log.info("START: createEmployee - Request received: {}", employeeDto);
        Employee employee = mapToEntity(employeeDto);
        Employee savedEmployee = employeeRepository.save(employee);
        EmployeeDto savedEmployeeDto = mapToDto(savedEmployee);
        log.info("END: createEmployee - Response sent: {}", savedEmployeeDto);
        return savedEmployeeDto;
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
            throw ex;
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
        } catch (Exception ex) {
            log.error("ERROR: getEmployeeById - Exception occurred: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public EmployeeDto updateEmployee(String id, EmployeeDto employeeDto) {
        log.info("START: updateEmployee - Request received for ID: {}, Data: {}", id, employeeDto);
        try {
            Employee existingEmployee = employeeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

            existingEmployee.setName(employeeDto.getName());
            existingEmployee.setEmail(employeeDto.getEmail());
            existingEmployee.setDepartment(employeeDto.getDepartment());
            existingEmployee.setSalary(employeeDto.getSalary());

            Employee updatedEmployee = employeeRepository.save(existingEmployee);
            EmployeeDto updatedEmployeeDto = mapToDto(updatedEmployee);
            log.info("END: updateEmployee - Response sent: {}", updatedEmployeeDto);
            return updatedEmployeeDto;
        } catch (Exception ex) {
            log.error("ERROR: updateEmployee - Exception occurred: {}", ex.getMessage(), ex);
            throw ex;
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
        } catch (Exception ex) {
            log.error("ERROR: deleteEmployee - Exception occurred: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    private Employee mapToEntity(EmployeeDto dto) {
        if (dto == null) {
            return null;
        }
        return Employee.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .salary(dto.getSalary())
                .build();
    }

    private EmployeeDto mapToDto(Employee entity) {
        if (entity == null) {
            return null;
        }
        return EmployeeDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .department(entity.getDepartment())
                .salary(entity.getSalary())
                .build();
    }
}