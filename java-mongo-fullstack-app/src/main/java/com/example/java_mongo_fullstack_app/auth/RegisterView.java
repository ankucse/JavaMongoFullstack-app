package com.example.java_mongo_fullstack_app.auth;

import com.example.java_mongo_fullstack_app.model.Employee;
import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.example.java_mongo_fullstack_app.service.AuthService;
import com.example.java_mongo_fullstack_app.service.EmployeeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
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
        
        // Unicorn SaaS Animated Background
        getStyle().set("background", "linear-gradient(-45deg, #ee7752, #e73c7e, #23a6d5, #23d5ab)");
        getStyle().set("background-size", "400% 400%");
        getStyle().set("animation", "dynamicBG 15s ease infinite");

        // Inject Custom Premium SaaS CSS
        add(new Html(
            "<style>" +
            "@keyframes dynamicBG { 0% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } 100% { background-position: 0% 50%; } }" +
            "@keyframes fadeInUp { from { opacity: 0; transform: translate3d(0, 40px, 0); } to { opacity: 1; transform: translate3d(0, 0, 0); } }" +
            ".unicorn-register-card { background: rgba(255, 255, 255, 0.85) !important; backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px); border: 1px solid rgba(255, 255, 255, 0.7); border-radius: 24px !important; box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1) !important; animation: fadeInUp 0.8s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards; }" +
            ".unicorn-primary-btn { background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%) !important; color: white !important; border-radius: 12px !important; box-shadow: 0 4px 15px rgba(37, 117, 252, 0.4) !important; transition: all 0.3s ease !important; height: 48px; font-weight: 600; font-size: 16px; margin-top: 10px !important; }" +
            ".unicorn-primary-btn:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(37, 117, 252, 0.6) !important; }" +
            ".unicorn-input { --lumo-contrast-10pct: rgba(226, 232, 240, 0.7); --lumo-border-radius-m: 12px; }" +
            ".unicorn-link { color: #6a11cb !important; font-weight: 600; text-decoration: none !important; transition: opacity 0.2s; font-size: 14px; }" +
            ".unicorn-link:hover { opacity: 0.8; text-decoration: underline !important; }" +
            "</style>"
        ));

        VerticalLayout card = new VerticalLayout();
        card.addClassName("unicorn-register-card");
        card.getStyle().set("padding", "40px");
        card.setSpacing(true);
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.setMaxWidth("450px");
        card.setWidth("100%");

        H1 title = new H1("Create Account");
        title.getStyle().set("margin", "0").set("background", "-webkit-linear-gradient(45deg, #6a11cb, #2575fc)");
        title.getStyle().set("-webkit-background-clip", "text");
        title.getStyle().set("-webkit-text-fill-color", "transparent");
        title.getStyle().set("font-weight", "800");
        title.getStyle().set("font-size", "2rem");

        Span subtitle = new Span("Join us and start managing your profile");
        subtitle.getStyle().set("color", "#64748b").set("font-size", "15px").set("margin-bottom", "15px");

        nameField.setWidthFull();
        nameField.setRequiredIndicatorVisible(true);
        nameField.addClassName("unicorn-input");
        nameField.setPlaceholder("John Doe");
        
        emailField.setWidthFull();
        emailField.setRequiredIndicatorVisible(true);
        emailField.addClassName("unicorn-input");
        emailField.setPlaceholder("your@example.com");
        
        passwordField.setWidthFull();
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.addClassName("unicorn-input");
        passwordField.setPlaceholder("Create a secure password");
        
        confirmPasswordField.setWidthFull();
        confirmPasswordField.setRequiredIndicatorVisible(true);
        confirmPasswordField.addClassName("unicorn-input");
        confirmPasswordField.setPlaceholder("Repeat password");

        Button registerBtn = new Button("Sign Up", e -> attemptRegistration());
        registerBtn.addClassName("unicorn-primary-btn");
        registerBtn.setWidthFull();

        Anchor loginLink = new Anchor("/login", "Already have an account? Login here");
        loginLink.addClassName("unicorn-link");

        card.add(title, subtitle, nameField, emailField, passwordField, confirmPasswordField, registerBtn, loginLink);
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