package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseDashboardController;
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
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DashboardOverviewController extends BaseDashboardController {

    @FXML
    private Label lblWelcome;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblRegisteredExams;
    @FXML
    private Label lblCompletedExams;
    @FXML
    private Label lblPendingResults;
    @FXML
    private Label lblAverageScore;
    @FXML
    private VBox activitiesContainer;
    @FXML
    private Button btnRegisterExam;
    @FXML
    private Button btnViewResults;
    @FXML
    private Button btnStudyMaterials;
    @FXML
    private Button btnPracticeTests;
    @FXML
    private VBox loadingContainer;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private Label loadingLabel;

    @Override
    protected void initializeComponents() {
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
        // Load candidate-specific data
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
        // TODO: Replace with actual data from DAO
        lblRegisteredExams.setText("3");
        lblCompletedExams.setText("2");
        lblPendingResults.setText("1");
        lblAverageScore.setText("87.5");
    }

    private void loadRecentActivities() {
        // TODO: Load actual activities from database
        activitiesContainer.getChildren().clear();

        // Add sample activities
        addActivityItem("Đăng ký thi A1", "1 ngày trước");
        addActivityItem("Hoàn thành thi B2", "3 ngày trước");
        addActivityItem("Xem kết quả thi C", "5 ngày trước");
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
    private void handleRegisterExam() {
        handleExamRegistration();
    }

    @FXML
    private void handleViewResults() {
        handleResultsView();
    }

    @FXML
    private void handleStudyMaterials() {
        handleMaterialsView();
    }

    @FXML
    private void handlePracticeTests() {
        handlePracticeView();
    }

    private void handleExamRegistration() {
        try {
            // Load exam registration screen
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/pocitaco/oopsh/candidate/register-exam.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) btnRegisterExam.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Đăng ký Thi - OOPSH");
            stage.show();
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình đăng ký thi: " + e.getMessage());
        }
    }

    private void handleResultsView() {
        try {
            // Load results view screen
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/pocitaco/oopsh/candidate/view-results.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) btnViewResults.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Xem Kết quả - OOPSH");
            stage.show();
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình xem kết quả: " + e.getMessage());
        }
    }

    private void handleMaterialsView() {
        try {
            // Load study materials screen
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/pocitaco/oopsh/candidate/study-materials.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) btnStudyMaterials.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Tài liệu Học tập - OOPSH");
            stage.show();
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình tài liệu học tập: " + e.getMessage());
        }
    }

    private void handlePracticeView() {
        try {
            // Load practice tests screen
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/pocitaco/oopsh/candidate/practice-tests.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) btnPracticeTests.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Bài thi Thử - OOPSH");
            stage.show();
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình bài thi thử: " + e.getMessage());
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
        // TODO: Implement export functionality
        showInfo("Xuất dữ liệu", "Chức năng xuất dữ liệu sẽ được triển khai trong phiên bản tiếp theo.");
    }

    public void showSystemStatus() {
        showInfo("Trạng thái hệ thống", "Hệ thống hoạt động bình thường.");
    }
}
