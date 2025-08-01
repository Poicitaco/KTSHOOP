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
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Study Materials Screen for Candidates
 * Màn hình tài liệu học tập cho thí sinh
 */
public class StudyMaterialsScreen extends BorderPane {

    private final UserDAO userDAO;
    private final StudyMaterialDAO studyMaterialDAO;
    private final ExamTypeDAO examTypeDAO;

    private TableView<StudyMaterial> materialsTable;
    private ObservableList<StudyMaterial> materialsData;

    private ComboBox<ExamType> examTypeFilter;
    private ComboBox<String> materialTypeFilter;
    private TextField searchField;
    private User currentUser;

    // Statistics labels
    private Label totalMaterialsLabel;
    private Label documentsLabel;
    private Label videosLabel;
    private Label testsLabel;

    public StudyMaterialsScreen(User currentUser) {
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();
        this.studyMaterialDAO = new StudyMaterialDAO();
        this.examTypeDAO = new ExamTypeDAO();
        this.materialsData = FXCollections.observableArrayList();

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
        Label titleLabel = new Label("STUDY MATERIALS");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        // Navigation buttons
        Button backButton = new Button("[BACK] Back to Dashboard");
        Button registerExamButton = new Button("[PLUS] Register for Exam");
        Button examResultsButton = new Button("[CHART] Exam Results");
        Button practiceTestsButton = new Button("[TEST] Practice Tests");

        // Style buttons
        Button[] buttons = { backButton, registerExamButton, examResultsButton, practiceTestsButton };
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
        practiceTestsButton.setOnAction(e -> openPracticeTests());

        sidebar.getChildren().addAll(titleLabel, new Separator(),
                backButton, registerExamButton, examResultsButton, practiceTestsButton);

        return sidebar;
    }

    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Study Materials");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        // Statistics cards
        HBox statsContainer = createStatisticsCards();

        // Filters
        HBox filtersContainer = createFiltersContainer();

        // Materials table
        VBox tableContainer = createTableContainer();

        // Action buttons
        HBox actionButtons = createActionButtons();

        content.getChildren().addAll(titleLabel, statsContainer, filtersContainer, tableContainer, actionButtons);
        return content;
    }

    private HBox createStatisticsCards() {
        HBox container = new HBox(20);
        container.setAlignment(Pos.CENTER);

        // Total materials card
        VBox totalCard = createStatCard("Total Materials", "0", "#3498db");
        totalMaterialsLabel = (Label) totalCard.getChildren().get(1);

        // Documents card
        VBox documentsCard = createStatCard("Documents", "0", "#2ecc71");
        documentsLabel = (Label) documentsCard.getChildren().get(1);

        // Videos card
        VBox videosCard = createStatCard("Videos", "0", "#f39c12");
        videosLabel = (Label) videosCard.getChildren().get(1);

        // Tests card
        VBox testsCard = createStatCard("Practice Tests", "0", "#e74c3c");
        testsLabel = (Label) testsCard.getChildren().get(1);

        container.getChildren().addAll(totalCard, documentsCard, videosCard, testsCard);
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

    private HBox createFiltersContainer() {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(0, 0, 10, 0));

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search materials...");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener((obs, oldText, newText) -> filterMaterials());

        // Exam type filter
        examTypeFilter = new ComboBox<>();
        examTypeFilter.setPromptText("Select exam type");
        examTypeFilter.setPrefWidth(150);
        loadExamTypesToFilter();
        examTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterMaterials());

        // Material type filter
        materialTypeFilter = new ComboBox<>();
        materialTypeFilter.getItems().addAll("All", "PDF", "Video", "Link", "Test");
        materialTypeFilter.setValue("All");
        materialTypeFilter.setPrefWidth(100);
        materialTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterMaterials());

        Button clearFiltersButton = new Button("Clear Filters");
        clearFiltersButton.setOnAction(e -> clearFilters());

        container.getChildren().addAll(
                new Label("Search:"), searchField,
                new Label("Exam Type:"), examTypeFilter,
                new Label("Type:"), materialTypeFilter,
                clearFiltersButton);

        return container;
    }

    private VBox createTableContainer() {
        VBox container = new VBox(10);

        materialsTable = new TableView<>();
        materialsTable.setItems(materialsData);

        // Create columns
        TableColumn<StudyMaterial, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<StudyMaterial, String> examTypeCol = new TableColumn<>("Exam Type");
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

        TableColumn<StudyMaterial, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("materialType"));
        typeCol.setPrefWidth(80);

        TableColumn<StudyMaterial, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setPrefWidth(250);

        TableColumn<StudyMaterial, String> uploadDateCol = new TableColumn<>("Upload Date");
        uploadDateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getUploadDate();
            return new javafx.beans.property.SimpleStringProperty(
                    date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });
        uploadDateCol.setPrefWidth(100);

        TableColumn<StudyMaterial, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<StudyMaterial, Void>() {
            private final Button downloadBtn = new Button("Download");
            private final Button viewBtn = new Button("View");
            private final HBox container = new HBox(5, downloadBtn, viewBtn);

            {
                downloadBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                viewBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");

                downloadBtn.setOnAction(e -> {
                    StudyMaterial material = getTableView().getItems().get(getIndex());
                    downloadMaterial(material);
                });

                viewBtn.setOnAction(e -> {
                    StudyMaterial material = getTableView().getItems().get(getIndex());
                    viewMaterial(material);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
        actionCol.setPrefWidth(150);

        materialsTable.getColumns().addAll(titleCol, examTypeCol, typeCol, descriptionCol, uploadDateCol, actionCol);

        container.getChildren().addAll(new Label("Available Study Materials:"), materialsTable);
        return container;
    }

    private HBox createActionButtons() {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);

        Button refreshButton = new Button("[REFRESH] Refresh");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px 20px;");
        refreshButton.setOnAction(e -> {
            loadData();
            updateStatistics();
        });

        Button uploadButton = new Button("[UPLOAD] Suggest Material");
        uploadButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10px 20px;");
        uploadButton.setOnAction(e -> suggestMaterial());

        container.getChildren().addAll(refreshButton, uploadButton);
        return container;
    }

    private void loadData() {
        try {
            List<StudyMaterial> materials = studyMaterialDAO.getAll();
            materialsData.clear();
            materialsData.addAll(materials);
        } catch (Exception e) {
            showAlert("Error", "Cannot load study materials: " + e.getMessage(), Alert.AlertType.ERROR);
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
            int total = materialsData.size();
            int documents = (int) materialsData.stream()
                    .filter(m -> "PDF".equals(m.getMaterialType()))
                    .count();
            int videos = (int) materialsData.stream()
                    .filter(m -> "Video".equals(m.getMaterialType()))
                    .count();
            int tests = (int) materialsData.stream()
                    .filter(m -> "Test".equals(m.getMaterialType()))
                    .count();

            totalMaterialsLabel.setText(String.valueOf(total));
            documentsLabel.setText(String.valueOf(documents));
            videosLabel.setText(String.valueOf(videos));
            testsLabel.setText(String.valueOf(tests));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterMaterials() {
        try {
            List<StudyMaterial> allMaterials = studyMaterialDAO.getAll();
            List<StudyMaterial> filteredMaterials = allMaterials.stream()
                    .filter(this::matchesFilters)
                    .collect(Collectors.toList());

            materialsData.clear();
            materialsData.addAll(filteredMaterials);
        } catch (Exception e) {
            showAlert("Error", "Cannot filter materials: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean matchesFilters(StudyMaterial material) {
        // Search filter
        String searchText = searchField.getText();
        if (searchText != null && !searchText.trim().isEmpty()) {
            if (!material.getTitle().toLowerCase().contains(searchText.toLowerCase()) &&
                    !material.getDescription().toLowerCase().contains(searchText.toLowerCase())) {
                return false;
            }
        }

        // Exam type filter
        ExamType selectedType = examTypeFilter.getValue();
        if (selectedType != null) {
            if (material.getExamTypeId() != selectedType.getId()) {
                return false;
            }
        }

        // Material type filter
        String selectedMaterialType = materialTypeFilter.getValue();
        if (selectedMaterialType != null && !"All".equals(selectedMaterialType)) {
            if (!selectedMaterialType.equals(material.getMaterialType())) {
                return false;
            }
        }

        return true;
    }

    private void clearFilters() {
        searchField.clear();
        examTypeFilter.setValue(null);
        materialTypeFilter.setValue("All");
        loadData();
    }

    private void downloadMaterial(StudyMaterial material) {
        if (material.getFilePath() != null && !material.getFilePath().isEmpty()) {
            showAlert("Info", "Download started for: " + material.getTitle(), Alert.AlertType.INFORMATION);
            // Here you would implement actual file download logic
        } else {
            showAlert("Warning", "No file available for download", Alert.AlertType.WARNING);
        }
    }

    private void viewMaterial(StudyMaterial material) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Study Material Details");
        dialog.setHeaderText(material.getTitle());

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        try {
            ExamType examType = examTypeDAO.findById(material.getExamTypeId()).orElse(null);
            String examTypeName = examType != null ? examType.getName() : "Unknown";

            content.getChildren().addAll(
                    new Label("Exam Type: " + examTypeName),
                    new Label("Type: " + material.getMaterialType()),
                    new Label("Upload Date: "
                            + material.getUploadDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                    new Separator(),
                    new Label("Description:"),
                    new TextArea(material.getDescription()));

            if (material.getFileUrl() != null && !material.getFileUrl().isEmpty()) {
                Button openUrlBtn = new Button("Open URL");
                openUrlBtn.setOnAction(e -> {
                    showAlert("Info", "Opening URL: " + material.getFileUrl(), Alert.AlertType.INFORMATION);
                    // Here you would implement URL opening logic
                });
                content.getChildren().add(openUrlBtn);
            }
        } catch (Exception e) {
            content.getChildren().add(new Label("Error loading material details"));
        }

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private void suggestMaterial() {
        Dialog<StudyMaterial> dialog = new Dialog<>();
        dialog.setTitle("Suggest Study Material");
        dialog.setHeaderText("Suggest a new study material");

        ButtonType suggestButtonType = new ButtonType("Suggest", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(suggestButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Material title");

        ComboBox<ExamType> examTypeCombo = new ComboBox<>();
        examTypeCombo.getItems().addAll(examTypeDAO.getAll());

        ComboBox<String> materialTypeCombo = new ComboBox<>();
        materialTypeCombo.getItems().addAll("PDF", "Video", "Link", "Test");

        TextArea descArea = new TextArea();
        descArea.setPromptText("Description");
        descArea.setPrefRowCount(3);

        TextField urlField = new TextField();
        urlField.setPromptText("URL (optional)");

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Exam Type:"), 0, 1);
        grid.add(examTypeCombo, 1, 1);
        grid.add(new Label("Material Type:"), 0, 2);
        grid.add(materialTypeCombo, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descArea, 1, 3);
        grid.add(new Label("URL:"), 0, 4);
        grid.add(urlField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == suggestButtonType) {
                if (titleField.getText().trim().isEmpty() ||
                        examTypeCombo.getValue() == null ||
                        materialTypeCombo.getValue() == null) {
                    showAlert("Error", "Please fill in all required fields", Alert.AlertType.ERROR);
                    return null;
                }

                StudyMaterial material = new StudyMaterial();
                material.setTitle(titleField.getText());
                material.setExamTypeId(examTypeCombo.getValue().getId());
                material.setMaterialType(materialTypeCombo.getValue());
                material.setDescription(descArea.getText());
                material.setFileUrl(urlField.getText());
                material.setUploadDate(LocalDate.now());

                return material;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(material -> {
            showAlert("Success", "Material suggestion submitted for review!", Alert.AlertType.INFORMATION);
            // Here you would implement saving the suggestion
        });
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
            new CandidateDashboardScreen(
                    (Stage) getScene().getWindow(), currentUser);
            // Navigation đã được thực hiện trong constructor của CandidateDashboardScreen
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
            ExamResultsScreen resultsScreen = new ExamResultsScreen((Stage) getScene().getWindow(), currentUser);
            Scene scene = resultsScreen.createScene();
            ((Stage) getScene().getWindow()).setScene(scene);
        } catch (Exception e) {
            showAlert("Error", "Cannot open exam results: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void openPracticeTests() {
        try {
            PracticeTestsScreen practiceScreen = new PracticeTestsScreen(currentUser);
            getScene().setRoot(practiceScreen);
        } catch (Exception e) {
            showAlert("Error", "Cannot open practice tests: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
