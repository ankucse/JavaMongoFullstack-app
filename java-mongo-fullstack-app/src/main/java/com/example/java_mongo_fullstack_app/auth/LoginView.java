package com.example.java_mongo_fullstack_app.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

        // Unicorn SaaS Animated Background
        getStyle().set("background", "linear-gradient(-45deg, #ee7752, #e73c7e, #23a6d5, #23d5ab)");
        getStyle().set("background-size", "400% 400%");
        getStyle().set("animation", "dynamicBG 15s ease infinite");

        // Inject Custom Premium SaaS CSS
        add(new Html(
            "<style>" +
            "@keyframes dynamicBG { 0% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } 100% { background-position: 0% 50%; } }" +
            "@keyframes fadeInUp { from { opacity: 0; transform: translate3d(0, 40px, 0); } to { opacity: 1; transform: translate3d(0, 0, 0); } }" +
            ".unicorn-login-card { background: rgba(255, 255, 255, 0.85) !important; backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px); border: 1px solid rgba(255, 255, 255, 0.7); border-radius: 24px !important; box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1) !important; animation: fadeInUp 0.8s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards; }" +
            ".unicorn-primary-btn { background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%) !important; color: white !important; border-radius: 12px !important; box-shadow: 0 4px 15px rgba(37, 117, 252, 0.4) !important; transition: all 0.3s ease !important; height: 48px; font-weight: 600; font-size: 16px; margin-top: 10px !important; }" +
            ".unicorn-primary-btn:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(37, 117, 252, 0.6) !important; }" +
            ".social-btn { background: white !important; color: #333 !important; border: 1px solid #e2e8f0 !important; border-radius: 12px !important; box-shadow: 0 2px 5px rgba(0,0,0,0.05) !important; transition: all 0.3s ease !important; height: 44px; font-weight: 500; flex: 1; min-width: 0; display: flex; align-items: center; justify-content: center; gap: 8px; }" +
            ".social-btn:hover { transform: translateY(-2px); box-shadow: 0 5px 15px rgba(0,0,0,0.1) !important; border-color: #cbd5e1 !important; }" +
            ".auth-divider { display: flex; align-items: center; text-align: center; color: #94a3b8; font-size: 13px; font-weight: 600; text-transform: uppercase; margin: 20px 0; width: 100%; }" +
            ".auth-divider::before, .auth-divider::after { content: ''; flex: 1; border-bottom: 1px solid #cbd5e1; }" +
            ".auth-divider::not(:empty)::before { margin-right: 12px; }" +
            ".auth-divider::not(:empty)::after { margin-left: 12px; }" +
            ".unicorn-input { --lumo-contrast-10pct: rgba(226, 232, 240, 0.7); --lumo-border-radius-m: 12px; }" +
            ".unicorn-link { color: #6a11cb !important; font-weight: 600; text-decoration: none !important; transition: opacity 0.2s; font-size: 14px; }" +
            ".unicorn-link:hover { opacity: 0.8; text-decoration: underline !important; }" +
            "</style>"
        ));

        VerticalLayout loginCard = createLoginCard();
        add(loginCard);
    }

    private VerticalLayout createLoginCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassName("unicorn-login-card");
        card.getStyle().set("padding", "40px");
        card.setSpacing(true);
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.setMaxWidth("450px");
        card.setWidth("100%");

        H1 title = new H1("Welcome Back!");
        title.getStyle().set("margin", "0").set("background", "-webkit-linear-gradient(45deg, #6a11cb, #2575fc)");
        title.getStyle().set("-webkit-background-clip", "text");
        title.getStyle().set("-webkit-text-fill-color", "transparent");
        title.getStyle().set("font-weight", "800");
        title.getStyle().set("font-size", "2rem");
        
        Span subtitle = new Span("Please enter your details to sign in");
        subtitle.getStyle().set("color", "#64748b").set("font-size", "15px").set("margin-bottom", "15px");

        // Social Login Buttons (With inline SVGs for perfect rendering)
        Button googleLoginButton = new Button("Google");
        googleLoginButton.addClassName("social-btn");
        Span googleIcon = new Span();
        googleIcon.getElement().setProperty("innerHTML", "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 48 48' width='20px' height='20px'><path fill='#FFC107' d='M43.611 20.083H42V20H24v8h11.303c-1.649 4.657-6.08 8-11.303 8-6.627 0-12-5.373-12-12s5.373-12 12-12c3.059 0 5.842 1.154 7.961 3.039l5.657-5.657C34.046 6.053 29.268 4 24 4 12.955 4 4 12.955 4 24s8.955 20 20 20 20-8.955 20-20c0-1.341-.262-2.628-.389-3.917z'/><path fill='#FF3D00' d='M6.306 14.691l6.571 4.819C14.655 15.108 18.961 12 24 12c3.059 0 5.842 1.154 7.961 3.039l5.657-5.657C34.046 6.053 29.268 4 24 4 16.318 4 9.656 8.337 6.306 14.691z'/><path fill='#4CAF50' d='M24 44c5.166 0 9.86-1.977 13.409-5.192l-6.19-5.238C29.211 35.091 26.715 36 24 36c-5.202 0-9.619-3.317-11.283-7.946l-6.522 5.025C9.505 39.556 16.227 44 24 44z'/><path fill='#1976D2' d='M43.611 20.083H42V20H24v8h11.303c-.792 2.237-2.231 4.166-4.087 5.571c.001-.001.002-.001.003-.002l6.19 5.238C36.971 39.205 44 34 44 24c0-1.341-.262-2.628-.389-3.917z'/></svg>");
        googleLoginButton.setIcon(googleIcon);
        googleLoginButton.setEnabled(false); // Placeholder

        Button githubLoginButton = new Button("GitHub");
        githubLoginButton.addClassName("social-btn");
        Span githubIcon = new Span();
        githubIcon.getElement().setProperty("innerHTML", "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' width='20px' height='20px'><path fill='#333' d='M12 0C5.37 0 0 5.373 0 12c0 5.302 3.438 9.8 8.207 11.387.6.111.793-.261.793-.579v-2.025c-3.338.726-4.043-1.61-4.043-1.61-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.73.083-.73 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23A11.509 11.509 0 0 1 12 5.803c1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.814 1.102.814 2.222v3.293c0 .32.192.694.801.576C20.566 21.797 24 17.3 24 12c0-6.627-5.373-12-12-12z'/></svg>");
        githubLoginButton.setIcon(githubIcon);
        githubLoginButton.setEnabled(false); // Placeholder
        
        HorizontalLayout socialRow = new HorizontalLayout(googleLoginButton, githubLoginButton);
        socialRow.setWidthFull();
        socialRow.setSpacing(true);
        
        Span divider = new Span("Or continue with email");
        divider.addClassName("auth-divider");

        emailField = new EmailField("Email");
        emailField.setWidth("100%");
        emailField.setPlaceholder("your@example.com");
        emailField.setRequiredIndicatorVisible(true);
        emailField.addClassName("unicorn-input");

        passwordField = new PasswordField("Password");
        passwordField.setWidth("100%");
        passwordField.setPlaceholder("Enter your password");
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.addClassName("unicorn-input");

        loginButton = new Button("Login", event -> authenticateUser());
        loginButton.addClassName("unicorn-primary-btn");
        loginButton.setWidth("100%");

        Anchor registerLink = new Anchor("/register", "Don't have an account? Register");
        registerLink.addClassName("unicorn-link");

        card.add(title, subtitle, socialRow, divider, emailField, passwordField, loginButton, registerLink);
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
