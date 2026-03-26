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
public class EmployeeServiceImplTest {

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
                .email("john.doe@example.com")
                .department("Engineering")
                .salary(75000.0)
                .build();

        employeeDto = EmployeeDto.builder()
                .id("1")
                .name("John Doe")
                .email("john.doe@example.com")
                .department("Engineering")
                .salary(75000.0)
                .build();
    }

    @Test
    void testCreateEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto result = employeeService.createEmployee(employeeDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testGetAllEmployees() {
        List<Employee> employees = Arrays.asList(employee);
        when(employeeRepository.findAll()).thenReturn(employees);

        List<EmployeeDto> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testGetEmployeeById_Success() {
        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));

        EmployeeDto result = employeeService.getEmployeeById("1");

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(employeeRepository, times(1)).findById("1");
    }

    @Test
    void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById("1"));
        verify(employeeRepository, times(1)).findById("1");
    }

    @Test
    void testUpdateEmployee_Success() {
        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeDto.setName("Jane Doe");
        EmployeeDto result = employeeService.updateEmployee("1", employeeDto);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        verify(employeeRepository, times(1)).findById("1");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployee_NotFound() {
        when(employeeRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee("1", employeeDto));
        verify(employeeRepository, times(1)).findById("1");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testDeleteEmployee_Success() {
        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        employeeService.deleteEmployee("1");

        verify(employeeRepository, times(1)).findById("1");
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void testDeleteEmployee_NotFound() {
        when(employeeRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee("1"));
        verify(employeeRepository, times(1)).findById("1");
        verify(employeeRepository, never()).delete(any(Employee.class));
    }
}
