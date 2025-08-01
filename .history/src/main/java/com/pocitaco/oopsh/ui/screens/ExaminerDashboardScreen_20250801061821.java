package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.dao.SessionReportDAO;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.models.ExamSchedule;
import com.pocitaco.oopsh.models.Result;
import com.pocitaco.oopsh.models.SessionReport;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Colors;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Typography;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Icons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Buttons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Cards;
import com.pocitaco.oopsh.utils.Logger;
import com.pocitaco.oopsh.utils.PerformanceMonitor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Examiner Dashboard Screen - Material Design 3.0
 * Giao diện dashboard cho giám khảo với các chức năng chấm thi
 */
public class ExaminerDashboardScreen {

    private Stage primaryStage;
    private Scene scene;
    private BorderPane mainLayout;
    private VBox navigationRail;
    private StackPane contentArea;
    private User currentUser;

    // DAOs
    private ExamScheduleDAO examScheduleDAO;
    private ResultDAO resultDAO;
    private SessionReportDAO sessionReportDAO;

    // Utilities
    private final Logger logger = Logger.getInstance();
    private final PerformanceMonitor performanceMonitor = PerformanceMonitor.getInstance();

    public ExaminerDashboardScreen(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.examScheduleDAO = new ExamScheduleDAO();
        this.resultDAO = new ResultDAO();
        this.sessionReportDAO = new SessionReportDAO();
        
        logger.userAction(currentUser.getUsername(), "ExaminerDashboardScreen", "Screen initialized");
        performanceMonitor.startOperation("ExaminerDashboardScreen_Initialization");
        
        try {
            initializeUI();
            logger.info("ExaminerDashboardScreen initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize ExaminerDashboardScreen", e);
        } finally {
            performanceMonitor.endOperation("ExaminerDashboardScreen_Initialization");
        }
    }

    private void initializeUI() {
        // Main layout
        mainLayout = new BorderPane();
        mainLayout.setBackground(new Background(new BackgroundFill(
                Colors.BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        // Create components
        HBox appBar = createAppBar();
        navigationRail = createNavigationRail();
        contentArea = createContentArea();

        // Layout setup
        mainLayout.setTop(appBar);
        mainLayout.setLeft(navigationRail);
        mainLayout.setCenter(contentArea);

        // Create scene
        scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setScene(scene);

        // Load default content
        loadDashboardContent();
    }

    private HBox createAppBar() {
        HBox appBar = new HBox();
        appBar.setAlignment(Pos.CENTER_LEFT);
        appBar.setSpacing(16);
        appBar.setPadding(new Insets(0, 24, 0, 24));
        appBar.setPrefHeight(64);
        appBar.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Title
        Label title = new Label("👨‍🏫 Examiner Dashboard - OOPSH");
        title.setFont(Typography.TITLE_LARGE);
        title.setTextFill(Colors.PRIMARY);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // User info
        HBox userInfo = createUserInfo();

        // Settings button
        Button settingsButton = Buttons.createIconButton(Icons.createSettingsIcon());

        // Logout button
        Button logoutButton = Buttons.createIconButton(Icons.createLogoutIcon());
        logoutButton.setOnAction(e -> handleLogout());

        appBar.getChildren().addAll(title, spacer, userInfo, settingsButton, logoutButton);

        // Add elevation
        appBar.setEffect(createElevation(2));

        return appBar;
    }

    private HBox createUserInfo() {
        HBox userInfo = new HBox();
        userInfo.setAlignment(Pos.CENTER);
        userInfo.setSpacing(12);

        // User avatar (using icon)
        FontIcon avatarIcon = Icons.createSchoolIcon();
        avatarIcon.setIconSize(24);
        avatarIcon.setIconColor(Colors.PRIMARY);

        // User name
        Label userName = new Label(currentUser.getFullName() + " (Examiner)");
        userName.setFont(Typography.BODY_MEDIUM);
        userName.setTextFill(Colors.ON_SURFACE);

        userInfo.getChildren().addAll(avatarIcon, userName);
        return userInfo;
    }

    private VBox createNavigationRail() {
        VBox navRail = new VBox();
        navRail.setAlignment(Pos.TOP_CENTER);
        navRail.setSpacing(8);
        navRail.setPadding(new Insets(16, 8, 16, 8));
        navRail.setPrefWidth(80);
        navRail.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Navigation items
        navRail.getChildren().addAll(
                createNavItem("Dashboard", Icons.createDashboardIcon(), true),
                createNavItem("Schedule", Icons.createCalendarIcon(), false),
                createNavItem("Grading", Icons.createSchoolIcon(), false),
                createNavItem("Sessions", Icons.createClockIcon(), false),
                createNavItem("Reports", Icons.createChartBarIcon(), false),
                createNavItem("Candidates", Icons.createAccountGroupIcon(), false));

        return navRail;
    }

    private VBox createNavItem(String text, FontIcon icon, boolean selected) {
        VBox navItem = new VBox();
        navItem.setAlignment(Pos.CENTER);
        navItem.setSpacing(4);
        navItem.setPadding(new Insets(8));
        navItem.setPrefWidth(64);
        navItem.setPrefHeight(64);

        // Set background and styling based on selection
        if (selected) {
            navItem.setBackground(new Background(new BackgroundFill(
                    Colors.PRIMARY_CONTAINER, new CornerRadii(16), Insets.EMPTY)));
            icon.setIconColor(Colors.ON_PRIMARY_CONTAINER);
        } else {
            navItem.setBackground(Background.EMPTY);
            icon.setIconColor(Colors.ON_SURFACE_VARIANT);
        }

        icon.setIconSize(24);

        Label label = new Label(text);
        label.setFont(Typography.LABEL_SMALL);
        label.setTextFill(selected ? Colors.ON_PRIMARY_CONTAINER : Colors.ON_SURFACE_VARIANT);

        navItem.getChildren().addAll(icon, label);

        // Add click handler
        navItem.setOnMouseClicked(e -> handleNavItemClick(text));
        navItem.setStyle("-fx-cursor: hand;");

        return navItem;
    }

    private StackPane createContentArea() {
        StackPane content = new StackPane();
        content.setPadding(new Insets(24));
        content.setBackground(new Background(new BackgroundFill(
                Colors.BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        return content;
    }

    private void loadDashboardContent() {
        VBox dashboardContent = new VBox();
        dashboardContent.setSpacing(24);
        dashboardContent.setAlignment(Pos.TOP_LEFT);

        // Page title
        Label pageTitle = new Label("📊 Examiner Dashboard Overview");
        pageTitle.setFont(Typography.HEADLINE_MEDIUM);
        pageTitle.setTextFill(Colors.ON_BACKGROUND);

        // Stats cards
        HBox statsRow = createStatsRow();

        // Today's schedule
        VBox todaySchedule = createTodayScheduleSection();

        // Quick actions
        VBox quickActions = createQuickActionsSection();

        dashboardContent.getChildren().addAll(
                pageTitle,
                statsRow,
                todaySchedule,
                quickActions);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardContent);
    }

    private HBox createStatsRow() {
        HBox statsRow = new HBox();
        statsRow.setSpacing(16);
        statsRow.setAlignment(Pos.CENTER);

        try {
            // Get today's sessions assigned to this examiner
            List<ExamSchedule> todaySchedules = examScheduleDAO.findByExaminerId(currentUser.getId());

            VBox todaySessionsCard = createStatCard("📅 Ca thi hôm nay",
                    String.valueOf(todaySchedules.size()),
                    Icons.createCalendarIcon(), Colors.PRIMARY);

            // Get pending grading count
            VBox pendingGradingCard = createStatCard("📝 Chờ chấm điểm",
                    "0", // TODO: implement getPendingGradingCount()
                    Icons.createSchoolIcon(), Colors.WARNING);

            // Get completed sessions this month
            VBox completedCard = createStatCard("✅ Đã hoàn thành",
                    "0", // TODO: implement getCompletedSessionsCount()
                    Icons.createFileDocumentIcon(), Colors.SUCCESS);

            // Average score
            VBox avgScoreCard = createStatCard("📈 Điểm TB",
                    "0.0", // TODO: implement getAverageScore()
                    Icons.createChartBarIcon(), Colors.SECONDARY);

            statsRow.getChildren().addAll(todaySessionsCard, pendingGradingCard, completedCard, avgScoreCard);
        } catch (Exception e) {
            System.err.println("❌ Error loading examiner statistics: " + e.getMessage());
            // Fallback card
            VBox fallbackCard = createStatCard("📊 Thống kê", "N/A", Icons.createChartBarIcon(), Colors.PRIMARY);
            statsRow.getChildren().add(fallbackCard);
        }

        return statsRow;
    }

    private VBox createStatCard(String title, String value, FontIcon icon, Color accentColor) {
        VBox card = Cards.createCard();
        card.setPrefWidth(280);
        card.setSpacing(16);

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(16);

        icon.setIconSize(32);
        icon.setIconColor(accentColor);

        VBox textInfo = new VBox();
        textInfo.setSpacing(4);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Typography.BODY_MEDIUM);
        titleLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Typography.HEADLINE_SMALL);
        valueLabel.setTextFill(Colors.ON_SURFACE);

        textInfo.getChildren().addAll(titleLabel, valueLabel);
        header.getChildren().addAll(icon, textInfo);

        card.getChildren().add(header);
        return card;
    }

    private VBox createTodayScheduleSection() {
        VBox section = new VBox();
        section.setSpacing(16);

        Label sectionTitle = new Label("📅 Lịch thi hôm nay");
        sectionTitle.setFont(Typography.TITLE_LARGE);
        sectionTitle.setTextFill(Colors.ON_BACKGROUND);

        VBox scheduleCard = Cards.createCard();
        scheduleCard.setSpacing(12);

        try {
            List<ExamSchedule> todaySchedules = examScheduleDAO.findByExaminerId(currentUser.getId());

            if (todaySchedules.isEmpty()) {
                Label noScheduleLabel = new Label("🎉 Không có ca thi nào hôm nay");
                noScheduleLabel.setFont(Typography.BODY_LARGE);
                noScheduleLabel.setTextFill(Colors.ON_SURFACE_VARIANT);
                scheduleCard.getChildren().add(noScheduleLabel);
            } else {
                for (ExamSchedule schedule : todaySchedules) {
                    HBox scheduleItem = createScheduleItem(schedule);
                    scheduleCard.getChildren().add(scheduleItem);
                }
            }
        } catch (Exception e) {
            Label errorLabel = new Label("❌ Không thể tải lịch thi");
            errorLabel.setTextFill(Colors.ERROR);
            scheduleCard.getChildren().add(errorLabel);
        }

        section.getChildren().addAll(sectionTitle, scheduleCard);
        return section;
    }

    private HBox createScheduleItem(ExamSchedule schedule) {
        HBox item = new HBox();
        item.setAlignment(Pos.CENTER_LEFT);
        item.setSpacing(16);
        item.setPadding(new Insets(12));
        item.setStyle("-fx-background-color: #F7F2FA; -fx-background-radius: 8;");

        FontIcon timeIcon = Icons.createClockIcon();
        timeIcon.setIconSize(20);
        timeIcon.setIconColor(Colors.PRIMARY);

        VBox textInfo = new VBox();
        textInfo.setSpacing(4);

        Label timeLabel = new Label(schedule.getTimeSlot().toString());
        timeLabel.setFont(Typography.BODY_MEDIUM);
        timeLabel.setTextFill(Colors.ON_SURFACE);

        Label examLabel = new Label("Phòng: " + schedule.getLocation());
        examLabel.setFont(Typography.BODY_SMALL);
        examLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        textInfo.getChildren().addAll(timeLabel, examLabel);

        Button startButton = Buttons.createFilledButton("Bắt đầu", Icons.createFileDocumentIcon());
        startButton.setOnAction(e -> startExamSession(schedule));

        item.getChildren().addAll(timeIcon, textInfo, startButton);
        return item;
    }

    private VBox createQuickActionsSection() {
        VBox section = new VBox();
        section.setSpacing(16);

        Label sectionTitle = new Label("⚡ Thao tác nhanh");
        sectionTitle.setFont(Typography.TITLE_LARGE);
        sectionTitle.setTextFill(Colors.ON_BACKGROUND);

        HBox actionsRow = new HBox();
        actionsRow.setSpacing(16);
        actionsRow.setAlignment(Pos.CENTER_LEFT);

        // Quick action buttons
        Button gradingBtn = Buttons.createFilledButton("📝 Chấm điểm", Icons.createSchoolIcon());
        gradingBtn.setOnAction(e -> openGradingScreen());

        Button sessionReportBtn = Buttons.createFilledButton("📊 Báo cáo ca thi", Icons.createFileDocumentIcon());
        sessionReportBtn.setOnAction(e -> openSessionReportsScreen());

        Button scheduleBtn = Buttons.createFilledButton("📅 Xem lịch thi", Icons.createCalendarIcon());
        scheduleBtn.setOnAction(e -> openScheduleScreen());

        Button candidatesBtn = Buttons.createOutlinedButton("👥 Quản lý thí sinh", Icons.createAccountGroupIcon());
        candidatesBtn.setOnAction(e -> openCandidatesScreen());

        actionsRow.getChildren().addAll(gradingBtn, sessionReportBtn, scheduleBtn, candidatesBtn);

        section.getChildren().addAll(sectionTitle, actionsRow);
        return section;
    }

    private void handleNavItemClick(String itemName) {
        System.out.println("🔄 Examiner navigating to: " + itemName);

        switch (itemName) {
            case "Schedule":
                openScheduleScreen();
                break;
            case "Grading":
                openGradingScreen();
                break;
            case "Sessions":
                openSessionReportsScreen();
                break;
            case "Reports":
                showAlert("📊 Báo cáo", "Đang mở giao diện báo cáo");
                break;
            case "Candidates":
                openCandidatesScreen();
                break;
            case "Dashboard":
                loadDashboardContent();
                break;
            default:
                System.out.println("🔍 Unknown navigation item: " + itemName);
                break;
        }
    }

    private void startExamSession(ExamSchedule schedule) {
        System.out.println("🎯 Starting exam session: " + schedule.getId());
        showAlert("🎯 Bắt đầu ca thi", "Đang mở giao diện giám sát ca thi");
    }

    private void openGradingScreen() {
        System.out.println("📝 Opening grading screen");
        showAlert("📝 Chấm điểm", "Đang mở giao diện chấm điểm");
    }

    private void openSessionReportsScreen() {
        System.out.println("📊 Opening session reports screen");
        showAlert("📊 Báo cáo ca thi", "Đang mở giao diện báo cáo ca thi");
    }

    private void openScheduleScreen() {
        System.out.println("📅 Opening schedule screen");
        showAlert("📅 Lịch thi", "Đang mở giao diện lịch thi");
    }

    private void openCandidatesScreen() {
        System.out.println("👥 Opening candidates screen");
        showAlert("👥 Thí sinh", "Đang mở giao diện quản lý thí sinh");
    }

    private void handleLogout() {
        System.out.println("🚪 Examiner đăng xuất...");
        LoginScreen loginScreen = new LoginScreen(primaryStage);
        loginScreen.show();
    }

    private javafx.scene.effect.DropShadow createElevation(int level) {
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.2));
        shadow.setOffsetY(level * 2);
        shadow.setRadius(level * 4);
        return shadow;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("✅ Examiner Dashboard displayed successfully!");
    }
}
