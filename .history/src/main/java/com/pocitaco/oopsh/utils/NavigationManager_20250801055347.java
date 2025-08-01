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
    
    private NavigationManager() {}
    
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
            LoginScreen loginScreen = new LoginScreen(primaryStage);
            Scene scene = new Scene(loginScreen);
            primaryStage.setScene(scene);
            primaryStage.show();
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
            switch (currentUser.getRole()) {
                case "ADMIN":
                    navigateToAdminDashboard();
                    break;
                case "EXAMINER":
                    navigateToExaminerDashboard();
                    break;
                case "CANDIDATE":
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
            AdminDashboardScreen screen = getCachedScreen("admin_dashboard", 
                () -> new AdminDashboardScreen(currentUser));
            setScene(screen);
        } catch (Exception e) {
            System.err.println("Error navigating to admin dashboard: " + e.getMessage());
        }
    }
    
    /**
     * Chuyển đến Examiner Dashboard
     */
    public void navigateToExaminerDashboard() {
        try {
            ExaminerDashboardScreen screen = getCachedScreen("examiner_dashboard", 
                () -> new ExaminerDashboardScreen(currentUser));
            setScene(screen);
        } catch (Exception e) {
            System.err.println("Error navigating to examiner dashboard: " + e.getMessage());
        }
    }
    
    /**
     * Chuyển đến Candidate Dashboard
     */
    public void navigateToCandidateDashboard() {
        try {
            CandidateDashboardScreen screen = getCachedScreen("candidate_dashboard", 
                () -> new CandidateDashboardScreen(currentUser));
            setScene(screen);
        } catch (Exception e) {
            System.err.println("Error navigating to candidate dashboard: " + e.getMessage());
        }
    }
    
    /**
     * Chuyển đến Exam Registration Screen
     */
    public void navigateToExamRegistration() {
        try {
            ExamRegistrationScreen screen = getCachedScreen("exam_registration", 
                () -> new ExamRegistrationScreen(currentUser));
            setScene(screen);
        } catch (Exception e) {
            System.err.println("Error navigating to exam registration: " + e.getMessage());
        }
    }
    
    /**
     * Chuyển đến Practice Tests Screen
     */
    public void navigateToPracticeTests() {
        try {
            PracticeTestsScreen screen = getCachedScreen("practice_tests", 
                () -> new PracticeTestsScreen(currentUser));
            setScene(screen);
        } catch (Exception e) {
            System.err.println("Error navigating to practice tests: " + e.getMessage());
        }
    }
    
    /**
     * Chuyển đến Exam Results Screen
     */
    public void navigateToExamResults() {
        try {
            ExamResultsScreen screen = getCachedScreen("exam_results", 
                () -> new ExamResultsScreen(currentUser));
            setScene(screen);
        } catch (Exception e) {
            System.err.println("Error navigating to exam results: " + e.getMessage());
        }
    }
    
    /**
     * Chuyển đến Study Materials Screen
     */
    public void navigateToStudyMaterials() {
        try {
            StudyMaterialsScreen screen = getCachedScreen("study_materials", 
                () -> new StudyMaterialsScreen(currentUser));
            setScene(screen);
        } catch (Exception e) {
            System.err.println("Error navigating to study materials: " + e.getMessage());
        }
    }
    
    /**
     * Chuyển đến Payment Screen
     */
    public void navigateToPayment() {
        try {
            PaymentScreen screen = getCachedScreen("payment", 
                () -> new PaymentScreen(currentUser));
            setScene(screen);
        } catch (Exception e) {
            System.err.println("Error navigating to payment: " + e.getMessage());
        }
    }
    
    /**
     * Chuyển đến User Management Screen (Admin only)
     */
    public void navigateToUserManagement() {
        if (!"ADMIN".equals(currentUser.getRole())) {
            System.err.println("Access denied: User management requires admin privileges");
            return;
        }
        
        try {
            UserManagementScreen screen = getCachedScreen("user_management", 
                () -> new UserManagementScreen(currentUser));
            setScene(screen);
        } catch (Exception e) {
            System.err.println("Error navigating to user management: " + e.getMessage());
        }
    }
    
    /**
     * Chuyển đến Exam Management Screen (Admin only)
     */
    public void navigateToExamManagement() {
        if (!"ADMIN".equals(currentUser.getRole())) {
            System.err.println("Access denied: Exam management requires admin privileges");
            return;
        }
        
        try {
            ExamScheduleManagementScreen screen = getCachedScreen("exam_management", 
                () -> new ExamScheduleManagementScreen(currentUser));
            setScene(screen);
        } catch (Exception e) {
            System.err.println("Error navigating to exam management: " + e.getMessage());
        }
    }
    
    /**
     * Chuyển đến Reports Screen (Admin/Examiner only)
     */
    public void navigateToReports() {
        if (!"ADMIN".equals(currentUser.getRole()) && !"EXAMINER".equals(currentUser.getRole())) {
            System.err.println("Access denied: Reports require admin or examiner privileges");
            return;
        }
        
        try {
            ReportsScreen screen = getCachedScreen("reports", 
                () -> new ReportsScreen(currentUser));
            setScene(screen);
        } catch (Exception e) {
            System.err.println("Error navigating to reports: " + e.getMessage());
        }
    }
    
    /**
     * Chuyển đến Exam Grading Screen (Examiner only)
     */
    public void navigateToExamGrading() {
        if (!"EXAMINER".equals(currentUser.getRole())) {
            System.err.println("Access denied: Exam grading requires examiner privileges");
            return;
        }
        
        try {
            ExamGradingScreen screen = getCachedScreen("exam_grading", 
                () -> new ExamGradingScreen(currentUser));
            setScene(screen);
        } catch (Exception e) {
            System.err.println("Error navigating to exam grading: " + e.getMessage());
        }
    }
    
    /**
     * Logout và chuyển về màn hình đăng nhập
     */
    public void logout() {
        try {
            currentUser = null;
            clearCache();
            navigateToLogin();
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
        }
    }
    
    /**
     * Lấy screen từ cache hoặc tạo mới
     */
    @SuppressWarnings("unchecked")
    private <T> T getCachedScreen(String key, ScreenFactory<T> factory) {
        if (screenCache.containsKey(key)) {
            return (T) screenCache.get(key);
        }
        
        T screen = factory.create();
        screenCache.put(key, screen);
        return screen;
    }
    
    /**
     * Set scene cho primary stage
     */
    private void setScene(Object screen) {
        if (screen instanceof javafx.scene.Parent) {
            Scene scene = new Scene((javafx.scene.Parent) screen);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }
    
    /**
     * Xóa cache
     */
    public void clearCache() {
        screenCache.clear();
    }
    
    /**
     * Xóa một screen cụ thể khỏi cache
     */
    public void clearScreenCache(String key) {
        screenCache.remove(key);
    }
    
    /**
     * Interface cho screen factory
     */
    @FunctionalInterface
    private interface ScreenFactory<T> {
        T create();
    }
} 