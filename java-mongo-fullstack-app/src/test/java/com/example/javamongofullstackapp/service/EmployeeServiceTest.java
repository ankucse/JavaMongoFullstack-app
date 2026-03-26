package com.example.javamongofullstackapp.service;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.example.java_mongo_fullstack_app.exception.ResourceNotFoundException;
import com.example.java_mongo_fullstack_app.model.Employee;
import com.example.java_mongo_fullstack_app.repository.EmployeeRepository;
import com.example.java_mongo_fullstack_app.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id("1")
                .name("John Doe")
                .email("john@example.com")
                .department("IT")
                .salary(50000.0)
                .build();

        employeeDto = EmployeeDto.builder()
                .id("1")
                .name("John Doe")
                .email("john@example.com")
                .department("IT")
                .salary(50000.0)
                .build();
    }

    @Test
    void createEmployee_ReturnsEmployeeDto() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto savedEmployee = employeeService.createEmployee(employeeDto);

        assertNotNull(savedEmployee);
        assertEquals(employeeDto.getName(), savedEmployee.getName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void getAllEmployees_ReturnsListOfEmployeeDtos() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee));

        List<EmployeeDto> employeeList = employeeService.getAllEmployees();

        assertNotNull(employeeList);
        assertEquals(1, employeeList.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void getEmployeeById_ExistingId_ReturnsEmployeeDto() {
        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));

        EmployeeDto foundEmployee = employeeService.getEmployeeById("1");

        assertNotNull(foundEmployee);
        assertEquals(employeeDto.getId(), foundEmployee.getId());
        verify(employeeRepository, times(1)).findById("1");
    }

    @Test
    void getEmployeeById_NonExistingId_ThrowsException() {
        when(employeeRepository.findById("2")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById("2"));
        verify(employeeRepository, times(1)).findById("2");
    }

    @Test
    void updateEmployee_ExistingId_ReturnsUpdatedEmployeeDto() {
        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeDto.setName("Jane Doe");
        EmployeeDto updatedEmployee = employeeService.updateEmployee("1", employeeDto);

        assertNotNull(updatedEmployee);
        assertEquals("Jane Doe", updatedEmployee.getName());
        verify(employeeRepository, times(1)).findById("1");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_ExistingId_DeletesEmployee() {
        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        employeeService.deleteEmployee("1");

        verify(employeeRepository, times(1)).findById("1");
        verify(employeeRepository, times(1)).delete(employee);
    }
}
