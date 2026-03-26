package com.example.java_mongo_fullstack_app.view;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.example.java_mongo_fullstack_app.model.EmploymentType;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Route("edit")
public class EditEmployeeView extends VerticalLayout implements HasUrlParameter<String> {

    private final RestTemplate restTemplate;
    private final Binder<EmployeeDto> binder = new BeanValidationBinder<>(EmployeeDto.class);
    private final String API_URL = "http://localhost:8080/employees";
    private String employeeId;

    // Personal Info
    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final TextField email = new TextField("Email");
    private final TextField phoneNumber = new TextField("Phone Number");

    // Job Info
    private final TextField department = new TextField("Department");
    private final TextField designation = new TextField("Designation");
    private final TextField role = new TextField("Role");
    private final ComboBox<EmploymentType> employmentType = new ComboBox<>("Employment Type");
    private final DatePicker dateOfJoining = new DatePicker("Date of Joining");
    private final IntegerField experienceYears = new IntegerField("Experience (Years)");
    private final ComboBox<EmployeeStatus> status = new ComboBox<>("Status");

    // Salary Info
    private final NumberField salary = new NumberField("Salary");
    private final NumberField bonus = new NumberField("Bonus");
    private final TextField currency = new TextField("Currency");

    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");

    public EditEmployeeView() {
        this.restTemplate = new RestTemplate();

        setupFields();
        binder.bindInstanceFields(this);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> saveEmployee());
        cancel.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        add(
            new H3("Edit Employee"),
            createFormLayout(),
            new HorizontalLayout(save, cancel)
        );
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.employeeId = parameter;
        fetchEmployeeData();
    }

    private void setupFields() {
        employmentType.setItems(EmploymentType.values());
        status.setItems(EmployeeStatus.values());
    }

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();
        
        formLayout.add(new H3("Personal Information"), 2);
        formLayout.add(firstName, lastName, email, phoneNumber);
        
        formLayout.add(new H3("Job Information"), 2);
        formLayout.add(department, designation, role, employmentType, dateOfJoining, experienceYears, status);
        
        formLayout.add(new H3("Salary Information"), 2);
        formLayout.add(salary, bonus, currency);

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        return formLayout;
    }

    private void fetchEmployeeData() {
        try {
            EmployeeDto employee = restTemplate.getForObject(API_URL + "/" + employeeId, EmployeeDto.class);
            if (employee != null) {
                binder.setBean(employee);
            } else {
                showError("Employee not found");
                getUI().ifPresent(ui -> ui.navigate(""));
            }
        } catch (Exception e) {
            showError("Error fetching employee data: " + e.getMessage());
        }
    }

    private void saveEmployee() {
        EmployeeDto employeeDto = new EmployeeDto();
        if (binder.writeBeanIfValid(employeeDto)) {
            try {
                restTemplate.put(API_URL + "/" + employeeId, employeeDto);
                showSuccess("Employee updated successfully.");
                getUI().ifPresent(ui -> ui.navigate(""));
            } catch (HttpClientErrorException e) {
                showError("Validation failed: " + e.getResponseBodyAsString());
            } catch (Exception e) {
                showError("Error saving employee: " + e.getMessage());
            }
        } else {
            showError("Please fill out the form correctly");
        }
    }

    private void showSuccess(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
