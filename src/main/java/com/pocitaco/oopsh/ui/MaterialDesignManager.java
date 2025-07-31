package com.pocitaco.oopsh.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

/**
 * Material Design Manager - Google Material Design 3.0 Implementation
 * Quản lý toàn bộ Material Design components theo chuẩn Google
 */
public class MaterialDesignManager {

    // Material Design 3.0 Color Palette
    public static class Colors {
        // Primary Colors
        public static final Color PRIMARY = Color.web("#6750A4");
        public static final Color ON_PRIMARY = Color.web("#FFFFFF");
        public static final Color PRIMARY_CONTAINER = Color.web("#EADDFF");
        public static final Color ON_PRIMARY_CONTAINER = Color.web("#21005D");

        // Secondary Colors
        public static final Color SECONDARY = Color.web("#625B71");
        public static final Color ON_SECONDARY = Color.web("#FFFFFF");
        public static final Color SECONDARY_CONTAINER = Color.web("#E8DEF8");
        public static final Color ON_SECONDARY_CONTAINER = Color.web("#1D192B");

        // Tertiary Colors
        public static final Color TERTIARY = Color.web("#7D5260");
        public static final Color ON_TERTIARY = Color.web("#FFFFFF");
        public static final Color TERTIARY_CONTAINER = Color.web("#FFD8E4");
        public static final Color ON_TERTIARY_CONTAINER = Color.web("#31111D");

        // Surface Colors
        public static final Color SURFACE = Color.web("#FEF7FF");
        public static final Color ON_SURFACE = Color.web("#1C1B1F");
        public static final Color SURFACE_VARIANT = Color.web("#E7E0EC");
        public static final Color ON_SURFACE_VARIANT = Color.web("#49454F");

        // Background
        public static final Color BACKGROUND = Color.web("#FFFBFE");
        public static final Color ON_BACKGROUND = Color.web("#1C1B1F");

        // Outline
        public static final Color OUTLINE = Color.web("#79747E");
        public static final Color OUTLINE_VARIANT = Color.web("#CAC4D0");

        // Error
        public static final Color ERROR = Color.web("#BA1A1A");
        public static final Color ON_ERROR = Color.web("#FFFFFF");
        public static final Color ERROR_CONTAINER = Color.web("#FFDAD6");
        public static final Color ON_ERROR_CONTAINER = Color.web("#410002");

        // Success (Custom for our system)
        public static final Color SUCCESS = Color.web("#4CAF50");
        public static final Color ON_SUCCESS = Color.web("#FFFFFF");

        // Warning (Custom for our system)
        public static final Color WARNING = Color.web("#FF9800");
        public static final Color ON_WARNING = Color.web("#FFFFFF");

        // Color strings for CSS
        public static final String BACKGROUND_SURFACE = "-fx-background-color: #FEF7FF;";
        public static final String TEXT_PRIMARY = "-fx-text-fill: #1C1B1F;";
        public static final String TEXT_SECONDARY = "-fx-text-fill: #49454F;";
        public static final String PRIMARY_COLOR = "#6750A4";
        public static final String SECONDARY_COLOR = "#625B71";
        public static final String TERTIARY_COLOR = "#7D5260";
    }

    // Typography
    public static class Typography {
        public static final Font DISPLAY_LARGE = Font.font("Roboto", FontWeight.NORMAL, 57);
        public static final Font DISPLAY_MEDIUM = Font.font("Roboto", FontWeight.NORMAL, 45);
        public static final Font DISPLAY_SMALL = Font.font("Roboto", FontWeight.NORMAL, 36);

        public static final Font HEADLINE_LARGE = Font.font("Roboto", FontWeight.NORMAL, 32);
        public static final Font HEADLINE_MEDIUM = Font.font("Roboto", FontWeight.NORMAL, 28);
        public static final Font HEADLINE_SMALL = Font.font("Roboto", FontWeight.NORMAL, 24);

        public static final Font TITLE_LARGE = Font.font("Roboto", FontWeight.NORMAL, 22);
        public static final Font TITLE_MEDIUM = Font.font("Roboto", FontWeight.MEDIUM, 16);
        public static final Font TITLE_SMALL = Font.font("Roboto", FontWeight.MEDIUM, 14);

        public static final Font BODY_LARGE = Font.font("Roboto", FontWeight.NORMAL, 16);
        public static final Font BODY_MEDIUM = Font.font("Roboto", FontWeight.NORMAL, 14);
        public static final Font BODY_SMALL = Font.font("Roboto", FontWeight.NORMAL, 12);

        public static final Font LABEL_LARGE = Font.font("Roboto", FontWeight.MEDIUM, 14);
        public static final Font LABEL_MEDIUM = Font.font("Roboto", FontWeight.MEDIUM, 12);
        public static final Font LABEL_SMALL = Font.font("Roboto", FontWeight.MEDIUM, 11);
    }

    /**
     * Material Design Icon Factory
     */
    public static class Icons {

        // Navigation Icons
        public static FontIcon createDashboardIcon() {
            return new FontIcon(MaterialDesignV.VIEW_DASHBOARD);
        }

        public static FontIcon createMenuIcon() {
            return new FontIcon(MaterialDesignM.MENU);
        }

        public static FontIcon createHomeIcon() {
            return new FontIcon(MaterialDesignH.HOME);
        }

        public static FontIcon createSettingsIcon() {
            return new FontIcon(MaterialDesignC.COG);
        }

        // User Management Icons
        public static FontIcon createAccountIcon() {
            return new FontIcon(MaterialDesignA.ACCOUNT);
        }

        public static FontIcon createAccountGroupIcon() {
            return new FontIcon(MaterialDesignA.ACCOUNT_GROUP);
        }

        public static FontIcon createAccountPlusIcon() {
            return new FontIcon(MaterialDesignA.ACCOUNT_PLUS);
        }

        public static FontIcon createShieldAccountIcon() {
            return new FontIcon(MaterialDesignS.SHIELD_ACCOUNT);
        }

        // Document Icons
        public static FontIcon createFileDocumentIcon() {
            return new FontIcon(MaterialDesignF.FILE_DOCUMENT);
        }

        public static FontIcon createFileDocumentPlusIcon() {
            return new FontIcon(MaterialDesignF.FILE_DOCUMENT_OUTLINE);
        }

        public static FontIcon createFileDocumentEditIcon() {
            return new FontIcon(MaterialDesignF.FILE_DOCUMENT_EDIT);
        }

        // Calendar & Time Icons
        public static FontIcon createCalendarIcon() {
            return new FontIcon(MaterialDesignC.CALENDAR);
        }

        public static FontIcon createCalendarClockIcon() {
            return new FontIcon(MaterialDesignC.CALENDAR_CLOCK);
        }

        public static FontIcon createClockIcon() {
            return new FontIcon(MaterialDesignC.CLOCK);
        }

        // Chart & Analytics Icons
        public static FontIcon createChartLineIcon() {
            return new FontIcon(MaterialDesignC.CHART_LINE);
        }

        public static FontIcon createChartBarIcon() {
            return new FontIcon(MaterialDesignC.CHART_BAR);
        }

        public static FontIcon createChartPieIcon() {
            return new FontIcon(MaterialDesignC.CHART_PIE);
        }

        public static FontIcon createTrendingUpIcon() {
            return new FontIcon(MaterialDesignT.TRENDING_UP);
        }

        // Payment & Money Icons
        public static FontIcon createCurrencyUsdIcon() {
            return new FontIcon(MaterialDesignC.CURRENCY_USD);
        }

        public static FontIcon createCreditCardIcon() {
            return new FontIcon(MaterialDesignC.CREDIT_CARD);
        }

        // Education Icons
        public static FontIcon createSchoolIcon() {
            return new FontIcon(MaterialDesignS.SCHOOL);
        }

        public static FontIcon createBookOpenIcon() {
            return new FontIcon(MaterialDesignB.BOOK_OPEN);
        }

        public static FontIcon createCertificateIcon() {
            return new FontIcon(MaterialDesignC.CERTIFICATE);
        }

        // Action Icons
        public static FontIcon createAddIcon() {
            return new FontIcon(MaterialDesignP.PLUS);
        }

        public static FontIcon createEditIcon() {
            return new FontIcon(MaterialDesignP.PENCIL);
        }

        public static FontIcon createDeleteIcon() {
            return new FontIcon(MaterialDesignD.DELETE);
        }

        public static FontIcon createSaveIcon() {
            return new FontIcon(MaterialDesignC.CONTENT_SAVE);
        }

        public static FontIcon createCancelIcon() {
            return new FontIcon(MaterialDesignC.CLOSE);
        }

        // Status Icons
        public static FontIcon createCheckCircleIcon() {
            return new FontIcon(MaterialDesignC.CHECK_CIRCLE);
        }

        public static FontIcon createErrorIcon() {
            return new FontIcon(MaterialDesignA.ALERT_CIRCLE);
        }

        public static FontIcon createWarningIcon() {
            return new FontIcon(MaterialDesignA.ALERT);
        }

        public static FontIcon createInfoIcon() {
            return new FontIcon(MaterialDesignI.INFORMATION);
        }

        // System Icons
        public static FontIcon createLogoutIcon() {
            return new FontIcon(MaterialDesignL.LOGOUT);
        }

        public static FontIcon createLoginIcon() {
            return new FontIcon(MaterialDesignL.LOGIN);
        }

        /**
         * Create icon by name with size
         */
        public static FontIcon createIcon(String iconName, int size) {
            FontIcon icon = createIconByName(iconName);
            icon.setIconSize(size);
            return icon;
        }

        /**
         * Create icon by name with size and color
         */
        public static FontIcon createIcon(String iconName, int size, String color) {
            FontIcon icon = createIconByName(iconName);
            icon.setIconSize(size);
            icon.setIconColor(Color.web(color));
            return icon;
        }

        private static FontIcon createIconByName(String iconName) {
            return switch (iconName) {
                case "mdi2a-assessment" -> new FontIcon(MaterialDesignC.CHART_BAR);
                case "mdi2b-book-open" -> new FontIcon(MaterialDesignB.BOOK_OPEN);
                case "mdi2c-car" -> new FontIcon(MaterialDesignC.CAR);
                case "mdi2p-plus" -> new FontIcon(MaterialDesignP.PLUS);
                case "mdi2r-refresh" -> new FontIcon(MaterialDesignR.REFRESH);
                case "mdi2p-pencil" -> new FontIcon(MaterialDesignP.PENCIL);
                case "mdi2d-delete" -> new FontIcon(MaterialDesignD.DELETE);
                default -> new FontIcon(MaterialDesignI.INFORMATION);
            };
        }

        public static FontIcon createHelpIcon() {
            return new FontIcon(MaterialDesignH.HELP_CIRCLE);
        }
    }

    /**
     * Material Design Button Factory
     */
    public static class Buttons {

        public static Button createFilledButton(String text, FontIcon icon) {
            Button button = new Button(text);
            if (icon != null) {
                icon.setIconSize(18);
                icon.setIconColor(Colors.ON_PRIMARY);
                button.setGraphic(icon);
            }

            button.setFont(Typography.LABEL_LARGE);
            button.setTextFill(Colors.ON_PRIMARY);
            button.setBackground(createBackground(Colors.PRIMARY, 20));
            button.setPadding(new Insets(10, 24, 10, 24));
            button.setMinHeight(40);

            // Hover effect
            button.setOnMouseEntered(
                    e -> button.setBackground(createBackground(Colors.PRIMARY.deriveColor(0, 1, 0.92, 1), 20)));
            button.setOnMouseExited(e -> button.setBackground(createBackground(Colors.PRIMARY, 20)));

            return button;
        }

        public static Button createOutlinedButton(String text, FontIcon icon) {
            Button button = new Button(text);
            if (icon != null) {
                icon.setIconSize(18);
                icon.setIconColor(Colors.PRIMARY);
                button.setGraphic(icon);
            }

            button.setFont(Typography.LABEL_LARGE);
            button.setTextFill(Colors.PRIMARY);
            button.setBackground(Background.EMPTY);
            button.setBorder(new Border(new BorderStroke(
                    Colors.PRIMARY, BorderStrokeStyle.SOLID,
                    new CornerRadii(20), new BorderWidths(1))));
            button.setPadding(new Insets(10, 24, 10, 24));
            button.setMinHeight(40);

            return button;
        }

        public static Button createTextButton(String text, FontIcon icon) {
            Button button = new Button(text);
            if (icon != null) {
                icon.setIconSize(18);
                icon.setIconColor(Colors.PRIMARY);
                button.setGraphic(icon);
            }

            button.setFont(Typography.LABEL_LARGE);
            button.setTextFill(Colors.PRIMARY);
            button.setBackground(Background.EMPTY);
            button.setBorder(Border.EMPTY);
            button.setPadding(new Insets(10, 12, 10, 12));
            button.setMinHeight(40);

            return button;
        }

        public static Button createIconButton(FontIcon icon) {
            Button button = new Button();
            icon.setIconSize(24);
            icon.setIconColor(Colors.ON_SURFACE_VARIANT);
            button.setGraphic(icon);

            button.setBackground(Background.EMPTY);
            button.setBorder(Border.EMPTY);
            button.setPrefSize(48, 48);
            button.setShape(new javafx.scene.shape.Circle(24));

            // Hover effect
            button.setOnMouseEntered(e -> button
                    .setBackground(createBackground(Colors.ON_SURFACE_VARIANT.deriveColor(0, 1, 1, 0.08), 24)));
            button.setOnMouseExited(e -> button.setBackground(Background.EMPTY));

            return button;
        }
    }

    /**
     * Material Design Card Factory
     */
    public static class Cards {

        public static VBox createCard() {
            VBox card = new VBox();
            card.setBackground(createBackground(Colors.SURFACE, 12));
            card.setPadding(new Insets(16));
            card.setSpacing(8);

            // Add shadow effect
            card.setEffect(createElevation(1));

            return card;
        }

        public static VBox createElevatedCard() {
            VBox card = createCard();
            card.setBackground(createBackground(Colors.SURFACE_VARIANT, 12));
            card.setEffect(createElevation(3));
            return card;
        }

        public static VBox createStatCard(String title, String value, FontIcon icon, Color backgroundColor) {
            VBox card = new VBox(12);
            card.setPrefWidth(200);
            card.setPrefHeight(120);
            card.setPadding(new Insets(20));
            card.setBackground(createBackground(backgroundColor, 16));
            card.setEffect(createElevation(2));

            // Icon
            icon.setIconSize(32);
            HBox iconContainer = new HBox();
            iconContainer.setAlignment(Pos.CENTER_LEFT);
            iconContainer.getChildren().add(icon);

            // Content
            VBox content = new VBox(4);
            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: medium; -fx-text-fill: #49454F;");

            Label valueLabel = new Label(value);
            valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1C1B1F;");

            content.getChildren().addAll(titleLabel, valueLabel);
            card.getChildren().addAll(iconContainer, content);

            return card;
        }
    }

    // Helper methods
    private static Background createBackground(Color color, double radius) {
        return new Background(new BackgroundFill(color, new CornerRadii(radius), Insets.EMPTY));
    }

    private static javafx.scene.effect.DropShadow createElevation(int level) {
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.2));
        shadow.setOffsetY(level * 2);
        shadow.setRadius(level * 4);
        return shadow;
    }

    /**
     * Create Material Design App Bar
     */
    public static HBox createAppBar(String title) {
        HBox appBar = new HBox();
        appBar.setAlignment(Pos.CENTER_LEFT);
        appBar.setSpacing(16);
        appBar.setPadding(new Insets(0, 16, 0, 16));
        appBar.setPrefHeight(64);
        appBar.setBackground(createBackground(Colors.SURFACE, 0));

        // Menu button
        Button menuButton = Buttons.createIconButton(Icons.createMenuIcon());

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setFont(Typography.TITLE_LARGE);
        titleLabel.setTextFill(Colors.ON_SURFACE);

        appBar.getChildren().addAll(menuButton, titleLabel);

        return appBar;
    }

    /**
     * Create Material Design Navigation Rail
     */
    public static VBox createNavigationRail() {
        VBox navRail = new VBox();
        navRail.setAlignment(Pos.TOP_CENTER);
        navRail.setSpacing(12);
        navRail.setPadding(new Insets(8));
        navRail.setPrefWidth(80);
        navRail.setBackground(createBackground(Colors.SURFACE, 0));

        return navRail;
    }
}
