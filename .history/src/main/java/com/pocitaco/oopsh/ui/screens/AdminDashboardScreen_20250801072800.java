package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Colors;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Typography;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Icons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Buttons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Cards;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Admin Dashboard Screen - Material Design 3.0
 * Giao diện dashboard quản trị với điều hướng tới các chức năng admin
 */
public class AdminDashboardScreen {

    private Stage primaryStage;
    private Scene scene;
    private BorderPane mainLayout;
    private VBox navigationRail;
    private StackPane contentArea;
    private User currentUser;

    // DAOs for statistics
    private UserDAO userDAO;
    private ExamScheduleDAO examScheduleDAO;
    private ExamTypeDAO examTypeDAO;

    public AdminDashboardScreen(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();
        this.examScheduleDAO = new ExamScheduleDAO();
        this.examTypeDAO = new ExamTypeDAO();
        initializeUI();
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
        Label title = new Label("Admin Dashboard - OOPSH");
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
        FontIcon avatarIcon = Icons.createShieldAccountIcon();
        avatarIcon.setIconSize(24);
        avatarIcon.setIconColor(Colors.PRIMARY);

        // User name
        String displayName = currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername();
        Label userName = new Label(displayName + " (Admin)");
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
                createNavItem("Users", Icons.createAccountGroupIcon(), false),
                createNavItem("ExamTypes", Icons.createFileDocumentIcon(), false),
                createNavItem("Schedules", Icons.createCalendarIcon(), false),
                createNavItem("Reports", Icons.createChartBarIcon(), false),
                createNavItem("Settings", Icons.createSettingsIcon(), false));

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
        Label pageTitle = new Label("Admin Dashboard Overview");
        pageTitle.setFont(Typography.HEADLINE_MEDIUM);
        pageTitle.setTextFill(Colors.ON_BACKGROUND);

        // Stats cards
        HBox statsRow = createStatsRow();
        HBox extraStatsRow = createExtraStatsRow();

        // Quick actions section
        VBox quickActions = createQuickActionsSection();

        // Recent activity
        VBox recentActivity = createRecentActivitySection();

        dashboardContent.getChildren().addAll(
                pageTitle,
                statsRow,
                extraStatsRow,
                quickActions,
                recentActivity);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardContent);
    }

    private HBox createStatsRow() {
        HBox statsRow = new HBox();
        statsRow.setSpacing(16);
        statsRow.setAlignment(Pos.CENTER);

        try {
            // Create stat cards with real data - First row
            VBox usersCard = createStatCard("Tổng người dùng",
                    String.valueOf(userDAO.getAllUsers().size()),
                    Icons.createAccountGroupIcon(), Colors.PRIMARY);

            VBox adminsCard = createStatCard("Quản trị viên",
                    String.valueOf(userDAO.getUsersByRole(UserRole.ADMIN).size()),
                    Icons.createShieldAccountIcon(), Colors.ERROR);

            VBox examinersCard = createStatCard("Giám khảo",
                    String.valueOf(userDAO.getUsersByRole(UserRole.EXAMINER).size()),
                    Icons.createSchoolIcon(), Colors.WARNING);

            VBox candidatesCard = createStatCard("Thí sinh",
                    String.valueOf(userDAO.getUsersByRole(UserRole.CANDIDATE).size()),
                    Icons.createAccountIcon(), Colors.SUCCESS);

            statsRow.getChildren().addAll(usersCard, adminsCard, examinersCard, candidatesCard);
        } catch (Exception e) {
            System.err.println("❌ Error loading statistics: " + e.getMessage());
            // Fallback to sample data
            VBox usersCard = createStatCard("👥 Tổng người dùng", "N/A", Icons.createAccountGroupIcon(),
                    Colors.PRIMARY);
            statsRow.getChildren().add(usersCard);
        }

        return statsRow;
    }

    private HBox createExtraStatsRow() {
        HBox extraStatsRow = new HBox();
        extraStatsRow.setSpacing(16);
        extraStatsRow.setAlignment(Pos.CENTER);

        try {
            // Second row of stats
            VBox examTypesCard = createStatCard("Loại thi",
                    String.valueOf(examTypeDAO.getAll().size()),
                    Icons.createFileDocumentIcon(), Colors.SECONDARY);

            VBox schedulesCard = createStatCard("Lịch thi",
                    String.valueOf(examScheduleDAO.getAllSchedules().size()),
                    Icons.createCalendarIcon(), Colors.WARNING);

            extraStatsRow.getChildren().addAll(examTypesCard, schedulesCard);
        } catch (Exception e) {
            System.err.println("❌ Error loading extra statistics: " + e.getMessage());
        }

        return extraStatsRow;
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

    private VBox createQuickActionsSection() {
        VBox section = new VBox();
        section.setSpacing(16);

        Label sectionTitle = new Label("Thao tác nhanh");
        sectionTitle.setFont(Typography.TITLE_LARGE);
        sectionTitle.setTextFill(Colors.ON_BACKGROUND);

        HBox actionsRow = new HBox();
        actionsRow.setSpacing(16);
        actionsRow.setAlignment(Pos.CENTER_LEFT);

        // Quick action buttons
        Button manageUsersBtn = Buttons.createFilledButton("Quản lý người dùng", Icons.createAccountGroupIcon());
        manageUsersBtn.setOnAction(e -> navigateToUserManagement());

        Button manageExamTypesBtn = Buttons.createFilledButton("Quản lý loại thi", Icons.createFileDocumentIcon());
        manageExamTypesBtn.setOnAction(e -> navigateToExamTypeManagement());

        Button manageSchedulesBtn = Buttons.createFilledButton("Quản lý lịch thi", Icons.createCalendarIcon());
        manageSchedulesBtn.setOnAction(e -> navigateToScheduleManagement());

        Button viewReportsBtn = Buttons.createOutlinedButton("Xem báo cáo", Icons.createChartBarIcon());

        actionsRow.getChildren().addAll(manageUsersBtn, manageExamTypesBtn, manageSchedulesBtn, viewReportsBtn);

        section.getChildren().addAll(sectionTitle, actionsRow);
        return section;
    }

    private VBox createRecentActivitySection() {
        VBox section = new VBox();
        section.setSpacing(16);

        Label sectionTitle = new Label("Hoạt động gần đây");
        sectionTitle.setFont(Typography.TITLE_LARGE);
        sectionTitle.setTextFill(Colors.ON_BACKGROUND);

        VBox activityCard = Cards.createCard();
        activityCard.setSpacing(12);

        // Sample activities
        activityCard.getChildren().addAll(
                createActivityItem("Thí sinh mới đăng ký", "5 phút trước", Icons.createAccountPlusIcon()),
                createActivityItem("Kỳ thi được lên lịch", "1 giờ trước", Icons.createCalendarIcon()),
                createActivityItem("Thanh toán được xử lý", "2 giờ trước", Icons.createCreditCardIcon()),
                createActivityItem("Chứng chỉ được cấp", "3 giờ trước", Icons.createCertificateIcon()));

        section.getChildren().addAll(sectionTitle, activityCard);
        return section;
    }

    private HBox createActivityItem(String activity, String time, FontIcon icon) {
        HBox item = new HBox();
        item.setAlignment(Pos.CENTER_LEFT);
        item.setSpacing(16);
        item.setPadding(new Insets(8, 0, 8, 0));

        icon.setIconSize(20);
        icon.setIconColor(Colors.PRIMARY);

        VBox textInfo = new VBox();
        textInfo.setSpacing(2);

        Label activityLabel = new Label(activity);
        activityLabel.setFont(Typography.BODY_MEDIUM);
        activityLabel.setTextFill(Colors.ON_SURFACE);

        Label timeLabel = new Label(time);
        timeLabel.setFont(Typography.BODY_SMALL);
        timeLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        textInfo.getChildren().addAll(activityLabel, timeLabel);

        item.getChildren().addAll(icon, textInfo);
        return item;
    }

    private void handleNavItemClick(String itemName) {
        System.out.println("🔄 Admin navigating to: " + itemName);

        switch (itemName) {
            case "Users":
                navigateToUserManagement();
                break;
            case "ExamTypes":
                navigateToExamTypeManagement();
                break;
            case "Schedules":
                navigateToScheduleManagement();
                break;
            case "Reports":
                openReportsScreen();
                break;
            case "Settings":
                openSettingsScreen();
                break;
            case "Dashboard":
                loadDashboardContent();
                break;
            default:
                System.out.println("🔍 Unknown navigation item: " + itemName);
                break;
        }
    }

    private void navigateToUserManagement() {
        System.out.println("🔄 Opening User Management Screen...");
        try {
            UserManagementScreen userManagement = new UserManagementScreen(primaryStage);
            Scene scene = userManagement.createScene();
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error opening User Management Screen: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi", "Không thể mở màn hình quản lý người dùng: " + e.getMessage());
        }
    }

    private void navigateToExamTypeManagement() {
        System.out.println("🔄 Opening Exam Type Management Screen...");
        try {
            ExamTypeManagementScreen examTypeManagement = new ExamTypeManagementScreen(primaryStage);
            Scene scene = examTypeManagement.createScene();
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error opening Exam Type Management Screen: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi", "Không thể mở màn hình quản lý loại thi: " + e.getMessage());
        }
    }

    private void navigateToScheduleManagement() {
        System.out.println("🔄 Opening Exam Schedule Management Screen...");
        try {
            ExamScheduleManagementScreen scheduleManagement = new ExamScheduleManagementScreen(currentUser, primaryStage);
            Scene scene = scheduleManagement.createScene();
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error opening Exam Schedule Management Screen: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi", "Không thể mở màn hình quản lý lịch thi: " + e.getMessage());
        }
    }

    private void openReportsScreen() {
        System.out.println("Opening Reports Screen...");
        ReportsScreen reports = new ReportsScreen();
        Scene scene = reports.createScene();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openSettingsScreen() {
        System.out.println("Opening Settings Screen...");
        // TODO: Implement settings screen when available
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cài đặt");
        alert.setHeaderText(null);
        alert.setContentText("Chức năng cài đặt đang được phát triển");
        alert.showAndWait();
    }

    private void handleLogout() {
        System.out.println("Admin đăng xuất...");
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

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println("✅ Admin Dashboard displayed successfully!");
    }
}
