package com.example.java_mongo_fullstack_app.controller;

import com.example.java_mongo_fullstack_app.controller.EmployeeController;
import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.example.java_mongo_fullstack_app.model.EmploymentType;
import com.example.java_mongo_fullstack_app.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        employeeDto = EmployeeDto.builder()
                .id("1")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .department("Engineering")
                .employmentType(EmploymentType.FULL_TIME)
                .dateOfJoining(LocalDate.now())
                .status(EmployeeStatus.ACTIVE)
                .salary(75000.0)
                .build();
    }

    @Test
    void testCreateEmployee() throws Exception {
        when(employeeService.createEmployee(any(EmployeeDto.class))).thenReturn(employeeDto);

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testGetAllEmployees() throws Exception {
        List<EmployeeDto> employees = Arrays.asList(employeeDto);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        when(employeeService.getEmployeeById("1")).thenReturn(employeeDto);

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.department").value("Engineering"));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        EmployeeDto updatedDto = EmployeeDto.builder()
                .id("1")
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .phoneNumber("1234567890")
                .department("HR")
                .employmentType(EmploymentType.FULL_TIME)
                .dateOfJoining(LocalDate.now())
                .status(EmployeeStatus.ACTIVE)
                .salary(80000.0)
                .build();

        when(employeeService.updateEmployee(anyString(), any(EmployeeDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.department").value("HR"));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        doNothing().when(employeeService).deleteEmployee("1");

        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isNoContent());
    }
}
