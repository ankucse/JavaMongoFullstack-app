package com.example.java_mongo_fullstack_app.controller;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.example.java_mongo_fullstack_app.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        log.info("START: createEmployee API - Request received: {}", employeeDto);
        try {
            EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);
            log.info("END: createEmployee API - Response sent: {}", createdEmployee);
            return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
        } catch (Exception ex) {
            log.error("ERROR: createEmployee API - Exception occurred: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        log.info("START: getAllEmployees API - Request received");
        try {
            List<EmployeeDto> employees = employeeService.getAllEmployees();
            log.info("END: getAllEmployees API - Response sent, size: {}", employees.size());
            return new ResponseEntity<>(employees, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("ERROR: getAllEmployees API - Exception occurred: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable String id) {
        log.info("START: getEmployeeById API - Request received for ID: {}", id);
        try {
            EmployeeDto employeeDto = employeeService.getEmployeeById(id);
            log.info("END: getEmployeeById API - Response sent: {}", employeeDto);
            return new ResponseEntity<>(employeeDto, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("ERROR: getEmployeeById API - Exception occurred: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable String id, @Valid @RequestBody EmployeeDto employeeDto) {
        log.info("START: updateEmployee API - Request received for ID: {}, Data: {}", id, employeeDto);
        try {
            EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDto);
            log.info("END: updateEmployee API - Response sent: {}", updatedEmployee);
            return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("ERROR: updateEmployee API - Exception occurred: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        log.info("START: deleteEmployee API - Request received for ID: {}", id);
        try {
            employeeService.deleteEmployee(id);
            log.info("END: deleteEmployee API - Successfully deleted ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            log.error("ERROR: deleteEmployee API - Exception occurred: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
