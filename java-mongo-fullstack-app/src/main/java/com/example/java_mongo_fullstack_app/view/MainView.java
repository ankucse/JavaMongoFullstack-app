package com.example.java_mongo_fullstack_app.view;

import com.example.java_mongo_fullstack_app.dto.EmployeeDto;
import com.example.java_mongo_fullstack_app.model.EmployeeStatus;
import com.example.java_mongo_fullstack_app.model.EmploymentType;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Route("")
public class MainView extends VerticalLayout {

    private final RestTemplate restTemplate;
    private final Grid<EmployeeDto> grid = new Grid<>(EmployeeDto.class, false);
    private final String API_URL = "http://localhost:8080/employees";
    private TextField searchField;
    private List<EmployeeDto> allEmployees;

    // KPI Cards
    private final Span totalEmployeesCount = new Span("0");
    private final Span activeEmployeesCount = new Span("0");
    private final Span onLeaveEmployeesCount = new Span("0");
    private final Span inactiveEmployeesCount = new Span("0");

    // Filter Buttons
    private Button allFilterButton;
    private Button activeFilterButton;
    private Button onLeaveFilterButton;
    private Button inactiveFilterButton;

    public MainView() {
        this.restTemplate = new RestTemplate();
        
        setSizeFull();
        setPadding(false);
        setMargin(false);
        getStyle().set("background-color", "#f5f7fa"); // Premium light grey background

        add(createHeader(), createMainContent());
        listEmployees();
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setAlignItems(Alignment.CENTER);
        header.addClassNames(LumoUtility.Background.BASE, LumoUtility.BoxShadow.SMALL);
        header.setHeight("70px"); // Slightly taller header

        VerticalLayout titleContainer = new VerticalLayout();
        titleContainer.setSpacing(false);
        titleContainer.setPadding(false);
        H2 title = new H2("Employee Management System");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE);
        title.getStyle().set("font-weight", "600");
        title.getStyle().set("color", "#2563eb"); // Primary blue

        Span subtitle = new Span("Manage your workforce efficiently");
        subtitle.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);

        titleContainer.add(title, subtitle);

        HorizontalLayout spacer = new HorizontalLayout();
        header.expand(spacer);

        Avatar avatar = new Avatar("Admin User");
        avatar.setColorIndex(3); // A nice color for the avatar
        Span welcomeText = new Span("Welcome, Admin");
        welcomeText.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontWeight.MEDIUM);

        HorizontalLayout profileArea = new HorizontalLayout(welcomeText, avatar);
        profileArea.setAlignItems(Alignment.CENTER);
        profileArea.setSpacing(true);

        header.add(titleContainer, spacer, profileArea);
        return header;
    }

    private VerticalLayout createMainContent() {
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setMaxWidth("1200px");
        mainContainer.setWidthFull();
        mainContainer.setMargin(true); // Centers the container
        mainContainer.setPadding(true);
        mainContainer.setAlignItems(Alignment.STRETCH);
        mainContainer.setSpacing(true); // Spacing between sections

        // KPI Cards Section
        HorizontalLayout kpiCards = new HorizontalLayout(
            createKpiCard("Total Employees", totalEmployeesCount, VaadinIcon.USERS, "#2563eb"),
            createKpiCard("Active Employees", activeEmployeesCount, VaadinIcon.CHECK_CIRCLE, "#22c55e"),
            createKpiCard("On Leave", onLeaveEmployeesCount, VaadinIcon.CLOCK, "#f97316"),
            createKpiCard("Inactive Employees", inactiveEmployeesCount, VaadinIcon.MINUS_CIRCLE, "#ef4444")
        );
        kpiCards.setWidthFull();
        kpiCards.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        kpiCards.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);
        mainContainer.add(kpiCards);

        // Toolbar and Grid Section (wrapped in a card)
        Div gridCard = new Div();
        gridCard.addClassNames(LumoUtility.Background.BASE, LumoUtility.BorderRadius.LARGE, LumoUtility.BoxShadow.SMALL, LumoUtility.Padding.LARGE);
        gridCard.setWidthFull();
        gridCard.setHeight("100%");
        gridCard.getStyle().set("display", "flex");
        gridCard.getStyle().set("flex-direction", "column");

        gridCard.add(createToolbar(), createGrid());
        
        mainContainer.add(gridCard);
        mainContainer.setFlexGrow(1, gridCard);
        return mainContainer;
    }

    private Component createKpiCard(String title, Span countSpan, VaadinIcon icon, String color) {
        Div card = new Div();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.BorderRadius.LARGE, LumoUtility.BoxShadow.XSMALL, LumoUtility.Padding.MEDIUM);
        card.getStyle().set("border", "1px solid #e5e7eb");
        card.setWidth("23%"); // Adjust width for 4 cards
        card.getStyle().set("min-width", "200px"); // Minimum width for responsiveness

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.setAlignItems(FlexComponent.Alignment.START);

        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        Icon cardIcon = icon.create();
        cardIcon.getStyle().set("color", color);
        cardIcon.setSize("24px");
        H3 cardTitle = new H3(title);
        cardTitle.addClassNames(LumoUtility.Margin.NONE, LumoUtility.FontSize.MEDIUM, LumoUtility.TextColor.SECONDARY);
        header.add(cardIcon, cardTitle);

        countSpan.addClassNames(LumoUtility.FontSize.XXXLARGE, LumoUtility.FontWeight.BOLD, LumoUtility.Margin.Top.SMALL);
        countSpan.getStyle().set("color", color);

        content.add(header, countSpan);
        card.add(content);
        return card;
    }

    private HorizontalLayout createToolbar() {
        searchField = new TextField();
        searchField.setPlaceholder("Search by name or email...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setClearButtonVisible(true);
        searchField.setWidth("350px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> applyFilters());
        searchField.getStyle().set("border-radius", "8px");
        searchField.addClassNames(LumoUtility.BoxShadow.XSMALL);

        // Filter buttons
        allFilterButton = new Button("All", e -> {
            clearFilterButtonStyles();
            allFilterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            applyFilters(null);
        });
        activeFilterButton = new Button("Active", e -> {
            clearFilterButtonStyles();
            activeFilterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            applyFilters(EmployeeStatus.ACTIVE);
        });
        onLeaveFilterButton = new Button("On Leave", e -> {
            clearFilterButtonStyles();
            onLeaveFilterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            applyFilters(EmployeeStatus.ON_LEAVE);
        });
        inactiveFilterButton = new Button("Inactive", e -> {
            clearFilterButtonStyles();
            inactiveFilterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            applyFilters(EmployeeStatus.INACTIVE);
        });

        // Set initial style for 'All' button
        allFilterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout filterButtons = new HorizontalLayout(allFilterButton, activeFilterButton, onLeaveFilterButton, inactiveFilterButton);
        filterButtons.setSpacing(true);
        filterButtons.addClassNames(LumoUtility.Margin.Left.MEDIUM);

        Button addButton = new Button("Add Employee", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClassNames(LumoUtility.BoxShadow.SMALL);
        addButton.getStyle().set("background-color", "#2563eb");
        addButton.getStyle().set("border-radius", "8px");
        addButton.addClickListener(e -> openEmployeeDialog(new EmployeeDto(), true));

        HorizontalLayout toolbarLeft = new HorizontalLayout(searchField, filterButtons);
        toolbarLeft.setAlignItems(Alignment.CENTER);
        toolbarLeft.setSpacing(true);

        HorizontalLayout toolbar = new HorizontalLayout(toolbarLeft, addButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        return toolbar;
    }

    private void clearFilterButtonStyles() {
        allFilterButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        activeFilterButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        onLeaveFilterButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        inactiveFilterButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    private Grid<EmployeeDto> createGrid() {
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.setHeight("100%");
        grid.getStyle().set("border", "none");
        grid.addClassNames(LumoUtility.BorderRadius.MEDIUM); // Rounded corners for grid itself

        grid.addColumn(new ComponentRenderer<>(emp -> {
            HorizontalLayout row = new HorizontalLayout();
            row.setAlignItems(Alignment.CENTER);
            Avatar avatar = new Avatar(emp.getFirstName() + " " + emp.getLastName());
            avatar.setColorIndex(emp.getFirstName().length() % 7); // Simple way to get varied avatar colors
            Span name = new Span(emp.getFirstName() + " " + emp.getLastName());
            name.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.SMALL);
            VerticalLayout nameLayout = new VerticalLayout(name, new Span(emp.getEmail()));
            nameLayout.setPadding(false);
            nameLayout.setSpacing(false);
            nameLayout.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.XSMALL);
            row.add(avatar, nameLayout);
            return row;
        })).setHeader("Name").setAutoWidth(true).setFlexGrow(1);

        grid.addColumn(EmployeeDto::getDepartment).setHeader("Department").setAutoWidth(true);
        grid.addColumn(EmployeeDto::getRole).setHeader("Role").setAutoWidth(true);
        
        grid.addColumn(new ComponentRenderer<>(this::createEmploymentTypeTag)).setHeader("Type").setAutoWidth(true);

        grid.addComponentColumn(this::createStatusBadge).setHeader("Status").setAutoWidth(true);
        
        grid.addColumn(new ComponentRenderer<>(emp -> {
            Button editBtn = new Button(new Icon(VaadinIcon.EDIT));
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.setTooltipText("Edit Employee");
            editBtn.addClickListener(e -> openEmployeeDialog(emp, false));
            
            Button deleteBtn = new Button(new Icon(VaadinIcon.TRASH));
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.setTooltipText("Delete Employee");
            deleteBtn.addClickListener(e -> confirmDelete(emp));
            
            HorizontalLayout actions = new HorizontalLayout(editBtn, deleteBtn);
            actions.setSpacing(false);
            return actions;
        })).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setAllRowsVisible(true); // Ensure all rows are visible if data is small

        // Handling empty state without the deprecated method
        // We will just not use the specific empty state API if it's causing issues.
        // If there are no items, the grid will be empty. We can show a notification instead if needed.
        // A more robust implementation would involve swapping the grid with an empty layout dynamically.

        grid.addItemClickListener(event -> openEmployeeDialog(event.getItem(), false)); // Row click to edit

        return grid;
    }

    private Span createStatusBadge(EmployeeDto employee) {
        Span badge = new Span(employee.getStatus() != null ? employee.getStatus().name() : "UNKNOWN");
        badge.getElement().getThemeList().add("badge"); // Vaadin Lumo badge theme
        
        String bgColor;
        String textColor;

        if (employee.getStatus() == EmployeeStatus.ACTIVE) {
            bgColor = "#dcfce7"; // Light green
            textColor = "#166534"; // Dark green
        } else if (employee.getStatus() == EmployeeStatus.INACTIVE) {
            bgColor = "#fee2e2"; // Light red
            textColor = "#991b1b"; // Dark red
        } else if (employee.getStatus() == EmployeeStatus.ON_LEAVE) {
            bgColor = "#ffedd5"; // Light orange
            textColor = "#9a3412"; // Dark orange
        } else {
            bgColor = "#e2e8f0"; // Light gray
            textColor = "#475569"; // Dark gray
        }
        
        badge.getStyle().set("background-color", bgColor);
        badge.getStyle().set("color", textColor);
        badge.getStyle().set("padding", "4px 8px");
        badge.getStyle().set("border-radius", "12px");
        badge.getStyle().set("font-size", "12px");
        badge.getStyle().set("font-weight", "bold");
        badge.getStyle().set("text-transform", "uppercase");
        return badge;
    }

    private Span createEmploymentTypeTag(EmployeeDto employee) {
        Span tag = new Span(employee.getEmploymentType() != null ? employee.getEmploymentType().name() : "-");
        tag.getElement().getThemeList().add("badge");
        tag.getElement().getThemeList().add("small");
        tag.getElement().getThemeList().add("contrast"); // Outlined chip style
        tag.getStyle().set("border-radius", "8px");
        tag.getStyle().set("text-transform", "uppercase");
        tag.getStyle().set("font-size", "10px");
        tag.getStyle().set("padding", "3px 6px");
        return tag;
    }

    private void applyFilters() {
        applyFilters(null); // Default to no status filter
    }

    private void applyFilters(EmployeeStatus statusFilter) {
        if (allEmployees == null) return;

        String searchTerm = searchField.getValue() != null ? searchField.getValue().toLowerCase() : "";

        List<EmployeeDto> filtered = allEmployees.stream()
            .filter(emp -> {
                boolean matchesSearch = searchTerm.isEmpty() ||
                                        (emp.getFirstName() != null && emp.getFirstName().toLowerCase().contains(searchTerm)) ||
                                        (emp.getLastName() != null && emp.getLastName().toLowerCase().contains(searchTerm)) ||
                                        (emp.getEmail() != null && emp.getEmail().toLowerCase().contains(searchTerm)) ||
                                        (emp.getDepartment() != null && emp.getDepartment().toLowerCase().contains(searchTerm)) ||
                                        (emp.getRole() != null && emp.getRole().toLowerCase().contains(searchTerm));

                boolean matchesStatus = statusFilter == null || emp.getStatus() == statusFilter;

                return matchesSearch && matchesStatus;
            })
            .collect(Collectors.toList());
        grid.setItems(filtered);
    }

    private void listEmployees() {
        try {
            EmployeeDto[] employees = restTemplate.getForObject(API_URL, EmployeeDto[].class);
            if (employees != null) {
                allEmployees = Arrays.asList(employees);
                applyFilters(null); // Apply initial filters (all employees)
                updateKpiCards();
            }
        } catch (Exception e) {
            Notification.show("Error fetching employees: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void updateKpiCards() {
        if (allEmployees == null) return;

        totalEmployeesCount.setText(String.valueOf(allEmployees.size()));
        activeEmployeesCount.setText(String.valueOf(allEmployees.stream().filter(e -> e.getStatus() == EmployeeStatus.ACTIVE).count()));
        onLeaveEmployeesCount.setText(String.valueOf(allEmployees.stream().filter(e -> e.getStatus() == EmployeeStatus.ON_LEAVE).count()));
        inactiveEmployeesCount.setText(String.valueOf(allEmployees.stream().filter(e -> e.getStatus() == EmployeeStatus.INACTIVE).count()));
    }

    private void openEmployeeDialog(EmployeeDto employee, boolean isNew) {
        Dialog dialog = new Dialog();
        dialog.setWidth("800px");
        dialog.setMaxWidth("95vw");
        dialog.setHeaderTitle(isNew ? "✨ Create New Employee" : "✏️ Edit Employee Details");

        Binder<EmployeeDto> binder = new BeanValidationBinder<>(EmployeeDto.class);

        // --- Form Fields ---
        // Personal
        TextField firstName = new TextField("First Name");
        TextField lastName = new TextField("Last Name");
        TextField email = new TextField("Email");
        TextField phoneNumber = new TextField("Phone Number");

        // Job
        TextField department = new TextField("Department");
        TextField designation = new TextField("Designation");
        TextField role = new TextField("Role");
        ComboBox<EmploymentType> employmentType = new ComboBox<>("Employment Type", EmploymentType.values());
        DatePicker dateOfJoining = new DatePicker("Date of Joining");
        IntegerField experienceYears = new IntegerField("Experience (Years)");
        
        // Comp
        NumberField salary = new NumberField("Salary");
        NumberField bonus = new NumberField("Bonus");
        TextField currency = new TextField("Currency");
        if(isNew) currency.setValue("INR");

        // Work
        TextField managerName = new TextField("Manager Name");
        TextField workLocation = new TextField("Work Location");
        ComboBox<EmployeeStatus> status = new ComboBox<>("Status", EmployeeStatus.values());
        if(isNew) status.setValue(EmployeeStatus.ACTIVE);

        TextArea notes = new TextArea("Notes");
        notes.setWidthFull();

        // Bindings
        binder.bind(firstName, "firstName");
        binder.bind(lastName, "lastName");
        binder.bind(email, "email");
        binder.bind(phoneNumber, "phoneNumber");
        binder.bind(department, "department");
        binder.bind(designation, "designation");
        binder.bind(role, "role");
        binder.bind(employmentType, "employmentType");
        binder.bind(dateOfJoining, "dateOfJoining");
        binder.bind(experienceYears, "experienceYears");
        binder.bind(salary, "salary");
        binder.bind(bonus, "bonus");
        binder.bind(currency, "currency");
        binder.bind(managerName, "managerName");
        binder.bind(workLocation, "workLocation");
        binder.bind(status, "status");
        binder.bind(notes, "notes");

        binder.readBean(employee);

        // Layouts
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(true);
        dialogLayout.getStyle().set("background-color", "#f5f7fa");
        dialogLayout.setPadding(true);

        dialogLayout.add(
            createSectionCard("Personal Information", firstName, lastName, email, phoneNumber),
            createSectionCard("Job Details", department, designation, role, employmentType, dateOfJoining, experienceYears),
            createSectionCard("Compensation", salary, bonus, currency),
            createSectionCard("Work Details", managerName, workLocation, status),
            createSectionCard("Additional Notes", notes)
        );

        dialog.add(dialogLayout);

        Button saveBtn = new Button("Save", e -> {
            if (binder.writeBeanIfValid(employee)) {
                try {
                    if (isNew) {
                        restTemplate.postForObject(API_URL, employee, EmployeeDto.class);
                        showSuccess("Employee created successfully!");
                    } else {
                        restTemplate.put(API_URL + "/" + employee.getId(), employee);
                        showSuccess("Employee updated successfully!");
                    }
                    dialog.close();
                    listEmployees(); // Refresh grid and KPIs
                } catch (HttpClientErrorException ex) {
                    showError("Validation Error: " + ex.getResponseBodyAsString());
                } catch (Exception ex) {
                    showError("Error saving employee.");
                }
            } else {
                showError("Please fill out all required fields correctly.");
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.getStyle().set("background-color", "#2563eb");
        
        Button cancelBtn = new Button("Cancel", e -> dialog.close());
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
    }

    private Div createSectionCard(String title, Component... fields) {
        Div card = new Div();
        card.addClassNames(LumoUtility.Background.BASE, LumoUtility.BorderRadius.LARGE, LumoUtility.Padding.LARGE, LumoUtility.BoxShadow.XSMALL, LumoUtility.Margin.Bottom.SMALL);
        card.getStyle().set("border", "1px solid #e5e7eb");
        
        H4 header = new H4(title);
        header.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.MEDIUM);
        
        FormLayout form = new FormLayout(fields);
        form.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );
        
        // Make text area full width in form
        for (Component field : fields) {
            if (field instanceof TextArea) {
                form.setColspan(field, 2);
            }
        }
        
        card.add(header, form);
        return card;
    }

    private void confirmDelete(EmployeeDto employee) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete Employee");
        dialog.setText("Are you sure you want to delete " + employee.getFirstName() + " " + employee.getLastName() + "? This action cannot be undone.");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");
        
        dialog.addConfirmListener(e -> {
            try {
                restTemplate.delete(API_URL + "/" + employee.getId());
                showSuccess("Employee deleted.");
                listEmployees(); // Refresh grid and KPIs
            } catch (Exception ex) {
                showError("Failed to delete employee: " + ex.getMessage());
            }
        });
        dialog.open();
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
