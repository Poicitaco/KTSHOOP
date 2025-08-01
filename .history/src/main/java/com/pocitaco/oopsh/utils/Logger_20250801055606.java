package com.pocitaco.oopsh.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple Logger Utility
 * Quản lý logging cho ứng dụng OOPSH
 * 
 * @author OOPSH Team
 * @version 1.0
 */
public class Logger {
    
    private static Logger instance;
    private final String logDirectory = "logs";
    private final String logFile = "oophs.log";
    private final Path logPath;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private Logger() {
        // Tạo thư mục logs nếu chưa tồn tại
        try {
            Files.createDirectories(Paths.get(logDirectory));
        } catch (IOException e) {
            System.err.println("Could not create log directory: " + e.getMessage());
        }
        
        logPath = Paths.get(logDirectory, logFile);
    }
    
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }
    
    /**
     * Log info message
     */
    public void info(String message) {
        log("INFO", message);
    }
    
    /**
     * Log warning message
     */
    public void warning(String message) {
        log("WARNING", message);
    }
    
    /**
     * Log error message
     */
    public void error(String message) {
        log("ERROR", message);
    }
    
    /**
     * Log error message with exception
     */
    public void error(String message, Throwable throwable) {
        log("ERROR", message + " - " + throwable.getMessage());
        // Log stack trace if available
        if (throwable.getStackTrace() != null) {
            for (StackTraceElement element : throwable.getStackTrace()) {
                log("ERROR", "  at " + element.toString());
            }
        }
    }
    
    /**
     * Log debug message
     */
    public void debug(String message) {
        log("DEBUG", message);
    }
    
    /**
     * Log user action
     */
    public void userAction(String username, String action, String details) {
        log("USER_ACTION", String.format("User: %s | Action: %s | Details: %s", username, action, details));
    }
    
    /**
     * Log system event
     */
    public void systemEvent(String event, String details) {
        log("SYSTEM", String.format("Event: %s | Details: %s", event, details));
    }
    
    /**
     * Log security event
     */
    public void securityEvent(String event, String username, String details) {
        log("SECURITY", String.format("Event: %s | User: %s | Details: %s", event, username, details));
    }
    
    /**
     * Log database operation
     */
    public void database(String operation, String table, String details) {
        log("DATABASE", String.format("Operation: %s | Table: %s | Details: %s", operation, table, details));
    }
    
    /**
     * Core logging method
     */
    private void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] [%s] %s%n", timestamp, level, message);
        
        // Print to console
        System.out.print(logEntry);
        
        // Write to file
        try {
            Files.write(logPath, logEntry.getBytes(), 
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    /**
     * Get log file path
     */
    public String getLogFilePath() {
        return logPath.toString();
    }
    
    /**
     * Check if log file exists
     */
    public boolean logFileExists() {
        return Files.exists(logPath);
    }
    
    /**
     * Get log file size in bytes
     */
    public long getLogFileSize() {
        try {
            return Files.size(logPath);
        } catch (IOException e) {
            return 0;
        }
    }
    
    /**
     * Clear log file
     */
    public void clearLog() {
        try {
            Files.write(logPath, "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            info("Log file cleared");
        } catch (IOException e) {
            System.err.println("Failed to clear log file: " + e.getMessage());
        }
    }
    
    /**
     * Archive current log file
     */
    public void archiveLog() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String archiveName = String.format("oophs_%s.log", timestamp);
            Path archivePath = Paths.get(logDirectory, archiveName);
            
            Files.move(logPath, archivePath);
            info("Log file archived to: " + archiveName);
        } catch (IOException e) {
            System.err.println("Failed to archive log file: " + e.getMessage());
        }
    }
    
    /**
     * Get log statistics
     */
    public String getLogStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("Log Statistics:\n");
        stats.append("  Log file: ").append(getLogFilePath()).append("\n");
        stats.append("  Exists: ").append(logFileExists()).append("\n");
        stats.append("  Size: ").append(getLogFileSize()).append(" bytes\n");
        stats.append("  Directory: ").append(logDirectory).append("\n");
        return stats.toString();
    }
} 