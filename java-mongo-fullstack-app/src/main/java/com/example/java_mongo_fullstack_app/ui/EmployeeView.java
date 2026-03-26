package com.example.java_mongo_fullstack_app.ui;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

// Changed the route to avoid conflict with MainView
@Route("employee-alt")
@PageTitle("Employee Management")
public class EmployeeView extends VerticalLayout {

    private final WebClient webClient;
    private final Grid<EmployeeDto> grid = new Grid<>(EmployeeDto.class, false);
    private final String API_URL = "http://localhost:8080/employees";

    public EmployeeView(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(API_URL).build();

        setSizeFull();
        configureGrid();
        
        Button addEmployeeBtn = new Button("Add Employee", new Icon(VaadinIcon.PLUS));
        addEmployeeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addEmployeeBtn.addClickListener(e -> openEmployeeForm(new EmployeeDto(), true));

        HorizontalLayout toolbar = new HorizontalLayout(new H2("Employee Management"), addEmployeeBtn);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        add(toolbar, grid);
        updateGrid();
    }

    private void configureGrid() {
        grid.addColumn(emp -> emp.getFirstName() + " " + emp.getLastName()).setHeader("Name");
        grid.addColumn(EmployeeDto::getEmail).setHeader("Email");
        grid.addColumn(EmployeeDto::getDepartment).setHeader("Department");
        grid.addColumn(EmployeeDto::getSalary).setHeader("Salary");
        
        grid.addComponentColumn(employee -> {
            Button editBtn = new Button(new Icon(VaadinIcon.EDIT));
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClickListener(e -> openEmployeeForm(employee, false));
            
            Button deleteBtn = new Button(new Icon(VaadinIcon.TRASH));
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> deleteEmployee(employee));
            
            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Actions");
        
        grid.setSizeFull();
    }

    private void updateGrid() {
        webClient.get()
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<EmployeeDto>>() {})
                .subscribe(
                        employees -> getUI().ifPresent(ui -> ui.access(() -> grid.setItems(employees))),
                        error -> getUI().ifPresent(ui -> ui.access(() -> showError("Failed to load employees: " + error.getMessage())))
                );
    }

    private void openEmployeeForm(EmployeeDto employee, boolean isNew) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(isNew ? "New Employee" : "Edit Employee");

        Binder<EmployeeDto> binder = new Binder<>(EmployeeDto.class);
        
        TextField firstNameField = new TextField("First Name");
        TextField lastNameField = new TextField("Last Name");
        TextField emailField = new TextField("Email");
        TextField departmentField = new TextField("Department");
        NumberField salaryField = new NumberField("Salary");

        binder.forField(firstNameField)
                .asRequired("First name is required")
                .bind(EmployeeDto::getFirstName, EmployeeDto::setFirstName);
                
        binder.forField(lastNameField)
                .asRequired("Last name is required")
                .bind(EmployeeDto::getLastName, EmployeeDto::setLastName);
                
        binder.forField(emailField)
                .asRequired("Email is required")
                .bind(EmployeeDto::getEmail, EmployeeDto::setEmail);
                
        binder.forField(departmentField)
                .asRequired("Department is required")
                .bind(EmployeeDto::getDepartment, EmployeeDto::setDepartment);
                
        binder.forField(salaryField)
                .asRequired("Salary is required")
                .bind(EmployeeDto::getSalary, EmployeeDto::setSalary);

        binder.readBean(employee);

        FormLayout formLayout = new FormLayout();
        formLayout.add(firstNameField, lastNameField, emailField, departmentField, salaryField);

        Button saveBtn = new Button("Save", e -> {
            try {
                binder.writeBean(employee);
                if (isNew) {
                    saveEmployee(employee);
                } else {
                    updateEmployee(employee);
                }
                dialog.close();
            } catch (ValidationException ex) {
                showError("Please fix the errors before saving");
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button cancelBtn = new Button("Cancel", e -> dialog.close());

        dialog.add(formLayout);
        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }

    private void saveEmployee(EmployeeDto employee) {
        webClient.post()
                .body(Mono.just(employee), EmployeeDto.class)
                .retrieve()
                .bodyToMono(EmployeeDto.class)
                .subscribe(
                        saved -> getUI().ifPresent(ui -> ui.access(() -> {
                            updateGrid();
                            showSuccess("Employee saved successfully");
                        })),
                        error -> getUI().ifPresent(ui -> ui.access(() -> showError("Failed to save employee: " + error.getMessage())))
                );
    }

    private void updateEmployee(EmployeeDto employee) {
        webClient.put()
                .uri("/" + employee.getId())
                .body(Mono.just(employee), EmployeeDto.class)
                .retrieve()
                .bodyToMono(EmployeeDto.class)
                .subscribe(
                        updated -> getUI().ifPresent(ui -> ui.access(() -> {
                            updateGrid();
                            showSuccess("Employee updated successfully");
                        })),
                        error -> getUI().ifPresent(ui -> ui.access(() -> showError("Failed to update employee: " + error.getMessage())))
                );
    }

    private void deleteEmployee(EmployeeDto employee) {
        webClient.delete()
                .uri("/" + employee.getId())
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe(
                        result -> getUI().ifPresent(ui -> ui.access(() -> {
                            updateGrid();
                            showSuccess("Employee deleted successfully");
                        })),
                        error -> getUI().ifPresent(ui -> ui.access(() -> showError("Failed to delete employee: " + error.getMessage())))
                );
    }

    private void showSuccess(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_END);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
