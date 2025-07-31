package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.ExamType;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Colors;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Typography;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Icons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Buttons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Cards;
import com.pocitaco.oopsh.ui.components.ExamTypeFormDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ExamTypeManagementScreen {
    private final Stage primaryStage;
    private final ExamTypeDAO examTypeDAO;
    private final ObservableList<ExamType> examTypeList;
    private TableView<ExamType> table;
    private TextField searchField;
    private Label totalExamTypesLabel;

    public ExamTypeManagementScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.examTypeDAO = new ExamTypeDAO();
        this.examTypeList = FXCollections.observableArrayList();
    }

    public Scene createScene() {
        VBox root = new VBox(24);
        root.setStyle(Colors.BACKGROUND_SURFACE);
        root.setPadding(new Insets(32));

        // Header Section
        VBox headerSection = createHeaderSection();

        // Statistics Cards
        HBox statsSection = createStatsSection();

        // Search and Actions Section
        HBox searchSection = createSearchSection();

        // Table Section
        VBox tableSection = createTableSection();

        root.getChildren().addAll(headerSection, statsSection, searchSection, tableSection);

        // Load data
        loadExamTypes();

        return new Scene(root, 1200, 800);
    }

    private VBox createHeaderSection() {
        VBox headerSection = new VBox(8);

        Label titleLabel = new Label("Exam Type Management");
        titleLabel.setStyle(Typography.DISPLAY_SMALL + Colors.TEXT_PRIMARY);

        Label subtitleLabel = new Label("Manage driving license exam types and configurations");
        subtitleLabel.setStyle(Typography.BODY_LARGE + Colors.TEXT_SECONDARY);

        headerSection.getChildren().addAll(titleLabel, subtitleLabel);
        return headerSection;
    }

    private HBox createStatsSection() {
        HBox statsSection = new HBox(16);
        statsSection.setAlignment(Pos.CENTER_LEFT);

        // Total Exam Types Card
        VBox totalCard = Cards.createStatCard(
                "Total Exam Types",
                "0",
                Icons.createIcon("mdi2a-assessment", 24, "#6750A4"),
                Colors.PRIMARY_CONTAINER);
        totalExamTypesLabel = (Label) ((VBox) totalCard.getChildren().get(1)).getChildren().get(0);

        // Theory Exams Card
        VBox theoryCard = Cards.createStatCard(
                "Theory Exams",
                "0",
                Icons.createIcon("mdi2b-book-open", 24, "#625B71"),
                Colors.SECONDARY_CONTAINER);

        // Practical Exams Card
        VBox practicalCard = Cards.createStatCard(
                "Practical Exams",
                "0",
                Icons.createIcon("mdi2c-car", 24, "#7D5260"),
                Colors.TERTIARY_CONTAINER);

        statsSection.getChildren().addAll(totalCard, theoryCard, practicalCard);
        return statsSection;
    }

    private HBox createSearchSection() {
        HBox searchSection = new HBox(16);
        searchSection.setAlignment(Pos.CENTER_LEFT);

        // Search Field
        VBox searchContainer = new VBox(8);
        Label searchLabel = new Label("Search Exam Types");
        searchLabel.setStyle(Typography.LABEL_MEDIUM + Colors.TEXT_PRIMARY);

        searchField = new TextField();
        searchField.setPromptText("Search by name, description, or code...");
        searchField.setPrefWidth(300);
        searchField.setStyle(
                "-fx-padding: 12 16;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #79747E;" +
                        "-fx-border-width: 1;" +
                        "-fx-background-color: #FEF7FF;" +
                        "-fx-font-size: 14px;");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterExamTypes(newVal));

        searchContainer.getChildren().addAll(searchLabel, searchField);

        // Action Buttons
        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button addButton = Buttons.createFilledButton("Add Exam Type", Icons.createIcon("mdi2p-plus", 18));
        addButton.setOnAction(e -> showAddExamTypeDialog());

        Button refreshButton = Buttons.createOutlinedButton("Refresh", Icons.createIcon("mdi2r-refresh", 18));
        refreshButton.setOnAction(e -> loadExamTypes());

        actionButtons.getChildren().addAll(addButton, refreshButton);

        HBox.setHgrow(searchContainer, Priority.NEVER);
        HBox.setHgrow(actionButtons, Priority.ALWAYS);

        searchSection.getChildren().addAll(searchContainer, actionButtons);
        return searchSection;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox(16);

        Label tableLabel = new Label("Exam Type List");
        tableLabel.setStyle(Typography.TITLE_MEDIUM + Colors.TEXT_PRIMARY);

        table = createExamTypeTable();

        tableSection.getChildren().addAll(tableLabel, table);
        return tableSection;
    }

    private TableView<ExamType> createExamTypeTable() {
        TableView<ExamType> table = new TableView<>();
        table.setItems(examTypeList);
        table.setRowFactory(tv -> {
            TableRow<ExamType> row = new TableRow<>();
            row.setStyle(
                    "-fx-background-color: " + Colors.SURFACE + ";" +
                            "-fx-border-color: transparent;" +
                            "-fx-border-width: 0 0 1 0;");

            row.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
                if (isNowHovered) {
                    row.setStyle(
                            "-fx-background-color: " + Colors.SURFACE_VARIANT + ";" +
                                    "-fx-border-color: transparent;" +
                                    "-fx-border-width: 0 0 1 0;");
                } else {
                    row.setStyle(
                            "-fx-background-color: " + Colors.SURFACE + ";" +
                                    "-fx-border-color: transparent;" +
                                    "-fx-border-width: 0 0 1 0;");
                }
            });

            return row;
        });

        // Style the table
        table.setStyle(
                "-fx-background-color: " + Colors.SURFACE + ";" +
                        "-fx-border-color: " + Colors.OUTLINE_VARIANT + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;");

        // ID Column
        TableColumn<ExamType, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);
        idColumn.setStyle("-fx-alignment: CENTER;");

        // Name Column
        TableColumn<ExamType, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        // Description Column
        TableColumn<ExamType, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(250);

        // Fee Column
        TableColumn<ExamType, Double> feeColumn = new TableColumn<>("Fee");
        feeColumn.setCellValueFactory(new PropertyValueFactory<>("fee"));
        feeColumn.setPrefWidth(120);
        feeColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        feeColumn.setCellFactory(column -> new TableCell<ExamType, Double>() {
            private final NumberFormat currencyFormat = NumberFormat
                    .getCurrencyInstance(Locale.forLanguageTag("vi-VN"));

            @Override
            protected void updateItem(Double fee, boolean empty) {
                super.updateItem(fee, empty);
                if (empty || fee == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(fee));
                }
            }
        });

        // Duration Column
        TableColumn<ExamType, Integer> durationColumn = new TableColumn<>("Duration (min)");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationColumn.setPrefWidth(120);
        durationColumn.setStyle("-fx-alignment: CENTER;");

        // Passing Score Column
        TableColumn<ExamType, Integer> passingScoreColumn = new TableColumn<>("Passing Score");
        passingScoreColumn.setCellValueFactory(new PropertyValueFactory<>("passingScore"));
        passingScoreColumn.setPrefWidth(120);
        passingScoreColumn.setStyle("-fx-alignment: CENTER;");

        // Actions Column
        TableColumn<ExamType, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setPrefWidth(180);
        actionsColumn.setCellFactory(column -> new TableCell<ExamType, Void>() {
            private final HBox actionBox = new HBox(8);
            private final Button editButton = Buttons.createIconButton(Icons.createIcon("mdi2p-pencil", 16));
            private final Button deleteButton = Buttons.createIconButton(Icons.createIcon("mdi2d-delete", 16));

            {
                editButton.setOnAction(e -> {
                    ExamType examType = getTableView().getItems().get(getIndex());
                    showEditExamTypeDialog(examType);
                });

                deleteButton.setOnAction(e -> {
                    ExamType examType = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(examType);
                });

                actionBox.setAlignment(Pos.CENTER);
                actionBox.getChildren().addAll(editButton, deleteButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });

        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(descriptionColumn);
        table.getColumns().add(feeColumn);
        table.getColumns().add(durationColumn);
        table.getColumns().add(passingScoreColumn);
        table.getColumns().add(actionsColumn);

        return table;
    }

    private void loadExamTypes() {
        try {
            List<ExamType> examTypes = examTypeDAO.getAll();
            examTypeList.clear();
            examTypeList.addAll(examTypes);
            updateStats();
            System.out.println("✅ Loaded " + examTypes.size() + " exam types");
        } catch (Exception e) {
            System.err.println("❌ Error loading exam types: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterExamTypes(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadExamTypes();
            return;
        }

        try {
            List<ExamType> allExamTypes = examTypeDAO.getAll();
            List<ExamType> filteredExamTypes = allExamTypes.stream()
                    .filter(examType -> examType.getName().toLowerCase().contains(query.toLowerCase()) ||
                            examType.getDescription().toLowerCase().contains(query.toLowerCase()))
                    .toList();

            examTypeList.clear();
            examTypeList.addAll(filteredExamTypes);
            updateStats();
        } catch (Exception e) {
            System.err.println("❌ Error filtering exam types: " + e.getMessage());
        }
    }

    private void updateStats() {
        int totalExamTypes = examTypeList.size();
        totalExamTypesLabel.setText(String.valueOf(totalExamTypes));
    }

    private void showAddExamTypeDialog() {
        ExamTypeFormDialog dialog = new ExamTypeFormDialog(primaryStage, "Add Exam Type", null);
        Optional<ExamType> result = dialog.showDialog();

        if (result.isPresent()) {
            ExamType newExamType = result.get();
            try {
                examTypeDAO.addExamType(newExamType);
                loadExamTypes();
                showSuccessMessage("Exam type added successfully!");
            } catch (Exception e) {
                showErrorMessage("Failed to add exam type: " + e.getMessage());
            }
        }
    }

    private void showEditExamTypeDialog(ExamType examType) {
        ExamTypeFormDialog dialog = new ExamTypeFormDialog(primaryStage, "Edit Exam Type", examType);
        Optional<ExamType> result = dialog.showDialog();

        if (result.isPresent()) {
            ExamType updatedExamType = result.get();
            try {
                examTypeDAO.updateExamType(updatedExamType);
                loadExamTypes();
                showSuccessMessage("Exam type updated successfully!");
            } catch (Exception e) {
                showErrorMessage("Failed to update exam type: " + e.getMessage());
            }
        }
    }

    private void showDeleteConfirmation(ExamType examType) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Exam Type");
        alert.setHeaderText("Are you sure you want to delete this exam type?");
        alert.setContentText("Name: " + examType.getName() + "\nThis action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                examTypeDAO.deleteById(examType.getId());
                loadExamTypes();
                showSuccessMessage("Exam type deleted successfully!");
            } catch (Exception e) {
                showErrorMessage("Failed to delete exam type: " + e.getMessage());
            }
        }
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
