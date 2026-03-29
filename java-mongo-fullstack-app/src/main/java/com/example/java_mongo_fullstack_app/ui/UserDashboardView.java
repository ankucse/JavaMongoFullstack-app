package com.example.java_mongo_fullstack_app.ui;

import com.example.java_mongo_fullstack_app.model.Employee;
import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.example.java_mongo_fullstack_app.model.EmploymentType;
import com.example.java_mongo_fullstack_app.service.EmployeeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

import java.util.Arrays;
import java.util.Optional;

@Route("user-dashboard")
@PageTitle("My Profile | Fullstack App")
@PermitAll
public class UserDashboardView extends VerticalLayout implements BeforeEnterObserver {

    private static final Logger logger = LoggerFactory.getLogger(UserDashboardView.class);

    private final EmployeeService employeeService;
    private Employee currentEmployee;
    private final Binder<Employee> binder = new BeanValidationBinder<>(Employee.class);

    private int animationDelayCounter = 1;

    private TextField firstName = new TextField("First Name");
    private TextField lastName = new TextField("Last Name");
    private EmailField email = new EmailField("Email");
    private TextField phoneNumber = new TextField("Phone Number");
    
    private TextField department = new TextField("Department");
    private TextField designation = new TextField("Designation");
    private TextField role = new TextField("Role");
    private ComboBox<EmploymentType> employmentType = new ComboBox<>("Employment Type", EmploymentType.values());
    private DatePicker dateOfJoining = new DatePicker("Date of Joining");
    private IntegerField experienceYears = new IntegerField("Experience (Years)");
    
    private NumberField salary = new NumberField("Salary");
    private NumberField bonus = new NumberField("Bonus");
    private TextField currency = new TextField("Currency");
    
    private TextField managerName = new TextField("Manager Name");
    private TextField workLocation = new TextField("Work Location");
    private ComboBox<EmployeeStatus> status = new ComboBox<>("Working Status", EmployeeStatus.values());
    private TextArea notes = new TextArea("Notes");

    public UserDashboardView(EmployeeService employeeService) {
        this.employeeService = employeeService;
        setSizeFull();
        setPadding(false);
        setMargin(false);
        setAlignItems(Alignment.CENTER);
        
        // Unicorn SaaS Background
        getStyle().set("background", "linear-gradient(135deg, #e0c3fc 0%, #8ec5fc 100%)");
        getStyle().set("background-attachment", "fixed");
        getStyle().set("overflow-x", "hidden");

        // Inject Custom Premium SaaS CSS
        add(new Html(
            "<style>" +
            "@keyframes fadeInUp {" +
            "    from { opacity: 0; transform: translate3d(0, 30px, 0); }" +
            "    to { opacity: 1; transform: translate3d(0, 0, 0); }" +
            "}" +
            ".unicorn-card {" +
            "    background: rgba(255, 255, 255, 0.85) !important;" +
            "    backdrop-filter: blur(12px);" +
            "    -webkit-backdrop-filter: blur(12px);" +
            "    border: 1px solid rgba(255, 255, 255, 0.6);" +
            "    border-radius: 24px !important;" +
            "    box-shadow: 0 8px 32px rgba(31, 38, 135, 0.08) !important;" +
            "    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275) !important;" +
            "    opacity: 0;" +
            "    animation: fadeInUp 0.7s ease-out forwards;" +
            "}" +
            ".unicorn-card:hover {" +
            "    transform: translateY(-6px);" +
            "    box-shadow: 0 15px 40px rgba(31, 38, 135, 0.15) !important;" +
            "}" +
            ".unicorn-btn {" +
            "    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;" +
            "    border-radius: 50px !important;" +
            "    box-shadow: 0 4px 15px rgba(118, 75, 162, 0.4) !important;" +
            "    transition: all 0.3s ease !important;" +
            "}" +
            ".unicorn-btn:hover {" +
            "    transform: scale(1.05) translateY(-2px);" +
            "    box-shadow: 0 8px 25px rgba(118, 75, 162, 0.6) !important;" +
            "}" +
            "</style>"
        ));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (currentEmployee == null) {
            loadUserProfile(event);
        }
    }

    private void loadUserProfile(BeforeEnterEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            logger.warn("Unauthenticated access attempt to UserDashboardView.");
            event.forwardTo("login");
            return;
        }
        
        String userEmail = authentication.getName();
        logger.info("Attempting to load Employee profile for authenticated user: '{}'", userEmail);
        Optional<Employee> userEmployee = employeeService.findModelByEmail(userEmail);
        
        if (userEmployee.isEmpty()) {
            logger.info("No Employee profile found for: '{}'. Creating a fresh profile in memory.", userEmail);
            
            // Create an empty profile for the user to fill out. DO NOT save it to the DB yet!
            currentEmployee = new Employee();
            currentEmployee.setEmail(userEmail);
            currentEmployee.setStatus(EmployeeStatus.ACTIVE);
        } else {
            logger.info("Successfully loaded Employee profile for: '{}'", userEmail);
            currentEmployee = userEmployee.get();
        }
        
        // RESTRICT USER EDITS: Only allow personal data changes. 
        // Administrative & HR fields are locked and strictly controlled by the Admin.
        email.setReadOnly(true);
        department.setReadOnly(true);
        role.setReadOnly(true);
        designation.setReadOnly(true);
        employmentType.setReadOnly(true);
        dateOfJoining.setReadOnly(true);
        salary.setReadOnly(true);
        bonus.setReadOnly(true);
        currency.setReadOnly(true);
        managerName.setReadOnly(true);
        
        binder.bindInstanceFields(this);
        binder.setBean(currentEmployee); // Live bind the bean

        buildDashboard();
    }

    private void buildDashboard() {
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setMaxWidth("800px");
        mainContainer.setWidthFull();
        mainContainer.setPadding(true);
        mainContainer.getStyle().set("padding-top", "40px");

        notes.setWidthFull();

        mainContainer.add(
            createHeaderCard(), 
            createSectionCard("Personal Information", VaadinIcon.USER, firstName, lastName, email, phoneNumber),
            createSectionCard("Job Details", VaadinIcon.BRIEFCASE, department, designation, role, employmentType, dateOfJoining, experienceYears),
            createSectionCard("Compensation", VaadinIcon.MONEY, salary, bonus, currency),
            createSectionCard("Work Details", VaadinIcon.BUILDING, managerName, workLocation, status),
            createSectionCard("Additional Notes", VaadinIcon.NOTEBOOK, notes),
            createActionCard()
        );
        add(mainContainer);
    }

    private Div createHeaderCard() {
        Div card = createCard();
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setWidthFull();

        Avatar avatar = new Avatar(currentEmployee.getFirstName() + " " + currentEmployee.getLastName());
        avatar.getStyle().set("width", "90px").set("height", "90px").set("font-size", "35px");
        avatar.getStyle().set("border", "4px solid white");
        avatar.getStyle().set("box-shadow", "0 4px 10px rgba(0,0,0,0.1)");
        avatar.setColorIndex(currentEmployee.getFirstName() != null ? currentEmployee.getFirstName().length() % 7 : 0);

        VerticalLayout info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        info.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        
        H2 name = new H2((currentEmployee.getFirstName() != null ? currentEmployee.getFirstName() : "") + " " + (currentEmployee.getLastName() != null ? currentEmployee.getLastName() : ""));
        name.getStyle().set("margin", "0").set("background", "-webkit-linear-gradient(45deg, #3b82f6, #8b5cf6)");
        name.getStyle().set("-webkit-background-clip", "text");
        name.getStyle().set("-webkit-text-fill-color", "transparent");
        name.getStyle().set("font-weight", "800");
        
        Span emailSpan = new Span(currentEmployee.getEmail());
        emailSpan.addClassNames(LumoUtility.TextColor.SECONDARY);
        emailSpan.getStyle().set("font-size", "15px").set("font-weight", "500");
        
        EmployeeStatus empStatus = currentEmployee.getStatus() != null ? currentEmployee.getStatus() : EmployeeStatus.ACTIVE;
        Span statusBadge = new Span(empStatus.name().replace("_", " "));
        statusBadge.getElement().getThemeList().add("badge");
        statusBadge.getStyle().set("margin-top", "8px").set("border-radius", "10px").set("padding", "4px 12px").set("font-weight", "bold").set("font-size", "12px");
        
        if (empStatus == EmployeeStatus.ACTIVE) {
            statusBadge.getStyle().set("background-color", "#dcfce7").set("color", "#166534"); // Green
        } else if (empStatus == EmployeeStatus.INACTIVE) {
            statusBadge.getStyle().set("background-color", "#fee2e2").set("color", "#991b1b"); // Red
        } else if (empStatus == EmployeeStatus.ON_LEAVE) {
            statusBadge.getStyle().set("background-color", "#fef08a").set("color", "#854d0e"); // Yellow
        }
        
        info.add(name, emailSpan, statusBadge);

        Button logoutBtn = new Button("Logout", new Icon(VaadinIcon.SIGN_OUT));
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        logoutBtn.addClickListener(e -> {
            SecurityContextHolder.clearContext();
            UI.getCurrent().getPage().setLocation("/login");
        });

        HorizontalLayout rightSide = new HorizontalLayout(logoutBtn);
        rightSide.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        layout.add(avatar, info, rightSide);
        layout.setFlexGrow(1, rightSide);
        
        card.add(layout);
        return card;
    }

    private Div createSectionCard(String title, VaadinIcon icon, Component... fields) {
        Div card = createCard();
        
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.getStyle().set("margin-bottom", "20px");
        
        Icon sectionIcon = icon.create();
        sectionIcon.getStyle().set("color", "#667eea").set("padding", "8px").set("background", "rgba(102, 126, 234, 0.1)").set("border-radius", "12px");
        
        H4 header = new H4(title);
        header.getStyle().set("margin", "0").set("color", "#2d3748").set("font-weight", "700");
        
        headerLayout.add(sectionIcon, header);

        FormLayout form = new FormLayout(fields);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("500px", 2));

        // Make text area full width in form
        for (Component field : fields) {
            if (field instanceof TextArea) {
                form.setColspan(field, 2);
            }
            // Soften field inputs for premium feel
            field.getElement().getStyle().set("--lumo-contrast-10pct", "rgba(226, 232, 240, 0.6)");
            field.getElement().getStyle().set("--lumo-border-radius-m", "10px");
        }

        card.add(headerLayout, form);
        return card;
    }

    private Div createActionCard() {
        Div card = createCard();
        card.getStyle().set("background", "transparent").set("box-shadow", "none").set("padding-top", "0");
        card.getStyle().set("border", "none");
        
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Button updateBtn = new Button("Update Profile", new Icon(VaadinIcon.CHECK));
        updateBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        updateBtn.addClassName("unicorn-btn");
        updateBtn.addClickListener(e -> updateProfile());

        layout.add(updateBtn);
        card.add(layout);
        return card;
    }

    private Div createCard() {
        Div card = new Div();
        card.addClassNames("unicorn-card", LumoUtility.Padding.LARGE, LumoUtility.Margin.Bottom.MEDIUM);
        card.setWidthFull();
        
        // Stagger the animation delay for a cascading load effect
        card.getStyle().set("animation-delay", (0.1 * animationDelayCounter++) + "s");
        
        return card;
    }

    private void updateProfile() {
        try {
            if (binder.isValid()) {
                // Re-assign currentEmployee to capture the MongoDB-generated ID
                currentEmployee = employeeService.saveEmployeeModel(currentEmployee);
                Notification.show("Profile updated successfully!", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                logger.info("User '{}' successfully updated their profile.", currentEmployee.getEmail());
                
                // Optional: UI.getCurrent().getPage().reload(); if you want the avatar to instantly update
            } else {
                Notification.show("Please correct the errors in the form.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                logger.warn("User '{}' attempted to save profile with validation errors.", currentEmployee.getEmail());
            }
        } catch (Exception e) {
            logger.error("Error saving profile for user '{}': {}", currentEmployee.getEmail(), e.getMessage(), e);
            Notification.show("Error saving profile: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
