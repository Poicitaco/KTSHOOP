package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseDashboardController;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.utils.DialogUtils;
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

/**
 * Controller for Admin Dashboard Overview
 * Displays statistics and quick actions for administrators
 */
public class AdminDashboardOverviewController extends BaseDashboardController {

    @FXML
    private Label lblWelcome;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblTotalUsers;
    @FXML
    private Label lblTotalExams;
    @FXML
    private Label lblActiveExams;
    @FXML
    private Label lblTotalRevenue;
    @FXML
    private VBox activitiesContainer;
    @FXML
    private Button btnCreateUser;
    @FXML
    private Button btnCreateExam;
    @FXML
    private Button btnViewReports;
    @FXML
    private Button btnSystemSettings;
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
        // Load admin-specific data
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
        lblTotalUsers.setText("1,234");
        lblTotalExams.setText("567");
        lblActiveExams.setText("23");
        lblTotalRevenue.setText("2.5M VNĐ");
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
            lblTotalUsers.getParent().getParent(),
            lblTotalExams.getParent().getParent(),
            lblActiveExams.getParent().getParent(),
            lblTotalRevenue.getParent().getParent()
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
    private void handleCreateUser() {
        try {
            // TODO: Navigate to user creation screen
            showInfo("Thông báo", "Chức năng tạo người dùng sẽ được triển khai trong phiên bản tiếp theo.");
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình tạo người dùng: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateExam() {
        try {
            // TODO: Navigate to exam creation screen
            showInfo("Thông báo", "Chức năng tạo kỳ thi sẽ được triển khai trong phiên bản tiếp theo.");
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình tạo kỳ thi: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewReports() {
        try {
            // TODO: Navigate to reports screen
            showInfo("Thông báo", "Chức năng xem báo cáo sẽ được triển khai trong phiên bản tiếp theo.");
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình báo cáo: " + e.getMessage());
        }
    }

    @FXML
    private void handleSystemSettings() {
        try {
            // TODO: Navigate to system settings screen
            showInfo("Thông báo", "Chức năng cài đặt hệ thống sẽ được triển khai trong phiên bản tiếp theo.");
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình cài đặt: " + e.getMessage());
        }
    }

    private void handleUserManagement() {
        try {
            // Load user management screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/admin/user-management.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) btnCreateUser.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Quản lý Người dùng - OOPSH");
            stage.show();
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình quản lý người dùng: " + e.getMessage());
        }
    }

    private void handleExamManagement() {
        try {
            // Load exam management screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/admin/exam-schedule.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) btnCreateExam.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Quản lý Lịch Thi - OOPSH");
            stage.show();
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình quản lý lịch thi: " + e.getMessage());
        }
    }

    private void handleReports() {
        try {
            // Load reports screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/admin/exam-results.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) btnViewReports.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Báo cáo Kết quả Thi - OOPSH");
            stage.show();
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình báo cáo: " + e.getMessage());
        }
    }

    private void handleSettings() {
        try {
            // Load settings screen (exam types management)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/admin/exam-types.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) btnSystemSettings.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Quản lý Loại Thi - OOPSH");
            stage.show();
        } catch (Exception e) {
            showError("Lỗi", "Không thể mở màn hình cài đặt: " + e.getMessage());
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
     * Show system status
     */
    public void showSystemStatus() {
        StringBuilder status = new StringBuilder();
        status.append("Trạng thái hệ thống:\n\n");
        status.append("• Cơ sở dữ liệu: Hoạt động bình thường\n");
        status.append("• Dịch vụ web: Hoạt động bình thường\n");
        status.append("• Sao lưu dữ liệu: Cập nhật lần cuối 2 giờ trước\n");
        status.append("• Bảo mật: Tất cả hệ thống an toàn\n");
        
        showInfo("Trạng thái hệ thống", status.toString());
    }
} 
