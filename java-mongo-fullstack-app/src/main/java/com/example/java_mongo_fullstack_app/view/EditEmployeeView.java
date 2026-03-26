package com.example.java_mongo_fullstack_app.view;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.RestTemplate;

@Route("edit")
public class EditEmployeeView extends VerticalLayout implements HasUrlParameter<String> {

    private final RestTemplate restTemplate;
    private final Binder<EmployeeDto> binder = new Binder<>(EmployeeDto.class);
    private final String API_URL = "http://localhost:8080/employees";
    private String employeeId;

    private final TextField name = new TextField("Name");
    private final TextField email = new TextField("Email");
    private final TextField department = new TextField("Department");
    private final NumberField salary = new NumberField("Salary");

    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");

    public EditEmployeeView() {
        this.restTemplate = new RestTemplate();

        FormLayout formLayout = new FormLayout();
        formLayout.add(name, email, department, salary);

        binder.bindInstanceFields(this);

        save.addClickListener(e -> saveEmployee());
        cancel.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        add(formLayout, save, cancel);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        this.employeeId = parameter;
        fetchEmployeeData();
    }

    private void fetchEmployeeData() {
        try {
            EmployeeDto employee = restTemplate.getForObject(API_URL + "/" + employeeId, EmployeeDto.class);
            if (employee != null) {
                binder.setBean(employee);
            } else {
                Notification.show("Employee not found");
                getUI().ifPresent(ui -> ui.navigate(""));
            }
        } catch (Exception e) {
            Notification.show("Error fetching employee data: " + e.getMessage());
        }
    }

    private void saveEmployee() {
        EmployeeDto employeeDto = new EmployeeDto();
        if (binder.writeBeanIfValid(employeeDto)) {
            try {
                restTemplate.put(API_URL + "/" + employeeId, employeeDto);
                Notification.show("Employee updated successfully.");
                getUI().ifPresent(ui -> ui.navigate(""));
            } catch (Exception e) {
                Notification.show("Error saving employee: " + e.getMessage());
            }
        } else {
            Notification.show("Please fill out the form correctly");
        }
    }
}
