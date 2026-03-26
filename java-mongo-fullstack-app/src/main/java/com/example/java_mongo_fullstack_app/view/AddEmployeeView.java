package com.example.java_mongo_fullstack_app.view;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.example.java_mongo_fullstack_app.service.EmployeeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

@Route("add")
public class AddEmployeeView extends VerticalLayout {

    private final EmployeeService employeeService;
    private final Binder<EmployeeDto> binder = new Binder<>(EmployeeDto.class);

    private final TextField name = new TextField("Name");
    private final TextField email = new TextField("Email");
    private final TextField department = new TextField("Department");
    private final NumberField salary = new NumberField("Salary");

    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    public AddEmployeeView(EmployeeService employeeService) {
        this.employeeService = employeeService;

        FormLayout formLayout = new FormLayout();
        formLayout.add(name, email, department, salary);

        binder.bindInstanceFields(this);

        save.addClickListener(e -> saveEmployee());
        cancel.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        add(formLayout, save, cancel);
    }

    private void saveEmployee() {
        EmployeeDto employeeDto = new EmployeeDto();
        if (binder.writeBeanIfValid(employeeDto)) {
            try {
                employeeService.createEmployee(employeeDto);
                Notification.show("Employee added successfully.");
                getUI().ifPresent(ui -> ui.navigate(""));
            } catch (Exception e) {
                Notification.show("Error saving employee: " + e.getMessage());
            }
        } else {
             Notification.show("Please fill out the form correctly");
        }
    }
}