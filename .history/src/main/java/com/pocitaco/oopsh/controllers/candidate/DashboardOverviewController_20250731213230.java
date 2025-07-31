package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseDashboardController;
import com.pocitaco.oopsh.models.User;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Controller for Candidate Dashboard Overview
 * Displays candidate-specific information and quick actions
 */
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
    private Label lblUpcomingExams;
    @FXML
    private Label lblStudyProgress;
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
        super.initializeComponents();
        
        // Set loading container reference
        this.loadingContainer = loadingContainer;
        this.loadingIndicator = loadingIndicator;
        this.loadingLabel = loadingLabel;
        
        // Update date
        updateDate();
        
        // Add entrance animations
        addEntranceAnimations();
    }

    @Override
    protected void setupEventHandlers() {
        // Button event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        showLoading("Đang tải dữ liệu dashboard...");
        
        // Simulate loading time
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(Duration.millis(1000), e -> {
                loadDashboardData();
                hideLoading();
            })
        );
        timeline.play();
    }

    @Override
    protected void updateUserInfo() {
        User user = getCurrentUser();
        if (user != null) {
            lblWelcome.setText("Chào mừng, " + user.getFullName() + "!");
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
        // Navigation is handled by MainLayoutController
    }

    /**
     * Update current date display
     */
    private void updateDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
        lblDate.setText("Hôm nay là " + today.format(formatter));
    }

    /**
     * Load dashboard statistics
     */
    private void loadStatistics() {
        // TODO: Replace with actual data from DAO
        lblRegisteredExams.setText("3");
        lblCompletedExams.setText("1");
        lblUpcomingExams.setText("2");
        lblStudyProgress.setText("75%");
    }

    /**
     * Load recent activities
     */
    private void loadRecentActivities() {
        // TODO: Load actual activities from database
        // For now, activities are static in FXML
    }

    /**
     * Load dashboard data
     */
    private void loadDashboardData() {
        loadStatistics();
        loadRecentActivities();
    }

    /**
     * Add entrance animations to dashboard elements
     */
    private void addEntranceAnimations() {
        // Animate statistics cards
        javafx.scene.Node[] cards = {
            lblRegisteredExams.getParent().getParent(),
            lblCompletedExams.getParent().getParent(),
            lblUpcomingExams.getParent().getParent(),
            lblStudyProgress.getParent().getParent()
        };

        for (int i = 0; i < cards.length; i++) {
            final int index = i;
            javafx.animation.Timeline delay = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.millis(200 * index), e -> {
                    addEntranceAnimation(cards[index]);
                })
            );
            delay.play();
        }
    }

    // ===== EVENT HANDLERS =====

    @FXML
    private void handleRegisterExam() {
        try {
            // TODO: Navigate to exam registration screen
            showInfo("Thông báo", "Chức năng đăng ký thi sẽ được triển khai trong phiên bản tiếp theo.");
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình đăng ký thi: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewResults() {
        try {
            // TODO: Navigate to results view screen
            showInfo("Thông báo", "Chức năng xem kết quả sẽ được triển khai trong phiên bản tiếp theo.");
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình xem kết quả: " + e.getMessage());
        }
    }

    @FXML
    private void handleStudyMaterials() {
        try {
            // TODO: Navigate to study materials screen
            showInfo("Thông báo", "Chức năng tài liệu học tập sẽ được triển khai trong phiên bản tiếp theo.");
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình tài liệu học tập: " + e.getMessage());
        }
    }

    @FXML
    private void handlePracticeTests() {
        try {
            // TODO: Navigate to practice tests screen
            showInfo("Thông báo", "Chức năng bài thi thử sẽ được triển khai trong phiên bản tiếp theo.");
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình bài thi thử: " + e.getMessage());
        }
    }

    // ===== UTILITY METHODS =====

    /**
     * Refresh dashboard data
     */
    public void refreshDashboard() {
        showLoading("Đang làm mới dữ liệu...");
        
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(Duration.millis(800), e -> {
                loadDashboardData();
                hideLoading();
                showInfo("Thành công", "Dữ liệu đã được làm mới!");
            })
        );
        timeline.play();
    }

    /**
     * Export dashboard data
     */
    public void exportDashboardData() {
        try {
            // TODO: Implement export functionality
            showInfo("Thông báo", "Chức năng xuất dữ liệu sẽ được triển khai trong phiên bản tiếp theo.");
        } catch (Exception e) {
            showError("Lỗi", "Không thể xuất dữ liệu: " + e.getMessage());
        }
    }

    /**
     * Show candidate status
     */
    public void showCandidateStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Trạng thái thí sinh:\n\n");
        status.append("• Kỳ thi đã đăng ký: 3 kỳ\n");
        status.append("• Kỳ thi đã hoàn thành: 1 kỳ\n");
        status.append("• Kỳ thi sắp tới: 2 kỳ\n");
        status.append("• Tiến độ học tập: 75%\n");
        status.append("• Trạng thái: Đang học tập\n");
        
        showInfo("Trạng thái thí sinh", status.toString());
    }
}

