package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.models.ExamSchedule;
import com.pocitaco.oopsh.models.ExamType;
import com.pocitaco.oopsh.enums.ScheduleStatus;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exam Schedule Management Screen
 * Quản lý lịch thi cử cho admin
 * 
 * @author OOPSH Team
 * @version 1.0
 */
public class ExamScheduleManagementScreen extends BorderPane {
    private TableView<ExamSchedule> scheduleTable;
    private ObservableList<ExamSchedule> scheduleData;
    private ExamScheduleDAO scheduleDAO;
    private ExamTypeDAO examTypeDAO;
    private UserDAO userDAO;
    private ComboBox<ExamType> examTypeFilter;
    private ComboBox<ScheduleStatus> statusFilter;
    private TextField searchField;

    // Statistics labels
    private Label totalSchedulesLabel;
    private Label todaySchedulesLabel;
    private Label upcomingSchedulesLabel;
    private Label completedSchedulesLabel;

    public ExamScheduleManagementScreen() {
        this.scheduleDAO = new ExamScheduleDAO();
        this.examTypeDAO = new ExamTypeDAO();
        this.userDAO = new UserDAO();
        this.scheduleData = FXCollections.observableArrayList();

        setupUI();
        loadData();
        updateStatistics();
    }

    private void setupUI() {
        // Create sidebar
        VBox sidebar = createSidebar();

        // Create main content
        VBox mainContent = createMainContent();

        // Set layout
        setLeft(sidebar);
        setCenter(mainContent);

        // Apply styling
        getStyleClass().add("admin-dashboard");
        setStyle("-fx-background-color: #f5f5f5;");
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");

        // Title
        Label titleLabel = new Label("EXAM SCHEDULE MANAGEMENT");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        // Navigation buttons
        Button backButton = new Button("[BACK] Back to Dashboard");
        Button examTypesButton = new Button("[LIST] Manage Exam Types");
        Button usersButton = new Button("[USERS] Manage Users");
        Button reportsButton = new Button("[CHART] Reports");

        // Style buttons
        Button[] buttons = { backButton, examTypesButton, usersButton, reportsButton };
        for (Button btn : buttons) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setStyle(
                    "-fx-background-color: #34495e; -fx-text-fill: white; -fx-border-color: #2c3e50; -fx-padding: 10px;");
            btn.setOnMouseEntered(e -> btn.setStyle(
                    "-fx-background-color: #4a6741; -fx-text-fill: white; -fx-border-color: #2c3e50; -fx-padding: 10px;"));
            btn.setOnMouseExited(e -> btn.setStyle(
                    "-fx-background-color: #34495e; -fx-text-fill: white; -fx-border-color: #2c3e50; -fx-padding: 10px;"));
        }

        // Set button actions
        backButton.setOnAction(e -> navigateToAdminDashboard());
        examTypesButton.setOnAction(e -> openExamTypeManagement());
        usersButton.setOnAction(e -> openUserManagement());
        reportsButton.setOnAction(e -> openReports());

        // Statistics section
        VBox statsSection = createStatisticsSection();

        sidebar.getChildren().addAll(titleLabel, backButton, examTypesButton, usersButton, reportsButton, statsSection);
        return sidebar;
    }

    private VBox createStatisticsSection() {
        VBox statsSection = new VBox(10);
        statsSection.setPadding(new Insets(20, 0, 0, 0));

        Label statsTitle = new Label("STATISTICS");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        statsTitle.setTextFill(Color.WHITE);
        statsTitle.setAlignment(Pos.CENTER);

        totalSchedulesLabel = createStatLabel("Total Schedules", "0", "#3498db");
        todaySchedulesLabel = createStatLabel("Today's Schedules", "0", "#e74c3c");
        upcomingSchedulesLabel = createStatLabel("Upcoming Schedules", "0", "#f39c12");
        completedSchedulesLabel = createStatLabel("Completed Schedules", "0", "#27ae60");

        statsSection.getChildren().addAll(statsTitle, totalSchedulesLabel, todaySchedulesLabel, 
                upcomingSchedulesLabel, completedSchedulesLabel);
        return statsSection;
    }

    private Label createStatLabel(String title, String value, String color) {
        VBox statBox = new VBox(5);
        statBox.setPadding(new Insets(10));
        statBox.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 5;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 10));
        titleLabel.setTextFill(Color.WHITE);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        valueLabel.setTextFill(Color.WHITE);

        statBox.getChildren().addAll(titleLabel, valueLabel);
        return new Label("", statBox);
    }

    private VBox createMainContent() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // Header
        Label headerLabel = new Label("Exam Schedule Management");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        headerLabel.setTextFill(Color.web("#2c3e50"));

        // Statistics cards
        HBox statsCards = createStatisticsCards();

        // Filters and table
        VBox contentArea = new VBox(15);
        contentArea.getChildren().addAll(createFiltersContainer(), createTableContainer());

        mainContent.getChildren().addAll(headerLabel, statsCards, contentArea);
        return mainContent;
    }

    private HBox createStatisticsCards() {
        HBox statsCards = new HBox(15);
        statsCards.setAlignment(Pos.CENTER_LEFT);

        // Create stat cards
        VBox totalCard = createStatCard("Total Schedules", "0", "#3498db");
        VBox todayCard = createStatCard("Today's Schedules", "0", "#e74c3c");
        VBox upcomingCard = createStatCard("Upcoming Schedules", "0", "#f39c12");
        VBox completedCard = createStatCard("Completed Schedules", "0", "#27ae60");

        statsCards.getChildren().addAll(totalCard, todayCard, upcomingCard, completedCard);
        return statsCards;
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(150);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 12));
        titleLabel.setTextFill(Color.web("#7f8c8d"));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        valueLabel.setTextFill(Color.web(color));

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private HBox createFiltersContainer() {
        HBox filtersContainer = new HBox(15);
        filtersContainer.setAlignment(Pos.CENTER_LEFT);
        filtersContainer.setPadding(new Insets(15));
        filtersContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        // Search field
        Label searchLabel = new Label("Search:");
        searchField = new TextField();
        searchField.setPromptText("Search by exam type, location...");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterData());

        // Exam type filter
        Label typeLabel = new Label("Exam Type:");
        examTypeFilter = new ComboBox<>();
        examTypeFilter.setPromptText("All Types");
        examTypeFilter.setPrefWidth(150);
        examTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterData());

        // Status filter
        Label statusLabel = new Label("Status:");
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll(ScheduleStatus.values());
        statusFilter.setPromptText("All Status");
        statusFilter.setPrefWidth(120);
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterData());

        // Action buttons
        Button clearButton = new Button("Clear Filters");
        clearButton.setOnAction(e -> clearFilters());
        clearButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");

        Button addButton = new Button("Add New Schedule");
        addButton.setOnAction(e -> showAddDialog());
        addButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        filtersContainer.getChildren().addAll(
                searchLabel, searchField, typeLabel, examTypeFilter,
                statusLabel, statusFilter, clearButton, addButton
        );

        return filtersContainer;
    }

    private VBox createTableContainer() {
        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(15));
        tableContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        // Table
        scheduleTable = new TableView<>();
        scheduleTable.setItems(scheduleData);
        scheduleTable.setPlaceholder(new Label("No exam schedules found"));

        // Columns
        TableColumn<ExamSchedule, String> examTypeCol = new TableColumn<>("Exam Type");
        examTypeCol.setCellValueFactory(cellData -> {
            try {
                ExamType examType = examTypeDAO.findById(cellData.getValue().getExamTypeId()).orElse(null);
                return new javafx.beans.property.SimpleStringProperty(examType != null ? examType.getName() : "N/A");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });
        examTypeCol.setPrefWidth(150);

        TableColumn<ExamSchedule, LocalDate> dateCol = new TableColumn<>("Exam Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("examDate"));
        dateCol.setCellFactory(column -> new TableCell<ExamSchedule, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });
        dateCol.setPrefWidth(120);

        TableColumn<ExamSchedule, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        timeCol.setPrefWidth(100);

        TableColumn<ExamSchedule, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationCol.setPrefWidth(150);

        TableColumn<ExamSchedule, ScheduleStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        TableColumn<ExamSchedule, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<ExamSchedule, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setOnAction(event -> {
                    ExamSchedule schedule = getTableView().getItems().get(getIndex());
                    showEditDialog(schedule);
                });
                deleteBtn.setOnAction(event -> {
                    ExamSchedule schedule = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(schedule);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
        actionCol.setPrefWidth(120);

        scheduleTable.getColumns().addAll(examTypeCol, dateCol, timeCol, locationCol, statusCol, actionCol);

        // Action buttons
        HBox actionButtons = createActionButtons();

        tableContainer.getChildren().addAll(scheduleTable, actionButtons);
        return tableContainer;
    }

    private HBox createActionButtons() {
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadData());
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        Button exportButton = new Button("Export");
        exportButton.setOnAction(e -> exportData());
        exportButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");

        actionButtons.getChildren().addAll(refreshButton, exportButton);
        return actionButtons;
    }

    private void loadData() {
        try {
            List<ExamSchedule> schedules = scheduleDAO.getAllSchedules();
            scheduleData.clear();
            scheduleData.addAll(schedules);
            loadExamTypesToFilter();
            updateStatistics();
        } catch (Exception e) {
            showAlert("Error", "Failed to load exam schedules: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadExamTypesToFilter() {
        try {
            List<ExamType> examTypes = examTypeDAO.getAll();
            examTypeFilter.getItems().clear();
            examTypeFilter.getItems().addAll(examTypes);
        } catch (Exception e) {
            showAlert("Error", "Failed to load exam types: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateStatistics() {
        try {
            int totalSchedules = scheduleData.size();
            int todaySchedules = (int) scheduleData.stream()
                    .filter(schedule -> schedule.getExamDate() != null && 
                            schedule.getExamDate().equals(LocalDate.now()))
                    .count();
            int upcomingSchedules = (int) scheduleData.stream()
                    .filter(schedule -> schedule.getExamDate() != null && 
                            schedule.getExamDate().isAfter(LocalDate.now()))
                    .count();
            int completedSchedules = (int) scheduleData.stream()
                    .filter(schedule -> ScheduleStatus.COMPLETED.equals(schedule.getStatus()))
                    .count();

            // Update sidebar statistics
            updateStatLabel(totalSchedulesLabel, String.valueOf(totalSchedules));
            updateStatLabel(todaySchedulesLabel, String.valueOf(todaySchedules));
            updateStatLabel(upcomingSchedulesLabel, String.valueOf(upcomingSchedules));
            updateStatLabel(completedSchedulesLabel, String.valueOf(completedSchedules));
        } catch (Exception e) {
            showAlert("Error", "Failed to update statistics: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateStatLabel(Label label, String value) {
        if (label.getGraphic() instanceof VBox) {
            VBox statBox = (VBox) label.getGraphic();
            if (statBox.getChildren().size() > 1) {
                Label valueLabel = (Label) statBox.getChildren().get(1);
                valueLabel.setText(value);
            }
        }
    }

    private void filterData() {
        try {
            String searchText = searchField.getText().toLowerCase().trim();
            ExamType selectedType = examTypeFilter.getValue();
            ScheduleStatus selectedStatus = statusFilter.getValue();

            List<ExamSchedule> allSchedules = scheduleDAO.getAllSchedules();
            List<ExamSchedule> filteredSchedules = allSchedules.stream()
                    .filter(schedule -> matchesFilters(schedule, searchText, selectedType, selectedStatus))
                    .collect(Collectors.toList());

            scheduleData.clear();
            scheduleData.addAll(filteredSchedules);
        } catch (Exception e) {
            showAlert("Error", "Failed to filter data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean matchesFilters(ExamSchedule schedule, String searchText, ExamType selectedType, ScheduleStatus selectedStatus) {
        // Search filter
        if (!searchText.isEmpty()) {
            try {
                ExamType examType = examTypeDAO.findById(schedule.getExamTypeId()).orElse(null);
                String examTypeName = examType != null ? examType.getName() : "";
                String location = schedule.getLocation() != null ? schedule.getLocation() : "";
                
                if (!examTypeName.toLowerCase().contains(searchText) && 
                    !location.toLowerCase().contains(searchText)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        // Type filter
        if (selectedType != null && schedule.getExamTypeId() != selectedType.getId()) {
            return false;
        }

        // Status filter
        if (selectedStatus != null && !selectedStatus.equals(schedule.getStatus())) {
            return false;
        }

        return true;
    }

    private void clearFilters() {
        searchField.clear();
        examTypeFilter.setValue(null);
        statusFilter.setValue(null);
        loadData();
    }

    private void showAddDialog() {
        // TODO: Implement add dialog
        showAlert("Info", "Add new schedule functionality will be implemented", Alert.AlertType.INFORMATION);
    }

    private void showEditDialog(ExamSchedule schedule) {
        // TODO: Implement edit dialog
        showAlert("Info", "Edit schedule functionality will be implemented", Alert.AlertType.INFORMATION);
    }

    private void showScheduleDialog(ExamSchedule schedule) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Schedule Details");
        dialog.setHeaderText("Exam Schedule Information");

        try {
            ExamType examType = examTypeDAO.findById(schedule.getExamTypeId()).orElse(null);
            String examTypeName = examType != null ? examType.getName() : "N/A";
            String examTypeCode = examType != null ? examType.getCode() : "N/A";

            String content = String.format(
                "Exam Type: %s (%s)\n" +
                "Exam Date: %s\n" +
                "Time Slot: %s\n" +
                "Location: %s\n" +
                "Status: %s\n" +
                "Max Candidates: %d\n" +
                "Registered Candidates: %d",
                examTypeName,
                examTypeCode,
                schedule.getExamDate() != null ? 
                    schedule.getExamDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                schedule.getTimeSlot() != null ? schedule.getTimeSlot().toString() : "N/A",
                schedule.getLocation() != null ? schedule.getLocation() : "N/A",
                schedule.getStatus() != null ? schedule.getStatus().toString() : "N/A",
                schedule.getMaxCandidates(),
                schedule.getRegisteredCandidates()
            );

            dialog.setContentText(content);
            dialog.showAndWait();
        } catch (Exception e) {
            showAlert("Error", "Failed to load schedule details: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showDeleteConfirmation(ExamSchedule schedule) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete Exam Schedule");
        confirmation.setContentText("Are you sure you want to delete this exam schedule?\n\n" +
                "This action cannot be undone.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    scheduleDAO.deleteById(schedule.getId());
                    loadData();
                    showAlert("Success", "Exam schedule deleted successfully", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    showAlert("Error", "Failed to delete schedule: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToAdminDashboard() {
        // TODO: Implement navigation
        System.out.println("Navigating to Admin Dashboard");
    }

    private void openExamTypeManagement() {
        // TODO: Implement navigation
        System.out.println("Opening Exam Type Management");
    }

    private void openUserManagement() {
        // TODO: Implement navigation
        System.out.println("Opening User Management");
    }

    private void openReports() {
        // TODO: Implement navigation
        System.out.println("Opening Reports");
    }

    private void exportData() {
        // TODO: Implement export functionality
        showAlert("Info", "Export functionality will be implemented", Alert.AlertType.INFORMATION);
    }
}
