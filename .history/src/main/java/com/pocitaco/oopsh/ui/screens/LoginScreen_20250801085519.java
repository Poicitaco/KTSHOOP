package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.ui.MaterialDesignManager;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Colors;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Typography;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Icons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Buttons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Cards;
import com.pocitaco.oopsh.ui.screens.AdminDashboardScreen;
import com.pocitaco.oopsh.ui.screens.ExaminerDashboardScreen;
import com.pocitaco.oopsh.ui.screens.CandidateDashboardScreen;
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
    private UserDAO userDAO;

    // Form fields
    private TextField usernameField;
    private PasswordField passwordField;
    private ComboBox<String> roleComboBox;

    public LoginScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.userDAO = new UserDAO();
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
        VBox usernameContainer = createUsernameField();

        // Password field
        VBox passwordContainer = createPasswordField();

        // Login button
        Button loginButton = Buttons.createFilledButton("Đăng nhập", null);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> handleLogin());

        card.getChildren().addAll(
                cardTitle,
                usernameContainer,
                passwordContainer,
                loginButton);

        return card;
    }

    private VBox createUsernameField() {
        VBox fieldContainer = new VBox();
        fieldContainer.setSpacing(4);

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
        FontIcon icon = Icons.createAccountIcon();
        icon.setIconSize(20);
        icon.setIconColor(Colors.ON_SURFACE_VARIANT);
        inputContainer.getChildren().add(icon);

        // Text field
        usernameField = new TextField();
        usernameField.setPromptText("Nhập tên đăng nhập");
        usernameField.setBorder(Border.EMPTY);
        usernameField.setBackground(Background.EMPTY);
        usernameField.setFont(Typography.BODY_LARGE);
        usernameField.setStyle("-fx-text-fill: " + toHexString(Colors.ON_SURFACE));
        HBox.setHgrow(usernameField, Priority.ALWAYS);

        // Focus effects
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                inputContainer.setBorder(new Border(new BorderStroke(
                        Colors.PRIMARY, BorderStrokeStyle.SOLID,
                        new CornerRadii(4), new BorderWidths(2))));
            } else {
                inputContainer.setBorder(new Border(new BorderStroke(
                        Colors.ON_SURFACE_VARIANT, BorderStrokeStyle.SOLID,
                        new CornerRadii(4), new BorderWidths(1))));
            }
        });

        inputContainer.getChildren().add(usernameField);
        fieldContainer.getChildren().add(inputContainer);

        return fieldContainer;
    }

    private VBox createPasswordField() {
        VBox fieldContainer = new VBox();
        fieldContainer.setSpacing(4);

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
        FontIcon icon = Icons.createLoginIcon();
        icon.setIconSize(20);
        icon.setIconColor(Colors.ON_SURFACE_VARIANT);
        inputContainer.getChildren().add(icon);

        // Password field
        passwordField = new PasswordField();
        passwordField.setPromptText("Nhập mật khẩu");
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
            } else {
                inputContainer.setBorder(new Border(new BorderStroke(
                        Colors.ON_SURFACE_VARIANT, BorderStrokeStyle.SOLID,
                        new CornerRadii(4), new BorderWidths(1))));
            }
        });

        inputContainer.getChildren().add(passwordField);
        fieldContainer.getChildren().add(inputContainer);

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

        roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Quản trị viên", "Giám khảo", "Thí sinh");
        roleComboBox.setValue("Quản trị viên");
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        roleComboBox.setStyle("-fx-background-color: " + toHexString(Colors.SURFACE) + ";" +
                "-fx-border-color: " + toHexString(Colors.ON_SURFACE_VARIANT) + ";" +
                "-fx-border-radius: 4px;");

        container.getChildren().addAll(label, roleComboBox);
        return container;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showErrorAlert("Lỗi đăng nhập", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }

        try {
            // Find user by username (automatic role detection)
            userDAO.findByUsername(username).ifPresentOrElse(user -> {
                // Check if user is active
                if (!"ACTIVE".equals(user.getStatus())) {
                    showErrorAlert("Đăng nhập thất bại", "Tài khoản đã bị khóa hoặc không hoạt động!");
                    return;
                }

                // Verify password
                if (PasswordUtils.verifyPassword(password, user.getPassword())) {
                    System.out.println(
                            "Đăng nhập thành công! User: " + user.getFullName() + " - Role: " + user.getRole());

                    // Navigate to appropriate dashboard based on role
                    switch (user.getRole()) {
                        case ADMIN:
                            System.out.println("Admin dashboard - Using AdminDashboardScreen");
                            AdminDashboardScreen adminDashboard = new AdminDashboardScreen(primaryStage, user);
                            adminDashboard.show();
                            break;
                        case EXAMINER:
                            System.out.println("Examiner dashboard - Using ExaminerDashboardScreen");
                            ExaminerDashboardScreen examinerDashboard = new ExaminerDashboardScreen(primaryStage, user);
                            examinerDashboard.show();
                            break;
                        case CANDIDATE:
                            System.out.println("Candidate dashboard - Using CandidateDashboardScreen");
                            CandidateDashboardScreen candidateDashboard = new CandidateDashboardScreen(primaryStage,
                                    user);
                            candidateDashboard.show();
                            break;
                    }
                } else {
                    showErrorAlert("Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không đúng!");
                }
            }, () -> {
                showErrorAlert("Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không đúng!");
            });

        } catch (Exception e) {
            showErrorAlert("Lỗi hệ thống", "Có lỗi xảy ra khi đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(primaryStage);
        alert.showAndWait();
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
