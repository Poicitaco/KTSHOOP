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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Material Design Login Screen
 * Màn hình đăng nhập theo chuẩn Material Design 3.0
 */
public class LoginScreen {

    private Stage primaryStage;
    private Scene scene;
    private VBox mainContainer;

    public LoginScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeUI();
    }

    private void initializeUI() {
        // Main container
        mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setSpacing(32);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setBackground(new Background(new BackgroundFill(
                Colors.BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        // Create login card
        VBox loginCard = createLoginCard();

        mainContainer.getChildren().addAll(
                createHeader(),
                loginCard);

        // Create scene
        scene = new Scene(mainContainer, 1200, 800);
        primaryStage.setScene(scene);
    }

    private VBox createHeader() {
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);
        header.setSpacing(16);

        // Logo area (sử dụng icon thay vì logo)
        FontIcon logoIcon = Icons.createSchoolIcon();
        logoIcon.setIconSize(64);
        logoIcon.setIconColor(Colors.PRIMARY);

        // Title
        Label title = new Label("OOPSH System");
        title.setFont(Typography.DISPLAY_MEDIUM);
        title.setTextFill(Colors.ON_BACKGROUND);

        // Subtitle
        Label subtitle = new Label("Hệ thống sát hạch bằng lái xe");
        subtitle.setFont(Typography.TITLE_MEDIUM);
        subtitle.setTextFill(Colors.ON_SURFACE_VARIANT);

        header.getChildren().addAll(logoIcon, title, subtitle);
        return header;
    }

    private VBox createLoginCard() {
        // Login card container
        VBox card = Cards.createElevatedCard();
        card.setMaxWidth(400);
        card.setAlignment(Pos.CENTER);
        card.setSpacing(24);
        card.setPadding(new Insets(32));

        // Card title
        Label cardTitle = new Label("Đăng nhập");
        cardTitle.setFont(Typography.HEADLINE_SMALL);
        cardTitle.setTextFill(Colors.ON_SURFACE);

        // Username field
        VBox usernameField = createMaterialTextField("Tên đăng nhập", Icons.createAccountIcon());

        // Password field
        VBox passwordField = createMaterialPasswordField("Mật khẩu", Icons.createLoginIcon());

        // Remember me checkbox
        HBox rememberMe = createRememberMeCheckbox();

        // Login button
        Button loginButton = Buttons.createFilledButton("Đăng nhập", null);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> handleLogin());

        // Forgot password link
        Button forgotPasswordLink = Buttons.createTextButton("Quên mật khẩu?", null);

        // Role selection
        VBox roleSelection = createRoleSelection();

        card.getChildren().addAll(
                cardTitle,
                usernameField,
                passwordField,
                rememberMe,
                roleSelection,
                loginButton,
                forgotPasswordLink);

        return card;
    }

    private VBox createMaterialTextField(String hint, FontIcon icon) {
        VBox fieldContainer = new VBox();
        fieldContainer.setSpacing(4);

        // Field label
        Label label = new Label(hint);
        label.setFont(Typography.BODY_SMALL);
        label.setTextFill(Colors.ON_SURFACE_VARIANT);

        // Input container
        HBox inputContainer = new HBox();
        inputContainer.setAlignment(Pos.CENTER_LEFT);
        inputContainer.setSpacing(12);
        inputContainer.setPadding(new Insets(12, 16, 12, 16));
        inputContainer.setBorder(new Border(new BorderStroke(
                Colors.ON_SURFACE_VARIANT, BorderStrokeStyle.SOLID,
                new CornerRadii(4), new BorderWidths(1))));
        inputContainer.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, new CornerRadii(4), Insets.EMPTY)));

        // Icon
        if (icon != null) {
            icon.setIconSize(20);
            icon.setIconColor(Colors.ON_SURFACE_VARIANT);
            inputContainer.getChildren().add(icon);
        }

        // Text field
        TextField textField = new TextField();
        textField.setPromptText(hint);
        textField.setBorder(Border.EMPTY);
        textField.setBackground(Background.EMPTY);
        textField.setFont(Typography.BODY_LARGE);
        textField.setStyle("-fx-text-fill: " + toHexString(Colors.ON_SURFACE));
        HBox.setHgrow(textField, Priority.ALWAYS);

        // Focus effects
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                inputContainer.setBorder(new Border(new BorderStroke(
                        Colors.PRIMARY, BorderStrokeStyle.SOLID,
                        new CornerRadii(4), new BorderWidths(2))));
                label.setTextFill(Colors.PRIMARY);
            } else {
                inputContainer.setBorder(new Border(new BorderStroke(
                        Colors.ON_SURFACE_VARIANT, BorderStrokeStyle.SOLID,
                        new CornerRadii(4), new BorderWidths(1))));
                label.setTextFill(Colors.ON_SURFACE_VARIANT);
            }
        });

        inputContainer.getChildren().add(textField);
        fieldContainer.getChildren().addAll(label, inputContainer);

        return fieldContainer;
    }

    private VBox createMaterialPasswordField(String hint, FontIcon icon) {
        VBox fieldContainer = new VBox();
        fieldContainer.setSpacing(4);

        // Field label
        Label label = new Label(hint);
        label.setFont(Typography.BODY_SMALL);
        label.setTextFill(Colors.ON_SURFACE_VARIANT);

        // Input container
        HBox inputContainer = new HBox();
        inputContainer.setAlignment(Pos.CENTER_LEFT);
        inputContainer.setSpacing(12);
        inputContainer.setPadding(new Insets(12, 16, 12, 16));
        inputContainer.setBorder(new Border(new BorderStroke(
                Colors.ON_SURFACE_VARIANT, BorderStrokeStyle.SOLID,
                new CornerRadii(4), new BorderWidths(1))));
        inputContainer.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, new CornerRadii(4), Insets.EMPTY)));

        // Icon
        if (icon != null) {
            icon.setIconSize(20);
            icon.setIconColor(Colors.ON_SURFACE_VARIANT);
            inputContainer.getChildren().add(icon);
        }

        // Password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(hint);
        passwordField.setBorder(Border.EMPTY);
        passwordField.setBackground(Background.EMPTY);
        passwordField.setFont(Typography.BODY_LARGE);
        passwordField.setStyle("-fx-text-fill: " + toHexString(Colors.ON_SURFACE));
        HBox.setHgrow(passwordField, Priority.ALWAYS);

        // Focus effects
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                inputContainer.setBorder(new Border(new BorderStroke(
                        Colors.PRIMARY, BorderStrokeStyle.SOLID,
                        new CornerRadii(4), new BorderWidths(2))));
                label.setTextFill(Colors.PRIMARY);
            } else {
                inputContainer.setBorder(new Border(new BorderStroke(
                        Colors.ON_SURFACE_VARIANT, BorderStrokeStyle.SOLID,
                        new CornerRadii(4), new BorderWidths(1))));
                label.setTextFill(Colors.ON_SURFACE_VARIANT);
            }
        });

        inputContainer.getChildren().add(passwordField);
        fieldContainer.getChildren().addAll(label, inputContainer);

        return fieldContainer;
    }

    private HBox createRememberMeCheckbox() {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        container.setSpacing(8);

        CheckBox checkBox = new CheckBox();
        checkBox.setStyle("-fx-text-fill: " + toHexString(Colors.ON_SURFACE));

        Label label = new Label("Ghi nhớ đăng nhập");
        label.setFont(Typography.BODY_MEDIUM);
        label.setTextFill(Colors.ON_SURFACE);

        container.getChildren().addAll(checkBox, label);
        return container;
    }

    private VBox createRoleSelection() {
        VBox container = new VBox();
        container.setSpacing(8);

        Label label = new Label("Vai trò");
        label.setFont(Typography.BODY_SMALL);
        label.setTextFill(Colors.ON_SURFACE_VARIANT);

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Quản trị viên", "Giám khảo", "Thí sinh");
        roleComboBox.setValue("Thí sinh");
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        roleComboBox.setStyle("-fx-background-color: " + toHexString(Colors.SURFACE) + ";" +
                "-fx-border-color: " + toHexString(Colors.ON_SURFACE_VARIANT) + ";" +
                "-fx-border-radius: 4px;");

        container.getChildren().addAll(label, roleComboBox);
        return container;
    }

    private void handleLogin() {
        // TODO: Implement login logic
        System.out.println("🔑 Đăng nhập được thực hiện!");

        // Demo: Show dashboard after login
        DashboardScreen dashboard = new DashboardScreen(primaryStage);
        dashboard.show();
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public void show() {
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
