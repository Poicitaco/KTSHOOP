package com.pocitaco.oopsh.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Navigation strategy for Candidate role
 * Implements candidate-specific functions
 */
public class CandidateNavigationStrategy implements NavigationStrategy {

    @Override
    public List<MenuCategory> getMenuCategories() {
        List<MenuCategory> categories = new ArrayList<>();

        // Dashboard Category
        List<MenuItem> dashboardItems = Arrays.asList(
                new MenuItem("My Dashboard", "Dashboard", "/com/pocitaco/oopsh/candidate/dashboard-overview.fxml"),
                new MenuItem("Profile", "Dashboard", "/com/pocitaco/oopsh/candidate/profile.fxml"));
        categories.add(new MenuCategory("Dashboard", dashboardItems));

        // Exam Registration Category
        List<MenuItem> registrationItems = Arrays.asList(
                new MenuItem("Available Exams", "Registration", "/com/pocitaco/oopsh/candidate/available-exams.fxml"),
                new MenuItem("Register for Exam", "Registration", "/com/pocitaco/oopsh/candidate/register-exam.fxml"),
                new MenuItem("My Registrations", "Registration", "/com/pocitaco/oopsh/candidate/my-registrations.fxml"),
                new MenuItem("Payment History", "Registration", "/com/pocitaco/oopsh/candidate/payment-history.fxml"));
        categories.add(new MenuCategory("Registration", registrationItems));

        // Exam Schedule Category
        List<MenuItem> scheduleItems = Arrays.asList(
                new MenuItem("My Exam Schedule", "Schedule", "/com/pocitaco/oopsh/candidate/exam-schedule.fxml"),
                new MenuItem("Upcoming Exams", "Schedule", "/com/pocitaco/oopsh/candidate/upcoming-exams.fxml"),
                new MenuItem("Exam Information", "Schedule", "/com/pocitaco/oopsh/candidate/exam-info.fxml"));
        categories.add(new MenuCategory("Schedule", scheduleItems));

        // Results Category
        List<MenuItem> resultItems = Arrays.asList(
                new MenuItem("My Results", "Results", "/com/pocitaco/oopsh/candidate/exam-results.fxml"),
                new MenuItem("Certificates", "Results", "/com/pocitaco/oopsh/candidate/certificates.fxml"),
                new MenuItem("Result History", "Results", "/com/pocitaco/oopsh/candidate/result-history.fxml"));
        categories.add(new MenuCategory("Results", resultItems));

        // Practice Category
        List<MenuItem> practiceItems = Arrays.asList(
                new MenuItem("Practice Tests", "Practice", "/com/pocitaco/oopsh/candidate/practice-tests.fxml"),
                new MenuItem("Study Materials", "Practice", "/com/pocitaco/oopsh/candidate/study-materials.fxml"),
                new MenuItem("Mock Exams", "Practice", "/com/pocitaco/oopsh/candidate/mock-exams.fxml"));
        categories.add(new MenuCategory("Practice", practiceItems));

        return categories;
    }

    @Override
    public String getDefaultPage() {
        return "/com/pocitaco/oopsh/candidate/dashboard-overview.fxml";
    }

    @Override
    public boolean hasAccess(String functionality) {
        // Candidate specific access controls
        List<String> allowedFunctions = Arrays.asList(
                "register_exam", "view_results", "view_schedule",
                "update_profile", "practice_tests", "view_certificates");
        return allowedFunctions.contains(functionality);
    }
}

