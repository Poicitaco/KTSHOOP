package com.pocitaco.oopsh.utils;

import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.models.User;

/**
 * Session Manager for handling user sessions
 * Implements Singleton pattern for global session management
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {
        // Private constructor for singleton
    }

    /**
     * Get singleton instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    /**
     * Set current logged-in user
     */
    public static void setCurrentUser(User user) {
        getInstance().currentUser = user;
    }

    /**
     * Get current logged-in user
     */
    public static User getCurrentUser() {
        return getInstance().currentUser;
    }

    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn() {
        return getInstance().currentUser != null;
    }

    /**
     * Clear current session
     */
    public static void clearSession() {
        getInstance().currentUser = null;
    }

    /**
     * Check if current user has specific role
     */
    public static boolean hasRole(UserRole role) {
        User user = getCurrentUser();
        return user != null && user.getRole() == role;
    }

    // Legacy methods for backward compatibility
    public void login(User user) {
        setCurrentUser(user);
    }

    public void logout() {
        clearSession();
    }
}