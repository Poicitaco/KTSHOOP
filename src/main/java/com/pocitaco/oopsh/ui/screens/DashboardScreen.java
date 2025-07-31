package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.ui.MaterialDesignManager;
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
 * Material Design Dashboard Screen
 * Màn hình dashboard chính theo chuẩn Material Design 3.0
 */
public class DashboardScreen {

    private Stage primaryStage;
    private Scene scene;
    private BorderPane mainLayout;
    private VBox navigationRail;
    private StackPane contentArea;

    public DashboardScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
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
        Label title = new Label("OOPSH Dashboard");
        title.setFont(Typography.TITLE_LARGE);
        title.setTextFill(Colors.ON_SURFACE);

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
        avatarIcon.setIconColor(Colors.ON_SURFACE);

        // User name
        Label userName = new Label("Admin User");
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
                createNavItem("Exams", Icons.createFileDocumentIcon(), false),
                createNavItem("Schedule", Icons.createCalendarIcon(), false),
                createNavItem("Reports", Icons.createChartBarIcon(), false),
                createNavItem("Payments", Icons.createCreditCardIcon(), false));

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

        // Click handler
        navItem.setOnMouseClicked(e -> handleNavItemClick(text));

        // Hover effects
        if (!selected) {
            navItem.setOnMouseEntered(e -> navItem.setBackground(new Background(new BackgroundFill(
                    Colors.ON_SURFACE_VARIANT.deriveColor(0, 1, 1, 0.08),
                    new CornerRadii(16), Insets.EMPTY))));
            navItem.setOnMouseExited(e -> navItem.setBackground(Background.EMPTY));
        }

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
        Label pageTitle = new Label("Dashboard Overview");
        pageTitle.setFont(Typography.HEADLINE_MEDIUM);
        pageTitle.setTextFill(Colors.ON_BACKGROUND);

        // Stats cards
        HBox statsRow = createStatsRow();

        // Charts section
        VBox chartsSection = createChartsSection();

        // Recent activity
        VBox recentActivity = createRecentActivitySection();

        dashboardContent.getChildren().addAll(
                pageTitle,
                statsRow,
                chartsSection,
                recentActivity);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardContent);
    }

    private HBox createStatsRow() {
        HBox statsRow = new HBox();
        statsRow.setSpacing(16);
        statsRow.setAlignment(Pos.CENTER);

        // Create stat cards
        VBox usersCard = createStatCard("Tổng người dùng", "1,234", Icons.createAccountGroupIcon(), Colors.PRIMARY);
        VBox examsCard = createStatCard("Kỳ thi", "56", Icons.createFileDocumentIcon(), Colors.SUCCESS);
        VBox paymentsCard = createStatCard("Thanh toán", "₫12,345,000", Icons.createCurrencyUsdIcon(), Colors.WARNING);
        VBox certificatesCard = createStatCard("Chứng chỉ", "890", Icons.createCertificateIcon(), Colors.SECONDARY);

        statsRow.getChildren().addAll(usersCard, examsCard, paymentsCard, certificatesCard);
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

    private VBox createChartsSection() {
        VBox section = new VBox();
        section.setSpacing(16);

        Label sectionTitle = new Label("Thống kê");
        sectionTitle.setFont(Typography.TITLE_LARGE);
        sectionTitle.setTextFill(Colors.ON_BACKGROUND);

        VBox chartCard = Cards.createCard();
        chartCard.setPrefHeight(300);
        chartCard.setAlignment(Pos.CENTER);

        Label chartPlaceholder = new Label("📊 Biểu đồ thống kê sẽ được hiển thị tại đây");
        chartPlaceholder.setFont(Typography.BODY_LARGE);
        chartPlaceholder.setTextFill(Colors.ON_SURFACE_VARIANT);

        chartCard.getChildren().add(chartPlaceholder);

        section.getChildren().addAll(sectionTitle, chartCard);
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
        System.out.println("🔄 Navigating to: " + itemName);

        switch (itemName) {
            case "Users":
                UserManagementScreen userManagement = new UserManagementScreen(primaryStage);
                userManagement.show();
                break;
            case "Exams":
                // TODO: Implement ExamManagementScreen
                System.out.println("📚 Exam Management will be implemented next");
                break;
            case "Schedule":
                // TODO: Implement ScheduleManagementScreen
                System.out.println("📅 Schedule Management will be implemented next");
                break;
            case "Reports":
                // TODO: Implement ReportsScreen
                System.out.println("📊 Reports will be implemented next");
                break;
            case "Payments":
                // TODO: Implement PaymentManagementScreen
                System.out.println("💰 Payment Management will be implemented next");
                break;
            default:
                System.out.println("⚠️ Unknown navigation item: " + itemName);
        }
    }

    private void handleLogout() {
        System.out.println("🚪 Đăng xuất...");
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

    public void show() {
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
