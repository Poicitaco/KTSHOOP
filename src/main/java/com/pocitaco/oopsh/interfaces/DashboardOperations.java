package com.pocitaco.oopsh.interfaces;

/**
 * Interface for dashboard functionality
 * Defines common operations for all dashboard types
 */
public interface DashboardOperations {

    /**
     * Load dashboard statistics
     */
    void loadStatistics();

    /**
     * Refresh dashboard data
     */
    void refreshData();

    /**
     * Export dashboard data
     */
    void exportData();

    /**
     * Search functionality
     */
    void performSearch(String query);

    /**
     * Show dashboard help
     */
    void showHelp();
}
