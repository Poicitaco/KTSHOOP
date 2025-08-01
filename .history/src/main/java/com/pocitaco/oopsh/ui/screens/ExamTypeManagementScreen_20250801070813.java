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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Exam Type Management Screen - Material Design 3.0 with Sidebar
 * Giao diện quản lý loại thi với sidebar navigation
 */
public class ExamTypeManagementScreen {
    private final Stage primaryStage;
    private final ExamTypeDAO examTypeDAO;
    private final ObservableList<ExamType> examTypeList;
    private TableView<ExamType> table;
    private TextField searchField;
    private Label totalExamTypesLabel;

    // UI Components
    private BorderPane mainLayout;
    private VBox navigationRail;
    private StackPane contentArea;

    public ExamTypeManagementScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.examTypeDAO = new ExamTypeDAO();
        this.examTypeList = FXCollections.observableArrayList();
    }

    public Scene createScene() {
        // Main layout with sidebar
        mainLayout = new BorderPane();
        mainLayout.setBackground(new Background(new BackgroundFill(
                Colors.BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        // Create components
        HBox appBar = createAppBar();
        navigationRail = createNavigationRail();
        contentArea = createContentArea();

        // Layout setup
        mainLayout.setTop(appBar);
        mainLayout.setLeft(navigationRail);
        mainLayout.setCenter(contentArea);

        // Load exam type management content
        loadExamTypeManagementContent();

        return new Scene(mainLayout, 1200, 800);
    }

    private HBox createAppBar() {
        HBox appBar = new HBox();
        appBar.setAlignment(Pos.CENTER_LEFT);
        appBar.setSpacing(16);
        appBar.setPadding(new Insets(0, 24, 0, 24));
        appBar.setPrefHeight(64);
        appBar.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Title
        Label title = new Label("Quản lý loại thi - OOPSH");
        title.setFont(Typography.TITLE_LARGE);
        title.setTextFill(Colors.PRIMARY);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Back to Dashboard button
        Button backButton = Buttons.createIconButton(Icons.createDashboardIcon());
        backButton.setOnAction(e -> backToDashboard());

        appBar.getChildren().addAll(title, spacer, backButton);

        // Add elevation
        appBar.setEffect(createElevation(2));

        return appBar;
    }

    private VBox createNavigationRail() {
        VBox navRail = new VBox();
        navRail.setAlignment(Pos.TOP_CENTER);
        navRail.setSpacing(8);
        navRail.setPadding(new Insets(16, 8, 16, 8));
        navRail.setPrefWidth(80);
        navRail.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Navigation items
        navRail.getChildren().addAll(
                createNavItem("Dashboard", Icons.createDashboardIcon(), false),
                createNavItem("Người dùng", Icons.createAccountGroupIcon(), false),
                createNavItem("Loại thi", Icons.createFileDocumentIcon(), true),
                createNavItem("Lịch thi", Icons.createCalendarIcon(), false),
                createNavItem("Báo cáo", Icons.createChartBarIcon(), false));

        return navRail;
    }

    private VBox createNavItem(String text, FontIcon icon, boolean selected) {
        VBox navItem = new VBox();
        navItem.setAlignment(Pos.CENTER);
        navItem.setSpacing(4);
        navItem.setPadding(new Insets(8));
        navItem.setPrefWidth(64);
        navItem.setPrefHeight(64);

        // Set background and styling based on selection
        if (selected) {
            navItem.setBackground(new Background(new BackgroundFill(
                    Colors.PRIMARY_CONTAINER, new CornerRadii(16), Insets.EMPTY)));
            icon.setIconColor(Colors.ON_PRIMARY_CONTAINER);
        } else {
            navItem.setBackground(Background.EMPTY);
            icon.setIconColor(Colors.ON_SURFACE_VARIANT);
        }

        icon.setIconSize(24);

        Label label = new Label(text);
        label.setFont(Typography.LABEL_SMALL);
        label.setTextFill(selected ? Colors.ON_PRIMARY_CONTAINER : Colors.ON_SURFACE_VARIANT);

        navItem.getChildren().addAll(icon, label);

        // Add click handler
        navItem.setOnMouseClicked(e -> handleNavItemClick(text));
        navItem.setStyle("-fx-cursor: hand;");

        return navItem;
    }

    private StackPane createContentArea() {
        StackPane content = new StackPane();
        content.setPadding(new Insets(24));
        content.setBackground(new Background(new BackgroundFill(
                Colors.BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        return content;
    }

    private void loadExamTypeManagementContent() {
        VBox managementContent = new VBox(24);
        managementContent.setAlignment(Pos.TOP_LEFT);

        // Header Section
        VBox headerSection = createHeaderSection();

        // Statistics Cards
        HBox statsSection = createStatsSection();

        // Search and Actions Section
        HBox searchSection = createSearchSection();

        // Table Section
        VBox tableSection = createTableSection();

        managementContent.getChildren().addAll(headerSection, statsSection, searchSection, tableSection);

        // Load data
        loadExamTypes();

        contentArea.getChildren().clear();
        contentArea.getChildren().add(managementContent);
    }

    private VBox createHeaderSection() {
        VBox headerSection = new VBox(8);

        Label titleLabel = new Label("Quản lý loại thi sát hạch");
        titleLabel.setFont(Typography.DISPLAY_SMALL);
        titleLabel.setTextFill(Colors.PRIMARY);

        Label subtitleLabel = new Label("Quản lý các loại bằng lái xe và cấu hình kỳ thi");
        subtitleLabel.setFont(Typography.BODY_LARGE);
        subtitleLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        headerSection.getChildren().addAll(titleLabel, subtitleLabel);
        return headerSection;
    }

    private HBox createStatsSection() {
        HBox statsSection = new HBox(16);
        statsSection.setAlignment(Pos.CENTER_LEFT);

        // Total Exam Types Card
        VBox totalCard = createStatCard(
                "Tổng loại thi",
                "0",
                Icons.createFileDocumentIcon(),
                Colors.PRIMARY);
        totalExamTypesLabel = (Label) ((VBox) ((HBox) totalCard.getChildren().get(0)).getChildren().get(1))
                .getChildren().get(1);

        // Theory Exams Card
        VBox theoryCard = createStatCard(
                "Thi lý thuyết",
                "0",
                Icons.createFileDocumentIcon(),
                Colors.SECONDARY);

        // Practical Exams Card
        VBox practicalCard = createStatCard(
                "Thi thực hành",
                "0",
                Icons.createFileDocumentIcon(),
                Colors.WARNING);

        statsSection.getChildren().addAll(totalCard, theoryCard, practicalCard);
        return statsSection;
    }

    private VBox createStatCard(String title, String value, FontIcon icon, Color accentColor) {
        VBox card = Cards.createCard();
        card.setPrefWidth(280);
        card.setSpacing(16);

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(16);

        icon.setIconSize(32);
        icon.setIconColor(accentColor);

        VBox textInfo = new VBox();
        textInfo.setSpacing(4);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Typography.BODY_MEDIUM);
        titleLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Typography.HEADLINE_SMALL);
        valueLabel.setTextFill(Colors.ON_SURFACE);

        textInfo.getChildren().addAll(titleLabel, valueLabel);
        header.getChildren().addAll(icon, textInfo);

        card.getChildren().add(header);
        return card;
    }

    private HBox createSearchSection() {
        HBox searchSection = new HBox(16);
        searchSection.setAlignment(Pos.CENTER_LEFT);

        // Search Field
        VBox searchContainer = new VBox(8);
        Label searchLabel = new Label("Tìm kiếm loại thi");
        searchLabel.setFont(Typography.LABEL_MEDIUM);
        searchLabel.setTextFill(Colors.ON_SURFACE);

        searchField = new TextField();
        searchField.setPromptText("Tìm kiếm theo tên, mô tả hoặc mã...");
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

        // Filter ComboBox
        ComboBox<String> filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll("Tất cả", "Lý thuyết", "Thực hành");
        filterComboBox.setValue("Tất cả");
        filterComboBox.setPrefWidth(150);

        searchContainer.getChildren().addAll(searchLabel, searchField, filterComboBox);

        // Action Buttons
        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button addButton = Buttons.createFilledButton("Thêm loại thi", Icons.createFileDocumentIcon());
        addButton.setOnAction(e -> showAddExamTypeDialog());

        Button refreshButton = Buttons.createOutlinedButton("Làm mới", Icons.createFileDocumentIcon());
        refreshButton.setOnAction(e -> loadExamTypes());

        actionButtons.getChildren().addAll(addButton, refreshButton);

        HBox.setHgrow(searchContainer, Priority.NEVER);
        HBox.setHgrow(actionButtons, Priority.ALWAYS);

        searchSection.getChildren().addAll(searchContainer, actionButtons);
        return searchSection;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox(16);

        Label tableLabel = new Label("Danh sách loại thi");
        tableLabel.setFont(Typography.TITLE_MEDIUM);
        tableLabel.setTextFill(Colors.ON_SURFACE);

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
        TableColumn<ExamType, Integer> idColumn = new TableColumn<>("Mã");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);
        idColumn.setStyle("-fx-alignment: CENTER;");

        // Name Column
        TableColumn<ExamType, String> nameColumn = new TableColumn<>("Tên loại thi");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        // Description Column
        TableColumn<ExamType, String> descriptionColumn = new TableColumn<>("Mô tả");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(250);

        // Fee Column
        TableColumn<ExamType, Double> feeColumn = new TableColumn<>("Phí thi (VNĐ)");
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
        TableColumn<ExamType, Integer> durationColumn = new TableColumn<>("Thời gian (phút)");
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationColumn.setPrefWidth(120);
        durationColumn.setStyle("-fx-alignment: CENTER;");

        // Passing Score Column
        TableColumn<ExamType, Integer> passingScoreColumn = new TableColumn<>("Điểm đậu");
        passingScoreColumn.setCellValueFactory(new PropertyValueFactory<>("passingScore"));
        passingScoreColumn.setPrefWidth(120);
        passingScoreColumn.setStyle("-fx-alignment: CENTER;");

        // Actions Column
        TableColumn<ExamType, Void> actionsColumn = new TableColumn<>("Thao tác");
        actionsColumn.setPrefWidth(180);
        actionsColumn.setCellFactory(column -> new TableCell<ExamType, Void>() {
            private final HBox actionBox = new HBox(8);
            private final Button editButton = Buttons.createIconButton(Icons.createFileDocumentIcon());
            private final Button deleteButton = Buttons.createIconButton(Icons.createFileDocumentIcon());

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
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleNavItemClick(String itemName) {
        System.out.println("🔄 Navigating to: " + itemName);

        switch (itemName) {
            case "Dashboard":
                backToDashboard();
                break;
            case "Người dùng":
                openUserManagement();
                break;
            case "Lịch thi":
                openScheduleManagement();
                break;
            case "Báo cáo":
                openReports();
                break;
            case "Loại thi":
                // Stay on current screen
                break;
            default:
                System.out.println("🔍 Unknown navigation item: " + itemName);
                break;
        }
    }

    private void backToDashboard() {
        AdminDashboardScreen adminDashboard = new AdminDashboardScreen(primaryStage, getCurrentUser());
        adminDashboard.show();
    }

    private void openUserManagement() {
        UserManagementScreen userManagement = new UserManagementScreen(primaryStage);
        userManagement.show();
    }

    private void openScheduleManagement() {
        ExamScheduleManagementScreen scheduleManagement = new ExamScheduleManagementScreen(getCurrentUser(),
                primaryStage);
        Scene scene = scheduleManagement.createScene();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openReports() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("📊 Báo cáo");
        alert.setHeaderText(null);
        alert.setContentText("Chức năng báo cáo đang được phát triển");
        alert.showAndWait();
    }

    private com.pocitaco.oopsh.models.User getCurrentUser() {
        // TODO: Get current user from session/context
        return new com.pocitaco.oopsh.models.User();
    }

    private javafx.scene.effect.DropShadow createElevation(int level) {
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.2));
        shadow.setOffsetY(level * 2);
        shadow.setRadius(level * 4);
        return shadow;
    }
}
