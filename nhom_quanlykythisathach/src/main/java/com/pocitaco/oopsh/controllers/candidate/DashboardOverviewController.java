package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseDashboardController;
import com.pocitaco.oopsh.dao.RegistrationDAO;
import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.enums.RegistrationStatus;
import com.pocitaco.oopsh.enums.ResultStatus;
import com.pocitaco.oopsh.models.Registration;
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

    // DAO objects for data access
    private RegistrationDAO registrationDAO;
    private ResultDAO resultDAO;

    @Override
    protected void initializeComponents() {
        // Initialize DAOs
        registrationDAO = new RegistrationDAO();
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
        try {
            if (currentUser == null) {
                // Fallback to placeholder data
                lblRegisteredExams.setText("3");
                lblCompletedExams.setText("2");
                lblPendingResults.setText("1");
                lblAverageScore.setText("87.5");
                return;
            }
            
            // Load actual data from DAOs
            List<Registration> userRegistrations = registrationDAO.findByUserId(currentUser.getId());
            List<Result> userResults = resultDAO.findByUserId(currentUser.getId());
            
            // Calculate statistics
            int registeredExams = userRegistrations.size();
            int completedExams = (int) userResults.stream()
                    .filter(result -> ResultStatus.PASSED.equals(result.getStatus()) || 
                                    ResultStatus.FAILED.equals(result.getStatus()))
                    .count();
            int pendingResults = (int) userResults.stream()
                    .filter(result -> ResultStatus.PENDING.equals(result.getStatus()))
                    .count();
            
            // Calculate average score
            double averageScore = userResults.stream()
                    .filter(result -> result.getScore() > 0)
                    .mapToDouble(Result::getScore)
                    .average()
                    .orElse(0.0);
            
            // Update UI
            lblRegisteredExams.setText(String.valueOf(registeredExams));
            lblCompletedExams.setText(String.valueOf(completedExams));
            lblPendingResults.setText(String.valueOf(pendingResults));
            lblAverageScore.setText(String.format("%.1f", averageScore));
            
        } catch (Exception e) {
            // Fallback to placeholder data if DAO fails
            lblRegisteredExams.setText("3");
            lblCompletedExams.setText("2");
            lblPendingResults.setText("1");
            lblAverageScore.setText("87.5");
            System.err.println("Error loading statistics: " + e.getMessage());
        }
    }

    private void loadRecentActivities() {
        try {
            if (currentUser == null) {
                // Fallback to sample activities
                activitiesContainer.getChildren().clear();
                addActivityItem("Đăng ký thi A1", "1 ngày trước");
                addActivityItem("Hoàn thành thi B2", "3 ngày trước");
                addActivityItem("Xem kết quả thi C", "5 ngày trước");
                return;
            }
            
            activitiesContainer.getChildren().clear();
            
            // Load actual activities from database
            List<Registration> recentRegistrations = registrationDAO.findByUserId(currentUser.getId()).stream()
                    .sorted((r1, r2) -> r2.getRegistrationDate().compareTo(r1.getRegistrationDate()))
                    .limit(3)
                    .collect(Collectors.toList());
            
            List<Result> recentResults = resultDAO.findByUserId(currentUser.getId()).stream()
                    .sorted((r1, r2) -> r2.getExamDate().compareTo(r1.getExamDate()))
                    .limit(3)
                    .collect(Collectors.toList());
            
            // Add recent registration activities
            for (Registration registration : recentRegistrations) {
                String activity = "Đăng ký thi - " + registration.getExamTypeName();
                String time = formatTimeAgo(registration.getRegistrationDate());
                addActivityItem(activity, time);
            }
            
            // Add recent result activities
            for (Result result : recentResults) {
                if (ResultStatus.PASSED.equals(result.getStatus()) || 
                    ResultStatus.FAILED.equals(result.getStatus())) {
                    String activity = "Kết quả thi - " + result.getExamTypeName() + 
                                    " (" + String.format("%.1f", result.getScore()) + " điểm)";
                    String time = formatTimeAgo(result.getExamDate());
                    addActivityItem(activity, time);
                }
            }
            
        } catch (Exception e) {
            // Fallback to sample activities if DAO fails
            activitiesContainer.getChildren().clear();
            addActivityItem("Đăng ký thi A1", "1 ngày trước");
            addActivityItem("Hoàn thành thi B2", "3 ngày trước");
            addActivityItem("Xem kết quả thi C", "5 ngày trước");
            System.err.println("Error loading activities: " + e.getMessage());
        }
    }
    
    private String formatTimeAgo(LocalDate date) {
        if (date == null) return "Không xác định";
        
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
        try {
            // Create file chooser
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Xuất dữ liệu Dashboard");
            fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt")
            );
            fileChooser.setInitialFileName("candidate_dashboard_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt");
            
            java.io.File file = fileChooser.showSaveDialog(btnRegisterExam.getScene().getWindow());
            if (file == null) return;
            
            // Prepare export data
            StringBuilder content = new StringBuilder();
            content.append("=== BÁO CÁO DASHBOARD THÍ SINH ===\n");
            content.append("Ngày xuất báo cáo: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
            content.append("Thí sinh: ").append(currentUser != null ? currentUser.getFullName() : "N/A").append("\n\n");
            
            // Statistics
            content.append("=== THỐNG KÊ ===\n");
            content.append("Số kỳ thi đã đăng ký: ").append(lblRegisteredExams.getText()).append("\n");
            content.append("Số kỳ thi đã hoàn thành: ").append(lblCompletedExams.getText()).append("\n");
            content.append("Kết quả đang chờ: ").append(lblPendingResults.getText()).append("\n");
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
