package com.example.java_mongo_fullstack_app.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.Collections;
import java.util.Set;

@Route("login")
@PageTitle("Login | Fullstack App")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private static final Logger logger = LoggerFactory.getLogger(LoginView.class);
    private final AuthenticationManager authenticationManager;

    private EmailField emailField;
    private PasswordField passwordField;
    private Button loginButton;

    public LoginView(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        addClassName("login-view"); // For custom styling

        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Apply gradient background via CSS
        getStyle().set("background", "linear-gradient(45deg, #6a11cb 0%, #2575fc 100%)");

        VerticalLayout loginCard = createLoginCard();
        add(loginCard);
    }

    private VerticalLayout createLoginCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("login-card"); // For custom styling
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.getStyle()
                .set("background-color", "white")
                .set("border-radius", "16px")
                .set("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.1)");
        card.setMaxWidth("400px");
        card.setWidth("100%");

        H1 title = new H1("Welcome Back!");
        title.getStyle().set("color", "#333");

        emailField = new EmailField("Email");
        emailField.setWidth("100%");
        emailField.setPlaceholder("your@example.com");
        emailField.setRequiredIndicatorVisible(true);

        passwordField = new PasswordField("Password");
        passwordField.setWidth("100%");
        passwordField.setPlaceholder("Enter your password");
        passwordField.setRequiredIndicatorVisible(true);

        loginButton = new Button("Login", event -> authenticateUser());
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidth("100%");

        // Use standard text for Google login since the icon is missing in this version
        Button googleLoginButton = new Button("Login with Google");
        googleLoginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        googleLoginButton.setWidth("100%");
        googleLoginButton.getStyle().set("color", "#4285F4"); // Google blue
        googleLoginButton.setEnabled(false); // Placeholder, not implemented

        // Use standard text for GitHub login since the icon is missing in this version
        Button githubLoginButton = new Button("Login with GitHub");
        githubLoginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        githubLoginButton.setWidth("100%");
        githubLoginButton.getStyle().set("color", "#333"); // GitHub dark
        githubLoginButton.setEnabled(false); // Placeholder, not implemented

        Anchor forgotPasswordLink = new Anchor("/forgot-password", "Forgot Password?");
        forgotPasswordLink.getStyle().set("font-size", "var(--lumo-font-size-s)");
        forgotPasswordLink.getStyle().set("color", "var(--lumo-primary-text-color)");
        forgotPasswordLink.getStyle().set("text-decoration", "none");
        forgotPasswordLink.setEnabled(false); // Placeholder, not implemented

        Anchor registerLink = new Anchor("/register", "Don't have an account? Register");
        registerLink.getStyle().set("font-size", "var(--lumo-font-size-s)");
        registerLink.getStyle().set("color", "var(--lumo-primary-text-color)");
        registerLink.getStyle().set("text-decoration", "none");

        card.add(title, emailField, passwordField, loginButton, googleLoginButton, githubLoginButton, forgotPasswordLink, registerLink);
        return card;
    }

    private void authenticateUser() {
        // Normalize email to match registration logic exactly
        String email = emailField.getValue().trim().toLowerCase();
        logger.info("Initiating authentication attempt for email: '{}'", email);
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, passwordField.getValue(), Collections.emptyList())
            );
            logger.info("Authentication Manager successfully verified credentials for: '{}'", email);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // In Spring Security 6, we must explicitly save the context to the session
            HttpServletRequest request = VaadinServletRequest.getCurrent().getHttpServletRequest();
            HttpServletResponse response = VaadinServletResponse.getCurrent().getHttpServletResponse();
            new HttpSessionSecurityContextRepository().saveContext(SecurityContextHolder.getContext(), request, response);

            // Use Vaadin's UI to redirect based on roles
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            if (roles.stream().anyMatch(role -> role.contains("ADMIN"))) {
                logger.info("Redirecting '{}' to Admin Dashboard (/)", email);
                UI.getCurrent().getPage().setLocation("/"); // Redirect to Admin Dashboard
            } else if (roles.stream().anyMatch(role -> role.contains("USER"))) {
                logger.info("Redirecting '{}' to User Dashboard (/user-dashboard)", email);
                UI.getCurrent().getPage().setLocation("/user-dashboard"); // Redirect to User Dashboard
            } else {
                logger.warn("User '{}' has no recognized roles. Roles found: {}", email, roles);
                UI.getCurrent().getPage().setLocation("/login?error");
            }

        } catch (AuthenticationException e) {
            logger.error("AuthenticationException for email '{}'. Reason: {} - {}", email, e.getClass().getSimpleName(), e.getMessage());
            Notification notification = Notification.show("Login failed: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            UI.getCurrent().getPage().setLocation("/login?error"); // Redirect to login with error param
        } catch (Exception e) {
            logger.error("Unexpected error during login for email '{}'", email, e);
            Notification notification = Notification.show("An unexpected error occurred: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            UI.getCurrent().getPage().setLocation("/login?error");
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Check if there's an error parameter in the URL
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            Notification notification = Notification.show("Invalid username or password.", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
