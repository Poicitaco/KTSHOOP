package com.pocitaco.oopsh.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Navigation strategy for Admin role
 * Implements all administrative functions
 */
public class AdminNavigationStrategy implements NavigationStrategy {

    @Override
    public List<MenuCategory> getMenuCategories() {
        List<MenuCategory> categories = new ArrayList<>();

        // Dashboard Category
        List<MenuItem> dashboardItems = Arrays.asList(
                new MenuItem("Overview", "Dashboard", "/com/pocitaco/oopsh/admin/dashboard-overview.fxml", "mdi2v-view-dashboard"),
                new MenuItem("Statistics", "Dashboard", "/com/pocitaco/oopsh/admin/dashboard-stats.fxml", "mdi2c-chart-line"));
        categories.add(new MenuCategory("Dashboard", dashboardItems));

        // User Management Category
        List<MenuItem> userItems = Arrays.asList(
                new MenuItem("Manage Users", "User Management", "/com/pocitaco/oopsh/admin/user-management.fxml", "mdi2a-account-group"),
                new MenuItem("Create User", "User Management", "/com/pocitaco/oopsh/admin/user-create.fxml", "mdi2a-account-plus"),
                new MenuItem("User Permissions", "User Management", "/com/pocitaco/oopsh/admin/user-permissions.fxml", "mdi2s-shield-account"));
        categories.add(new MenuCategory("User Management", userItems));

        // Exam Management Category
        List<MenuItem> examItems = Arrays.asList(
                new MenuItem("Manage Exams", "Exam Management", "/com/pocitaco/oopsh/admin/exam-management.fxml", "mdi2f-file-document-multiple"),
                new MenuItem("Create Exam", "Exam Management", "/com/pocitaco/oopsh/admin/exam-create.fxml", "mdi2f-file-document-plus"),
                new MenuItem("Exam Types", "Exam Management", "/com/pocitaco/oopsh/admin/exam-types.fxml", "mdi2f-file-document-edit"),
                new MenuItem("Exam Schedules", "Exam Management", "/com/pocitaco/oopsh/admin/exam-schedules.fxml", "mdi2c-calendar-clock"));
        categories.add(new MenuCategory("Exam Management", examItems));

        // Results & Reports Category
        List<MenuItem> reportItems = Arrays.asList(
                new MenuItem("Exam Results", "Reports", "/com/pocitaco/oopsh/admin/exam-results.fxml", "mdi2c-chart-bar"),
                new MenuItem("Statistics Report", "Reports", "/com/pocitaco/oopsh/admin/statistics-report.fxml", "mdi2c-chart-pie"),
                new MenuItem("Revenue Report", "Reports", "/com/pocitaco/oopsh/admin/revenue-report.fxml", "mdi2c-currency-usd"),
                new MenuItem("Performance Report", "Reports", "/com/pocitaco/oopsh/admin/performance-report.fxml", "mdi2c-trending-up"));
        categories.add(new MenuCategory("Reports", reportItems));

        // System Settings Category
        List<MenuItem> settingsItems = Arrays.asList(
                new MenuItem("System Configuration", "Settings", "/com/pocitaco/oopsh/admin/system-config.fxml", "mdi2c-cog"),
                new MenuItem("Backup & Restore", "Settings", "/com/pocitaco/oopsh/admin/backup-restore.fxml", "mdi2c-cloud-upload"),
                new MenuItem("Audit Logs", "Settings", "/com/pocitaco/oopsh/admin/audit-logs.fxml", "mdi2f-file-document-outline"));
        categories.add(new MenuCategory("Settings", settingsItems));

        return categories;
    }

    @Override
    public String getDefaultPage() {
        return "/com/pocitaco/oopsh/admin/dashboard-overview.fxml";
    }

    @Override
    public boolean hasAccess(String functionality) {
        // Admin has access to all functionalities
        return true;
    }
}
