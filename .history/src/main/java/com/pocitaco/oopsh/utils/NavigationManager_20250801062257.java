package com.pocitaco.oopsh.utils;

import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.ui.screens.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * Navigation Manager - Quản lý chuyển đổi màn hình
 * Singleton pattern để đảm bảo một instance duy nhất
 * 
 * @author OOPSH Team
 * @version 1.0
 */
public class NavigationManager {

    private static NavigationManager instance;
    private Stage primaryStage;
    private User currentUser;
    private final Map<String, Object> screenCache = new HashMap<>();
    private final Logger logger = Logger.getInstance();
    private final PerformanceMonitor performanceMonitor = PerformanceMonitor.getInstance();

    private NavigationManager() {
    }

    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Chuyển đến màn hình đăng nhập
     */
    public void navigateToLogin() {
        try {
            // LoginScreen sẽ tự quản lý scene của nó
            System.out.println("Navigating to login screen");
            clearCache();
        } catch (Exception e) {
            System.err.println("Error navigating to login: " + e.getMessage());
        }
    }

    /**
     * Chuyển đến dashboard dựa trên role của user
     */
    public void navigateToDashboard() {
        if (currentUser == null) {
            navigateToLogin();
            return;
        }

        try {
            UserRole role = currentUser.getRole();
            switch (role) {
                case ADMIN:
                    navigateToAdminDashboard();
                    break;
                case EXAMINER:
                    navigateToExaminerDashboard();
                    break;
                case CANDIDATE:
                    navigateToCandidateDashboard();
                    break;
                default:
                    navigateToLogin();
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error navigating to dashboard: " + e.getMessage());
            navigateToLogin();
        }
    }

    /**
     * Chuyển đến Admin Dashboard
     */
    public void navigateToAdminDashboard() {
        try {
            performanceMonitor.startOperation("NavigateToAdminDashboard");
            logger.userAction(currentUser.getUsername(), "Navigation", "Navigate to Admin Dashboard");

            String cacheKey = "admin_dashboard_" + currentUser.getId();
            AdminDashboardScreen adminScreen = (AdminDashboardScreen) screenCache.get(cacheKey);

            if (adminScreen == null) {
                adminScreen = new AdminDashboardScreen(primaryStage, currentUser);
                screenCache.put(cacheKey, adminScreen);
                logger.info("Created new AdminDashboardScreen instance");
            }

            adminScreen.show();
            logger.info("Successfully navigated to Admin Dashboard");
            performanceMonitor.endOperation("NavigateToAdminDashboard");
        } catch (Exception e) {
            logger.error("Error navigating to admin dashboard", e);
            performanceMonitor.endOperation("NavigateToAdminDashboard");
            throw new RuntimeException("Failed to navigate to Admin Dashboard", e);
        }
    }

    /**
     * Chuyển đến Examiner Dashboard
     */
    public void navigateToExaminerDashboard() {
        try {
            performanceMonitor.startOperation("NavigateToExaminerDashboard");
            logger.userAction(currentUser.getUsername(), "Navigation", "Navigate to Examiner Dashboard");

            String cacheKey = "examiner_dashboard_" + currentUser.getId();
            ExaminerDashboardScreen examinerScreen = (ExaminerDashboardScreen) screenCache.get(cacheKey);

            if (examinerScreen == null) {
                examinerScreen = new ExaminerDashboardScreen(primaryStage, currentUser);
                screenCache.put(cacheKey, examinerScreen);
                logger.info("Created new ExaminerDashboardScreen instance");
            }

            examinerScreen.show();
            logger.info("Successfully navigated to Examiner Dashboard");
            performanceMonitor.endOperation("NavigateToExaminerDashboard");
        } catch (Exception e) {
            logger.error("Error navigating to examiner dashboard", e);
            performanceMonitor.endOperation("NavigateToExaminerDashboard");
            throw new RuntimeException("Failed to navigate to Examiner Dashboard", e);
        }
    }

    /**
     * Chuyển đến Candidate Dashboard
     */
    public void navigateToCandidateDashboard() {
        try {
            performanceMonitor.startOperation("NavigateToCandidateDashboard");
            logger.userAction(currentUser.getUsername(), "Navigation", "Navigate to Candidate Dashboard");
            
            String cacheKey = "candidate_dashboard_" + currentUser.getId();
            CandidateDashboardScreen candidateScreen = (CandidateDashboardScreen) screenCache.get(cacheKey);
            
            if (candidateScreen == null) {
                candidateScreen = new CandidateDashboardScreen(primaryStage, currentUser);
                screenCache.put(cacheKey, candidateScreen);
                logger.info("Created new CandidateDashboardScreen instance");
            }
            
            candidateScreen.show();
            logger.info("Successfully navigated to Candidate Dashboard");
            performanceMonitor.endOperation("NavigateToCandidateDashboard");
        } catch (Exception e) {
            logger.error("Error navigating to candidate dashboard", e);
            performanceMonitor.endOperation("NavigateToCandidateDashboard");
            throw new RuntimeException("Failed to navigate to Candidate Dashboard", e);
        }
    }

    /**
     * Chuyển đến Exam Registration Screen
     */
    public void navigateToExamRegistration() {
        try {
            performanceMonitor.startOperation("NavigateToExamRegistration");
            logger.userAction(currentUser.getUsername(), "Navigation", "Navigate to Exam Registration");
            
            String cacheKey = "exam_registration_" + currentUser.getId();
            ExamRegistrationScreen examRegistrationScreen = (ExamRegistrationScreen) screenCache.get(cacheKey);
            
            if (examRegistrationScreen == null) {
                examRegistrationScreen = new ExamRegistrationScreen(currentUser);
                screenCache.put(cacheKey, examRegistrationScreen);
                logger.info("Created new ExamRegistrationScreen instance");
            }
            
            // Create scene and show
            Scene scene = new Scene(examRegistrationScreen, 1200, 800);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            logger.info("Successfully navigated to Exam Registration");
            performanceMonitor.endOperation("NavigateToExamRegistration");
        } catch (Exception e) {
            logger.error("Error navigating to exam registration", e);
            performanceMonitor.endOperation("NavigateToExamRegistration");
            throw new RuntimeException("Failed to navigate to Exam Registration", e);
        }
    }

    /**
     * Chuyển đến Practice Tests Screen
     */
    public void navigateToPracticeTests() {
        try {
            System.out.println("Navigating to Practice Tests for user: " + currentUser.getUsername());
            // TODO: Implement actual navigation when screen constructors are available
        } catch (Exception e) {
            System.err.println("Error navigating to practice tests: " + e.getMessage());
        }
    }

    /**
     * Chuyển đến Exam Results Screen
     */
    public void navigateToExamResults() {
        try {
            System.out.println("Navigating to Exam Results for user: " + currentUser.getUsername());
            // TODO: Implement actual navigation when screen constructors are available
        } catch (Exception e) {
            System.err.println("Error navigating to exam results: " + e.getMessage());
        }
    }

    /**
     * Chuyển đến Study Materials Screen
     */
    public void navigateToStudyMaterials() {
        try {
            System.out.println("Navigating to Study Materials for user: " + currentUser.getUsername());
            // TODO: Implement actual navigation when screen constructors are available
        } catch (Exception e) {
            System.err.println("Error navigating to study materials: " + e.getMessage());
        }
    }

    /**
     * Chuyển đến Payment Screen
     */
    public void navigateToPayment() {
        try {
            System.out.println("Navigating to Payment for user: " + currentUser.getUsername());
            // TODO: Implement actual navigation when screen constructors are available
        } catch (Exception e) {
            System.err.println("Error navigating to payment: " + e.getMessage());
        }
    }

    /**
     * Chuyển đến User Management Screen (Admin only)
     */
    public void navigateToUserManagement() {
        if (!UserRole.ADMIN.equals(currentUser.getRole())) {
            System.err.println("Access denied: User management requires admin privileges");
            return;
        }

        try {
            System.out.println("Navigating to User Management for admin: " + currentUser.getUsername());
            // TODO: Implement actual navigation when screen constructors are available
        } catch (Exception e) {
            System.err.println("Error navigating to user management: " + e.getMessage());
        }
    }

    /**
     * Chuyển đến Exam Management Screen (Admin only)
     */
    public void navigateToExamManagement() {
        if (!UserRole.ADMIN.equals(currentUser.getRole())) {
            System.err.println("Access denied: Exam management requires admin privileges");
            return;
        }

        try {
            System.out.println("Navigating to Exam Management for admin: " + currentUser.getUsername());
            // TODO: Implement actual navigation when screen constructors are available
        } catch (Exception e) {
            System.err.println("Error navigating to exam management: " + e.getMessage());
        }
    }

    /**
     * Chuyển đến Reports Screen (Admin/Examiner only)
     */
    public void navigateToReports() {
        if (!UserRole.ADMIN.equals(currentUser.getRole()) && !UserRole.EXAMINER.equals(currentUser.getRole())) {
            System.err.println("Access denied: Reports require admin or examiner privileges");
            return;
        }

        try {
            System.out.println("Navigating to Reports for user: " + currentUser.getUsername());
            // TODO: Implement actual navigation when screen constructors are available
        } catch (Exception e) {
            System.err.println("Error navigating to reports: " + e.getMessage());
        }
    }

    /**
     * Chuyển đến Exam Grading Screen (Examiner only)
     */
    public void navigateToExamGrading() {
        if (!UserRole.EXAMINER.equals(currentUser.getRole())) {
            System.err.println("Access denied: Exam grading requires examiner privileges");
            return;
        }

        try {
            System.out.println("Navigating to Exam Grading for examiner: " + currentUser.getUsername());
            // TODO: Implement actual navigation when screen constructors are available
        } catch (Exception e) {
            System.err.println("Error navigating to exam grading: " + e.getMessage());
        }
    }

    /**
     * Logout và chuyển về màn hình đăng nhập
     */
    public void logout() {
        try {
            System.out.println("Logging out user: " + (currentUser != null ? currentUser.getUsername() : "Unknown"));
            currentUser = null;
            clearCache();
            navigateToLogin();
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
        }
    }

    /**
     * Xóa cache
     */
    public void clearCache() {
        screenCache.clear();
        System.out.println("Screen cache cleared");
    }

    /**
     * Xóa một screen cụ thể khỏi cache
     */
    public void clearScreenCache(String key) {
        screenCache.remove(key);
        System.out.println("Cleared screen cache for key: " + key);
    }

    /**
     * Get primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}