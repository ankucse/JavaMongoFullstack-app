package com.example.java_mongo_fullstack_app.view;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Route("")
public class MainView extends VerticalLayout {

    private final RestTemplate restTemplate;
    private final Grid<EmployeeDto> grid = new Grid<>(EmployeeDto.class, false);
    private final String API_URL = "http://localhost:8080/employees";

    public MainView() {
        this.restTemplate = new RestTemplate();
        
        Button addButton = new Button("Add Employee", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("add")));

        HorizontalLayout toolbar = new HorizontalLayout(addButton);
        
        configureGrid();

        add(toolbar, grid);
        setSizeFull();
        listEmployees();
    }

    private void configureGrid() {
        grid.addColumn(emp -> emp.getFirstName() + " " + emp.getLastName()).setHeader("Name");
        grid.addColumn(EmployeeDto::getEmail).setHeader("Email");
        grid.addColumn(EmployeeDto::getDepartment).setHeader("Department");
        grid.addColumn(EmployeeDto::getRole).setHeader("Role");
        
        grid.addComponentColumn(this::createStatusBadge).setHeader("Status");
        
        grid.addComponentColumn(employee -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(EditEmployeeView.class, employee.getId())));
            
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            deleteButton.addClickListener(e -> deleteEmployee(employee.getId()));
            
            return new HorizontalLayout(editButton, deleteButton);
        }).setHeader("Actions");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private Span createStatusBadge(EmployeeDto employee) {
        Span badge = new Span(employee.getStatus() != null ? employee.getStatus().name() : "UNKNOWN");
        badge.getElement().getThemeList().add("badge");
        
        if (employee.getStatus() == EmployeeStatus.ACTIVE) {
            badge.getElement().getThemeList().add("success");
        } else if (employee.getStatus() == EmployeeStatus.INACTIVE) {
            badge.getElement().getThemeList().add("error");
        } else if (employee.getStatus() == EmployeeStatus.ON_LEAVE) {
            badge.getElement().getThemeList().add("warning");
        }
        return badge;
    }

    private void listEmployees() {
        try {
            EmployeeDto[] employees = restTemplate.getForObject(API_URL, EmployeeDto[].class);
            if (employees != null) {
                List<EmployeeDto> employeeList = Arrays.asList(employees);
                grid.setItems(employeeList);
            }
        } catch (Exception e) {
            Notification.show("Error fetching employees: " + e.getMessage());
        }
    }

    private void deleteEmployee(String id) {
        try {
            restTemplate.delete(API_URL + "/" + id);
            Notification.show("Employee deleted successfully");
            listEmployees(); // Refresh the grid
        } catch (Exception e) {
            Notification.show("Error deleting employee: " + e.getMessage());
        }
    }
}
