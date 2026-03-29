package com.example.java_mongo_fullstack_app.ui;

import com.example.java_mongo_fullstack_app.service.EmployeeService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

@Route("user-dashboard")
@PageTitle("User Dashboard | Fullstack App")
@PermitAll // This view is accessible to authenticated users (both ADMIN and USER, but logic restricts USER)
public class UserDashboardView extends VerticalLayout {

    private final EmployeeService employeeService;

    private Grid<com.example.java_mongo_fullstack_app.model.Employee> grid = new Grid<>(com.example.java_mongo_fullstack_app.model.Employee.class);
    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private EmailField email = new EmailField("Email");
    private TextField department = new TextField("Department");

    private Button saveButton = new Button("Save");
    private Button cancelButton = new Button("Cancel");
    private Button deleteButton = new Button("Delete");
    private Button addEmployeeButton = new Button("Add Employee"); // This will be disabled/hidden

    private Binder<com.example.java_mongo_fullstack_app.model.Employee> binder = new BeanValidationBinder<>(com.example.java_mongo_fullstack_app.model.Employee.class);
    private com.example.java_mongo_fullstack_app.model.Employee currentEmployee;

    public UserDashboardView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        addClassName("user-dashboard-view");
        setSizeFull();
        configureGrid();
        configureForm();

        add(
                new H2("Your Employee Information"),
                getToolbar(),
                getContent()
        );

        updateList();
        closeEditor();
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, getForm());
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, getForm());
        content.setSizeFull();
        return content;
    }

    private void configureGrid() {
        grid.addClassNames("employee-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "email", "department");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editEmployee(event.getValue()));
    }

    private Component getForm() {
        FormLayout formLayout = new FormLayout();
        binder.bindInstanceFields(this);

        email.setReadOnly(true); // Email should not be editable by user to prevent changing identity

        formLayout.add(firstName, lastName, email, department);

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton, deleteButton);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        saveButton.addClickListener(event -> validateAndSave());
        deleteButton.addClickListener(event -> deleteEmployee(currentEmployee));
        cancelButton.addClickListener(event -> closeEditor());

        VerticalLayout form = new VerticalLayout(formLayout, buttons);
        form.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
        form.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        form.setPadding(true);
        return form;
    }

    private void configureForm() {
         // Form configuration is largely handled in getForm(), this method can be empty or used for further specific setup if needed.
    }

    private HorizontalLayout getToolbar() {
        addEmployeeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addEmployeeButton.setEnabled(false); // Users cannot add employees
        addEmployeeButton.setVisible(false); // Hide the button completely for users

        HorizontalLayout toolbar = new HorizontalLayout(addEmployeeButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String userEmail = authentication.getName();
            // Fetch only the employee record associated with the logged-in user's email
            Optional<com.example.java_mongo_fullstack_app.model.Employee> userEmployee = employeeService.findModelByEmail(userEmail);
            grid.setItems(userEmployee.map(Collections::singletonList).orElse(Collections.emptyList()));
        } else {
            grid.setItems(Collections.emptyList());
        }
    }

    public void editEmployee(com.example.java_mongo_fullstack_app.model.Employee employee) {
        if (employee == null) {
            closeEditor();
            return;
        }
        currentEmployee = employee;
        binder.readBean(employee);
        setVisible(true);
        addClassName("editing");
    }

    private void closeEditor() {
        currentEmployee = null;
        binder.readBean(null); // Clear form
        removeClassName("editing");
    }

    private void validateAndSave() {
        try {
            if (binder.isValid()) {
                employeeService.saveEmployeeModel(currentEmployee);
                Notification.show("Employee saved successfully!", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                updateList();
                closeEditor();
            } else {
                Notification.show("Please correct the errors in the form.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (Exception e) {
            Notification.show("Error saving employee: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteEmployee(com.example.java_mongo_fullstack_app.model.Employee employee) {
        if (employee == null || employee.getId() == null) {
            Notification.show("No employee selected for deletion.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }
        try {
            employeeService.deleteEmployee(employee.getId());
            Notification.show("Employee deleted successfully!", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            updateList();
            closeEditor();
        } catch (Exception e) {
            Notification.show("Error deleting employee: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
