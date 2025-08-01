package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.*;
import com.pocitaco.oopsh.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Practice Tests Screen for Candidates
 * Màn hình luyện tập cho thí sinh
 */
public class PracticeTestsScreen extends BorderPane {

    private final UserDAO userDAO;
    private final PracticeTestDAO practiceTestDAO;
    private final MockExamDAO mockExamDAO;
    private final ExamTypeDAO examTypeDAO;

    private TableView<PracticeTest> practiceTestsTable;
    private TableView<MockExam> mockExamsTable;
    private ObservableList<PracticeTest> practiceTestsData;
    private ObservableList<MockExam> mockExamsData;

    private ComboBox<ExamType> examTypeFilter;
    private ComboBox<String> difficultyFilter;
    private TextField searchField;
    private User currentUser;

    // Statistics labels
    private Label totalTestsLabel;
    private Label completedTestsLabel;
    private Label averageScoreLabel;
    private Label bestScoreLabel;

    public PracticeTestsScreen(User currentUser) {
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();
        this.practiceTestDAO = new PracticeTestDAO();
        this.mockExamDAO = new MockExamDAO();
        this.examTypeDAO = new ExamTypeDAO();
        this.practiceTestsData = FXCollections.observableArrayList();
        this.mockExamsData = FXCollections.observableArrayList();

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
        Label titleLabel = new Label("PRACTICE TESTS");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        // Navigation buttons
        Button backButton = new Button("[BACK] Back to Dashboard");
        Button registerExamButton = new Button("[PLUS] Register for Exam");
        Button examResultsButton = new Button("[CHART] Exam Results");
        Button studyMaterialsButton = new Button("[BOOK] Study Materials");

        // Style buttons
        Button[] buttons = { backButton, registerExamButton, examResultsButton, studyMaterialsButton };
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
        registerExamButton.setOnAction(e -> openExamRegistration());
        examResultsButton.setOnAction(e -> openExamResults());
        studyMaterialsButton.setOnAction(e -> openStudyMaterials());

        sidebar.getChildren().addAll(titleLabel, new Separator(),
                backButton, registerExamButton, examResultsButton, studyMaterialsButton);

        return sidebar;
    }

    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Practice Tests & Mock Exams");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        // Statistics cards
        HBox statsContainer = createStatisticsCards();

        // Tab pane for practice tests and mock exams
        TabPane tabPane = createTabPane();

        content.getChildren().addAll(titleLabel, statsContainer, tabPane);
        return content;
    }

    private HBox createStatisticsCards() {
        HBox container = new HBox(20);
        container.setAlignment(Pos.CENTER);

        // Total tests card
        VBox totalCard = createStatCard("Total Tests", "0", "#3498db");
        totalTestsLabel = (Label) totalCard.getChildren().get(1);

        // Completed tests card
        VBox completedCard = createStatCard("Completed", "0", "#2ecc71");
        completedTestsLabel = (Label) completedCard.getChildren().get(1);

        // Average score card
        VBox averageCard = createStatCard("Average Score", "0.0", "#f39c12");
        averageScoreLabel = (Label) averageCard.getChildren().get(1);

        // Best score card
        VBox bestCard = createStatCard("Best Score", "0.0", "#e74c3c");
        bestScoreLabel = (Label) bestCard.getChildren().get(1);

        container.getChildren().addAll(totalCard, completedCard, averageCard, bestCard);
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

        // Practice Tests Tab
        Tab practiceTestsTab = new Tab("Practice Tests");
        practiceTestsTab.setContent(createPracticeTestsContent());
        practiceTestsTab.setClosable(false);

        // Mock Exams Tab
        Tab mockExamsTab = new Tab("Mock Exams");
        mockExamsTab.setContent(createMockExamsContent());
        mockExamsTab.setClosable(false);

        tabPane.getTabs().addAll(practiceTestsTab, mockExamsTab);
        return tabPane;
    }

    private VBox createPracticeTestsContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Filters
        HBox filtersContainer = createFiltersContainer();

        // Practice tests table
        practiceTestsTable = createPracticeTestsTable();

        // Action buttons
        HBox actionButtons = createPracticeTestsActionButtons();

        content.getChildren().addAll(filtersContainer, practiceTestsTable, actionButtons);
        return content;
    }

    private HBox createFiltersContainer() {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search tests...");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener((obs, oldText, newText) -> filterPracticeTests());

        // Exam type filter
        examTypeFilter = new ComboBox<>();
        examTypeFilter.setPromptText("Select exam type");
        examTypeFilter.setPrefWidth(150);
        loadExamTypesToFilter();
        examTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterPracticeTests());

        // Difficulty filter
        difficultyFilter = new ComboBox<>();
        difficultyFilter.getItems().addAll("All", "Easy", "Medium", "Hard");
        difficultyFilter.setValue("All");
        difficultyFilter.setPrefWidth(100);
        difficultyFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterPracticeTests());

        Button clearFiltersButton = new Button("Clear Filters");
        clearFiltersButton.setOnAction(e -> clearFilters());

        container.getChildren().addAll(
                new Label("Search:"), searchField,
                new Label("Exam Type:"), examTypeFilter,
                new Label("Difficulty:"), difficultyFilter,
                clearFiltersButton);

        return container;
    }

    private TableView<PracticeTest> createPracticeTestsTable() {
        TableView<PracticeTest> table = new TableView<>();
        table.setItems(practiceTestsData);

        // Columns
        TableColumn<PracticeTest, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<PracticeTest, String> examTypeCol = new TableColumn<>("Exam Type");
        examTypeCol.setCellValueFactory(cellData -> {
            try {
                ExamType examType = examTypeDAO.findById(cellData.getValue().getExamTypeId()).orElse(null);
                return new javafx.beans.property.SimpleStringProperty(
                        examType != null ? examType.getName() : "Unknown");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });
        examTypeCol.setPrefWidth(150);

        TableColumn<PracticeTest, String> difficultyCol = new TableColumn<>("Difficulty");
        difficultyCol.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        difficultyCol.setPrefWidth(100);

        TableColumn<PracticeTest, Integer> questionsCol = new TableColumn<>("Questions");
        questionsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfQuestions"));
        questionsCol.setPrefWidth(80);

        TableColumn<PracticeTest, Integer> durationCol = new TableColumn<>("Duration (min)");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationCol.setPrefWidth(100);

        TableColumn<PracticeTest, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> {
            // Check if user has taken this test
            boolean taken = hasUserTakenTest(cellData.getValue().getId());
            return new javafx.beans.property.SimpleStringProperty(taken ? "Completed" : "Available");
        });
        statusCol.setPrefWidth(100);

        TableColumn<PracticeTest, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<PracticeTest, Void>() {
            private final Button startBtn = new Button("Start Test");
            private final Button viewBtn = new Button("View Results");
            private final Button retakeBtn = new Button("Retake");
            private final HBox container = new HBox(5);

            {
                startBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                retakeBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");

                startBtn.setOnAction(e -> {
                    PracticeTest test = getTableView().getItems().get(getIndex());
                    startPracticeTest(test);
                });

                viewBtn.setOnAction(e -> {
                    PracticeTest test = getTableView().getItems().get(getIndex());
                    viewTestResults(test);
                });

                retakeBtn.setOnAction(e -> {
                    PracticeTest test = getTableView().getItems().get(getIndex());
                    retakePracticeTest(test);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    PracticeTest test = getTableView().getItems().get(getIndex());
                    container.getChildren().clear();

                    boolean taken = hasUserTakenTest(test.getId());
                    if (taken) {
                        container.getChildren().addAll(viewBtn, retakeBtn);
                    } else {
                        container.getChildren().add(startBtn);
                    }
                    setGraphic(container);
                }
            }
        });
        actionCol.setPrefWidth(200);

        table.getColumns().addAll(titleCol, examTypeCol, difficultyCol, questionsCol, durationCol, statusCol,
                actionCol);
        return table;
    }

    private HBox createPracticeTestsActionButtons() {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);

        Button refreshButton = new Button("[REFRESH] Refresh");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px 20px;");
        refreshButton.setOnAction(e -> {
            loadData();
            updateStatistics();
        });

        container.getChildren().add(refreshButton);
        return container;
    }

    private VBox createMockExamsContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Mock exams table
        mockExamsTable = createMockExamsTable();

        // Action buttons
        HBox actionButtons = createMockExamsActionButtons();

        content.getChildren().addAll(mockExamsTable, actionButtons);
        return content;
    }

    private TableView<MockExam> createMockExamsTable() {
        TableView<MockExam> table = new TableView<>();
        table.setItems(mockExamsData);

        // Columns
        TableColumn<MockExam, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<MockExam, String> examTypeCol = new TableColumn<>("Exam Type");
        examTypeCol.setCellValueFactory(cellData -> {
            try {
                ExamType examType = examTypeDAO.findById(cellData.getValue().getExamTypeId()).orElse(null);
                return new javafx.beans.property.SimpleStringProperty(
                        examType != null ? examType.getName() : "Unknown");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });
        examTypeCol.setPrefWidth(150);

        TableColumn<MockExam, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setPrefWidth(250);

        TableColumn<MockExam, Integer> durationCol = new TableColumn<>("Duration (min)");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationCol.setPrefWidth(100);

        TableColumn<MockExam, String> createdDateCol = new TableColumn<>("Created Date");
        createdDateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getCreatedDate();
            return new javafx.beans.property.SimpleStringProperty(
                    date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });
        createdDateCol.setPrefWidth(100);

        TableColumn<MockExam, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<MockExam, Void>() {
            private final Button startBtn = new Button("Start Exam");
            private final Button viewBtn = new Button("View Details");
            private final HBox container = new HBox(5, startBtn, viewBtn);

            {
                startBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

                startBtn.setOnAction(e -> {
                    MockExam exam = getTableView().getItems().get(getIndex());
                    startMockExam(exam);
                });

                viewBtn.setOnAction(e -> {
                    MockExam exam = getTableView().getItems().get(getIndex());
                    viewMockExamDetails(exam);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
        actionCol.setPrefWidth(150);

        table.getColumns().addAll(titleCol, examTypeCol, descriptionCol, durationCol, createdDateCol, actionCol);
        return table;
    }

    private HBox createMockExamsActionButtons() {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);

        Button refreshButton = new Button("[REFRESH] Refresh");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px 20px;");
        refreshButton.setOnAction(e -> loadMockExams());

        container.getChildren().add(refreshButton);
        return container;
    }

    private void loadData() {
        loadPracticeTests();
        loadMockExams();
    }

    private void loadPracticeTests() {
        try {
            List<PracticeTest> tests = practiceTestDAO.getAll();
            practiceTestsData.clear();
            practiceTestsData.addAll(tests);
        } catch (Exception e) {
            showAlert("Error", "Cannot load practice tests: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadMockExams() {
        try {
            List<MockExam> exams = mockExamDAO.getAll();
            mockExamsData.clear();
            mockExamsData.addAll(exams);
        } catch (Exception e) {
            showAlert("Error", "Cannot load mock exams: " + e.getMessage(), Alert.AlertType.ERROR);
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
            int totalTests = practiceTestsData.size();
            int completedTests = (int) practiceTestsData.stream()
                    .filter(test -> hasUserTakenTest(test.getId()))
                    .count();

            // Simulated scores for now
            double averageScore = 75.5;
            double bestScore = 92.0;

            totalTestsLabel.setText(String.valueOf(totalTests));
            completedTestsLabel.setText(String.valueOf(completedTests));
            averageScoreLabel.setText(String.format("%.1f", averageScore));
            bestScoreLabel.setText(String.format("%.1f", bestScore));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterPracticeTests() {
        try {
            List<PracticeTest> allTests = practiceTestDAO.getAll();
            List<PracticeTest> filteredTests = allTests.stream()
                    .filter(this::matchesFilters)
                    .collect(Collectors.toList());

            practiceTestsData.clear();
            practiceTestsData.addAll(filteredTests);
        } catch (Exception e) {
            showAlert("Error", "Cannot filter tests: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean matchesFilters(PracticeTest test) {
        // Search filter
        String searchText = searchField.getText();
        if (searchText != null && !searchText.trim().isEmpty()) {
            if (!test.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                return false;
            }
        }

        // Exam type filter
        ExamType selectedType = examTypeFilter.getValue();
        if (selectedType != null) {
            if (test.getExamTypeId() != selectedType.getId()) {
                return false;
            }
        }

        // Difficulty filter
        String selectedDifficulty = difficultyFilter.getValue();
        if (selectedDifficulty != null && !"All".equals(selectedDifficulty)) {
            if (!selectedDifficulty.equals(test.getDifficulty())) {
                return false;
            }
        }

        return true;
    }

    private void clearFilters() {
        searchField.clear();
        examTypeFilter.setValue(null);
        difficultyFilter.setValue("All");
        loadPracticeTests();
    }

    private boolean hasUserTakenTest(int testId) {
        // For now, simulate that user has taken some tests
        return testId % 3 == 0;
    }

    private void startPracticeTest(PracticeTest test) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Start Practice Test");
        confirmAlert.setHeaderText("Start " + test.getTitle() + "?");
        confirmAlert.setContentText("Duration: " + test.getDuration() + " minutes\n" +
                "Questions: " + test.getNumberOfQuestions() + "\n" +
                "Difficulty: " + test.getDifficulty());

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showAlert("Info", "Starting practice test: " + test.getTitle(), Alert.AlertType.INFORMATION);
                // Here you would implement the actual test taking interface
            }
        });
    }

    private void viewTestResults(PracticeTest test) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Test Results");
        dialog.setHeaderText("Results for " + test.getTitle());

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Simulated results
        content.getChildren().addAll(
                new Label("Score: 85/100"),
                new Label("Percentage: 85%"),
                new Label("Time Taken: 45 minutes"),
                new Label("Date Taken: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                new Separator(),
                new Label("Breakdown:"),
                new Label("• Correct Answers: 17/20"),
                new Label("• Incorrect Answers: 3/20"),
                new Label("• Time per Question: 2.25 minutes"));

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private void retakePracticeTest(PracticeTest test) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Retake Practice Test");
        confirmAlert.setHeaderText("Retake " + test.getTitle() + "?");
        confirmAlert.setContentText("Your previous score will be replaced with the new score.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                startPracticeTest(test);
            }
        });
    }

    private void startMockExam(MockExam exam) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Start Mock Exam");
        confirmAlert.setHeaderText("Start " + exam.getTitle() + "?");
        confirmAlert.setContentText("Duration: " + exam.getDuration() + " minutes\n" +
                "This is a full mock exam simulation.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showAlert("Info", "Starting mock exam: " + exam.getTitle(), Alert.AlertType.INFORMATION);
                // Here you would implement the actual mock exam interface
            }
        });
    }

    private void viewMockExamDetails(MockExam exam) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Mock Exam Details");
        dialog.setHeaderText(exam.getTitle());

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        try {
            ExamType examType = examTypeDAO.findById(exam.getExamTypeId()).orElse(null);
            String examTypeName = examType != null ? examType.getName() : "Unknown";

            content.getChildren().addAll(
                    new Label("Exam Type: " + examTypeName),
                    new Label("Duration: " + exam.getDuration() + " minutes"),
                    new Label("Created: " + exam.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                    new Separator(),
                    new Label("Description:"),
                    new TextArea(exam.getDescription()));
        } catch (Exception e) {
            content.getChildren().add(new Label("Error loading exam details"));
        }

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
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
        try {
            // TODO: Implement proper navigation with Stage and User
            System.out.println("Navigating to Candidate Dashboard");
        } catch (Exception e) {
            showAlert("Error", "Cannot navigate to dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void openExamRegistration() {
        try {
            ExamRegistrationScreen registrationScreen = new ExamRegistrationScreen(currentUser);
            getScene().setRoot(registrationScreen);
        } catch (Exception e) {
            showAlert("Error", "Cannot open exam registration: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void openExamResults() {
        try {
            ExamResultsScreen resultsScreen = new ExamResultsScreen(currentUser);
            getScene().setRoot(resultsScreen);
        } catch (Exception e) {
            showAlert("Error", "Cannot open exam results: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void openStudyMaterials() {
        try {
            StudyMaterialsScreen materialsScreen = new StudyMaterialsScreen(currentUser);
            getScene().setRoot(materialsScreen);
        } catch (Exception e) {
            showAlert("Error", "Cannot open study materials: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
