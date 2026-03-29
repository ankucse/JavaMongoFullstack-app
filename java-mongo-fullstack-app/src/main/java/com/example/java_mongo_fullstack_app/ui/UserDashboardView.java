package com.example.java_mongo_fullstack_app.ui;

import com.example.java_mongo_fullstack_app.model.Employee;
import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.example.java_mongo_fullstack_app.model.EmploymentType;
import com.example.java_mongo_fullstack_app.service.EmployeeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Component;
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
        getStyle().set("background", "linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)"); // Premium soft background
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
            createSectionCard("Personal Information", firstName, lastName, email, phoneNumber),
            createSectionCard("Job Details", department, designation, role, employmentType, dateOfJoining, experienceYears),
            createSectionCard("Compensation", salary, bonus, currency),
            createSectionCard("Work Details", managerName, workLocation, status),
            createSectionCard("Additional Notes", notes),
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
        avatar.getStyle().set("width", "80px").set("height", "80px").set("font-size", "30px");
        avatar.setColorIndex(currentEmployee.getFirstName() != null ? currentEmployee.getFirstName().length() % 7 : 0);

        VerticalLayout info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        H2 name = new H2((currentEmployee.getFirstName() != null ? currentEmployee.getFirstName() : "") + " " + (currentEmployee.getLastName() != null ? currentEmployee.getLastName() : ""));
        name.getStyle().set("margin", "0");
        Span emailSpan = new Span(currentEmployee.getEmail());
        emailSpan.addClassNames(LumoUtility.TextColor.SECONDARY);
        info.add(name, emailSpan);

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

    private Div createSectionCard(String title, Component... fields) {
        Div card = createCard();
        H4 header = new H4(title);
        header.getStyle().set("margin-top", "0");

        FormLayout form = new FormLayout(fields);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("500px", 2));

        // Make text area full width in form
        for (Component field : fields) {
            if (field instanceof TextArea) {
                form.setColspan(field, 2);
            }
        }

        card.add(header, form);
        return card;
    }

    private Div createActionCard() {
        Div card = createCard();
        card.getStyle().set("background", "transparent").set("box-shadow", "none").set("padding-top", "0");
        
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Button updateBtn = new Button("Update Profile", new Icon(VaadinIcon.CHECK));
        updateBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        updateBtn.addClickListener(e -> updateProfile());

        layout.add(updateBtn);
        card.add(layout);
        return card;
    }

    private Div createCard() {
        Div card = new Div();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.BorderRadius.LARGE, LumoUtility.BoxShadow.SMALL, LumoUtility.Padding.LARGE, LumoUtility.Margin.Bottom.MEDIUM);
        card.setWidthFull();
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
