package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.models.Result;
import com.pocitaco.oopsh.models.ExamSchedule;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.enums.ResultStatus;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Colors;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Typography;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Icons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Buttons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Cards;
import javafx.application.Platform;
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

public class ExamGradingScreen {

    private Stage primaryStage;
    private Scene scene;
    private BorderPane mainLayout;
    private VBox navigationRail;
    private StackPane contentArea;

    private final ResultDAO resultDAO;
    private final ExamScheduleDAO examScheduleDAO;
    private final UserDAO userDAO;
    private final ObservableList<Result> pendingResults;

    private TableView<Result> resultsTable;
    private TextField searchField;
    private ComboBox<String> examFilter;
    private ComboBox<String> statusFilter;

    private Label totalPendingLabel;
    private Label gradedTodayLabel;
    private Label averageScoreLabel;

    public ExamGradingScreen() {
        this.resultDAO = new ResultDAO();
        this.examScheduleDAO = new ExamScheduleDAO();
        this.userDAO = new UserDAO();
        this.pendingResults = FXCollections.observableArrayList();
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
        appBar.setPadding(new Insets(16, 24));
        appBar.setAlignment(Pos.CENTER_LEFT);
        appBar.setSpacing(16);

        FontIcon menuIcon = Icons.createMenuIcon();
        menuIcon.setIconSize(24);
        menuIcon.setIconColor(Colors.ON_SURFACE);

        Label title = new Label("Chấm điểm thi");
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
        rail.setPadding(new Insets(16, 8));
        rail.setSpacing(8);

        rail.getChildren().addAll(
                createNavItem("Dashboard", Icons.createHomeIcon(), false),
                createNavItem("Chấm điểm", Icons.createFileDocumentIcon(), true),
                createNavItem("Báo cáo", Icons.createChartBarIcon(), false),
                createNavItem("Lịch thi", Icons.createCalendarIcon(), false),
                createNavItem("Thí sinh", Icons.createAccountGroupIcon(), false));

        return rail;
    }

    private VBox createNavItem(String text, FontIcon icon, boolean selected) {
        VBox navItem = new VBox(4);
        navItem.setAlignment(Pos.CENTER);
        navItem.setPadding(new Insets(12, 8));
        navItem.setBackground(new Background(new BackgroundFill(
                selected ? Colors.PRIMARY_CONTAINER : Colors.TRANSPARENT,
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
        loadGradingContent();
        return content;
    }

    private void loadGradingContent() {
        VBox gradingContent = new VBox();
        gradingContent.setSpacing(24);
        gradingContent.setAlignment(Pos.TOP_LEFT);

        Label pageTitle = new Label("Chấm điểm thi");
        pageTitle.setFont(Typography.HEADLINE_MEDIUM);
        pageTitle.setTextFill(Colors.ON_BACKGROUND);

        HBox statsSection = createStatsSection();
        VBox filtersSection = createFiltersSection();
        VBox tableSection = createTableSection();

        gradingContent.getChildren().addAll(pageTitle, statsSection, filtersSection, tableSection);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(gradingContent);
    }

    private HBox createStatsSection() {
        HBox statsSection = new HBox(16);
        statsSection.setAlignment(Pos.CENTER_LEFT);

        totalPendingLabel = new Label("0");
        totalPendingLabel.setFont(Typography.HEADLINE_LARGE);
        totalPendingLabel.setTextFill(Colors.PRIMARY);

        Label totalPendingDesc = new Label("Chờ chấm điểm");
        totalPendingDesc.setFont(Typography.BODY_MEDIUM);
        totalPendingDesc.setTextFill(Colors.ON_SURFACE_VARIANT);

        VBox totalPendingBox = new VBox(4);
        totalPendingBox.setAlignment(Pos.CENTER_LEFT);
        totalPendingBox.getChildren().addAll(totalPendingLabel, totalPendingDesc);

        gradedTodayLabel = new Label("0");
        gradedTodayLabel.setFont(Typography.HEADLINE_LARGE);
        gradedTodayLabel.setTextFill(Colors.SECONDARY);

        Label gradedTodayDesc = new Label("Đã chấm hôm nay");
        gradedTodayDesc.setFont(Typography.BODY_MEDIUM);
        gradedTodayDesc.setTextFill(Colors.ON_SURFACE_VARIANT);

        VBox gradedTodayBox = new VBox(4);
        gradedTodayBox.setAlignment(Pos.CENTER_LEFT);
        gradedTodayBox.getChildren().addAll(gradedTodayLabel, gradedTodayDesc);

        averageScoreLabel = new Label("0.0");
        averageScoreLabel.setFont(Typography.HEADLINE_LARGE);
        averageScoreLabel.setTextFill(Colors.TERTIARY);

        Label averageScoreDesc = new Label("Điểm trung bình");
        averageScoreDesc.setFont(Typography.BODY_MEDIUM);
        averageScoreDesc.setTextFill(Colors.ON_SURFACE_VARIANT);

        VBox averageScoreBox = new VBox(4);
        averageScoreBox.setAlignment(Pos.CENTER_LEFT);
        averageScoreBox.getChildren().addAll(averageScoreLabel, averageScoreDesc);

        statsSection.getChildren().addAll(totalPendingBox, gradedTodayBox, averageScoreBox);
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

        GridPane filterGrid = new GridPane();
        filterGrid.setHgap(16);
        filterGrid.setVgap(16);

        // Search field
        VBox searchBox = new VBox(4);
        Label searchLabel = new Label("Tìm kiếm");
        searchLabel.setFont(Typography.LABEL_MEDIUM);
        searchLabel.setTextFill(Colors.ON_SURFACE);

        searchField = new TextField();
        searchField.setPromptText("Tìm theo tên thí sinh...");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterResults());

        searchBox.getChildren().addAll(searchLabel, searchField);

        // Exam filter
        VBox examBox = new VBox(4);
        Label examLabel = new Label("Kỳ thi");
        examLabel.setFont(Typography.LABEL_MEDIUM);
        examLabel.setTextFill(Colors.ON_SURFACE);

        examFilter = new ComboBox<>();
        examFilter.setPromptText("Tất cả kỳ thi");
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
        statusFilter.getItems().addAll("Tất cả", "Chờ chấm", "Đã chấm");
        statusFilter.setValue("Tất cả");
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterResults());

        statusBox.getChildren().addAll(statusLabel, statusFilter);

        filterGrid.add(searchBox, 0, 0);
        filterGrid.add(examBox, 1, 0);
        filterGrid.add(statusBox, 2, 0);

        filtersSection.getChildren().addAll(header, filterGrid);
        return filtersSection;
    }

    private VBox createTableSection() {
        VBox tableSection = Cards.createCard();
        tableSection.setSpacing(16);
        tableSection.setPadding(new Insets(20));

        HBox header = new HBox();
        header.setAlignment(Pos.SPACE_BETWEEN);

        Label tableLabel = new Label("Danh sách kết quả chờ chấm điểm");
        tableLabel.setFont(Typography.TITLE_MEDIUM);
        tableLabel.setTextFill(Colors.ON_SURFACE);

        Button refreshButton = Buttons.createFilledButton("Làm mới", Icons.createRefreshIcon());
        refreshButton.setOnAction(e -> loadData());

        header.getChildren().addAll(tableLabel, refreshButton);

        resultsTable = createResultsTable();

        tableSection.getChildren().addAll(header, resultsTable);
        return tableSection;
    }

    private TableView<Result> createResultsTable() {
        TableView<Result> table = new TableView<>();
        table.setPlaceholder(new Label("Không có kết quả nào chờ chấm điểm"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Candidate name column
        TableColumn<Result, String> candidateCol = new TableColumn<>("Thí sinh");
        candidateCol.setCellValueFactory(data -> {
            try {
                User candidate = userDAO.getUserById(data.getValue().getCandidateId());
                return javafx.beans.property.SimpleStringProperty(
                        candidate != null ? candidate.getFullName() : "Không xác định");
            } catch (Exception e) {
                return javafx.beans.property.SimpleStringProperty("Lỗi");
            }
        });

        // Exam name column
        TableColumn<Result, String> examCol = new TableColumn<>("Kỳ thi");
        examCol.setCellValueFactory(data -> {
            try {
                ExamSchedule exam = examScheduleDAO.getExamScheduleById(data.getValue().getExamScheduleId());
                return javafx.beans.property.SimpleStringProperty(
                        exam != null ? exam.getExamName() : "Không xác định");
            } catch (Exception e) {
                return javafx.beans.property.SimpleStringProperty("Lỗi");
            }
        });

        // Exam date column
        TableColumn<Result, String> dateCol = new TableColumn<>("Ngày thi");
        dateCol.setCellValueFactory(data -> {
            try {
                ExamSchedule exam = examScheduleDAO.getExamScheduleById(data.getValue().getExamScheduleId());
                return javafx.beans.property.SimpleStringProperty(
                        exam != null ? exam.getExamDate().toString() : "Không xác định");
            } catch (Exception e) {
                return javafx.beans.property.SimpleStringProperty("Lỗi");
            }
        });

        // Status column
        TableColumn<Result, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(data -> {
            ResultStatus status = data.getValue().getStatus();
            String statusText = status == ResultStatus.PENDING ? "Chờ chấm" : "Đã chấm";
            return javafx.beans.property.SimpleStringProperty(statusText);
        });

        // Score column
        TableColumn<Result, String> scoreCol = new TableColumn<>("Điểm");
        scoreCol.setCellValueFactory(data -> {
            Double score = data.getValue().getScore();
            String scoreText = score != null ? String.format("%.1f", score) : "Chưa chấm";
            return javafx.beans.property.SimpleStringProperty(scoreText);
        });

        // Actions column
        TableColumn<Result, Void> actionsCol = new TableColumn<>("Thao tác");
        actionsCol.setCellFactory(col -> new TableCell<Result, Void>() {
            private final HBox actionBox = new HBox(8);
            private final Button gradeButton = Buttons.createIconButton(Icons.createFileDocumentIcon());
            private final Button viewButton = Buttons.createIconButton(Icons.createEyeIcon());

            {
                gradeButton.setTooltip(new Tooltip("Chấm điểm"));
                gradeButton.setOnAction(e -> {
                    Result result = getTableView().getItems().get(getIndex());
                    showGradingDialog(result);
                });

                viewButton.setTooltip(new Tooltip("Xem chi tiết"));
                viewButton.setOnAction(e -> {
                    Result result = getTableView().getItems().get(getIndex());
                    showResultDetails(result);
                });

                actionBox.getChildren().addAll(gradeButton, viewButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBox);
            }
        });

        table.getColumns().addAll(candidateCol, examCol, dateCol, statusCol, scoreCol, actionsCol);
        table.setItems(pendingResults);

        return table;
    }

    private void loadData() {
        try {
            List<Result> results = resultDAO.getAllResults();
            pendingResults.clear();
            pendingResults.addAll(results);

            // Load exam names for filter
            List<ExamSchedule> exams = examScheduleDAO.getAllExamSchedules();
            examFilter.getItems().clear();
            examFilter.getItems().add("Tất cả kỳ thi");
            for (ExamSchedule exam : exams) {
                examFilter.getItems().add(exam.getExamName());
            }

            updateStats();
        } catch (Exception e) {
            showError("Lỗi", "Không thể tải dữ liệu: " + e.getMessage());
        }
    }

    private void filterResults() {
        String searchText = searchField.getText().toLowerCase();
        String selectedExam = examFilter.getValue();
        String selectedStatus = statusFilter.getValue();

        List<Result> allResults = resultDAO.getAllResults();
        pendingResults.clear();

        for (Result result : allResults) {
            boolean matchesSearch = true;
            boolean matchesExam = true;
            boolean matchesStatus = true;

            // Search filter
            if (!searchText.isEmpty()) {
                try {
                    User candidate = userDAO.getUserById(result.getCandidateId());
                    String candidateName = candidate != null ? candidate.getFullName() : "";
                    matchesSearch = candidateName.toLowerCase().contains(searchText);
                } catch (Exception e) {
                    matchesSearch = false;
                }
            }

            // Exam filter
            if (selectedExam != null && !selectedExam.equals("Tất cả kỳ thi")) {
                try {
                    ExamSchedule exam = examScheduleDAO.getExamScheduleById(result.getExamScheduleId());
                    matchesExam = exam != null && exam.getExamName().equals(selectedExam);
                } catch (Exception e) {
                    matchesExam = false;
                }
            }

            // Status filter
            if (selectedStatus != null && !selectedStatus.equals("Tất cả")) {
                if (selectedStatus.equals("Chờ chấm")) {
                    matchesStatus = result.getStatus() == ResultStatus.PENDING;
                } else if (selectedStatus.equals("Đã chấm")) {
                    matchesStatus = result.getStatus() == ResultStatus.COMPLETED;
                }
            }

            if (matchesSearch && matchesExam && matchesStatus) {
                pendingResults.add(result);
            }
        }
    }

    private void updateStats() {
        int totalPending = 0;
        int gradedToday = 0;
        double totalScore = 0;
        int gradedCount = 0;

        for (Result result : pendingResults) {
            if (result.getStatus() == ResultStatus.PENDING) {
                totalPending++;
            } else if (result.getStatus() == ResultStatus.COMPLETED) {
                gradedToday++;
                if (result.getScore() != null) {
                    totalScore += result.getScore();
                    gradedCount++;
                }
            }
        }

        double averageScore = gradedCount > 0 ? totalScore / gradedCount : 0.0;

        totalPendingLabel.setText(String.valueOf(totalPending));
        gradedTodayLabel.setText(String.valueOf(gradedToday));
        averageScoreLabel.setText(String.format("%.1f", averageScore));
    }

    private void showGradingDialog(Result result) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Chấm điểm");
        dialog.setHeaderText("Nhập điểm cho thí sinh");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        // Get candidate and exam info
        String candidateName = "Không xác định";
        String examName = "Không xác định";

        try {
            User candidate = userDAO.getUserById(result.getCandidateId());
            candidateName = candidate != null ? candidate.getFullName() : "Không xác định";

            ExamSchedule exam = examScheduleDAO.getExamScheduleById(result.getExamScheduleId());
            examName = exam != null ? exam.getExamName() : "Không xác định";
        } catch (Exception e) {
            // Use default values
        }

        Label infoLabel = new Label(String.format("Thí sinh: %s\nKỳ thi: %s", candidateName, examName));
        infoLabel.setFont(Typography.BODY_MEDIUM);

        Label scoreLabel = new Label("Điểm (0-10):");
        scoreLabel.setFont(Typography.LABEL_MEDIUM);

        TextField scoreField = new TextField();
        scoreField.setPromptText("Nhập điểm từ 0 đến 10");
        if (result.getScore() != null) {
            scoreField.setText(String.valueOf(result.getScore()));
        }

        content.getChildren().addAll(infoLabel, scoreLabel, scoreField);

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String scoreText = scoreField.getText().trim();
                    if (scoreText.isEmpty()) {
                        showError("Lỗi", "Vui lòng nhập điểm");
                        return null;
                    }

                    double score = Double.parseDouble(scoreText);
                    if (score < 0 || score > 10) {
                        showError("Lỗi", "Điểm phải từ 0 đến 10");
                        return null;
                    }

                    return score;
                } catch (NumberFormatException e) {
                    showError("Lỗi", "Điểm không hợp lệ");
                    return null;
                }
            }
            return null;
        });

        Optional<Double> score = dialog.showAndWait();
        score.ifPresent(s -> {
            try {
                result.setScore(s);
                result.setStatus(ResultStatus.COMPLETED);
                resultDAO.updateResult(result);
                loadData();
                showSuccess("Thành công", "Đã chấm điểm thành công");
            } catch (Exception e) {
                showError("Lỗi", "Không thể lưu điểm: " + e.getMessage());
            }
        });
    }

    private void showResultDetails(Result result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết kết quả");
        alert.setHeaderText(null);

        String candidateName = "Không xác định";
        String examName = "Không xác định";
        String examDate = "Không xác định";

        try {
            User candidate = userDAO.getUserById(result.getCandidateId());
            candidateName = candidate != null ? candidate.getFullName() : "Không xác định";

            ExamSchedule exam = examScheduleDAO.getExamScheduleById(result.getExamScheduleId());
            if (exam != null) {
                examName = exam.getExamName();
                examDate = exam.getExamDate().toString();
            }
        } catch (Exception e) {
            // Use default values
        }

        String content = String.format(
                "Thí sinh: %s\n" +
                        "Kỳ thi: %s\n" +
                        "Ngày thi: %s\n" +
                        "Trạng thái: %s\n" +
                        "Điểm: %s",
                candidateName,
                examName,
                examDate,
                result.getStatus() == ResultStatus.PENDING ? "Chờ chấm" : "Đã chấm",
                result.getScore() != null ? String.format("%.1f", result.getScore()) : "Chưa chấm");

        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleNavItemClick(String itemName) {
        System.out.println("ExamGrading navigating to: " + itemName);
        switch (itemName) {
            case "Dashboard":
                handleBack();
                break;
            case "Chấm điểm":
                // Stay on current screen
                break;
            case "Báo cáo":
                // TODO: Navigate to SessionReportsScreen
                showInfo("Thông báo", "Chức năng báo cáo đang được phát triển");
                break;
            case "Lịch thi":
                // TODO: Navigate to ExamScheduleScreen
                showInfo("Thông báo", "Chức năng lịch thi đang được phát triển");
                break;
            case "Thí sinh":
                // TODO: Navigate to CandidatesScreen
                showInfo("Thông báo", "Chức năng quản lý thí sinh đang được phát triển");
                break;
            default:
                System.out.println("Unknown navigation item: " + itemName);
                break;
        }
    }

    private void handleBack() {
        System.out.println("Going back to examiner dashboard");
        // TODO: Navigate back to examiner dashboard
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
        return scene;
    }
}
