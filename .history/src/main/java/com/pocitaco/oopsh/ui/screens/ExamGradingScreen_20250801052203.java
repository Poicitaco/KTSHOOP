package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.*;
import com.pocitaco.oopsh.models.*;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Exam Grading Screen for Examiners
 * Màn hình chấm điểm thi cho giám khảo
 */
public class ExamGradingScreen extends BorderPane {

    private final UserDAO userDAO;
    private final ResultDAO resultDAO;
    private final RegistrationDAO registrationDAO;
    private final ExamScheduleDAO examScheduleDAO;
    private final ExamTypeDAO examTypeDAO;

    private TableView<Result> pendingGradeTable;
    private TableView<Result> gradedTable;
    private ObservableList<Result> pendingGradeData;
    private ObservableList<Result> gradedData;

    private ComboBox<ExamType> examTypeFilter;
    private DatePicker dateFilter;
    private TextField searchField;
    private User currentUser;

    // Statistics labels
    private Label totalExamsLabel;
    private Label pendingGradingLabel;
    private Label gradedTodayLabel;
    private Label averageScoreLabel;

    public ExamGradingScreen(User currentUser) {
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();
        this.resultDAO = new ResultDAO();
        this.registrationDAO = new RegistrationDAO();
        this.examScheduleDAO = new ExamScheduleDAO();
        this.examTypeDAO = new ExamTypeDAO();
        this.pendingGradeData = FXCollections.observableArrayList();
        this.gradedData = FXCollections.observableArrayList();

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
        setStyle("-fx-background-color: #f5f5f5;");
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");

        // Title
        Label titleLabel = new Label("EXAM GRADING");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        // Navigation buttons
        Button backButton = new Button("[BACK] Back to Dashboard");
        Button sessionButton = new Button("[SESSION] Exam Sessions");
        Button reportButton = new Button("[REPORT] Performance Reports");
        Button scheduleButton = new Button("[SCHEDULE] Today's Schedule");

        // Style buttons
        Button[] buttons = { backButton, sessionButton, reportButton, scheduleButton };
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

        // Button actions
        backButton.setOnAction(e -> navigateToDashboard());
        sessionButton.setOnAction(e -> openExamSessions());
        reportButton.setOnAction(e -> openPerformanceReports());
        scheduleButton.setOnAction(e -> openTodaySchedule());

        sidebar.getChildren().addAll(titleLabel, new Separator(),
                backButton, sessionButton, reportButton, scheduleButton);

        return sidebar;
    }

    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Exam Grading Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        // Statistics cards
        HBox statsContainer = createStatisticsCards();

        // Tab pane for pending and graded exams
        TabPane tabPane = createTabPane();

        content.getChildren().addAll(titleLabel, statsContainer, tabPane);
        return content;
    }

    private HBox createStatisticsCards() {
        HBox container = new HBox(20);
        container.setAlignment(Pos.CENTER);

        // Total exams card
        VBox totalCard = createStatCard("Total Exams", "0", "#3498db");
        totalExamsLabel = (Label) totalCard.getChildren().get(1);

        // Pending grading card
        VBox pendingCard = createStatCard("Pending Grading", "0", "#f39c12");
        pendingGradingLabel = (Label) pendingCard.getChildren().get(1);

        // Graded today card
        VBox gradedCard = createStatCard("Graded Today", "0", "#2ecc71");
        gradedTodayLabel = (Label) gradedCard.getChildren().get(1);

        // Average score card
        VBox averageCard = createStatCard("Average Score", "0.0", "#e74c3c");
        averageScoreLabel = (Label) averageCard.getChildren().get(1);

        container.getChildren().addAll(totalCard, pendingCard, gradedCard, averageCard);
        return container;
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(200);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        titleLabel.setTextFill(Color.web("#7f8c8d"));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setTextFill(Color.web(color));

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();

        // Pending Grading Tab
        Tab pendingTab = new Tab("Pending Grading");
        pendingTab.setContent(createPendingGradingContent());
        pendingTab.setClosable(false);

        // Graded Exams Tab
        Tab gradedTab = new Tab("Graded Exams");
        gradedTab.setContent(createGradedContent());
        gradedTab.setClosable(false);

        tabPane.getTabs().addAll(pendingTab, gradedTab);
        return tabPane;
    }

    private VBox createPendingGradingContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Filters
        HBox filtersContainer = createFiltersContainer();

        // Pending grade table
        pendingGradeTable = createPendingGradeTable();

        // Action buttons
        HBox actionButtons = createPendingActionButtons();

        content.getChildren().addAll(filtersContainer, pendingGradeTable, actionButtons);
        return content;
    }

    private HBox createFiltersContainer() {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search candidate...");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener((obs, oldText, newText) -> filterPendingExams());

        // Exam type filter
        examTypeFilter = new ComboBox<>();
        examTypeFilter.setPromptText("Select exam type");
        examTypeFilter.setPrefWidth(150);
        loadExamTypesToFilter();
        examTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterPendingExams());

        // Date filter
        dateFilter = new DatePicker();
        dateFilter.setPromptText("Exam date");
        dateFilter.setPrefWidth(130);
        dateFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterPendingExams());

        Button clearFiltersButton = new Button("Clear Filters");
        clearFiltersButton.setOnAction(e -> clearFilters());

        container.getChildren().addAll(
                new Label("Search:"), searchField,
                new Label("Exam Type:"), examTypeFilter,
                new Label("Date:"), dateFilter,
                clearFiltersButton);

        return container;
    }

    private TableView<Result> createPendingGradeTable() {
        TableView<Result> table = new TableView<>();
        table.setItems(pendingGradeData);

        // Columns
        TableColumn<Result, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Result, String> candidateCol = new TableColumn<>("Candidate");
        candidateCol.setCellValueFactory(cellData -> {
            try {
                User user = userDAO.findById(cellData.getValue().getUserId()).orElse(null);
                return new javafx.beans.property.SimpleStringProperty(user != null ? user.getFullName() : "Unknown");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });
        candidateCol.setPrefWidth(150);

        TableColumn<Result, String> examCol = new TableColumn<>("Exam Type");
        examCol.setCellValueFactory(cellData -> {
            try {
                ExamType examType = examTypeDAO.findById(cellData.getValue().getExamTypeId()).orElse(null);
                return new javafx.beans.property.SimpleStringProperty(
                        examType != null ? examType.getName() : "Unknown");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });
        examCol.setPrefWidth(150);

        TableColumn<Result, String> examDateCol = new TableColumn<>("Exam Date");
        examDateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getExamDate();
            return new javafx.beans.property.SimpleStringProperty(
                    date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });
        examDateCol.setPrefWidth(100);

        TableColumn<Result, String> theoryScoreCol = new TableColumn<>("Theory Score");
        theoryScoreCol.setCellValueFactory(cellData -> {
            double score = cellData.getValue().getTheoryScore();
            return new javafx.beans.property.SimpleStringProperty(score > 0 ? String.valueOf(score) : "Not graded");
        });
        theoryScoreCol.setPrefWidth(100);

        TableColumn<Result, String> practicalScoreCol = new TableColumn<>("Practical Score");
        practicalScoreCol.setCellValueFactory(cellData -> {
            double score = cellData.getValue().getPracticalScore();
            return new javafx.beans.property.SimpleStringProperty(score > 0 ? String.valueOf(score) : "Not graded");
        });
        practicalScoreCol.setPrefWidth(120);

        TableColumn<Result, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<Result, Void>() {
            private final Button gradeBtn = new Button("Grade");
            private final Button viewBtn = new Button("View");
            private final HBox container = new HBox(5, gradeBtn, viewBtn);

            {
                gradeBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

                gradeBtn.setOnAction(e -> {
                    Result result = getTableView().getItems().get(getIndex());
                    gradeExam(result);
                });

                viewBtn.setOnAction(e -> {
                    Result result = getTableView().getItems().get(getIndex());
                    viewExamDetails(result);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
        actionCol.setPrefWidth(150);

        table.getColumns().addAll(idCol, candidateCol, examCol, examDateCol, theoryScoreCol, practicalScoreCol,
                actionCol);
        return table;
    }

    private HBox createPendingActionButtons() {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);

        Button refreshButton = new Button("[REFRESH] Refresh");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px 20px;");
        refreshButton.setOnAction(e -> {
            loadData();
            updateStatistics();
        });

        Button bulkGradeButton = new Button("[BULK] Bulk Grade");
        bulkGradeButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10px 20px;");
        bulkGradeButton.setOnAction(e -> openBulkGrading());

        container.getChildren().addAll(refreshButton, bulkGradeButton);
        return container;
    }

    private VBox createGradedContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Graded table
        gradedTable = createGradedTable();

        // Action buttons
        HBox actionButtons = createGradedActionButtons();

        content.getChildren().addAll(gradedTable, actionButtons);
        return content;
    }

    private TableView<Result> createGradedTable() {
        TableView<Result> table = new TableView<>();
        table.setItems(gradedData);

        // Columns
        TableColumn<Result, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Result, String> candidateCol = new TableColumn<>("Candidate");
        candidateCol.setCellValueFactory(cellData -> {
            try {
                User user = userDAO.findById(cellData.getValue().getUserId()).orElse(null);
                return new javafx.beans.property.SimpleStringProperty(user != null ? user.getFullName() : "Unknown");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });
        candidateCol.setPrefWidth(150);

        TableColumn<Result, String> examCol = new TableColumn<>("Exam Type");
        examCol.setCellValueFactory(cellData -> {
            try {
                ExamType examType = examTypeDAO.findById(cellData.getValue().getExamTypeId()).orElse(null);
                return new javafx.beans.property.SimpleStringProperty(
                        examType != null ? examType.getName() : "Unknown");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });
        examCol.setPrefWidth(150);

        TableColumn<Result, Double> theoryScoreCol = new TableColumn<>("Theory Score");
        theoryScoreCol.setCellValueFactory(new PropertyValueFactory<>("theoryScore"));
        theoryScoreCol.setPrefWidth(100);

        TableColumn<Result, Double> practicalScoreCol = new TableColumn<>("Practical Score");
        practicalScoreCol.setCellValueFactory(new PropertyValueFactory<>("practicalScore"));
        practicalScoreCol.setPrefWidth(120);

        TableColumn<Result, Double> totalScoreCol = new TableColumn<>("Total Score");
        totalScoreCol.setCellValueFactory(new PropertyValueFactory<>("totalScore"));
        totalScoreCol.setPrefWidth(100);

        TableColumn<Result, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(column -> new TableCell<Result, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("PASSED".equals(status)) {
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    } else if ("FAILED".equals(status)) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });
        statusCol.setPrefWidth(100);

        TableColumn<Result, String> dateCol = new TableColumn<>("Graded Date");
        dateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getExamDate();
            return new javafx.beans.property.SimpleStringProperty(
                    date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });
        dateCol.setPrefWidth(100);

        TableColumn<Result, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<Result, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button reportBtn = new Button("Report");
            private final HBox container = new HBox(5, editBtn, reportBtn);

            {
                editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                reportBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");

                editBtn.setOnAction(e -> {
                    Result result = getTableView().getItems().get(getIndex());
                    editGrade(result);
                });

                reportBtn.setOnAction(e -> {
                    Result result = getTableView().getItems().get(getIndex());
                    generateExamReport(result);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
        actionCol.setPrefWidth(120);

        table.getColumns().addAll(idCol, candidateCol, examCol, theoryScoreCol, practicalScoreCol, totalScoreCol,
                statusCol, dateCol, actionCol);
        return table;
    }

    private HBox createGradedActionButtons() {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);

        Button refreshButton = new Button("[REFRESH] Refresh");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px 20px;");
        refreshButton.setOnAction(e -> loadGradedExams());

        Button exportButton = new Button("[EXPORT] Export Report");
        exportButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10px 20px;");
        exportButton.setOnAction(e -> exportGradingReport());

        container.getChildren().addAll(refreshButton, exportButton);
        return container;
    }

    private void loadData() {
        loadPendingExams();
        loadGradedExams();
    }

    private void loadPendingExams() {
        try {
            List<Result> results = resultDAO.getAll();
            List<Result> pendingResults = results.stream()
                    .filter(r -> "PENDING".equals(r.getStatus()) || r.getTotalScore() == 0)
                    .toList();

            pendingGradeData.clear();
            pendingGradeData.addAll(pendingResults);
        } catch (Exception e) {
            showAlert("Error", "Cannot load pending exams: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadGradedExams() {
        try {
            List<Result> results = resultDAO.getAll();
            List<Result> gradedResults = results.stream()
                    .filter(r -> !"PENDING".equals(r.getStatus()) && r.getTotalScore() > 0)
                    .toList();

            gradedData.clear();
            gradedData.addAll(gradedResults);
        } catch (Exception e) {
            showAlert("Error", "Cannot load graded exams: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadExamTypesToFilter() {
        try {
            List<ExamType> examTypes = examTypeDAO.getAll();
            examTypeFilter.getItems().clear();
            examTypeFilter.getItems().addAll(examTypes);
        } catch (Exception e) {
            showAlert("Error", "Cannot load exam types: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateStatistics() {
        try {
            int totalExams = pendingGradeData.size() + gradedData.size();
            int pendingGrading = pendingGradeData.size();
            int gradedToday = (int) gradedData.stream()
                    .filter(r -> r.getExamDate() != null && r.getExamDate().equals(LocalDate.now()))
                    .count();
            double averageScore = gradedData.stream()
                    .mapToDouble(Result::getTotalScore)
                    .average()
                    .orElse(0.0);

            totalExamsLabel.setText(String.valueOf(totalExams));
            pendingGradingLabel.setText(String.valueOf(pendingGrading));
            gradedTodayLabel.setText(String.valueOf(gradedToday));
            averageScoreLabel.setText(String.format("%.1f", averageScore));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterPendingExams() {
        // Implementation for filtering pending exams based on search criteria
        loadPendingExams();
    }

    private void clearFilters() {
        searchField.clear();
        examTypeFilter.setValue(null);
        dateFilter.setValue(null);
        loadPendingExams();
    }

    private void gradeExam(Result result) {
        Dialog<Result> gradeDialog = new Dialog<>();
        gradeDialog.setTitle("Grade Exam");
        gradeDialog.setHeaderText("Grading for Result ID: " + result.getId());

        ButtonType gradeButtonType = new ButtonType("Submit Grade", ButtonBar.ButtonData.OK_DONE);
        gradeDialog.getDialogPane().getButtonTypes().addAll(gradeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField theoryScoreField = new TextField(String.valueOf(result.getTheoryScore()));
        theoryScoreField.setPromptText("Theory Score (0-100)");

        TextField practicalScoreField = new TextField(String.valueOf(result.getPracticalScore()));
        practicalScoreField.setPromptText("Practical Score (0-100)");

        TextArea notesArea = new TextArea(result.getNotes());
        notesArea.setPromptText("Grading notes and comments");
        notesArea.setPrefRowCount(3);

        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("PASSED", "FAILED", "UNDER_REVIEW");
        statusComboBox.setValue(result.getStatusAsString());

        grid.add(new Label("Theory Score:"), 0, 0);
        grid.add(theoryScoreField, 1, 0);
        grid.add(new Label("Practical Score:"), 0, 1);
        grid.add(practicalScoreField, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(statusComboBox, 1, 2);
        grid.add(new Label("Notes:"), 0, 3);
        grid.add(notesArea, 1, 3);

        gradeDialog.getDialogPane().setContent(grid);

        gradeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == gradeButtonType) {
                try {
                    double theoryScore = Double.parseDouble(theoryScoreField.getText());
                    double practicalScore = Double.parseDouble(practicalScoreField.getText());
                    double totalScore = (theoryScore + practicalScore) / 2;

                    result.setTheoryScore(theoryScore);
                    result.setPracticalScore(practicalScore);
                    result.setTotalScore(totalScore);
                    result.setStatus(statusComboBox.getValue());
                    result.setNotes(notesArea.getText());

                    return result;
                } catch (NumberFormatException e) {
                    showAlert("Error", "Please enter valid numeric scores", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Result> gradeResult = gradeDialog.showAndWait();
        gradeResult.ifPresent(gradedResult -> {
            try {
                resultDAO.update(gradedResult);
                showAlert("Success", "Exam graded successfully!", Alert.AlertType.INFORMATION);
                loadData();
                updateStatistics();
            } catch (Exception e) {
                showAlert("Error", "Cannot save grade: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void editGrade(Result result) {
        gradeExam(result); // Reuse the same grading dialog
    }

    private void viewExamDetails(Result result) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Exam Details");
        dialog.setHeaderText("Result ID: " + result.getId());

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        try {
            User candidate = userDAO.findById(result.getUserId()).orElse(null);
            ExamType examType = examTypeDAO.findById(result.getExamTypeId()).orElse(null);

            content.getChildren().addAll(
                    new Label("Candidate: " + (candidate != null ? candidate.getFullName() : "Unknown")),
                    new Label("Exam Type: " + (examType != null ? examType.getName() : "Unknown")),
                    new Label("Exam Date: " + (result.getExamDate() != null
                            ? result.getExamDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "Unknown")),
                    new Label("Theory Score: " + result.getTheoryScore()),
                    new Label("Practical Score: " + result.getPracticalScore()),
                    new Label("Total Score: " + result.getTotalScore()),
                    new Label("Status: " + result.getStatus()),
                    new Separator(),
                    new Label("Notes:"),
                    new TextArea(result.getNotes() != null ? result.getNotes() : "No notes available"));
        } catch (Exception e) {
            content.getChildren().add(new Label("Error loading exam details"));
        }

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private void openBulkGrading() {
        showAlert("Info", "Bulk grading functionality would be implemented here.", Alert.AlertType.INFORMATION);
    }

    private void generateExamReport(Result result) {
        showAlert("Info", "Exam report generation for Result ID: " + result.getId() +
                " would be implemented here.", Alert.AlertType.INFORMATION);
    }

    private void exportGradingReport() {
        showAlert("Info", "Grading report export functionality would be implemented here.",
                Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation methods
    private void navigateToDashboard() {
        showAlert("Info", "Navigating back to dashboard...", Alert.AlertType.INFORMATION);
    }

    private void openExamSessions() {
        showAlert("Info", "Opening exam sessions management...", Alert.AlertType.INFORMATION);
    }

    private void openPerformanceReports() {
        showAlert("Info", "Opening performance reports...", Alert.AlertType.INFORMATION);
    }

    private void openTodaySchedule() {
        showAlert("Info", "Opening today's exam schedule...", Alert.AlertType.INFORMATION);
    }
}
