package com.pocitaco.oopsh;

import com.pocitaco.oopsh.ui.screens.LoginScreen;
import com.pocitaco.oopsh.utils.ApplicationConfig;
import com.pocitaco.oopsh.utils.Logger;
import com.pocitaco.oopsh.utils.NavigationManager;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App - Driving License Examination System
 * Material Design 3.0 Implementation with Enhanced Architecture
 * 
 * @author OOPSH Team
 * @version 2.0
 */
public class App extends Application {

    private static final Logger logger = Logger.getInstance();
    private static final ApplicationConfig config = ApplicationConfig.getInstance();
    private static final NavigationManager navigationManager = NavigationManager.getInstance();

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            // Initialize application
            initializeApplication(primaryStage);
            
            // Setup primary stage
            setupPrimaryStage(primaryStage);
            
            // Initialize navigation manager
            navigationManager.initialize(primaryStage);
            
            // Show login screen
            showLoginScreen(primaryStage);
            
            // Log successful startup
            logger.systemEvent("Application Started", "OOPSH v2.0 with enhanced architecture");
            logger.info("🎨 OOPSH Material Design 3.0 Application đã khởi động!");
            logger.info("📋 Backend hoàn thành 100% - UI Material Design mới!");
            logger.info("🔧 Enhanced with NavigationManager, ApplicationConfig, and Logger");
            
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            System.err.println("❌ Critical error during application startup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize application components
     */
    private void initializeApplication(Stage primaryStage) {
        logger.info("Initializing OOPSH Application v2.0");
        
        // Print configuration
        logger.debug("Application Configuration:");
        logger.debug(config.getPropertiesAsString());
        
        // Set application properties
        System.setProperty("javafx.application.platform", "desktop");
        System.setProperty("java.util.logging.config.file", "config/logging.properties");
    }

    /**
     * Setup primary stage with configuration
     */
    private void setupPrimaryStage(Stage primaryStage) {
        // Set window properties from configuration
        primaryStage.setTitle(config.getAppTitle());
        primaryStage.setWidth(config.getWindowWidth());
        primaryStage.setHeight(config.getWindowHeight());
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        
        // Set window icons (if available)
        try {
            // TODO: Add application icon
            // primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        } catch (Exception e) {
            logger.warning("Could not load application icon: " + e.getMessage());
        }
        
        // Handle window close event
        primaryStage.setOnCloseRequest(event -> {
            logger.systemEvent("Application Shutdown", "User requested application close");
            logger.info("👋 OOPSH Application đã tắt!");
        });
        
        logger.info("Primary stage configured successfully");
    }

    /**
     * Show login screen
     */
    private void showLoginScreen(Stage primaryStage) {
        try {
            LoginScreen loginScreen = new LoginScreen(primaryStage);
            loginScreen.show();
            logger.info("Login screen displayed successfully");
        } catch (Exception e) {
            logger.error("Failed to show login screen", e);
            throw new RuntimeException("Could not initialize login screen", e);
        }
    }

    public static void main(String[] args) {
        try {
            // Initialize logger first
            logger.systemEvent("Application Launch", "Starting OOPSH v2.0");
            
            // Launch JavaFX application
            launch(args);
            
        } catch (Exception e) {
            System.err.println("❌ Critical error in main method: " + e.getMessage());
            e.printStackTrace();
        }
    }
}