package com.pocitaco.oopsh.ui.screens;

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
 * Reports Screen - Material Design 3.0
 * Giao diện báo cáo cho admin
 */
public class ReportsScreen {

    private Stage primaryStage;
    private Scene scene;
    private BorderPane mainLayout;
    private VBox navigationRail;
    private StackPane contentArea;

    public ReportsScreen() {
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
        Label title = new Label("Báo cáo - OOPSH");
        title.setFont(Typography.TITLE_LARGE);
        title.setTextFill(Colors.PRIMARY);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Back button
        Button backButton = Buttons.createIconButton(Icons.createMenuIcon());
        backButton.setOnAction(e -> handleBack());

        appBar.getChildren().addAll(title, spacer, backButton);

        // Add elevation
        appBar.setEffect(createElevation(2));

        return appBar;
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
                createNavItem("Dashboard", Icons.createDashboardIcon(), false),
                createNavItem("Users", Icons.createAccountGroupIcon(), false),
                createNavItem("ExamTypes", Icons.createFileDocumentIcon(), false),
                createNavItem("Schedules", Icons.createCalendarIcon(), false),
                createNavItem("Reports", Icons.createChartBarIcon(), true),
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

        // Load reports content
        loadReportsContent();

        return content;
    }

    private void loadReportsContent() {
        VBox reportsContent = new VBox();
        reportsContent.setSpacing(24);
        reportsContent.setAlignment(Pos.TOP_LEFT);

        // Page title
        Label pageTitle = new Label("Báo cáo hệ thống");
        pageTitle.setFont(Typography.HEADLINE_MEDIUM);
        pageTitle.setTextFill(Colors.ON_BACKGROUND);

        // Reports grid
        GridPane reportsGrid = new GridPane();
        reportsGrid.setHgap(16);
        reportsGrid.setVgap(16);

        // Report cards
        reportsGrid.add(createReportCard("Báo cáo người dùng", "Thống kê người dùng theo vai trò",
                Icons.createAccountGroupIcon()), 0, 0);
        reportsGrid.add(
                createReportCard("Báo cáo kỳ thi", "Thống kê kỳ thi theo thời gian", Icons.createFileDocumentIcon()), 1,
                0);
        reportsGrid.add(
                createReportCard("Báo cáo lịch thi", "Thống kê lịch thi theo trạng thái", Icons.createCalendarIcon()),
                2, 0);
        reportsGrid.add(createReportCard("Báo cáo kết quả", "Thống kê kết quả thi", Icons.createChartBarIcon()), 0, 1);
        reportsGrid.add(createReportCard("Báo cáo thanh toán", "Thống kê thanh toán", Icons.createCreditCardIcon()), 1,
                1);
        reportsGrid.add(
                createReportCard("Báo cáo chứng chỉ", "Thống kê chứng chỉ được cấp", Icons.createCertificateIcon()), 2,
                1);

        reportsContent.getChildren().addAll(pageTitle, reportsGrid);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(reportsContent);
    }

    private VBox createReportCard(String title, String description, FontIcon icon) {
        VBox card = Cards.createCard();
        card.setPrefWidth(350);
        card.setPrefHeight(200);
        card.setSpacing(16);
        card.setPadding(new Insets(20));

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(16);

        icon.setIconSize(32);
        icon.setIconColor(Colors.PRIMARY);

        VBox textInfo = new VBox();
        textInfo.setSpacing(4);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Typography.TITLE_MEDIUM);
        titleLabel.setTextFill(Colors.ON_SURFACE);

        Label descLabel = new Label(description);
        descLabel.setFont(Typography.BODY_SMALL);
        descLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        textInfo.getChildren().addAll(titleLabel, descLabel);
        header.getChildren().addAll(icon, textInfo);

        // Action button
        Button viewButton = Buttons.createFilledButton("Xem báo cáo", null);
        viewButton.setOnAction(e -> viewReport(title));

        card.getChildren().addAll(header, viewButton);
        return card;
    }

    private void handleNavItemClick(String itemName) {
        System.out.println("Reports navigating to: " + itemName);

        switch (itemName) {
            case "Dashboard":
                handleBack();
                break;
            case "Users":
                // TODO: Navigate to user management
                break;
            case "ExamTypes":
                // TODO: Navigate to exam type management
                break;
            case "Schedules":
                // TODO: Navigate to schedule management
                break;
            case "Reports":
                loadReportsContent();
                break;
            case "Settings":
                // TODO: Navigate to settings
                break;
            default:
                System.out.println("Unknown navigation item: " + itemName);
                break;
        }
    }

    private void viewReport(String reportTitle) {
        System.out.println("Opening report: " + reportTitle);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Báo cáo");
        alert.setHeaderText(reportTitle);
        alert.setContentText("Báo cáo " + reportTitle + " đang được phát triển");
        alert.showAndWait();
    }

    private void handleBack() {
        System.out.println("Going back to admin dashboard");
        // TODO: Navigate back to admin dashboard
    }

    private javafx.scene.effect.DropShadow createElevation(int level) {
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.2));
        shadow.setOffsetY(level * 2);
        shadow.setRadius(level * 4);
        return shadow;
    }

    public Scene createScene() {
        return scene;
    }
}
