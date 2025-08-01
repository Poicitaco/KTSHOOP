package com.pocitaco.oopsh.utils;

import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.models.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Security Manager - Quản lý bảo mật ứng dụng
 * Singleton pattern để đảm bảo một instance duy nhất
 * 
 * @author OOPSH Team
 * @version 1.0
 */
public class SecurityManager {
    
    private static SecurityManager instance;
    private final Map<String, UserSession> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, Integer> failedLoginAttempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastFailedLogin = new ConcurrentHashMap<>();
    
    private final Logger logger = Logger.getInstance();
    private final ApplicationConfig config = ApplicationConfig.getInstance();
    
    // Security constants
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    private static final int SESSION_TIMEOUT_MINUTES = 30;
    
    private SecurityManager() {}
    
    public static SecurityManager getInstance() {
        if (instance == null) {
            instance = new SecurityManager();
        }
        return instance;
    }
    
    /**
     * Tạo session mới cho user
     */
    public String createSession(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        // Kiểm tra xem user đã có session chưa
        String existingSessionId = findExistingSession(user.getUsername());
        if (existingSessionId != null) {
            // Cập nhật session hiện tại
            UserSession existingSession = activeSessions.get(existingSessionId);
            existingSession.setLastActivity(LocalDateTime.now());
            logger.securityEvent("Session Updated", user.getUsername(), "Existing session refreshed");
            return existingSessionId;
        }
        
        // Tạo session mới
        String sessionId = generateSessionId();
        UserSession session = new UserSession(user, sessionId, LocalDateTime.now());
        activeSessions.put(sessionId, session);
        
        // Reset failed login attempts
        failedLoginAttempts.remove(user.getUsername());
        lastFailedLogin.remove(user.getUsername());
        
        logger.securityEvent("Session Created", user.getUsername(), "New session: " + sessionId);
        return sessionId;
    }
    
    /**
     * Validate session
     */
    public boolean validateSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return false;
        }
        
        UserSession session = activeSessions.get(sessionId);
        if (session == null) {
            return false;
        }
        
        // Kiểm tra session timeout
        if (isSessionExpired(session)) {
            removeSession(sessionId);
            logger.securityEvent("Session Expired", session.getUser().getUsername(), "Session timeout");
            return false;
        }
        
        // Cập nhật last activity
        session.setLastActivity(LocalDateTime.now());
        return true;
    }
    
    /**
     * Lấy user từ session
     */
    public User getUserFromSession(String sessionId) {
        if (!validateSession(sessionId)) {
            return null;
        }
        
        UserSession session = activeSessions.get(sessionId);
        return session != null ? session.getUser() : null;
    }
    
    /**
     * Xóa session
     */
    public void removeSession(String sessionId) {
        UserSession session = activeSessions.remove(sessionId);
        if (session != null) {
            logger.securityEvent("Session Removed", session.getUser().getUsername(), "Session ended");
        }
    }
    
    /**
     * Logout user
     */
    public void logoutUser(String username) {
        String sessionId = findExistingSession(username);
        if (sessionId != null) {
            removeSession(sessionId);
        }
        logger.securityEvent("User Logout", username, "User logged out");
    }
    
    /**
     * Kiểm tra quyền truy cập
     */
    public boolean hasPermission(String sessionId, String requiredRole) {
        User user = getUserFromSession(sessionId);
        if (user == null) {
            return false;
        }
        
        UserRole userRole = user.getRole();
        if (userRole == null) {
            return false;
        }
        
        switch (requiredRole.toUpperCase()) {
            case "ADMIN":
                return UserRole.ADMIN.equals(userRole);
            case "EXAMINER":
                return UserRole.ADMIN.equals(userRole) || UserRole.EXAMINER.equals(userRole);
            case "CANDIDATE":
                return UserRole.ADMIN.equals(userRole) || UserRole.EXAMINER.equals(userRole) || UserRole.CANDIDATE.equals(userRole);
            default:
                return false;
        }
    }
    
    /**
     * Kiểm tra quyền truy cập với role cụ thể
     */
    public boolean hasRole(String sessionId, UserRole requiredRole) {
        User user = getUserFromSession(sessionId);
        if (user == null) {
            return false;
        }
        
        return requiredRole.equals(user.getRole());
    }
    
    /**
     * Ghi nhận failed login attempt
     */
    public void recordFailedLogin(String username) {
        failedLoginAttempts.merge(username, 1, Integer::sum);
        lastFailedLogin.put(username, LocalDateTime.now());
        
        int attempts = failedLoginAttempts.get(username);
        logger.securityEvent("Failed Login", username, "Attempt " + attempts + " of " + MAX_FAILED_ATTEMPTS);
        
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            logger.securityEvent("Account Locked", username, "Too many failed attempts");
        }
    }
    
    /**
     * Kiểm tra xem account có bị khóa không
     */
    public boolean isAccountLocked(String username) {
        Integer attempts = failedLoginAttempts.get(username);
        if (attempts == null || attempts < MAX_FAILED_ATTEMPTS) {
            return false;
        }
        
        LocalDateTime lastFailed = lastFailedLogin.get(username);
        if (lastFailed == null) {
            return false;
        }
        
        // Kiểm tra xem đã hết thời gian khóa chưa
        long minutesSinceLastFailed = ChronoUnit.MINUTES.between(lastFailed, LocalDateTime.now());
        if (minutesSinceLastFailed >= LOCKOUT_DURATION_MINUTES) {
            // Reset failed attempts
            failedLoginAttempts.remove(username);
            lastFailedLogin.remove(username);
            logger.securityEvent("Account Unlocked", username, "Lockout period expired");
            return false;
        }
        
        return true;
    }
    
    /**
     * Lấy thời gian còn lại của lockout
     */
    public long getRemainingLockoutTime(String username) {
        if (!isAccountLocked(username)) {
            return 0;
        }
        
        LocalDateTime lastFailed = lastFailedLogin.get(username);
        if (lastFailed == null) {
            return 0;
        }
        
        long minutesSinceLastFailed = ChronoUnit.MINUTES.between(lastFailed, LocalDateTime.now());
        return Math.max(0, LOCKOUT_DURATION_MINUTES - minutesSinceLastFailed);
    }
    
    /**
     * Lấy số lần failed attempts
     */
    public int getFailedLoginAttempts(String username) {
        return failedLoginAttempts.getOrDefault(username, 0);
    }
    
    /**
     * Cleanup expired sessions
     */
    public void cleanupExpiredSessions() {
        activeSessions.entrySet().removeIf(entry -> {
            if (isSessionExpired(entry.getValue())) {
                logger.securityEvent("Session Cleanup", entry.getValue().getUser().getUsername(), "Expired session removed");
                return true;
            }
            return false;
        });
    }
    
    /**
     * Lấy thống kê sessions
     */
    public Map<String, Object> getSessionStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeSessions", activeSessions.size());
        stats.put("failedLoginAttempts", failedLoginAttempts.size());
        stats.put("lockedAccounts", getLockedAccountsCount());
        return stats;
    }
    
    /**
     * Lấy danh sách active sessions
     */
    public Map<String, UserSession> getActiveSessions() {
        return new HashMap<>(activeSessions);
    }
    
    /**
     * Tìm session hiện tại của user
     */
    private String findExistingSession(String username) {
        return activeSessions.entrySet().stream()
                .filter(entry -> entry.getValue().getUser().getUsername().equals(username))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Kiểm tra session có expired không
     */
    private boolean isSessionExpired(UserSession session) {
        long minutesSinceLastActivity = ChronoUnit.MINUTES.between(session.getLastActivity(), LocalDateTime.now());
        return minutesSinceLastActivity >= SESSION_TIMEOUT_MINUTES;
    }
    
    /**
     * Generate session ID
     */
    private String generateSessionId() {
        return java.util.UUID.randomUUID().toString();
    }
    
    /**
     * Đếm số account bị khóa
     */
    private int getLockedAccountsCount() {
        return (int) failedLoginAttempts.entrySet().stream()
                .filter(entry -> isAccountLocked(entry.getKey()))
                .count();
    }
    
    /**
     * Class đại diện cho user session
     */
    public static class UserSession {
        private final User user;
        private final String sessionId;
        private LocalDateTime lastActivity;
        
        public UserSession(User user, String sessionId, LocalDateTime lastActivity) {
            this.user = user;
            this.sessionId = sessionId;
            this.lastActivity = lastActivity;
        }
        
        public User getUser() {
            return user;
        }
        
        public String getSessionId() {
            return sessionId;
        }
        
        public LocalDateTime getLastActivity() {
            return lastActivity;
        }
        
        public void setLastActivity(LocalDateTime lastActivity) {
            this.lastActivity = lastActivity;
        }
    }
} 