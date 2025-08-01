package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
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

import java.util.List;
import java.util.Optional;

public class ExamResultsScreen {
    private Stage primaryStage;
    private Scene scene;
    private BorderPane mainLayout;
    private VBox navigationRail;
    private StackPane contentArea;

    private final ResultDAO resultDAO;
    private final ExamTypeDAO examTypeDAO;
    private final User currentUser;
    private final ObservableList<Result> userResults;

    private TableView<Result> resultsTable;
    private ComboBox<String> examFilter;
    private ComboBox<String> statusFilter;

    private Label totalExamsLabel;
    private Label passedExamsLabel;
    private Label averageScoreLabel;

    public ExamResultsScreen(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.resultDAO = new ResultDAO();
        this.examTypeDAO = new ExamTypeDAO();
        this.userResults = FXCollections.observableArrayList();
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

        Label title = new Label("Kết quả thi");
        title.setFont(Typography.HEADLINE_MEDIUM);
        title.setTextFill(Colors.ON_SURFACE);

        appBar.getChildren().addAll(menuIcon, title);
        return appBar;
    }

    private VBox createNavigationRail() {
        VBox rail = new VBox();
        rail.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));
        rail.setPrefWidth(80);
        rail.setPadding(new Insets(16, 8, 16, 8));
        rail.setSpacing(8);

        rail.getChildren().addAll(
                createNavItem("Dashboard", Icons.createHomeIcon(), false),
                createNavItem("Kết quả", Icons.createChartBarIcon(), true),
                createNavItem("Luyện tập", Icons.createFileDocumentIcon(), false),
                createNavItem("Tài liệu", Icons.createFileDocumentIcon(), false),
                createNavItem("Thanh toán", Icons.createCreditCardIcon(), false),
                createNavItem("Chứng chỉ", Icons.createCertificateIcon(), false));

        return rail;
    }

    private VBox createNavItem(String text, FontIcon icon, boolean selected) {
        VBox navItem = new VBox(4);
        navItem.setAlignment(Pos.CENTER);
        navItem.setPadding(new Insets(12, 8, 12, 8));
        navItem.setBackground(new Background(new BackgroundFill(
                selected ? Colors.PRIMARY_CONTAINER : Color.TRANSPARENT,
                new CornerRadii(8), Insets.EMPTY)));
        navItem.setPrefHeight(72);

        icon.setIconSize(24);
        icon.setIconColor(selected ? Colors.ON_PRIMARY_CONTAINER : Colors.ON_SURFACE_VARIANT);

        Label label = new Label(text);
        label.setFont(Typography.LABEL_SMALL);
        label.setTextFill(selected ? Colors.ON_PRIMARY_CONTAINER : Colors.ON_SURFACE_VARIANT);
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);

        navItem.getChildren().addAll(icon, label);

        navItem.setOnMouseClicked(e -> handleNavItemClick(text));

        return navItem;
    }

    private StackPane createContentArea() {
        StackPane content = new StackPane();
        content.setPadding(new Insets(24));
        loadResultsContent();
        return content;
    }

    private void loadResultsContent() {
        VBox resultsContent = new VBox();
        resultsContent.setSpacing(24);
        resultsContent.setAlignment(Pos.TOP_LEFT);

        Label pageTitle = new Label("Kết quả thi của tôi");
        pageTitle.setFont(Typography.HEADLINE_MEDIUM);
        pageTitle.setTextFill(Colors.ON_BACKGROUND);

        HBox statsSection = createStatsSection();
        VBox filtersSection = createFiltersSection();
        VBox tableSection = createTableSection();

        resultsContent.getChildren().addAll(pageTitle, statsSection, filtersSection, tableSection);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(resultsContent);
    }

    private HBox createStatsSection() {
        HBox statsSection = new HBox(16);
        statsSection.setAlignment(Pos.CENTER_LEFT);

        totalExamsLabel = new Label("0");
        totalExamsLabel.setFont(Typography.HEADLINE_LARGE);
        totalExamsLabel.setTextFill(Colors.PRIMARY);

        Label totalExamsDesc = new Label("Tổng số bài thi");
        totalExamsDesc.setFont(Typography.BODY_MEDIUM);
        totalExamsDesc.setTextFill(Colors.ON_SURFACE_VARIANT);

        VBox totalExamsBox = new VBox(4);
        totalExamsBox.setAlignment(Pos.CENTER_LEFT);
        totalExamsBox.getChildren().addAll(totalExamsLabel, totalExamsDesc);

        passedExamsLabel = new Label("0");
        passedExamsLabel.setFont(Typography.HEADLINE_LARGE);
        passedExamsLabel.setTextFill(Colors.SECONDARY);

        Label passedExamsDesc = new Label("Bài thi đạt");
        passedExamsDesc.setFont(Typography.BODY_MEDIUM);
        passedExamsDesc.setTextFill(Colors.ON_SURFACE_VARIANT);

        VBox passedExamsBox = new VBox(4);
        passedExamsBox.setAlignment(Pos.CENTER_LEFT);
        passedExamsBox.getChildren().addAll(passedExamsLabel, passedExamsDesc);

        averageScoreLabel = new Label("0.0");
        averageScoreLabel.setFont(Typography.HEADLINE_LARGE);
        averageScoreLabel.setTextFill(Colors.TERTIARY);

        Label averageScoreDesc = new Label("Điểm trung bình");
        averageScoreDesc.setFont(Typography.BODY_MEDIUM);
        averageScoreDesc.setTextFill(Colors.ON_SURFACE_VARIANT);

        VBox averageScoreBox = new VBox(4);
        averageScoreBox.setAlignment(Pos.CENTER_LEFT);
        averageScoreBox.getChildren().addAll(averageScoreLabel, averageScoreDesc);

        statsSection.getChildren().addAll(totalExamsBox, passedExamsBox, averageScoreBox);
        return statsSection;
    }

    private VBox createFiltersSection() {
        VBox filtersSection = Cards.createCard();
        filtersSection.setSpacing(16);
        filtersSection.setPadding(new Insets(20));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(12);

        FontIcon filterIcon = Icons.createFileDocumentIcon();
        filterIcon.setIconSize(24);
        filterIcon.setIconColor(Colors.PRIMARY);

        Label filterLabel = new Label("Lọc kết quả");
        filterLabel.setFont(Typography.TITLE_MEDIUM);
        filterLabel.setTextFill(Colors.ON_SURFACE);

        header.getChildren().addAll(filterIcon, filterLabel);

        HBox filterControls = new HBox(16);
        filterControls.setAlignment(Pos.CENTER_LEFT);

        // Exam filter
        VBox examBox = new VBox(4);
        Label examLabel = new Label("Loại thi");
        examLabel.setFont(Typography.LABEL_MEDIUM);
        examLabel.setTextFill(Colors.ON_SURFACE);

        examFilter = new ComboBox<>();
        examFilter.setPromptText("Tất cả loại thi");
        examFilter.setPrefWidth(200);
        examFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterResults());

        examBox.getChildren().addAll(examLabel, examFilter);

        // Status filter
        VBox statusBox = new VBox(4);
        Label statusLabel = new Label("Trạng thái");
        statusLabel.setFont(Typography.LABEL_MEDIUM);
        statusLabel.setTextFill(Colors.ON_SURFACE);

        statusFilter = new ComboBox<>();
        statusFilter.setPromptText("Tất cả trạng thái");
        statusFilter.setPrefWidth(200);
        statusFilter.getItems().addAll("Tất cả", "Đạt", "Không đạt", "Chờ chấm");
        statusFilter.setValue("Tất cả");
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterResults());

        statusBox.getChildren().addAll(statusLabel, statusFilter);

        filterControls.getChildren().addAll(examBox, statusBox);

        filtersSection.getChildren().addAll(header, filterControls);
        return filtersSection;
    }

    private VBox createTableSection() {
        VBox tableSection = Cards.createCard();
        tableSection.setSpacing(16);
        tableSection.setPadding(new Insets(20));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label tableLabel = new Label("Danh sách kết quả thi");
        tableLabel.setFont(Typography.TITLE_MEDIUM);
        tableLabel.setTextFill(Colors.ON_SURFACE);

        Button refreshButton = Buttons.createOutlinedButton("Làm mới", Icons.createFileDocumentIcon());
        refreshButton.setOnAction(e -> loadData());

        header.getChildren().addAll(tableLabel, refreshButton);

        resultsTable = createResultsTable();

        tableSection.getChildren().addAll(header, resultsTable);
        return tableSection;
    }

    private TableView<Result> createResultsTable() {
        TableView<Result> table = new TableView<>();
        table.setPlaceholder(new Label("Không có kết quả thi nào"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Exam type column
        TableColumn<Result, String> examCol = new TableColumn<>("Loại thi");
        examCol.setCellValueFactory(data -> {
            try {
                Optional<ExamType> examTypeOpt = examTypeDAO.get(data.getValue().getExamTypeId());
                ExamType examType = examTypeOpt.orElse(null);
                return new SimpleStringProperty(
                        examType != null ? examType.getName() : "Không xác định");
            } catch (Exception e) {
                return new SimpleStringProperty("Lỗi");
            }
        });

        // Exam date column
        TableColumn<Result, String> dateCol = new TableColumn<>("Ngày thi");
        dateCol.setCellValueFactory(data -> {
            return new SimpleStringProperty(data.getValue().getExamDate().toString());
        });

        // Status column
        TableColumn<Result, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(data -> {
            ResultStatus status = data.getValue().getStatus();
            String statusText;
            switch (status) {
                case PENDING:
                    statusText = "Chờ chấm";
                    break;
                case PASSED:
                    statusText = "Đạt";
                    break;
                case FAILED:
                    statusText = "Không đạt";
                    break;
                default:
                    statusText = "Không xác định";
            }
            return new SimpleStringProperty(statusText);
        });

        // Score column
        TableColumn<Result, String> scoreCol = new TableColumn<>("Điểm");
        scoreCol.setCellValueFactory(data -> {
            double score = data.getValue().getScore();
            String scoreText = score > 0 ? String.format("%.1f", score) : "Chưa chấm";
            return new SimpleStringProperty(scoreText);
        });

        // Actions column
        TableColumn<Result, Void> actionsCol = new TableColumn<>("Thao tác");
        actionsCol.setCellFactory(col -> new TableCell<Result, Void>() {
            private final HBox actionBox = new HBox(8);
            private final Button viewButton = Buttons.createIconButton(Icons.createFileDocumentIcon());

            {
                viewButton.setTooltip(new Tooltip("Xem chi tiết"));
                viewButton.setOnAction(e -> {
                    Result result = getTableView().getItems().get(getIndex());
                    showResultDetails(result);
                });

                actionBox.getChildren().addAll(viewButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBox);
            }
        });

        table.getColumns().clear();
        table.getColumns().addAll(examCol, dateCol, statusCol, scoreCol, actionsCol);
        table.setItems(userResults);

        return table;
    }

    private void loadData() {
        try {
            List<Result> allResults = resultDAO.getAll();
            userResults.clear();

            // Filter results for current user
            for (Result result : allResults) {
                if (result.getUserId() == currentUser.getId()) {
                    userResults.add(result);
                }
            }

            // Load exam types for filter
            List<ExamType> examTypes = examTypeDAO.getAll();
            examFilter.getItems().clear();
            examFilter.getItems().add("Tất cả loại thi");
            for (ExamType examType : examTypes) {
                examFilter.getItems().add(examType.getName());
            }

            updateStats();
        } catch (Exception e) {
            showError("Lỗi", "Không thể tải dữ liệu: " + e.getMessage());
        }
    }

    private void filterResults() {
        String selectedExam = examFilter.getValue();
        String selectedStatus = statusFilter.getValue();

        List<Result> allResults = resultDAO.getAll();
        userResults.clear();

        for (Result result : allResults) {
            if (result.getUserId() != currentUser.getId()) {
                continue;
            }

            boolean matchesExam = true;
            boolean matchesStatus = true;

            // Exam filter
            if (selectedExam != null && !selectedExam.equals("Tất cả loại thi")) {
                try {
                    Optional<ExamType> examTypeOpt = examTypeDAO.get(result.getExamTypeId());
                    ExamType examType = examTypeOpt.orElse(null);
                    matchesExam = examType != null && examType.getName().equals(selectedExam);
                } catch (Exception e) {
                    matchesExam = false;
                }
            }

            // Status filter
            if (selectedStatus != null && !selectedStatus.equals("Tất cả")) {
                switch (selectedStatus) {
                    case "Đạt":
                        matchesStatus = result.getStatus() == ResultStatus.PASSED;
                        break;
                    case "Không đạt":
                        matchesStatus = result.getStatus() == ResultStatus.FAILED;
                        break;
                    case "Chờ chấm":
                        matchesStatus = result.getStatus() == ResultStatus.PENDING;
                        break;
                }
            }

            if (matchesExam && matchesStatus) {
                userResults.add(result);
            }
        }
    }

    private void updateStats() {
        int total = userResults.size();
        int passed = 0;
        double totalScore = 0;
        int scoredCount = 0;

        for (Result result : userResults) {
            if (result.getStatus() == ResultStatus.PASSED) {
                passed++;
            }
            if (result.getScore() > 0) {
                totalScore += result.getScore();
                scoredCount++;
            }
        }

        double averageScore = scoredCount > 0 ? totalScore / scoredCount : 0.0;

        totalExamsLabel.setText(String.valueOf(total));
        passedExamsLabel.setText(String.valueOf(passed));
        averageScoreLabel.setText(String.format("%.1f", averageScore));
    }

    private void showResultDetails(Result result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết kết quả");
        alert.setHeaderText(null);

        String examName = "Không xác định";
        try {
            Optional<ExamType> examTypeOpt = examTypeDAO.get(result.getExamTypeId());
            ExamType examType = examTypeOpt.orElse(null);
            examName = examType != null ? examType.getName() : "Không xác định";
        } catch (Exception e) {
            // Use default value
        }

        String statusText;
        switch (result.getStatus()) {
            case PENDING:
                statusText = "Chờ chấm";
                break;
            case PASSED:
                statusText = "Đạt";
                break;
            case FAILED:
                statusText = "Không đạt";
                break;
            default:
                statusText = "Không xác định";
        }

        String content = String.format(
                "Loại thi: %s\n" +
                        "Ngày thi: %s\n" +
                        "Trạng thái: %s\n" +
                        "Điểm: %s",
                examName,
                result.getExamDate().toString(),
                statusText,
                result.getScore() > 0 ? String.format("%.1f", result.getScore()) : "Chưa chấm");

        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleNavItemClick(String itemName) {
        System.out.println("ExamResults navigating to: " + itemName);
        switch (itemName) {
            case "Dashboard":
                handleBack();
                break;
            case "Kết quả":
                // Stay on current screen
                break;
            case "Luyện tập":
                showInfo("Thông báo", "Chức năng luyện tập đang được phát triển");
                break;
            case "Tài liệu":
                showInfo("Thông báo", "Chức năng tài liệu đang được phát triển");
                break;
            case "Thanh toán":
                showInfo("Thông báo", "Chức năng thanh toán đang được phát triển");
                break;
            case "Chứng chỉ":
                showInfo("Thông báo", "Chức năng chứng chỉ đang được phát triển");
                break;
            default:
                System.out.println("Unknown navigation item: " + itemName);
                break;
        }
    }

    private void handleBack() {
        System.out.println("Going back to candidate dashboard");
        // TODO: Navigate back to candidate dashboard
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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
        return scene;
    }
}
