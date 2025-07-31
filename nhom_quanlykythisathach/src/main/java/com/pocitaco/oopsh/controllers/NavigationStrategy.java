package com.pocitaco.oopsh.controllers;

import java.util.List;

/**
 * Strategy interface for navigation based on user role
 * Implements Strategy pattern for different role-based navigation
 */
public interface NavigationStrategy {

    /**
     * Get menu categories for this role
     */
    List<MenuCategory> getMenuCategories();

    /**
     * Get default landing page for this role
     */
    String getDefaultPage();

    /**
     * Check if this role has access to specific functionality
     */
    boolean hasAccess(String functionality);
}

