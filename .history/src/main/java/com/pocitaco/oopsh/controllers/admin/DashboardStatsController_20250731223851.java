package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.*;
import com.pocitaco.oopsh.enums.ResultStatus;
import com.pocitaco.oopsh.enums.ScheduleStatus;
import com.pocitaco.oopsh.models.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardStatsController extends BaseController {

    @FXML
    private Label lblTotalUsers;
    @FXML
    private Label lblTotalCandidates;
    @FXML
    private Label lblTotalExaminers;
    @FXML
    private Label lblTotalAdmins;

    @FXML
    private Label lblTotalExams;
    @FXML
    private Label lblTotalSchedules;
    @FXML
    private Label lblUpcomingExams;
    @FXML
    private Label lblCompletedExams;

    @FXML
    private Label lblTotalRegistrations;
    @FXML
    private Label lblPendingRegistrations;
    @FXML
    private Label lblCompletedRegistrations;

    @FXML
    private Label lblTotalResults;
    @FXML
    private Label lblPassedResults;
    @FXML
    private Label lblFailedResults;
    @FXML
    private Label lblPassRate;

    @FXML
    private Label lblTotalRevenue;
    @FXML
    private Label lblMonthlyRevenue;
    @FXML
    private Label lblAverageExamFee;

    @FXML
    private ProgressBar pbPassRate;
    @FXML
    private ProgressBar pbRegistrationRate;
    @FXML
    private ProgressBar pbCompletionRate;

    @FXML
    private VBox vbRecentActivity;
    @FXML
    private VBox vbTopExamTypes;
    @FXML
    private VBox vbMonthlyStats;

    private UserDAO userDAO;
    private ExamTypeDAO examTypeDAO;
    private ExamScheduleDAO examScheduleDAO;
    private RegistrationDAO registrationDAO;
    private ResultDAO resultDAO;
    private PaymentDAO paymentDAO;

    @Override
    protected void initializeComponents() {
        userDAO = new UserDAO();
        examTypeDAO = new ExamTypeDAO();
        examScheduleDAO = new ExamScheduleDAO();
        registrationDAO = new RegistrationDAO();
        resultDAO = new ResultDAO();
        paymentDAO = new PaymentDAO();
    }

    @Override
    protected void setupEventHandlers() {
        // Auto-refresh every 30 seconds
        // Platform.runLater(() -> {
        // Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(30), event ->
        // loadStatistics()));
        // timeline.setCycleCount(Timeline.INDEFINITE);
        // timeline.play();
        // });
    }

    @Override
    protected void loadInitialData() {
        loadStatistics();
    }

    private void loadStatistics() {
        loadUserStatistics();
        loadExamStatistics();
        loadRegistrationStatistics();
        loadResultStatistics();
        loadRevenueStatistics();
        loadProgressBars();
        loadRecentActivity();
        loadTopExamTypes();
        loadMonthlyStats();
    }

    private void loadUserStatistics() {
        List<User> allUsers = userDAO.getAllUsers();

        long totalCandidates = allUsers.stream()
                .filter(user -> "CANDIDATE".equals(user.getRole().toString()))
                .count();

        long totalExaminers = allUsers.stream()
                .filter(user -> "EXAMINER".equals(user.getRole().toString()))
                .count();

        long totalAdmins = allUsers.stream()
                .filter(user -> "ADMIN".equals(user.getRole().toString()))
                .count();

        lblTotalUsers.setText(String.valueOf(allUsers.size()));
        lblTotalCandidates.setText(String.valueOf(totalCandidates));
        lblTotalExaminers.setText(String.valueOf(totalExaminers));
        lblTotalAdmins.setText(String.valueOf(totalAdmins));
    }

    private void loadExamStatistics() {
        List<ExamType> examTypes = examTypeDAO.getAll();
        List<ExamSchedule> schedules = examScheduleDAO.getAllSchedules();

        LocalDate today = LocalDate.now();

        long upcomingExams = schedules.stream()
                .filter(schedule -> schedule.getExamDate().isAfter(today) || schedule.getExamDate().isEqual(today))
                .count();

        long completedExams = schedules.stream()
                .filter(schedule -> schedule.getStatus() == ScheduleStatus.COMPLETED)
                .count();

        lblTotalExams.setText(String.valueOf(examTypes.size()));
        lblTotalSchedules.setText(String.valueOf(schedules.size()));
        lblUpcomingExams.setText(String.valueOf(upcomingExams));
        lblCompletedExams.setText(String.valueOf(completedExams));
    }

    private void loadRegistrationStatistics() {
        List<Registration> registrations = registrationDAO.findAll();

        long pendingRegistrations = registrations.stream()
                .filter(reg -> "PENDING".equals(reg.getStatus().toString()))
                .count();

        long completedRegistrations = registrations.stream()
                .filter(reg -> "COMPLETED".equals(reg.getStatus().toString()))
                .count();

        lblTotalRegistrations.setText(String.valueOf(registrations.size()));
        lblPendingRegistrations.setText(String.valueOf(pendingRegistrations));
        lblCompletedRegistrations.setText(String.valueOf(completedRegistrations));
    }

    private void loadResultStatistics() {
        List<Result> results = resultDAO.findAll();

        long passedResults = results.stream()
                .filter(result -> result.getStatus() == ResultStatus.PASSED)
                .count();

        long failedResults = results.stream()
                .filter(result -> result.getStatus() == ResultStatus.FAILED)
                .count();

        double passRate = results.isEmpty() ? 0.0 : (double) passedResults / results.size() * 100;

        lblTotalResults.setText(String.valueOf(results.size()));
        lblPassedResults.setText(String.valueOf(passedResults));
        lblFailedResults.setText(String.valueOf(failedResults));
        lblPassRate.setText(String.format("%.1f%%", passRate));

        // Update progress bar
        pbPassRate.setProgress(passRate / 100.0);
    }

    private void loadRevenueStatistics() {
        List<Payment> payments = paymentDAO.findAll();

        double totalRevenue = payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        // Calculate monthly revenue (current month)
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        double monthlyRevenue = payments.stream()
                .filter(payment -> payment.getPaymentDate().toLocalDate().isAfter(currentMonth.minusDays(1)))
                .mapToDouble(Payment::getAmount)
                .sum();

        // Calculate average exam fee
        List<ExamType> examTypes = examTypeDAO.getAll();
        double averageExamFee = examTypes.isEmpty() ? 0.0
                : examTypes.stream().mapToDouble(ExamType::getFee).average().orElse(0.0);

        lblTotalRevenue.setText(formatCurrency(totalRevenue));
        lblMonthlyRevenue.setText(formatCurrency(monthlyRevenue));
        lblAverageExamFee.setText(formatCurrency(averageExamFee));
    }

    private void loadProgressBars() {
        // Registration rate (registrations vs total candidates)
        List<User> candidates = userDAO.getAllUsers().stream()
                .filter(user -> "CANDIDATE".equals(user.getRole().toString()))
                .collect(Collectors.toList());

        List<Registration> registrations = registrationDAO.findAll();
        double registrationRate = candidates.isEmpty() ? 0.0 : (double) registrations.size() / candidates.size();
        pbRegistrationRate.setProgress(registrationRate);

        // Completion rate (completed vs total registrations)
        long completedRegistrations = registrations.stream()
                .filter(reg -> "COMPLETED".equals(reg.getStatus().toString()))
                .count();
        double completionRate = registrations.isEmpty() ? 0.0 : (double) completedRegistrations / registrations.size();
        pbCompletionRate.setProgress(completionRate);
    }

    private void loadRecentActivity() {
        vbRecentActivity.getChildren().clear();

        // Add recent registrations
        List<Registration> recentRegistrations = registrationDAO.findAll().stream()
                .sorted((r1, r2) -> r2.getRegistrationDate().compareTo(r1.getRegistrationDate()))
                .limit(5)
                .collect(Collectors.toList());

        Label activityTitle = new Label("Hoạt động gần đây:");
        activityTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        vbRecentActivity.getChildren().add(activityTitle);

        for (Registration reg : recentRegistrations) {
            Label activityLabel = new Label(String.format(
                    "• Thí sinh đăng ký thi ngày %s",
                    reg.getRegistrationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            activityLabel.setWrapText(true);
            vbRecentActivity.getChildren().add(activityLabel);
        }
    }

    private void loadTopExamTypes() {
        vbTopExamTypes.getChildren().clear();

        // Count registrations by exam type
        List<Registration> registrations = registrationDAO.findAll();
        Map<Integer, Long> examTypeCounts = registrations.stream()
                .collect(Collectors.groupingBy(Registration::getExamTypeId, Collectors.counting()));

        // Sort by count and get top 5
        List<Map.Entry<Integer, Long>> topExamTypes = examTypeCounts.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        Label topTitle = new Label("Loại thi phổ biến:");
        topTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        vbTopExamTypes.getChildren().add(topTitle);

        for (Map.Entry<Integer, Long> entry : topExamTypes) {
            examTypeDAO.get(entry.getKey()).ifPresent(examType -> {
                Label examLabel = new Label(String.format(
                        "• %s: %d đăng ký",
                        examType.getName(),
                        entry.getValue()));
                examLabel.setWrapText(true);
                vbTopExamTypes.getChildren().add(examLabel);
            });
        }
    }

    private void loadMonthlyStats() {
        vbMonthlyStats.getChildren().clear();

        // Get current month stats
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate nextMonth = currentMonth.plusMonths(1);

        List<Registration> monthlyRegistrations = registrationDAO.findAll().stream()
                .filter(reg -> reg.getRegistrationDate().isAfter(currentMonth.minusDays(1)) &&
                        reg.getRegistrationDate().isBefore(nextMonth))
                .collect(Collectors.toList());

        List<Result> monthlyResults = resultDAO.findAll().stream()
                .filter(result -> result.getExamDate().isAfter(currentMonth.minusDays(1)) &&
                        result.getExamDate().isBefore(nextMonth))
                .collect(Collectors.toList());

        long monthlyPassed = monthlyResults.stream()
                .filter(result -> result.getStatus() == ResultStatus.PASSED)
                .count();

        Label monthlyTitle = new Label("Thống kê tháng này:");
        monthlyTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        vbMonthlyStats.getChildren().add(monthlyTitle);

        Label regLabel = new Label(String.format("• Đăng ký: %d", monthlyRegistrations.size()));
        Label resultLabel = new Label(String.format("• Kết quả: %d", monthlyResults.size()));
        Label passedLabel = new Label(String.format("• Đậu: %d", monthlyPassed));

        vbMonthlyStats.getChildren().addAll(regLabel, resultLabel, passedLabel);
    }

    private String formatCurrency(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }

    @Override
    protected void clearForm() {
        // Not applicable for statistics
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        // Not applicable for statistics
    }
}
