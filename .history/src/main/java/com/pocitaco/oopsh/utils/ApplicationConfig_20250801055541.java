package com.pocitaco.oopsh.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application Configuration Manager
 * Quản lý cấu hình ứng dụng từ file properties
 * 
 * @author OOPSH Team
 * @version 1.0
 */
public class ApplicationConfig {
    
    private static ApplicationConfig instance;
    private Properties properties;
    private static final String CONFIG_FILE = "config/application.properties";
    
    // Default values
    private static final String DEFAULT_APP_TITLE = "OOPSH - Hệ thống sát hạch bằng lái xe";
    private static final int DEFAULT_WINDOW_WIDTH = 1200;
    private static final int DEFAULT_WINDOW_HEIGHT = 800;
    private static final String DEFAULT_DATA_DIR = "data";
    private static final String DEFAULT_LOG_LEVEL = "INFO";
    private static final int DEFAULT_SESSION_TIMEOUT = 30; // minutes
    
    private ApplicationConfig() {
        loadProperties();
    }
    
    public static ApplicationConfig getInstance() {
        if (instance == null) {
            instance = new ApplicationConfig();
        }
        return instance;
    }
    
    /**
     * Load properties từ file config
     */
    private void loadProperties() {
        properties = new Properties();
        
        try {
            // Try to load from file first
            try (InputStream input = new FileInputStream(CONFIG_FILE)) {
                properties.load(input);
                System.out.println("✅ Loaded configuration from: " + CONFIG_FILE);
            } catch (IOException e) {
                System.out.println("⚠️  Could not load config file, using defaults: " + e.getMessage());
                loadDefaultProperties();
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading configuration: " + e.getMessage());
            loadDefaultProperties();
        }
    }
    
    /**
     * Load default properties
     */
    private void loadDefaultProperties() {
        properties.setProperty("app.title", DEFAULT_APP_TITLE);
        properties.setProperty("app.window.width", String.valueOf(DEFAULT_WINDOW_WIDTH));
        properties.setProperty("app.window.height", String.valueOf(DEFAULT_WINDOW_HEIGHT));
        properties.setProperty("app.data.directory", DEFAULT_DATA_DIR);
        properties.setProperty("app.log.level", DEFAULT_LOG_LEVEL);
        properties.setProperty("app.session.timeout", String.valueOf(DEFAULT_SESSION_TIMEOUT));
        properties.setProperty("app.theme", "material-design");
        properties.setProperty("app.language", "vi");
        properties.setProperty("app.timezone", "Asia/Ho_Chi_Minh");
    }
    
    /**
     * Get application title
     */
    public String getAppTitle() {
        return properties.getProperty("app.title", DEFAULT_APP_TITLE);
    }
    
    /**
     * Get window width
     */
    public int getWindowWidth() {
        try {
            return Integer.parseInt(properties.getProperty("app.window.width", String.valueOf(DEFAULT_WINDOW_WIDTH)));
        } catch (NumberFormatException e) {
            return DEFAULT_WINDOW_WIDTH;
        }
    }
    
    /**
     * Get window height
     */
    public int getWindowHeight() {
        try {
            return Integer.parseInt(properties.getProperty("app.window.height", String.valueOf(DEFAULT_WINDOW_HEIGHT)));
        } catch (NumberFormatException e) {
            return DEFAULT_WINDOW_HEIGHT;
        }
    }
    
    /**
     * Get data directory
     */
    public String getDataDirectory() {
        return properties.getProperty("app.data.directory", DEFAULT_DATA_DIR);
    }
    
    /**
     * Get log level
     */
    public String getLogLevel() {
        return properties.getProperty("app.log.level", DEFAULT_LOG_LEVEL);
    }
    
    /**
     * Get session timeout in minutes
     */
    public int getSessionTimeout() {
        try {
            return Integer.parseInt(properties.getProperty("app.session.timeout", String.valueOf(DEFAULT_SESSION_TIMEOUT)));
        } catch (NumberFormatException e) {
            return DEFAULT_SESSION_TIMEOUT;
        }
    }
    
    /**
     * Get application theme
     */
    public String getTheme() {
        return properties.getProperty("app.theme", "material-design");
    }
    
    /**
     * Get application language
     */
    public String getLanguage() {
        return properties.getProperty("app.language", "vi");
    }
    
    /**
     * Get application timezone
     */
    public String getTimezone() {
        return properties.getProperty("app.timezone", "Asia/Ho_Chi_Minh");
    }
    
    /**
     * Get property by key
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Get property by key with default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Set property
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    /**
     * Check if property exists
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }
    
    /**
     * Get all properties as string for debugging
     */
    public String getPropertiesAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Application Configuration:\n");
        for (String key : properties.stringPropertyNames()) {
            sb.append("  ").append(key).append(" = ").append(properties.getProperty(key)).append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Print configuration to console
     */
    public void printConfiguration() {
        System.out.println(getPropertiesAsString());
    }
} 