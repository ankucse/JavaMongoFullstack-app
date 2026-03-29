package com.example.java_mongo_fullstack_app.auth;

import com.example.java_mongo_fullstack_app.model.Employee;
import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.example.java_mongo_fullstack_app.service.AuthService;
import com.example.java_mongo_fullstack_app.service.EmployeeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Route("register")
@PageTitle("Register | Fullstack App")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(RegisterView.class);

    private final AuthService authService;
    private final EmployeeService employeeService;

    private TextField nameField = new TextField("Full Name");
    private EmailField emailField = new EmailField("Email");
    private PasswordField passwordField = new PasswordField("Password");
    private PasswordField confirmPasswordField = new PasswordField("Confirm Password");

    public RegisterView(AuthService authService, EmployeeService employeeService) {
        this.authService = authService;
        this.employeeService = employeeService;

        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getStyle().set("background", "linear-gradient(45deg, #6a11cb 0%, #2575fc 100%)");

        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.getStyle()
                .set("background-color", "white")
                .set("border-radius", "16px")
                .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.1)");
        card.setMaxWidth("400px");
        card.setWidth("100%");

        H1 title = new H1("Create Account");
        title.getStyle().set("color", "#333");

        nameField.setWidthFull();
        nameField.setRequiredIndicatorVisible(true);
        
        emailField.setWidthFull();
        emailField.setRequiredIndicatorVisible(true);
        
        passwordField.setWidthFull();
        passwordField.setRequiredIndicatorVisible(true);
        
        confirmPasswordField.setWidthFull();
        confirmPasswordField.setRequiredIndicatorVisible(true);

        Button registerBtn = new Button("Sign Up", e -> attemptRegistration());
        registerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerBtn.setWidthFull();

        Anchor loginLink = new Anchor("/login", "Already have an account? Login here");
        loginLink.getStyle().set("font-size", "var(--lumo-font-size-s)");
        loginLink.getStyle().set("text-decoration", "none");

        card.add(title, nameField, emailField, passwordField, confirmPasswordField, registerBtn, loginLink);
        add(card);
    }

    private void attemptRegistration() {
        if (nameField.isEmpty() || emailField.isEmpty() || passwordField.isEmpty()) {
            Notification.show("Please fill all fields").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
            Notification.show("Passwords do not match").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            // Normalize email to prevent case-sensitivity login issues
            String normalizedEmail = emailField.getValue().trim().toLowerCase();

            logger.info("Attempting to register new user with email: '{}'", normalizedEmail);
            authService.registerUser(nameField.getValue(), normalizedEmail, passwordField.getValue(), "USER");

            logger.info("Successfully created User account for: '{}'", normalizedEmail);
            Notification.show("Registration successful! Please login.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate("login");
        } catch (Exception e) {
            logger.error("Registration failed for email '{}': {}", emailField.getValue(), e.getMessage(), e);
            Notification.show("Registration failed: " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}