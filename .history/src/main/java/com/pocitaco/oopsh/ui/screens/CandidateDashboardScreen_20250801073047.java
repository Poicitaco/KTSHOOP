package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.RegistrationDAO;
import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.dao.CertificateDAO;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.models.Registration;
import com.pocitaco.oopsh.models.ExamSchedule;
import com.pocitaco.oopsh.models.ExamType;
import com.pocitaco.oopsh.models.Certificate;
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
 * Candidate Dashboard Screen - Material Design 3.0
 * Giao diện dashboard cho thí sinh với các chức năng thi cử
 */
public class CandidateDashboardScreen {

    private Stage primaryStage;
    private Scene scene;
    private BorderPane mainLayout;
    private VBox navigationRail;
    private StackPane contentArea;
    private User currentUser;

    // DAOs
    private RegistrationDAO registrationDAO;
    private ExamScheduleDAO examScheduleDAO;
    private ExamTypeDAO examTypeDAO;
    private CertificateDAO certificateDAO;

    // Utilities
    private final Logger logger = Logger.getInstance();
    private final PerformanceMonitor performanceMonitor = PerformanceMonitor.getInstance();

    public CandidateDashboardScreen(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.registrationDAO = new RegistrationDAO();
        this.examScheduleDAO = new ExamScheduleDAO();
        this.examTypeDAO = new ExamTypeDAO();
        this.certificateDAO = new CertificateDAO();

        logger.userAction(currentUser.getUsername(), "CandidateDashboardScreen", "Screen initialized");
        performanceMonitor.startOperation("CandidateDashboardScreen_Initialization");

        try {
            initializeUI();
            logger.info("CandidateDashboardScreen initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize CandidateDashboardScreen", e);
        } finally {
            performanceMonitor.endOperation("CandidateDashboardScreen_Initialization");
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

    public BorderPane getMainLayout() {
        return mainLayout;
    }

    public Scene getScene() {
        return scene;
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
        Label title = new Label("🎓 Candidate Dashboard - OOPSH");
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
        FontIcon avatarIcon = Icons.createAccountIcon();
        avatarIcon.setIconSize(24);
        avatarIcon.setIconColor(Colors.PRIMARY);

        // User name
        Label userName = new Label(currentUser.getFullName() + " (Candidate)");
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
                createNavItem("Exams", Icons.createFileDocumentIcon(), false),
                createNavItem("Register", Icons.createAccountPlusIcon(), false),
                createNavItem("Results", Icons.createChartBarIcon(), false),
                createNavItem("Materials", Icons.createFileDocumentIcon(), false),
                createNavItem("Certificates", Icons.createCertificateIcon(), false));

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
        Label pageTitle = new Label("📊 Candidate Dashboard Overview");
        pageTitle.setFont(Typography.HEADLINE_MEDIUM);
        pageTitle.setTextFill(Colors.ON_BACKGROUND);

        // Stats cards
        HBox statsRow = createStatsRow();

        // Upcoming exams
        VBox upcomingExams = createUpcomingExamsSection();

        // Quick actions
        VBox quickActions = createQuickActionsSection();

        dashboardContent.getChildren().addAll(
                pageTitle,
                statsRow,
                upcomingExams,
                quickActions);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardContent);
    }

    private HBox createStatsRow() {
        HBox statsRow = new HBox();
        statsRow.setSpacing(16);
        statsRow.setAlignment(Pos.CENTER);

        try {
            // Get candidate's registrations
            List<Registration> myRegistrations = registrationDAO.findByUserId(currentUser.getId());

            VBox registrationsCard = createStatCard("📝 Đăng ký",
                    String.valueOf(myRegistrations.size()),
                    Icons.createFileDocumentIcon(), Colors.PRIMARY);

            // Get upcoming exams count
            VBox upcomingCard = createStatCard("📅 Kỳ thi sắp tới",
                    String.valueOf(getUpcomingExamsCount()),
                    Icons.createCalendarIcon(), Colors.WARNING);

            // Get completed exams count
            VBox completedCard = createStatCard("✅ Đã hoàn thành",
                    String.valueOf(getCompletedExamsCount()),
                    Icons.createFileDocumentIcon(), Colors.SUCCESS);

            // Certificates earned
            VBox certificatesCard = createStatCard("🏆 Chứng chỉ",
                    String.valueOf(getCertificatesCount()),
                    Icons.createCertificateIcon(), Colors.SECONDARY);

            statsRow.getChildren().addAll(registrationsCard, upcomingCard, completedCard, certificatesCard);
        } catch (Exception e) {
            System.err.println("❌ Error loading candidate statistics: " + e.getMessage());
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

    private VBox createUpcomingExamsSection() {
        VBox section = new VBox();
        section.setSpacing(16);

        Label sectionTitle = new Label("📅 Kỳ thi sắp tới");
        sectionTitle.setFont(Typography.TITLE_LARGE);
        sectionTitle.setTextFill(Colors.ON_BACKGROUND);

        VBox examsCard = Cards.createCard();
        examsCard.setSpacing(12);

        try {
            // Get upcoming exams for this candidate
            List<Registration> upcomingRegistrations = registrationDAO.findByUserId(currentUser.getId());

            if (upcomingRegistrations.isEmpty()) {
                Label noExamLabel = new Label("📚 Chưa có kỳ thi nào được đăng ký");
                noExamLabel.setFont(Typography.BODY_LARGE);
                noExamLabel.setTextFill(Colors.ON_SURFACE_VARIANT);
                examsCard.getChildren().add(noExamLabel);
            } else {
                // Show first 3 upcoming exams
                int count = Math.min(3, upcomingRegistrations.size());
                for (int i = 0; i < count; i++) {
                    Registration registration = upcomingRegistrations.get(i);
                    HBox examItem = createExamItem(registration);
                    examsCard.getChildren().add(examItem);
                }

                if (upcomingRegistrations.size() > 3) {
                    Button viewAllButton = Buttons.createTextButton("Xem tất cả", null);
                    viewAllButton.setOnAction(e -> openMyRegistrationsScreen());
                    examsCard.getChildren().add(viewAllButton);
                }
            }
        } catch (Exception e) {
            Label errorLabel = new Label("❌ Không thể tải thông tin kỳ thi");
            errorLabel.setTextFill(Colors.ERROR);
            examsCard.getChildren().add(errorLabel);
        }

        section.getChildren().addAll(sectionTitle, examsCard);
        return section;
    }

    private HBox createExamItem(Registration registration) {
        HBox item = new HBox();
        item.setAlignment(Pos.CENTER_LEFT);
        item.setSpacing(16);
        item.setPadding(new Insets(12));
        item.setStyle("-fx-background-color: #F7F2FA; -fx-background-radius: 8;");

        FontIcon examIcon = Icons.createFileDocumentIcon();
        examIcon.setIconSize(20);
        examIcon.setIconColor(Colors.PRIMARY);

        VBox textInfo = new VBox();
        textInfo.setSpacing(4);

        // TODO: Get exam type name by ID
        Label examLabel = new Label("Kỳ thi #" + registration.getExamTypeId());
        examLabel.setFont(Typography.BODY_MEDIUM);
        examLabel.setTextFill(Colors.ON_SURFACE);

        Label statusLabel = new Label("Trạng thái: " + registration.getStatus());
        statusLabel.setFont(Typography.BODY_SMALL);
        statusLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        textInfo.getChildren().addAll(examLabel, statusLabel);

        Button viewButton = Buttons.createOutlinedButton("Chi tiết", null);
        viewButton.setOnAction(e -> viewExamDetails(registration));

        item.getChildren().addAll(examIcon, textInfo, viewButton);
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
        Button registerBtn = Buttons.createFilledButton("📝 Đăng ký thi", Icons.createAccountPlusIcon());
        registerBtn.setOnAction(e -> openRegisterExamScreen());

        Button practiceBtn = Buttons.createFilledButton("📚 Luyện tập", Icons.createFileDocumentIcon());
        practiceBtn.setOnAction(e -> openPracticeTestsScreen());

        Button resultsBtn = Buttons.createFilledButton("📊 Xem kết quả", Icons.createChartBarIcon());
        resultsBtn.setOnAction(e -> openResultsScreen());

        Button certificatesBtn = Buttons.createOutlinedButton("🏆 Chứng chỉ", Icons.createCertificateIcon());
        certificatesBtn.setOnAction(e -> openCertificatesScreen());

        actionsRow.getChildren().addAll(registerBtn, practiceBtn, resultsBtn, certificatesBtn);

        section.getChildren().addAll(sectionTitle, actionsRow);
        return section;
    }

    private void handleNavItemClick(String itemName) {
        System.out.println("Candidate navigating to: " + itemName);

        switch (itemName) {
            case "Exams":
                openAvailableExamsScreen();
                break;
            case "Register":
                openRegisterExamScreen();
                break;
            case "Results":
                openResultsScreen();
                break;
            case "Materials":
                openStudyMaterialsScreen();
                break;
            case "Certificates":
                openCertificatesScreen();
                break;
            case "Dashboard":
                loadDashboardContent();
                break;
            default:
                System.out.println("Unknown navigation item: " + itemName);
                break;
        }
    }

    private void viewExamDetails(Registration registration) {
        System.out.println("Viewing exam details: " + registration.getId());
        showAlert("Chi tiết kỳ thi", "Đang mở thông tin chi tiết kỳ thi");
    }

    private void openMyRegistrationsScreen() {
        System.out.println("Opening my registrations screen");
        showAlert("Đăng ký của tôi", "Đang mở danh sách đăng ký");
    }

    private void openAvailableExamsScreen() {
        System.out.println("Opening available exams screen");
        showAlert("Kỳ thi khả dụng", "Đang mở danh sách kỳ thi");
    }

    private void openRegisterExamScreen() {
        System.out.println("Opening register exam screen");
        showAlert("Đăng ký thi", "Đang mở giao diện đăng ký thi");
    }

    private void openPracticeTestsScreen() {
        System.out.println("📚 Opening practice tests screen");
        showAlert("📚 Luyện tập", "Đang mở giao diện luyện tập");
    }

    private void openResultsScreen() {
        System.out.println("📊 Opening results screen");
        showAlert("📊 Kết quả", "Đang mở giao diện kết quả thi");
    }

    private void openStudyMaterialsScreen() {
        System.out.println("📖 Opening study materials screen");
        showAlert("📖 Tài liệu", "Đang mở giao diện tài liệu học tập");
    }

    private void openCertificatesScreen() {
        System.out.println("🏆 Opening certificates screen");
        showAlert("🏆 Chứng chỉ", "Đang mở giao diện chứng chỉ");
    }

    private void handleLogout() {
        System.out.println("🚪 Candidate đăng xuất...");
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
        System.out.println("✅ Candidate Dashboard displayed successfully!");
    }

    // Statistics Methods Implementation
    private int getUpcomingExamsCount() {
        try {
            performanceMonitor.startOperation("GetUpcomingExamsCount");
            logger.database("SELECT", "exam_schedules",
                    "Getting upcoming exams count for user: " + currentUser.getUsername());

            List<Registration> registrations = registrationDAO.findByUserId(currentUser.getId());
            int upcomingCount = 0;

            for (Registration registration : registrations) {
                if ("REGISTERED".equals(registration.getStatus()) || "PENDING".equals(registration.getStatus())) {
                    // Check if the exam date is in the future
                    try {
                        ExamType examType = examTypeDAO.findById(registration.getExamTypeId()).orElse(null);
                        if (examType != null) {
                            // For now, we'll count all registered/pending exams as upcoming
                            // In a real system, you'd check against actual exam schedules
                            upcomingCount++;
                        }
                    } catch (Exception e) {
                        logger.warning("Error checking exam type for registration " + registration.getId() + ": "
                                + e.getMessage());
                    }
                }
            }

            logger.info("Found " + upcomingCount + " upcoming exams for user");
            performanceMonitor.endOperation("GetUpcomingExamsCount");
            return upcomingCount;
        } catch (Exception e) {
            logger.error("Error getting upcoming exams count", e);
            performanceMonitor.endOperation("GetUpcomingExamsCount");
            return 0;
        }
    }

    private int getCompletedExamsCount() {
        try {
            performanceMonitor.startOperation("GetCompletedExamsCount");
            logger.database("SELECT", "registrations",
                    "Getting completed exams count for user: " + currentUser.getUsername());

            List<Registration> registrations = registrationDAO.findByUserId(currentUser.getId());
            int completedCount = (int) registrations.stream()
                    .filter(reg -> "COMPLETED".equals(reg.getStatus()))
                    .count();

            logger.info("Found " + completedCount + " completed exams for user");
            performanceMonitor.endOperation("GetCompletedExamsCount");
            return completedCount;
        } catch (Exception e) {
            logger.error("Error getting completed exams count", e);
            performanceMonitor.endOperation("GetCompletedExamsCount");
            return 0;
        }
    }

    private int getCertificatesCount() {
        try {
            performanceMonitor.startOperation("GetCertificatesCount");
            logger.database("SELECT", "certificates",
                    "Getting certificates count for user: " + currentUser.getUsername());

            List<Certificate> certificates = certificateDAO.findByUserId(currentUser.getId());
            int certificateCount = certificates.size();

            logger.info("Found " + certificateCount + " certificates for user");
            performanceMonitor.endOperation("GetCertificatesCount");
            return certificateCount;
        } catch (Exception e) {
            logger.error("Error getting certificates count", e);
            performanceMonitor.endOperation("GetCertificatesCount");
            return 0;
        }
    }
}
