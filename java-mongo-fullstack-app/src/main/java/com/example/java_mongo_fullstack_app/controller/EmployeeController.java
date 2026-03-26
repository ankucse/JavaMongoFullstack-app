package com.example.java_mongo_fullstack_app.controller;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.example.java_mongo_fullstack_app.model.EmploymentType;
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
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);
        log.info("END: createEmployee API - Response sent: {}", createdEmployee);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        log.info("START: getAllEmployees API - Request received");
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        log.info("END: getAllEmployees API - Response sent, size: {}", employees.size());
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable String id) {
        log.info("START: getEmployeeById API - Request received for ID: {}", id);
        EmployeeDto employeeDto = employeeService.getEmployeeById(id);
        log.info("END: getEmployeeById API - Response sent: {}", employeeDto);
        return new ResponseEntity<>(employeeDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable String id, @Valid @RequestBody EmployeeDto employeeDto) {
        log.info("START: updateEmployee API - Request received for ID: {}, Data: {}", id, employeeDto);
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDto);
        log.info("END: updateEmployee API - Response sent: {}", updatedEmployee);
        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        log.info("START: deleteEmployee API - Request received for ID: {}", id);
        employeeService.deleteEmployee(id);
        log.info("END: deleteEmployee API - Successfully deleted ID: {}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeDto>> searchEmployeesByName(@RequestParam String name) {
         log.info("START: searchEmployeesByName API - Name: {}", name);
         List<EmployeeDto> employees = employeeService.searchEmployeesByName(name);
         log.info("END: searchEmployeesByName API - Found: {}", employees.size());
         return ResponseEntity.ok(employees);
    }

    @GetMapping("/search/email")
    public ResponseEntity<EmployeeDto> searchEmployeeByEmail(@RequestParam String email) {
         log.info("START: searchEmployeeByEmail API - Email: {}", email);
         EmployeeDto employee = employeeService.searchEmployeeByEmail(email);
         log.info("END: searchEmployeeByEmail API - Found");
         return ResponseEntity.ok(employee);
    }

    @GetMapping("/filter/department")
    public ResponseEntity<List<EmployeeDto>> filterByDepartment(@RequestParam String department) {
         log.info("START: filterByDepartment API - Department: {}", department);
         List<EmployeeDto> employees = employeeService.filterEmployeesByDepartment(department);
         log.info("END: filterByDepartment API - Found: {}", employees.size());
         return ResponseEntity.ok(employees);
    }

    @GetMapping("/filter/status")
    public ResponseEntity<List<EmployeeDto>> filterByStatus(@RequestParam EmployeeStatus status) {
         log.info("START: filterByStatus API - Status: {}", status);
         List<EmployeeDto> employees = employeeService.filterEmployeesByStatus(status);
         log.info("END: filterByStatus API - Found: {}", employees.size());
         return ResponseEntity.ok(employees);
    }

    @GetMapping("/filter/type")
    public ResponseEntity<List<EmployeeDto>> filterByEmploymentType(@RequestParam EmploymentType type) {
         log.info("START: filterByEmploymentType API - Type: {}", type);
         List<EmployeeDto> employees = employeeService.filterEmployeesByEmploymentType(type);
         log.info("END: filterByEmploymentType API - Found: {}", employees.size());
         return ResponseEntity.ok(employees);
    }
}
