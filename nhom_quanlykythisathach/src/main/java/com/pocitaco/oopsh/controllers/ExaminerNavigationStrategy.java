package com.pocitaco.oopsh.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Navigation strategy for Examiner role
 * Implements examiner-specific functions
 */
public class ExaminerNavigationStrategy implements NavigationStrategy {

    @Override
    public List<MenuCategory> getMenuCategories() {
        List<MenuCategory> categories = new ArrayList<>();

        // Dashboard Category
        List<MenuItem> dashboardItems = Arrays.asList(
                new MenuItem("My Dashboard", "Dashboard", "/com/pocitaco/oopsh/examiner/dashboard-overview.fxml"),
                new MenuItem("Today's Schedule", "Dashboard", "/com/pocitaco/oopsh/examiner/today-schedule.fxml"));
        categories.add(new MenuCategory("Dashboard", dashboardItems));

        // Exam Sessions Category
        List<MenuItem> sessionItems = Arrays.asList(
                new MenuItem("My Exam Sessions", "Exam Sessions", "/com/pocitaco/oopsh/examiner/exam-sessions.fxml"),
                new MenuItem("Current Session", "Exam Sessions", "/com/pocitaco/oopsh/examiner/current-session.fxml"),
                new MenuItem("Session History", "Exam Sessions", "/com/pocitaco/oopsh/examiner/session-history.fxml"));
        categories.add(new MenuCategory("Exam Sessions", sessionItems));

        // Grading Category
        List<MenuItem> gradingItems = Arrays.asList(
                new MenuItem("Grade Exams", "Grading", "/com/pocitaco/oopsh/examiner/grade-exams.fxml"),
                new MenuItem("Theory Grading", "Grading", "/com/pocitaco/oopsh/examiner/theory-grading.fxml"),
                new MenuItem("Practice Grading", "Grading", "/com/pocitaco/oopsh/examiner/practice-grading.fxml"),
                new MenuItem("Grading History", "Grading", "/com/pocitaco/oopsh/examiner/grading-history.fxml"));
        categories.add(new MenuCategory("Grading", gradingItems));

        // Candidates Category
        List<MenuItem> candidateItems = Arrays.asList(
                new MenuItem("My Candidates", "Candidates", "/com/pocitaco/oopsh/examiner/candidates.fxml"),
                new MenuItem("Candidate Details", "Candidates", "/com/pocitaco/oopsh/examiner/candidate-details.fxml"));
        categories.add(new MenuCategory("Candidates", candidateItems));

        // Reports Category
        List<MenuItem> reportItems = Arrays.asList(
                new MenuItem("My Performance", "Reports", "/com/pocitaco/oopsh/examiner/performance-report.fxml"),
                new MenuItem("Session Reports", "Reports", "/com/pocitaco/oopsh/examiner/session-reports.fxml"));
        categories.add(new MenuCategory("Reports", reportItems));

        return categories;
    }

    @Override
    public String getDefaultPage() {
        return "/com/pocitaco/oopsh/examiner/dashboard-overview.fxml";
    }

    @Override
    public boolean hasAccess(String functionality) {
        // Examiner specific access controls
        List<String> allowedFunctions = Arrays.asList(
                "grade_exams", "view_candidates", "manage_sessions",
                "view_schedule", "generate_reports");
        return allowedFunctions.contains(functionality);
    }
}

