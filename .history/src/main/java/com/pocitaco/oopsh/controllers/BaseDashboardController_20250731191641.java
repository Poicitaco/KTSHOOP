package com.pocitaco.oopsh.controllers;

import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.utils.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Abstract base class for all dashboard controllers
 * Implements common dashboard functionality using Template Method pattern
 */
public abstract class BaseDashboardController extends BaseController {

    protected User currentUser;
    protected VBox loadingContainer;
    protected ProgressIndicator loadingIndicator;
    protected Label loadingLabel;

    /**
     * Set the current logged-in user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserInfo();
        loadUserSpecificData();
    }

    /**
     * Template method to update user information display
     * Subclasses should override to show user-specific info
     */
    protected abstract void updateUserInfo();

    /**
     * Template method to load user-specific data
     * Subclasses should override to load role-specific data
     */
    protected abstract void loadUserSpecificData();

    /**
     * Template method to setup dashboard navigation
     * Subclasses should override to setup their specific navigation
     */
    protected abstract void setupNavigation();

    /**
     * Template method to handle logout
     */
    protected void handleLogout() {
        if (showConfirmation("Đăng xuất", "Bạn có chắc chắn muốn đăng xuất?")) {
            SessionManager.clearSession();
            // Navigation will be handled by MainLayoutController
        }
    }

    /**
     * Common method to refresh dashboard data
     */
    public void refreshData() {
        loadInitialData();
        loadUserSpecificData();
    }

    /**
     * Show loading indicator
     */
    protected void showLoading(String message) {
        if (loadingContainer != null) {
            if (loadingLabel != null) {
                loadingLabel.setText(message);
            }
            loadingContainer.setVisible(true);
            
            // Fade in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), loadingContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }

    /**
     * Hide loading indicator
     */
    protected void hideLoading() {
        if (loadingContainer != null) {
            // Fade out animation
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), loadingContainer);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> loadingContainer.setVisible(false));
            fadeOut.play();
        }
    }

    /**
     * Add entrance animation to a node
     */
    protected void addEntranceAnimation(Node node) {
        node.setOpacity(0);
        node.setScaleX(0.9);
        node.setScaleY(0.9);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), node);
        fadeIn.setToValue(1.0);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(500), node);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        
        fadeIn.play();
        scaleIn.play();
    }

    /**
     * Get current user
     */
    protected User getCurrentUser() {
        return currentUser != null ? currentUser : SessionManager.getCurrentUser();
    }

    /**
     * Check if user has specific role
     */
    protected boolean hasRole(String role) {
        User user = getCurrentUser();
        return user != null && user.getRole().toString().equals(role);
    }

    /**
     * Check if user is admin
     */
    protected boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if user is examiner
     */
    protected boolean isExaminer() {
        return hasRole("EXAMINER");
    }

    /**
     * Check if user is candidate
     */
    protected boolean isCandidate() {
        return hasRole("CANDIDATE");
    }

    @Override
    protected void initializeComponents() {
        setupNavigation();
    }

    @Override
    protected void setupEventHandlers() {
        // Common event handlers can be added here
    }

    @Override
    protected void clearForm() {
        // Common form clearing logic
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        // Common form enabling/disabling logic
    }
}
