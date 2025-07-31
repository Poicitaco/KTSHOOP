package com.pocitaco.oopsh.controllers;

import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.interfaces.DashboardOperations;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.utils.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Main Layout Controller implementing navigation and content management
 * Uses Strategy pattern for different role-based navigation
 */
public class MainLayoutController extends BaseController implements DashboardOperations {

    @FXML
    private VBox navigationMenu;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserRole;
    @FXML
    private Button btnLogout;
    @FXML
    private Label lblPageTitle;
    @FXML
    private Label lblBreadcrumb;
    @FXML
    private HBox actionButtons;
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox defaultContent;

    private User currentUser;
    private NavigationStrategy navigationStrategy;
    private List<MenuItem> menuItems;
    private MenuItem selectedMenuItem;

    @Override
    protected void initializeComponents() {
        menuItems = new ArrayList<>();
        setupStylesheets();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are already set in FXML
    }

    @Override
    protected void loadInitialData() {
        currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            updateUserInfo();
            setupNavigationForRole(currentUser.getRole());
        }
    }

    /**
     * Set current user and update UI accordingly
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        SessionManager.setCurrentUser(user);
        updateUserInfo();
        setupNavigationForRole(user.getRole());
    }

    /**
     * Update user information display
     */
    private void updateUserInfo() {
        if (currentUser != null) {
            lblUserName.setText(currentUser.getFullName());
            lblUserRole.setText(getRoleDisplayName(currentUser.getRole()));
        }
    }

    /**
     * Setup navigation based on user role using Strategy pattern
     */
    private void setupNavigationForRole(UserRole role) {
        switch (role) {
            case ADMIN:
                navigationStrategy = new AdminNavigationStrategy();
                break;
            case EXAMINER:
                navigationStrategy = new ExaminerNavigationStrategy();
                break;
            case CANDIDATE:
                navigationStrategy = new CandidateNavigationStrategy();
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }

        buildNavigationMenu();
    }

    /**
     * Build navigation menu using current strategy
     */
    private void buildNavigationMenu() {
        navigationMenu.getChildren().clear();
        menuItems.clear();

        List<MenuCategory> categories = navigationStrategy.getMenuCategories();

        for (MenuCategory category : categories) {
            // Add category header
            Label categoryLabel = new Label(category.getName());
            categoryLabel.getStyleClass().add("menu-category");
            navigationMenu.getChildren().add(categoryLabel);

            // Add menu items
            for (MenuItem item : category.getItems()) {
                Button menuButton = createMenuButton(item);
                navigationMenu.getChildren().add(menuButton);
                menuItems.add(item);
            }
        }
    }

    /**
     * Create menu button for a menu item
     */
    private Button createMenuButton(MenuItem item) {
        HBox buttonContent = new HBox(10);
        buttonContent.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        // Add icon if available (temporarily disabled due to dependency issues)
        if (item.getIconLiteral() != null && !item.getIconLiteral().isEmpty()) {
            // TODO: Re-enable when Ikonli dependency is properly configured
            // FontIcon icon = new FontIcon(item.getIconLiteral());
            // icon.setIconSize(18);
            // icon.setStyle("-fx-text-fill: white;");
            // buttonContent.getChildren().add(icon);
        }
        
        // Add text
        Label textLabel = new Label(item.getTitle());
        textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        buttonContent.getChildren().add(textLabel);
        
        Button button = new Button();
        button.setGraphic(buttonContent);
        button.getStyleClass().add("menu-item");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(40);
        button.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);

        button.setOnAction(e -> selectMenuItem(item));

        return button;
    }

    /**
     * Select and load a menu item
     */
    private void selectMenuItem(MenuItem item) {
        // Update selected state
        if (selectedMenuItem != null) {
            updateMenuButtonStyle(selectedMenuItem, false);
        }

        selectedMenuItem = item;
        updateMenuButtonStyle(item, true);

        // Update breadcrumb and title
        lblPageTitle.setText(item.getTitle());
        lblBreadcrumb.setText("Home > " + item.getCategory() + " > " + item.getTitle());

        // Clear action buttons
        actionButtons.getChildren().clear();

        // Load content
        loadContent(item);

        // Add action buttons if any
        if (item.getActionButtons() != null) {
            for (ActionButtonConfig btnConfig : item.getActionButtons()) {
                Button actionBtn = new Button(btnConfig.getText());
                actionBtn.getStyleClass().addAll("btn-primary");
                actionBtn.setOnAction(btnConfig.getAction());
                actionButtons.getChildren().add(actionBtn);
            }
        }
    }

    /**
     * Update menu button visual state
     */
    private void updateMenuButtonStyle(MenuItem item, boolean selected) {
        for (Node node : navigationMenu.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                if (button.getText().equals(item.getTitle())) {
                    if (selected) {
                        button.getStyleClass().add("selected");
                    } else {
                        button.getStyleClass().remove("selected");
                    }
                    break;
                }
            }
        }
    }

    /**
     * Load content for selected menu item
     */
    private void loadContent(MenuItem item) {
        try {
            if (item.getFxmlPath() != null) {
                // Load FXML content
                FXMLLoader loader = new FXMLLoader(getClass().getResource(item.getFxmlPath()));
                Parent content = loader.load();

                contentArea.getChildren().clear();
                contentArea.getChildren().add(content);

                // If controller implements BaseDashboardController, set current user
                Object controller = loader.getController();
                if (controller instanceof BaseDashboardController) {
                    ((BaseDashboardController) controller).setCurrentUser(currentUser);
                }

            } else if (item.getContentSupplier() != null) {
                // Load programmatic content
                Node content = item.getContentSupplier().get();
                contentArea.getChildren().clear();
                contentArea.getChildren().add(content);
            }
        } catch (IOException e) {
            showError("Loading Error", "Failed to load content: " + e.getMessage());
        }
    }

    /**
     * Setup stylesheets
     */
    private void setupStylesheets() {
        Scene scene = navigationMenu.getScene();
        if (scene != null) {
            scene.getStylesheets().add(
                    getClass().getResource("/com/pocitaco/oopsh/styles/material-design.css").toExternalForm());
        } else {
            // If scene is not available yet, add stylesheet when it becomes available
            Platform.runLater(() -> {
                Scene sceneAfter = navigationMenu.getScene();
                if (sceneAfter != null) {
                    sceneAfter.getStylesheets().add(
                            getClass().getResource("/com/pocitaco/oopsh/styles/material-design.css").toExternalForm());
                }
            });
        }
    }

    /**
     * Get display name for user role
     */
    private String getRoleDisplayName(UserRole role) {
        switch (role) {
            case ADMIN:
                return "Administrator";
            case EXAMINER:
                return "Examiner";
            case CANDIDATE:
                return "Candidate";
            default:
                return role.toString();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        if (showConfirmation("Logout", "Are you sure you want to logout?")) {
            try {
                SessionManager.clearSession();

                // Load login screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/login.fxml"));
                Parent loginRoot = loader.load();

                Scene loginScene = new Scene(loginRoot);
                Stage stage = (Stage) btnLogout.getScene().getWindow();
                stage.setScene(loginScene);
                stage.setTitle("OOPSH - Login");

            } catch (IOException e) {
                showError("Error", "Failed to load login screen: " + e.getMessage());
            }
        }
    }

    @Override
    protected void clearForm() {
        // No form to clear in main layout
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        // No form to enable/disable in main layout
    }

    @Override
    public void loadStatistics() {
        // Delegate to selected content if it supports statistics
    }

    @Override
    public void refreshData() {
        if (selectedMenuItem != null) {
            loadContent(selectedMenuItem);
        }
    }

    @Override
    public void exportData() {
        // Implement export functionality
        showInfo("Export", "Export functionality will be implemented");
    }

    @Override
    public void performSearch(String query) {
        // Implement search functionality
        showInfo("Search", "Search functionality will be implemented for: " + query);
    }

    @Override
    public void showHelp() {
        showInfo("Help", "Help documentation will be available soon");
    }
}
