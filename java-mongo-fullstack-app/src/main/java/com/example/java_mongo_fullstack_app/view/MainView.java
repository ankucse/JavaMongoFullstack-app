package com.example.java_mongo_fullstack_app.view;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.example.java_mongo_fullstack_app.service.EmployeeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import java.util.List;

@Route("")
public class MainView extends VerticalLayout {

    private final EmployeeService employeeService;
    private final Grid<EmployeeDto> grid = new Grid<>(EmployeeDto.class);

    public MainView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        
        Button addButton = new Button("Add Employee");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("add")));
        
        configureGrid();

        add(addButton, grid);
        listEmployees();
    }

    private void configureGrid() {
        grid.setColumns("name", "email", "department", "salary");
        
        grid.addComponentColumn(employee -> {
            Button editButton = new Button("Edit");
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(EditEmployeeView.class, employee.getId())));
            
            Button deleteButton = new Button("Delete");
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            deleteButton.addClickListener(e -> deleteEmployee(employee.getId()));
            
            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader("Actions");
    }

    private void listEmployees() {
        try {
            List<EmployeeDto> employees = employeeService.getAllEmployees();
            grid.setItems(employees);
        } catch (Exception e) {
            Notification.show("Error fetching employees: " + e.getMessage());
        }
    }

    private void deleteEmployee(String id) {
        try {
            employeeService.deleteEmployee(id);
            Notification.show("Employee deleted successfully");
            listEmployees(); // Refresh the grid
        } catch (Exception e) {
            Notification.show("Error deleting employee: " + e.getMessage());
        }
    }
}
