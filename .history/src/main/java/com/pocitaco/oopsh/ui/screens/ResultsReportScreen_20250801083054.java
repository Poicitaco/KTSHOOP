package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.models.Result;
import com.pocitaco.oopsh.models.ExamType;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.enums.ResultStatus;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Colors;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Typography;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Icons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Buttons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Cards;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ResultsReportScreen {

    private Stage primaryStage;
    private Scene scene;
    private BorderPane mainLayout;
    private VBox navigationRail;
    private StackPane contentArea;
    private User currentUser;

    private final ResultDAO resultDAO;
    private final ExamTypeDAO examTypeDAO;
    private final UserDAO userDAO;
    private final ObservableList<Result> allResults;

    private TableView<Result> resultsTable;
    private TextField searchField;
    private ComboBox<ExamType> examTypeFilter;
    private ComboBox<ResultStatus> statusFilter;
    private DatePicker dateFromPicker;
    private DatePicker dateToPicker;

    private Label totalResultsLabel;
    private Label passedResultsLabel;
    private Label failedResultsLabel;
    private Label averageScoreLabel;

    public ResultsReportScreen(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.resultDAO = new ResultDAO();
        this.examTypeDAO = new ExamTypeDAO();
        this.userDAO = new UserDAO();
        this.allResults = FXCollections.observableArrayList();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        mainLayout = new BorderPane();
        mainLayout.setBackground(new Background(new BackgroundFill(
                Colors.BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox appBar = createAppBar();
        navigationRail = createNavigationRail();
        contentArea = createContentArea();

        mainLayout.setTop(appBar);
        mainLayout.setLeft(navigationRail);
        mainLayout.setCenter(contentArea);

        scene = new Scene(mainLayout, 1200, 800);
    }

    private HBox createAppBar() {
        HBox appBar = new HBox();
        appBar.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));
        appBar.setPadding(new Insets(16, 24, 16, 24));
        appBar.setAlignment(Pos.CENTER_LEFT);
        appBar.setSpacing(16);

        FontIcon menuIcon = Icons.createMenuIcon();
        menuIcon.setIconSize(24);
        menuIcon.setIconColor(Colors.ON_SURFACE);

        Label title = new Label("Báo cáo kết quả thi");
        title.setFont(Typography.HEADLINE_MEDIUM);
        title.setTextFill(Colors.ON_SURFACE);

        appBar.getChildren().addAll(menuIcon, title);
        return appBar;
    }

    private VBox createNavigationRail() {
        VBox rail = new VBox();
        rail.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));
        rail.setPrefWidth(250);
        rail.setPadding(new Insets(16));
        rail.setSpacing(8);

        // Navigation items
        VBox dashboardItem = createNavItem("Dashboard", Icons.createHomeIcon(), true);
        VBox resultsItem = createNavItem("Kết quả", Icons.createFileDocumentIcon(), false);
        VBox reportsItem = createNavItem("Báo cáo", Icons.createChartBarIcon(), false);
        VBox settingsItem = createNavItem("Cài đặt", Icons.createSettingsIcon(), false);

        // Logout button
        Button logoutButton = Buttons.createFilledButton("Đăng xuất", Icons.createLogoutIcon());
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setOnAction(e -> handleLogout());

        rail.getChildren().addAll(dashboardItem, resultsItem, reportsItem, settingsItem, logoutButton);
        return rail;
    }

    private VBox createNavItem(String text, FontIcon icon, boolean selected) {
        VBox navItem = new VBox(8);
        navItem.setPadding(new Insets(12, 16, 12, 16));
        navItem.setAlignment(Pos.CENTER_LEFT);
        navItem.setBackground(new Background(new BackgroundFill(
                selected ? Colors.PRIMARY_CONTAINER : Color.TRANSPARENT,
                new CornerRadii(8), Insets.EMPTY)));

        icon.setIconSize(24);
        icon.setIconColor(selected ? Colors.ON_PRIMARY_CONTAINER : Colors.ON_SURFACE_VARIANT);

        Label label = new Label(text);
        label.setFont(Typography.LABEL_SMALL);
        label.setTextFill(selected ? Colors.ON_PRIMARY_CONTAINER : Colors.ON_SURFACE_VARIANT);

        navItem.getChildren().addAll(icon, label);
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

    private void loadResultsContent() {
        VBox content = new VBox(24);
        content.setAlignment(Pos.TOP_LEFT);

        // Page title
        Label pageTitle = new Label("Báo cáo kết quả thi");
        pageTitle.setFont(Typography.HEADLINE_MEDIUM);
        pageTitle.setTextFill(Colors.ON_BACKGROUND);

        // Statistics section
        HBox statsSection = createStatsSection();

        // Filters section
        VBox filtersSection = createFiltersSection();

        // Table section
        VBox tableSection = createTableSection();

        content.getChildren().addAll(pageTitle, statsSection, filtersSection, tableSection);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }

    private HBox createStatsSection() {
        HBox statsRow = new HBox(16);
        statsRow.setAlignment(Pos.CENTER);

        VBox totalCard = createStatCard("Tổng kết quả", "0", Icons.createFileDocumentIcon(), Colors.PRIMARY);
        VBox passedCard = createStatCard("Đạt", "0", Icons.createCheckCircleIcon(), Colors.SUCCESS);
        VBox failedCard = createStatCard("Không đạt", "0", Icons.createCancelIcon(), Colors.ERROR);
        VBox avgCard = createStatCard("Điểm TB", "0.0", Icons.createChartBarIcon(), Colors.SECONDARY);

        totalResultsLabel = (Label) ((VBox) ((HBox) totalCard.getChildren().get(0)).getChildren().get(1)).getChildren()
                .get(1);
        passedResultsLabel = (Label) ((VBox) ((HBox) passedCard.getChildren().get(0)).getChildren().get(1))
                .getChildren().get(1);
        failedResultsLabel = (Label) ((VBox) ((HBox) failedCard.getChildren().get(0)).getChildren().get(1))
                .getChildren().get(1);
        averageScoreLabel = (Label) ((VBox) ((HBox) avgCard.getChildren().get(0)).getChildren().get(1)).getChildren()
                .get(1);

        statsRow.getChildren().addAll(totalCard, passedCard, failedCard, avgCard);
        return statsRow;
    }

    private VBox createStatCard(String title, String value, FontIcon icon, Color accentColor) {
        VBox card = Cards.createCard();
        card.setPrefWidth(280);
        card.setSpacing(16);

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

    private VBox createFiltersSection() {
        VBox filtersContainer = new VBox(16);
        filtersContainer.setPadding(new Insets(16));
        filtersContainer.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, new CornerRadii(12), Insets.EMPTY)));

        Label filtersTitle = new Label("Bộ lọc");
        filtersTitle.setFont(Typography.TITLE_MEDIUM);
        filtersTitle.setTextFill(Colors.ON_SURFACE);

        HBox filtersRow = new HBox(16);
        filtersRow.setAlignment(Pos.CENTER_LEFT);

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Tìm kiếm theo tên thí sinh...");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterResults());

        // Exam type filter
        examTypeFilter = new ComboBox<>();
        examTypeFilter.setPromptText("Loại thi");
        examTypeFilter.setPrefWidth(200);
        examTypeFilter.setOnAction(e -> filterResults());

        // Status filter
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll(ResultStatus.values());
        statusFilter.setPromptText("Trạng thái");
        statusFilter.setPrefWidth(150);
        statusFilter.setOnAction(e -> filterResults());

        // Date filters
        dateFromPicker = new DatePicker();
        dateFromPicker.setPromptText("Từ ngày");
        dateFromPicker.setOnAction(e -> filterResults());

        dateToPicker = new DatePicker();
        dateToPicker.setPromptText("Đến ngày");
        dateToPicker.setOnAction(e -> filterResults());

        // Clear filters button
        Button clearButton = Buttons.createOutlinedButton("Xóa bộ lọc", Icons.createCancelIcon());
        clearButton.setOnAction(e -> clearFilters());

        filtersRow.getChildren().addAll(searchField, examTypeFilter, statusFilter, dateFromPicker, dateToPicker,
                clearButton);
        filtersContainer.getChildren().addAll(filtersTitle, filtersRow);

        return filtersContainer;
    }

    private VBox createTableSection() {
        VBox tableContainer = new VBox(16);
        tableContainer.setPadding(new Insets(16));
        tableContainer.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, new CornerRadii(12), Insets.EMPTY)));

        Label tableTitle = new Label("Danh sách kết quả");
        tableTitle.setFont(Typography.TITLE_MEDIUM);
        tableTitle.setTextFill(Colors.ON_SURFACE);

        resultsTable = createResultsTable();

        tableContainer.getChildren().addAll(tableTitle, resultsTable);
        return tableContainer;
    }

    private TableView<Result> createResultsTable() {
        TableView<Result> table = new TableView<>();
        table.setPlaceholder(new Label("Không có dữ liệu"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Candidate column
        TableColumn<Result, String> candidateCol = new TableColumn<>("Thí sinh");
        candidateCol.setCellValueFactory(cellData -> {
            int userId = cellData.getValue().getUserId();
            Optional<User> user = userDAO.get(userId);
            return new SimpleStringProperty(user.map(User::getFullName).orElse("Unknown"));
        });

        // Exam type column
        TableColumn<Result, String> examTypeCol = new TableColumn<>("Loại thi");
        examTypeCol.setCellValueFactory(cellData -> {
            int examTypeId = cellData.getValue().getExamTypeId();
            Optional<ExamType> examType = examTypeDAO.get(examTypeId);
            return new SimpleStringProperty(examType.map(ExamType::getName).orElse("Unknown"));
        });

        // Score column
        TableColumn<Result, String> scoreCol = new TableColumn<>("Điểm");
        scoreCol.setCellValueFactory(cellData -> {
            double score = cellData.getValue().getScore();
            return new SimpleStringProperty(String.format("%.2f", score));
        });

        // Status column
        TableColumn<Result, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(cellData -> {
            ResultStatus status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status.name());
        });

        // Date column
        TableColumn<Result, String> dateCol = new TableColumn<>("Ngày thi");
        dateCol.setCellValueFactory(cellData -> {
            LocalDate examDate = cellData.getValue().getExamDate();
            return new SimpleStringProperty(
                    examDate != null ? examDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A");
        });

        // Actions column
        TableColumn<Result, Void> actionsCol = new TableColumn<>("Thao tác");
        actionsCol.setCellFactory(param -> new TableCell<Result, Void>() {
            private final HBox actionBox = new HBox(8);
            private final Button viewButton = Buttons.createIconButton(Icons.createFileDocumentIcon());
            private final Button editButton = Buttons.createIconButton(Icons.createEditIcon());

            {
                viewButton.setTooltip(new Tooltip("Xem chi tiết"));
                editButton.setTooltip(new Tooltip("Chỉnh sửa"));

                viewButton.setOnAction(e -> {
                    Result result = getTableView().getItems().get(getIndex());
                    showResultDetails(result);
                });

                editButton.setOnAction(e -> {
                    Result result = getTableView().getItems().get(getIndex());
                    showEditDialog(result);
                });

                actionBox.getChildren().addAll(viewButton, editButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBox);
            }
        });

        table.getColumns().addAll(candidateCol, examTypeCol, scoreCol, statusCol, dateCol, actionsCol);
        table.setItems(allResults);

        return table;
    }

    private void loadData() {
        try {
            List<Result> results = resultDAO.getAll();
            allResults.clear();
            allResults.addAll(results);

            // Load exam types for filter
            List<ExamType> examTypes = examTypeDAO.getAll();
            examTypeFilter.getItems().clear();
            examTypeFilter.getItems().addAll(examTypes);

            updateStats();
        } catch (Exception e) {
            System.err.println("Error loading results data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterResults() {
        String searchText = searchField.getText().toLowerCase();
        ExamType selectedExamType = examTypeFilter.getValue();
        ResultStatus selectedStatus = statusFilter.getValue();

        List<Result> filteredResults = resultDAO.getAll().stream()
                .filter(result -> {
                    // Search filter
                    if (!searchText.isEmpty()) {
                        Optional<User> user = userDAO.get(result.getUserId());
                        String userName = user.map(User::getFullName).orElse("");
                        if (!userName.toLowerCase().contains(searchText)) {
                            return false;
                        }
                    }

                    // Exam type filter
                    if (selectedExamType != null && result.getExamTypeId() != selectedExamType.getId()) {
                        return false;
                    }

                    // Status filter
                    if (selectedStatus != null && result.getStatus() != selectedStatus) {
                        return false;
                    }

                    return true;
                })
                .toList();

        allResults.clear();
        allResults.addAll(filteredResults);
        updateStats();
    }

    private void clearFilters() {
        searchField.clear();
        examTypeFilter.setValue(null);
        statusFilter.setValue(null);
        dateFromPicker.setValue(null);
        dateToPicker.setValue(null);
        loadData();
    }

    private void updateStats() {
        int total = allResults.size();
        long passed = allResults.stream().filter(r -> r.getStatus() == ResultStatus.PASSED).count();
        long failed = allResults.stream().filter(r -> r.getStatus() == ResultStatus.FAILED).count();
        double average = allResults.stream().mapToDouble(Result::getScore).average().orElse(0.0);

        totalResultsLabel.setText(String.valueOf(total));
        passedResultsLabel.setText(String.valueOf(passed));
        failedResultsLabel.setText(String.valueOf(failed));
        averageScoreLabel.setText(String.format("%.1f", average));
    }

    private void showResultDetails(Result result) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Chi tiết kết quả");
        dialog.setHeaderText("Kết quả thi ID: " + result.getId());

        ButtonType closeButtonType = new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        try {
            Optional<User> user = userDAO.get(result.getUserId());
            Optional<ExamType> examType = examTypeDAO.get(result.getExamTypeId());

            content.getChildren().addAll(
                    new Label("Thí sinh: " + user.map(User::getFullName).orElse("Unknown")),
                    new Label("Loại thi: " + examType.map(ExamType::getName).orElse("Unknown")),
                    new Label("Điểm: " + String.format("%.2f", result.getScore())),
                    new Label("Trạng thái: " + result.getStatus()),
                    new Label("Ngày thi: " + (result.getExamDate() != null ? result.getExamDate() : "N/A")),
                    new Separator(),
                    new Label("Ghi chú: " + (result.getNotes() != null ? result.getNotes() : "Không có")));
        } catch (Exception e) {
            content.getChildren().add(new Label("Lỗi khi tải thông tin chi tiết"));
        }

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private void showEditDialog(Result result) {
        Dialog<Result> dialog = new Dialog<>();
        dialog.setTitle("Chỉnh sửa kết quả");
        dialog.setHeaderText("Chỉnh sửa kết quả thi ID: " + result.getId());

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField scoreField = new TextField(String.valueOf(result.getScore()));
        scoreField.setPromptText("Điểm");

        ComboBox<ResultStatus> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll(ResultStatus.values());
        statusCombo.setValue(result.getStatus());

        TextArea notesArea = new TextArea(result.getNotes());
        notesArea.setPromptText("Ghi chú");
        notesArea.setPrefRowCount(3);

        grid.add(new Label("Điểm:"), 0, 0);
        grid.add(scoreField, 1, 0);
        grid.add(new Label("Trạng thái:"), 0, 1);
        grid.add(statusCombo, 1, 1);
        grid.add(new Label("Ghi chú:"), 0, 2);
        grid.add(notesArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    double newScore = Double.parseDouble(scoreField.getText());
                    result.setScore(newScore);
                    result.setStatus(statusCombo.getValue());
                    result.setNotes(notesArea.getText());
                    return result;
                } catch (NumberFormatException e) {
                    showError("Lỗi", "Điểm không hợp lệ");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedResult -> {
            try {
                resultDAO.updateResult(updatedResult);
                showSuccess("Thành công", "Đã cập nhật kết quả thi");
                loadData();
            } catch (Exception e) {
                showError("Lỗi", "Không thể cập nhật kết quả: " + e.getMessage());
            }
        });
    }

    private void handleNavItemClick(String itemName) {
        switch (itemName) {
            case "Dashboard":
                handleBack();
                break;
            case "Kết quả":
                loadResultsContent();
                break;
            case "Báo cáo":
                showInfo("Báo cáo", "Chức năng báo cáo đang được phát triển");
                break;
            case "Cài đặt":
                showInfo("Cài đặt", "Chức năng cài đặt đang được phát triển");
                break;
            default:
                System.out.println("Unknown navigation item: " + itemName);
                break;
        }
    }

    private void handleBack() {
        try {
            if (currentUser.getRole().name().equals("ADMIN")) {
                AdminDashboardScreen adminDashboard = new AdminDashboardScreen(primaryStage, currentUser);
                adminDashboard.show();
            } else if (currentUser.getRole().name().equals("EXAMINER")) {
                ExaminerDashboardScreen examinerDashboard = new ExaminerDashboardScreen(primaryStage, currentUser);
                examinerDashboard.show();
            }
        } catch (Exception e) {
            System.err.println("Error navigating back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLogout() {
        try {
            LoginScreen loginScreen = new LoginScreen(primaryStage);
            loginScreen.show();
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene createScene() {
        loadResultsContent();
        return scene;
    }
}