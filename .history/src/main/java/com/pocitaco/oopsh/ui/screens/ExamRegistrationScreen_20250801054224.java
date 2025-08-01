package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.*;
import com.pocitaco.oopsh.models.*;
import com.pocitaco.oopsh.ui.MaterialDesignManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exam Registration Screen - Material Design 3.0
 * Giao diện đăng ký thi cử cho thí sinh với sidebar navigation
 * 
 * @author OOPSH Team
 * @version 1.0
 */
public class ExamRegistrationScreen extends BorderPane {

    // Data
    private final User currentUser;

    // DAOs
    private final ExamScheduleDAO examScheduleDAO = new ExamScheduleDAO();
    private final ExamTypeDAO examTypeDAO = new ExamTypeDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    // Data Collections
    private final ObservableList<ExamSchedule> availableExamsData = FXCollections.observableArrayList();
    private final ObservableList<Registration> myRegistrationsData = FXCollections.observableArrayList();

    // UI Components
    private TableView<ExamSchedule> availableExamsTable;
    private TableView<Registration> myRegistrationsTable;
    private TextField searchField;
    private ComboBox<ExamType> examTypeFilter;
    private ComboBox<String> statusFilter;

    // Statistics labels
    private Label totalExamsLabel;
    private Label registeredExamsLabel;
    private Label pendingExamsLabel;
    private Label completedExamsLabel;

    private TabPane mainTabPane;
    private VBox sidebar;

    public ExamRegistrationScreen(User user) {
        this.currentUser = user;
        initializeScreen();
        loadData();
        updateStatistics();
    }

    private void initializeScreen() {
        // Set Material Design background
        setStyle(MaterialDesignManager.Colors.BACKGROUND_SURFACE);

        // Create sidebar
        sidebar = createSidebar();

        // Create main content
        VBox mainContent = createMainContent();

        // Set layout
        setLeft(sidebar);
        setCenter(mainContent);
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(16);
        sidebar.setPrefWidth(280);
        sidebar.setPadding(new Insets(24));
        sidebar.setStyle("-fx-background-color: " + MaterialDesignManager.Colors.PRIMARY_COLOR + ";");

        // Header
        VBox header = createSidebarHeader();

        // Navigation buttons
        VBox navigationButtons = createNavigationButtons();

        // Statistics section
        VBox statisticsSection = createSidebarStatistics();

        sidebar.getChildren().addAll(header, navigationButtons, statisticsSection);
        return sidebar;
    }

    private VBox createSidebarHeader() {
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);

        // Icon
        var icon = MaterialDesignManager.Icons.createSchoolIcon();
        icon.setIconSize(48);
        icon.setIconColor(MaterialDesignManager.Colors.ON_PRIMARY);

        // Title
        Label titleLabel = new Label("EXAM REGISTRATION");
        titleLabel.setFont(MaterialDesignManager.Typography.TITLE_LARGE);
        titleLabel.setTextFill(MaterialDesignManager.Colors.ON_PRIMARY);
        titleLabel.setAlignment(Pos.CENTER);

        // Subtitle
        Label subtitleLabel = new Label("Manage your exam registrations");
        subtitleLabel.setFont(MaterialDesignManager.Typography.BODY_MEDIUM);
        subtitleLabel.setTextFill(MaterialDesignManager.Colors.ON_PRIMARY);
        subtitleLabel.setOpacity(0.8);
        subtitleLabel.setAlignment(Pos.CENTER);

        header.getChildren().addAll(icon, titleLabel, subtitleLabel);
        return header;
    }

    private VBox createNavigationButtons() {
        VBox buttonsContainer = new VBox(8);
        buttonsContainer.setAlignment(Pos.TOP_CENTER);

        // Navigation buttons
        Button dashboardButton = MaterialDesignManager.Buttons.createFilledButton(
                "Dashboard", MaterialDesignManager.Icons.createDashboardIcon());
        dashboardButton.setOnAction(e -> navigateToDashboard());

        Button practiceTestsButton = MaterialDesignManager.Buttons.createOutlinedButton(
                "Practice Tests", MaterialDesignManager.Icons.createBookOpenIcon());
        practiceTestsButton.setOnAction(e -> openPracticeTests());

        Button examResultsButton = MaterialDesignManager.Buttons.createOutlinedButton(
                "Exam Results", MaterialDesignManager.Icons.createChartLineIcon());
        examResultsButton.setOnAction(e -> openExamResults());

        Button studyMaterialsButton = MaterialDesignManager.Buttons.createOutlinedButton(
                "Study Materials", MaterialDesignManager.Icons.createFileDocumentIcon());
        studyMaterialsButton.setOnAction(e -> openStudyMaterials());

        Button logoutButton = MaterialDesignManager.Buttons.createTextButton(
                "Logout", MaterialDesignManager.Icons.createLogoutIcon());
        logoutButton.setOnAction(e -> logout());

        buttonsContainer.getChildren().addAll(
                dashboardButton, practiceTestsButton, examResultsButton,
                studyMaterialsButton, logoutButton);

        return buttonsContainer;
    }

    private VBox createSidebarStatistics() {
        VBox statsContainer = new VBox(12);
        statsContainer.setAlignment(Pos.CENTER);
        statsContainer.setPadding(new Insets(16));
        statsContainer.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 8;");

        Label statsTitle = new Label("STATISTICS");
        statsTitle.setFont(MaterialDesignManager.Typography.TITLE_SMALL);
        statsTitle.setTextFill(MaterialDesignManager.Colors.ON_PRIMARY);
        statsTitle.setAlignment(Pos.CENTER);

        // Statistics cards
        totalExamsLabel = createSidebarStatCard("Available", "0", MaterialDesignManager.Colors.SECONDARY_CONTAINER);
        registeredExamsLabel = createSidebarStatCard("Registered", "0", MaterialDesignManager.Colors.PRIMARY_CONTAINER);
        pendingExamsLabel = createSidebarStatCard("Pending", "0", MaterialDesignManager.Colors.TERTIARY_CONTAINER);
        completedExamsLabel = createSidebarStatCard("Completed", "0", MaterialDesignManager.Colors.SUCCESS);

        statsContainer.getChildren().addAll(
                statsTitle, totalExamsLabel, registeredExamsLabel,
                pendingExamsLabel, completedExamsLabel);

        return statsContainer;
    }

    private Label createSidebarStatCard(String title, String value, Color backgroundColor) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(8));
        card.setStyle("-fx-background-color: " + colorToHex(backgroundColor) + "; -fx-background-radius: 6;");

        Label valueLabel = new Label(value);
        valueLabel.setFont(MaterialDesignManager.Typography.HEADLINE_SMALL);
        valueLabel.setTextFill(MaterialDesignManager.Colors.ON_PRIMARY);

        Label titleLabel = new Label(title);
        titleLabel.setFont(MaterialDesignManager.Typography.LABEL_MEDIUM);
        titleLabel.setTextFill(MaterialDesignManager.Colors.ON_PRIMARY);
        titleLabel.setOpacity(0.8);

        card.getChildren().addAll(valueLabel, titleLabel);

        Label result = new Label();
        result.setGraphic(card);
        return result;
    }

    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private VBox createMainContent() {
        VBox mainContent = new VBox(24);
        mainContent.setPadding(new Insets(24));
        mainContent.setAlignment(Pos.TOP_CENTER);

        // Header with app bar
        HBox appBar = MaterialDesignManager.createAppBar("Exam Registration Management");

        // Tab Pane
        mainTabPane = createTabPane();

        mainContent.getChildren().addAll(appBar, mainTabPane);
        return mainContent;
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: transparent;");

        // Available Exams Tab
        Tab availableTab = new Tab("Available Exams");
        availableTab.setContent(createAvailableExamsContent());
        availableTab.setClosable(false);

        // My Registrations Tab
        Tab registrationsTab = new Tab("My Registrations");
        registrationsTab.setContent(createMyRegistrationsContent());
        registrationsTab.setClosable(false);

        tabPane.getTabs().addAll(availableTab, registrationsTab);
        return tabPane;
    }

    private VBox createAvailableExamsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(16));

        // Filters section
        VBox filtersSection = createFiltersSection();

        // Table section
        VBox tableSection = createTableSection();

        content.getChildren().addAll(filtersSection, tableSection);
        return content;
    }

    private VBox createFiltersSection() {
        VBox filtersSection = new VBox(16);
        filtersSection.setPadding(new Insets(16));
        filtersSection.setStyle("-fx-background-color: " + MaterialDesignManager.Colors.SURFACE_VARIANT
                + "; -fx-background-radius: 8;");

        // Title
        Label titleLabel = new Label("Search & Filter");
        titleLabel.setFont(MaterialDesignManager.Typography.TITLE_MEDIUM);
        titleLabel.setTextFill(MaterialDesignManager.Colors.ON_SURFACE);

        // Filters
        HBox filtersBox = new HBox(16);
        filtersBox.setAlignment(Pos.CENTER_LEFT);

        // Search field
        VBox searchContainer = new VBox(4);
        Label searchLabel = new Label("Search");
        searchLabel.setFont(MaterialDesignManager.Typography.LABEL_MEDIUM);
        searchField = new TextField();
        searchField.setPromptText("Search by exam name, location...");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        searchContainer.getChildren().addAll(searchLabel, searchField);

        // Exam type filter
        VBox typeContainer = new VBox(4);
        Label typeLabel = new Label("Exam Type");
        typeLabel.setFont(MaterialDesignManager.Typography.LABEL_MEDIUM);
        examTypeFilter = new ComboBox<>();
        examTypeFilter.setPromptText("All Types");
        examTypeFilter.setPrefWidth(180);
        examTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        typeContainer.getChildren().addAll(typeLabel, examTypeFilter);

        // Status filter
        VBox statusContainer = new VBox(4);
        Label statusLabel = new Label("Status");
        statusLabel.setFont(MaterialDesignManager.Typography.LABEL_MEDIUM);
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Available", "Registered");
        statusFilter.setValue("All");
        statusFilter.setPrefWidth(120);
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        statusContainer.getChildren().addAll(statusLabel, statusFilter);

        // Action buttons
        VBox actionsContainer = new VBox(4);
        Label actionsLabel = new Label("Actions");
        actionsLabel.setFont(MaterialDesignManager.Typography.LABEL_MEDIUM);
        HBox actionButtons = new HBox(8);

        Button clearButton = MaterialDesignManager.Buttons.createOutlinedButton(
                "Clear", MaterialDesignManager.Icons.createCancelIcon());
        clearButton.setOnAction(e -> clearFilters());

        Button refreshButton = MaterialDesignManager.Buttons.createFilledButton(
                "Refresh", MaterialDesignManager.Icons.createIcon("mdi2r-refresh", 16));
        refreshButton.setOnAction(e -> loadData());

        actionButtons.getChildren().addAll(clearButton, refreshButton);
        actionsContainer.getChildren().addAll(actionsLabel, actionButtons);

        filtersBox.getChildren().addAll(searchContainer, typeContainer, statusContainer, actionsContainer);
        filtersSection.getChildren().addAll(titleLabel, filtersBox);

        return filtersSection;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox(16);
        tableSection.setPadding(new Insets(16));
        tableSection.setStyle(
                "-fx-background-color: " + MaterialDesignManager.Colors.SURFACE + "; -fx-background-radius: 8;");

        // Title
        Label titleLabel = new Label("Available Exams");
        titleLabel.setFont(MaterialDesignManager.Typography.TITLE_MEDIUM);
        titleLabel.setTextFill(MaterialDesignManager.Colors.ON_SURFACE);

        // Table
        availableExamsTable = createAvailableExamsTable();

        tableSection.getChildren().addAll(titleLabel, availableExamsTable);
        return tableSection;
    }

    private TableView<ExamSchedule> createAvailableExamsTable() {
        TableView<ExamSchedule> table = new TableView<>();
        table.setItems(availableExamsData);
        table.setPlaceholder(new Label("No available exams found"));

        // Exam Name Column
        TableColumn<ExamSchedule, String> nameCol = new TableColumn<>("Exam Name");
        nameCol.setCellValueFactory(cellData -> {
            try {
                ExamType examType = examTypeDAO.findById(cellData.getValue().getExamTypeId()).orElse(null);
                return new SimpleStringProperty(examType != null ? examType.getName() : "N/A");
            } catch (Exception e) {
                return new SimpleStringProperty("N/A");
            }
        });
        nameCol.setPrefWidth(200);
        nameCol.setStyle("-fx-font-weight: bold;");

        // Type Column
        TableColumn<ExamSchedule, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> {
            try {
                ExamType examType = examTypeDAO.findById(cellData.getValue().getExamTypeId()).orElse(null);
                return new SimpleStringProperty(examType != null ? examType.getCode() : "N/A");
            } catch (Exception e) {
                return new SimpleStringProperty("N/A");
            }
        });
        typeCol.setPrefWidth(120);

        // Date Column
        TableColumn<ExamSchedule, String> dateCol = new TableColumn<>("Exam Date");
        dateCol.setCellValueFactory(cellData -> {
            LocalDate dateValue = cellData.getValue().getExamDate();
            return new SimpleStringProperty(
                    dateValue != null ? dateValue.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A");
        });
        dateCol.setPrefWidth(120);

        // Time Column
        TableColumn<ExamSchedule, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(cellData -> {
            String timeSlot = cellData.getValue().getTimeSlot();
            return new SimpleStringProperty(timeSlot != null ? timeSlot : "N/A");
        });
        timeCol.setPrefWidth(100);

        // Location Column
        TableColumn<ExamSchedule, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationCol.setPrefWidth(150);

        // Status Column
        TableColumn<ExamSchedule, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> {
            boolean isRegistered = isAlreadyRegistered(cellData.getValue());
            return new SimpleStringProperty(isRegistered ? "Registered" : "Available");
        });
        statusCol.setPrefWidth(100);

        // Action Column
        TableColumn<ExamSchedule, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<ExamSchedule, Void>() {
            private final Button registerBtn = MaterialDesignManager.Buttons.createFilledButton(
                    "Register", MaterialDesignManager.Icons.createAddIcon());

            {
                registerBtn.setOnAction(event -> {
                    ExamSchedule schedule = getTableView().getItems().get(getIndex());
                    if (!isAlreadyRegistered(schedule)) {
                        registerForExam(schedule);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ExamSchedule schedule = getTableView().getItems().get(getIndex());
                    boolean isRegistered = isAlreadyRegistered(schedule);
                    registerBtn.setDisable(isRegistered);
                    registerBtn.setText(isRegistered ? "Registered" : "Register");
                    setGraphic(registerBtn);
                }
            }
        });
        actionCol.setPrefWidth(120);

        table.getColumns().addAll(nameCol, typeCol, dateCol, timeCol, locationCol, statusCol, actionCol);
        return table;
    }

    private VBox createMyRegistrationsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(16));

        // Header
        VBox header = new VBox(8);
        Label titleLabel = new Label("My Exam Registrations");
        titleLabel.setFont(MaterialDesignManager.Typography.TITLE_LARGE);
        titleLabel.setTextFill(MaterialDesignManager.Colors.ON_SURFACE);

        Label subtitleLabel = new Label("View and manage your exam registrations");
        subtitleLabel.setFont(MaterialDesignManager.Typography.BODY_MEDIUM);
        subtitleLabel.setTextFill(MaterialDesignManager.Colors.ON_SURFACE_VARIANT);

        header.getChildren().addAll(titleLabel, subtitleLabel);

        // Table
        VBox tableContainer = new VBox(16);
        tableContainer.setPadding(new Insets(16));
        tableContainer.setStyle(
                "-fx-background-color: " + MaterialDesignManager.Colors.SURFACE + "; -fx-background-radius: 8;");

        myRegistrationsTable = createMyRegistrationsTable();
        tableContainer.getChildren().add(myRegistrationsTable);

        content.getChildren().addAll(header, tableContainer);
        return content;
    }

    private TableView<Registration> createMyRegistrationsTable() {
        TableView<Registration> table = new TableView<>();
        table.setItems(myRegistrationsData);
        table.setPlaceholder(new Label("No registrations found"));

        // Registration ID Column
        TableColumn<Registration, String> idCol = new TableColumn<>("Registration ID");
        idCol.setCellValueFactory(cellData -> new SimpleStringProperty("REG-" + cellData.getValue().getId()));
        idCol.setPrefWidth(150);

        // Exam Type Column
        TableColumn<Registration, String> examTypeCol = new TableColumn<>("Exam Type");
        examTypeCol.setCellValueFactory(cellData -> {
            try {
                ExamType examType = examTypeDAO.findById(cellData.getValue().getExamTypeId()).orElse(null);
                return new SimpleStringProperty(examType != null ? examType.getName() : "N/A");
            } catch (Exception e) {
                return new SimpleStringProperty("N/A");
            }
        });
        examTypeCol.setPrefWidth(200);

        // Registration Date Column
        TableColumn<Registration, String> regDateCol = new TableColumn<>("Registration Date");
        regDateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getRegistrationDate();
            return new SimpleStringProperty(
                    date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A");
        });
        regDateCol.setPrefWidth(150);

        // Status Column
        TableColumn<Registration, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(120);

        // Action Column
        TableColumn<Registration, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<Registration, Void>() {
            private final Button cancelBtn = MaterialDesignManager.Buttons.createOutlinedButton(
                    "Cancel", MaterialDesignManager.Icons.createDeleteIcon());
            private final Button viewBtn = MaterialDesignManager.Buttons.createTextButton(
                    "View", MaterialDesignManager.Icons.createFileDocumentIcon());
            private final HBox container = new HBox(8, viewBtn, cancelBtn);

            {
                cancelBtn.setOnAction(event -> {
                    Registration registration = getTableView().getItems().get(getIndex());
                    cancelRegistration(registration);
                });

                viewBtn.setOnAction(event -> {
                    Registration registration = getTableView().getItems().get(getIndex());
                    viewRegistrationDetails(registration);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Registration registration = getTableView().getItems().get(getIndex());
                    boolean canCancel = "REGISTERED".equals(registration.getStatus()) ||
                            "PENDING".equals(registration.getStatus());
                    cancelBtn.setDisable(!canCancel);
                    setGraphic(container);
                }
            }
        });
        actionCol.setPrefWidth(150);

        table.getColumns().addAll(idCol, examTypeCol, regDateCol, statusCol, actionCol);
        return table;
    }

    private void loadData() {
        loadAvailableExams();
        loadMyRegistrations();
        loadExamTypes();
        updateStatistics();
    }

    private void loadAvailableExams() {
        try {
            List<ExamSchedule> schedules = examScheduleDAO.getAllSchedules();
            // Filter only future exams that are open for registration
            List<ExamSchedule> availableSchedules = schedules.stream()
                    .filter(schedule -> schedule.getExamDate() != null &&
                            schedule.getExamDate().isAfter(LocalDate.now()))
                    .collect(Collectors.toList());

            availableExamsData.clear();
            availableExamsData.addAll(availableSchedules);
            updateStatistics();
        } catch (Exception e) {
            showError("Error loading available exams: " + e.getMessage());
        }
    }

    private void loadMyRegistrations() {
        try {
            List<Registration> registrations = registrationDAO.getRegistrationsByUser(currentUser.getId());
            myRegistrationsData.clear();
            myRegistrationsData.addAll(registrations);
            updateStatistics();
        } catch (Exception e) {
            showError("Error loading registrations: " + e.getMessage());
        }
    }

    private void loadExamTypes() {
        try {
            List<ExamType> examTypes = examTypeDAO.getAllExamTypes();
            examTypeFilter.getItems().clear();
            examTypeFilter.getItems().addAll(examTypes);
        } catch (Exception e) {
            showError("Error loading exam types: " + e.getMessage());
        }
    }

    private void applyFilters() {
        try {
            String searchText = searchField.getText().toLowerCase().trim();
            ExamType selectedType = examTypeFilter.getValue();

            List<ExamSchedule> allSchedules = examScheduleDAO.getAllSchedules();
            List<ExamSchedule> filteredSchedules = allSchedules.stream()
                    .filter(schedule -> matchesFilters(schedule))
                    .filter(schedule -> {
                        // Search filter
                        if (searchText.isEmpty())
                            return true;
                        try {
                            ExamType examType = examTypeDAO.findById(schedule.getExamTypeId()).orElse(null);
                            String examName = examType != null ? examType.getName() : "";
                            String location = schedule.getLocation() != null ? schedule.getLocation() : "";
                            return examName.toLowerCase().contains(searchText) ||
                                    location.toLowerCase().contains(searchText);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .filter(schedule -> {
                        // Type filter
                        if (selectedType == null)
                            return true;
                        return schedule.getExamTypeId() == selectedType.getId();
                    })
                    .collect(Collectors.toList());

            availableExamsData.clear();
            availableExamsData.addAll(filteredSchedules);
        } catch (Exception e) {
            showError("Error applying filters: " + e.getMessage());
        }
    }

    private boolean matchesFilters(ExamSchedule schedule) {
        // Only show future exams
        return schedule.getExamDate() != null &&
                schedule.getExamDate().isAfter(LocalDate.now());
    }

    private void clearFilters() {
        searchField.clear();
        examTypeFilter.setValue(null);
        loadAvailableExams();
    }

    private boolean isAlreadyRegistered(ExamSchedule schedule) {
        return myRegistrationsData.stream()
                .anyMatch(reg -> reg.getExamTypeId() == schedule.getExamTypeId());
    }

    private void registerForExam(ExamSchedule schedule) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Registration");
        confirmation.setHeaderText("Register for Exam");

        try {
            ExamType examType = examTypeDAO.findById(schedule.getExamTypeId()).orElse(null);
            String examName = examType != null ? examType.getName() : "N/A";
            String location = schedule.getLocation() != null ? schedule.getLocation() : "N/A";

            confirmation.setContentText("Are you sure you want to register for this exam?\n\n" +
                    "Exam: " + examName + "\n" +
                    "Date: " + schedule.getExamDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                    "Location: " + location);
        } catch (Exception e) {
            confirmation.setContentText("Are you sure you want to register for this exam?");
        }

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Registration registration = new Registration();
                    registration.setUserId(currentUser.getId());
                    registration.setExamTypeId(schedule.getExamTypeId());
                    registration.setRegistrationDate(LocalDate.now());
                    registration.setStatus("REGISTERED");

                    registrationDAO.saveRegistration(registration);
                    loadMyRegistrations();
                    loadAvailableExams(); // Refresh to update status
                    showSuccess("Registration successful!");
                } catch (Exception e) {
                    showError("Registration failed: " + e.getMessage());
                }
            }
        });
    }

    private void cancelRegistration(Registration registration) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancel Registration");
        confirmation.setHeaderText("Cancel Exam Registration");
        confirmation.setContentText("Are you sure you want to cancel this registration?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    registrationDAO.deleteRegistration(registration.getId());
                    loadMyRegistrations();
                    loadAvailableExams(); // Refresh to update status
                    showSuccess("Registration cancelled successfully!");
                } catch (Exception e) {
                    showError("Failed to cancel registration: " + e.getMessage());
                }
            }
        });
    }

    private void updateStatistics() {
        int totalExams = availableExamsData.size();
        int registeredExams = myRegistrationsData.size();

        Label totalValueLabel = (Label) ((VBox) totalExamsLabel.getGraphic()).getChildren().get(0);
        totalValueLabel.setText(String.valueOf(totalExams));

        Label regValueLabel = (Label) ((VBox) registeredExamsLabel.getGraphic()).getChildren().get(0);
        regValueLabel.setText(String.valueOf(registeredExams));
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Placeholder methods for navigation
    private void navigateToDashboard() {
        // Implement navigation to DashboardScreen
        System.out.println("Navigating to Dashboard");
    }

    private void openPracticeTests() {
        // Implement navigation to PracticeTestsScreen
        System.out.println("Opening Practice Tests");
    }

    private void openExamResults() {
        // Implement navigation to ExamResultsScreen
        System.out.println("Opening Exam Results");
    }

    private void openStudyMaterials() {
        // Implement navigation to StudyMaterialsScreen
        System.out.println("Opening Study Materials");
    }

    private void logout() {
        // Implement logout logic
        System.out.println("Logging out");
        // Example: Stage stage = (Stage) searchField.getScene().getWindow();
        // stage.close();
    }

    private void viewRegistrationDetails(Registration registration) {
        // Implement navigation to RegistrationDetailsScreen
        System.out.println("Viewing registration details for ID: " + registration.getId());
    }
}
