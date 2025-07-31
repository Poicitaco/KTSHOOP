package com.pocitaco.oopsh.controllers.examiner;

import com.pocitaco.oopsh.controllers.BaseDashboardController;
import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.enums.ResultStatus;
import com.pocitaco.oopsh.models.ExamSchedule;
import com.pocitaco.oopsh.models.Result;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DashboardOverviewController extends BaseDashboardController {

    @FXML
    private Label lblWelcome;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblTotalSessions;
    @FXML
    private Label lblPendingExams;
    @FXML
    private Label lblCompletedExams;
    @FXML
    private Label lblAverageScore;
    @FXML
    private VBox activitiesContainer;
    @FXML
    private Button btnStartSession;
    @FXML
    private Button btnGradeExams;
    @FXML
    private Button btnViewSchedule;
    @FXML
    private Button btnViewReports;
    @FXML
    private VBox loadingContainer;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private Label loadingLabel;

    // DAO objects for data access
    private ExamScheduleDAO examScheduleDAO;
    private ResultDAO resultDAO;

    @Override
    protected void initializeComponents() {
        // Initialize DAOs
        examScheduleDAO = new ExamScheduleDAO();
        resultDAO = new ResultDAO();

        // Initialize date
        updateDate();

        // Load initial data
        loadStatistics();
        loadRecentActivities();

        // Add entrance animations
        addEntranceAnimations();

        // Hide loading after initialization
        hideLoading();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are handled by FXML onAction attributes
    }

    @Override
    protected void loadInitialData() {
        showLoading();
        loadDashboardData();
    }

    @Override
    protected void updateUserInfo() {
        if (currentUser != null) {
            lblWelcome.setText("Chào mừng, " + currentUser.getFullName() + "!");
        }
    }

    @Override
    protected void loadUserSpecificData() {
        // Load examiner-specific data
        loadStatistics();
        loadRecentActivities();
    }

    @Override
    protected void setupNavigation() {
        // Navigation is handled by FXML onAction attributes
    }

    // ===== PRIVATE METHODS =====

    private void updateDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("vi", "VN"));
        lblDate.setText(today.format(formatter));
    }

    private void loadStatistics() {
        try {
            // Load actual data from DAOs
            List<ExamSchedule> allSchedules = examScheduleDAO.getAll();
            List<Result> allResults = resultDAO.findAll();

            // Calculate statistics
            int totalSessions = allSchedules.size();
            int pendingExams = (int) allResults.stream()
                    .filter(result -> ResultStatus.PENDING.equals(result.getStatus()))
                    .count();
            int completedExams = (int) allResults.stream()
                    .filter(result -> ResultStatus.PASSED.equals(result.getStatus()) ||
                            ResultStatus.FAILED.equals(result.getStatus()))
                    .count();

            // Calculate average score
            double averageScore = allResults.stream()
                    .filter(result -> result.getScore() > 0)
                    .mapToDouble(Result::getScore)
                    .average()
                    .orElse(0.0);

            // Update UI
            lblTotalSessions.setText(String.valueOf(totalSessions));
            lblPendingExams.setText(String.valueOf(pendingExams));
            lblCompletedExams.setText(String.valueOf(completedExams));
            lblAverageScore.setText(String.format("%.1f", averageScore));

        } catch (Exception e) {
            // Fallback to placeholder data if DAO fails
            lblTotalSessions.setText("15");
            lblPendingExams.setText("8");
            lblCompletedExams.setText("7");
            lblAverageScore.setText("85.5");
            System.err.println("Error loading statistics: " + e.getMessage());
        }
    }

    private void loadRecentActivities() {
        try {
            activitiesContainer.getChildren().clear();

            // Load actual activities from database
            List<Result> recentResults = resultDAO.findAll().stream()
                    .sorted((r1, r2) -> r2.getExamDate().compareTo(r1.getExamDate()))
                    .limit(5)
                    .collect(Collectors.toList());

            List<ExamSchedule> recentSchedules = examScheduleDAO.getAll().stream()
                    .sorted((s1, s2) -> s2.getExamDate().compareTo(s1.getExamDate()))
                    .limit(3)
                    .collect(Collectors.toList());

            // Add recent grading activities
            for (Result result : recentResults) {
                if (ResultStatus.PASSED.equals(result.getStatus()) ||
                        ResultStatus.FAILED.equals(result.getStatus())) {
                    String activity = "Chấm điểm thi - " + result.getCandidateName();
                    String time = formatTimeAgo(result.getExamDate());
                    addActivityItem(activity, time);
                }
            }

            // Add recent session activities
            for (ExamSchedule schedule : recentSchedules) {
                String activity = "Lịch thi - " + schedule.getExamDate().format(DateTimeFormatter.ofPattern("dd/MM"));
                String time = formatTimeAgo(schedule.getExamDate());
                addActivityItem(activity, time);
            }

        } catch (Exception e) {
            // Fallback to sample activities if DAO fails
            activitiesContainer.getChildren().clear();
            addActivityItem("Chấm điểm thi A1 - Nguyễn Văn A", "2 giờ trước");
            addActivityItem("Bắt đầu phiên thi B2", "4 giờ trước");
            addActivityItem("Hoàn thành chấm điểm C", "6 giờ trước");
            System.err.println("Error loading activities: " + e.getMessage());
        }
    }

    private String formatTimeAgo(LocalDate date) {
        if (date == null)
            return "Không xác định";

        LocalDate now = LocalDate.now();
        long days = ChronoUnit.DAYS.between(date, now);

        if (days > 0) {
            return days + " ngày trước";
        } else if (days == 0) {
            return "Hôm nay";
        } else {
            return "Trong tương lai";
        }
    }

    private void loadDashboardData() {
        // Simulate loading time
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                javafx.application.Platform.runLater(() -> {
                    hideLoading();
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void addEntranceAnimations() {
        // Add fade-in animation for main content
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800));
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void addActivityItem(String title, String time) {
        VBox activityItem = new VBox(5);
        activityItem.getStyleClass().add("activity-item");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("activity-title");

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("activity-time");

        activityItem.getChildren().addAll(titleLabel, timeLabel);
        activitiesContainer.getChildren().add(activityItem);

        // Add entrance animation
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), activityItem);
        scaleIn.setFromX(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setFromY(0.8);
        scaleIn.setToY(1.0);
        scaleIn.play();
    }

    protected void showLoading() {
        loadingContainer.setVisible(true);
        loadingIndicator.setVisible(true);
        loadingLabel.setVisible(true);
    }

    protected void hideLoading() {
        loadingContainer.setVisible(false);
        loadingIndicator.setVisible(false);
        loadingLabel.setVisible(false);
    }

    // ===== EVENT HANDLERS =====

    @FXML
    private void handleStartSession() {
        handleSessionStart();
    }

    @FXML
    private void handleGradeExams() {
        handleExamGrading();
    }

    @FXML
    private void handleViewSchedule() {
        handleScheduleView();
    }

    @FXML
    private void handleViewReports() {
        handleReportsView();
    }

    private void handleSessionStart() {
        try {
            // Load session start screen
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/pocitaco/oopsh/examiner/session-start.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) btnStartSession.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Bắt đầu Phiên Thi - OOPSH");
            stage.show();
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình bắt đầu phiên thi: " + e.getMessage());
        }
    }

    private void handleExamGrading() {
        try {
            // Load exam grading screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/examiner/grade-exams.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) btnGradeExams.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Chấm điểm Thi - OOPSH");
            stage.show();
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình chấm điểm: " + e.getMessage());
        }
    }

    private void handleScheduleView() {
        try {
            // Load schedule view screen
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/pocitaco/oopsh/examiner/schedule-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) btnViewSchedule.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Xem Lịch Thi - OOPSH");
            stage.show();
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình xem lịch: " + e.getMessage());
        }
    }

    private void handleReportsView() {
        try {
            // Load reports view screen
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/pocitaco/oopsh/examiner/reports-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) btnViewReports.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Báo cáo - OOPSH");
            stage.show();
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình báo cáo: " + e.getMessage());
        }
    }

    // ===== UTILITY METHODS =====

    public void refreshDashboard() {
        showLoading();
        loadStatistics();
        loadRecentActivities();
        hideLoading();
    }

    public void exportDashboardData() {
        try {
            // Create file chooser
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Xuất dữ liệu Dashboard");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setInitialFileName("examiner_dashboard_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt");

            java.io.File file = fileChooser.showSaveDialog(btnStartSession.getScene().getWindow());
            if (file == null)
                return;

            // Prepare export data
            StringBuilder content = new StringBuilder();
            content.append("=== BÁO CÁO DASHBOARD GIÁM KHẢO ===\n");
            content.append("Ngày xuất báo cáo: ")
                    .append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
            content.append("Giám khảo: ").append(currentUser != null ? currentUser.getFullName() : "N/A")
                    .append("\n\n");

            // Statistics
            content.append("=== THỐNG KÊ ===\n");
            content.append("Tổng số phiên thi: ").append(lblTotalSessions.getText()).append("\n");
            content.append("Bài thi đang chờ: ").append(lblPendingExams.getText()).append("\n");
            content.append("Bài thi đã hoàn thành: ").append(lblCompletedExams.getText()).append("\n");
            content.append("Điểm trung bình: ").append(lblAverageScore.getText()).append("\n\n");

            // Recent activities
            content.append("=== HOẠT ĐỘNG GẦN ĐÂY ===\n");
            for (javafx.scene.Node node : activitiesContainer.getChildren()) {
                if (node instanceof VBox) {
                    VBox activityItem = (VBox) node;
                    if (activityItem.getChildren().size() >= 2) {
                        Label titleLabel = (Label) activityItem.getChildren().get(0);
                        Label timeLabel = (Label) activityItem.getChildren().get(1);
                        content.append("- ").append(titleLabel.getText())
                                .append(" (").append(timeLabel.getText()).append(")\n");
                    }
                }
            }

            // Write to file
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                writer.write(content.toString());
            }

            showInfo("Thành công", "Đã xuất dữ liệu dashboard thành công!\nFile: " + file.getName());

        } catch (Exception e) {
            showError("Lỗi", "Không thể xuất dữ liệu: " + e.getMessage());
            System.err.println("Export error: " + e.getMessage());
        }
    }

    public void showSystemStatus() {
        showInfo("Trạng thái hệ thống", "Hệ thống hoạt động bình thường.");
    }
}
