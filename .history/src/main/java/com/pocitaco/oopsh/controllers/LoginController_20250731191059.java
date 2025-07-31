package com.pocitaco.oopsh.controllers;

import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.utils.DialogUtils;
import com.pocitaco.oopsh.utils.SessionManager;
import com.pocitaco.oopsh.dao.UserDAO;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for login screen with Material Design animations
 */
public class LoginController implements Initializable {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;

    private UserDAO userDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDAO = new UserDAO();
        setupMaterialDesignEffects();

        // Enable login button only when both fields have content
        txtUsername.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        txtPassword.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        btnLogin.setDisable(true); // Initially disabled
    }

    private void validateFields() {
        boolean fieldsHaveContent = !txtUsername.getText().trim().isEmpty()
                && !txtPassword.getText().trim().isEmpty();
        btnLogin.setDisable(!fieldsHaveContent);
    }

    private void setupMaterialDesignEffects() {
        // Setup button hover effects with premium animations
        setupPremiumButtonEffects(btnLogin);

        // Setup input field focus effects with smooth transitions
        setupPremiumInputFieldEffects(txtUsername);
        setupPremiumInputFieldEffects(txtPassword);

        // Add subtle entrance animations
        addEntranceAnimations();
    }

    private void addEntranceAnimations() {
        // Enhanced entrance animations for the entire form
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000));
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Scale animation with bounce effect
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(800));
        scaleIn.setFromX(0.95);
        scaleIn.setFromY(0.95);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        // Slide animation from left
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(900));
        slideIn.setFromX(-20);
        slideIn.setToX(0);

        // Play animations when scene is ready
        Timeline delayedStart = new Timeline(new KeyFrame(Duration.millis(300), e -> {
            if (btnLogin.getScene() != null) {
                Node root = btnLogin.getScene().getRoot();
                fadeIn.setNode(root);
                scaleIn.setNode(root);
                slideIn.setNode(root);
                
                // Play animations in sequence
                fadeIn.play();
                scaleIn.play();
                slideIn.play();
                
                // Add subtle floating animation to logo
                addLogoFloatingAnimation();
            }
        }));
        delayedStart.play();
    }
    
    private void addLogoFloatingAnimation() {
        // Find the logo container (first StackPane in the scene)
        if (btnLogin.getScene() != null) {
            Node root = btnLogin.getScene().getRoot();
            if (root instanceof HBox) {
                HBox mainContainer = (HBox) root;
                if (mainContainer.getChildren().size() > 0) {
                    Node leftSide = mainContainer.getChildren().get(0);
                    if (leftSide instanceof VBox) {
                        VBox leftVBox = (VBox) leftSide;
                        if (leftVBox.getChildren().size() > 0) {
                            Node logoContainer = leftVBox.getChildren().get(0);
                            if (logoContainer instanceof StackPane) {
                                // Add subtle floating animation
                                TranslateTransition floatAnimation = new TranslateTransition(Duration.millis(3000), logoContainer);
                                floatAnimation.setFromY(0);
                                floatAnimation.setToY(-5);
                                floatAnimation.setAutoReverse(true);
                                floatAnimation.setCycleCount(Timeline.INDEFINITE);
                                floatAnimation.play();
                            }
                        }
                    }
                }
            }
        }
    }

    private void setupPremiumButtonEffects(Button button) {
        // Store original style
        String originalStyle = button.getStyle();

        // Enhanced hover effect with smooth scaling
        button.setOnMouseEntered(e -> {
            // Color transition for new layout
            button.setStyle(originalStyle.replace("#1976D2", "#1565C0"));

            // Smooth scale animation
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), button);
            scaleIn.setToX(1.02);
            scaleIn.setToY(1.02);
            scaleIn.play();

            // Enhanced glow effect
            DropShadow glow = new DropShadow();
            glow.setColor(Color.rgb(25, 118, 210, 0.4));
            glow.setOffsetY(4);
            glow.setRadius(12);
            glow.setSpread(0.2);
            button.setEffect(glow);
        });

        button.setOnMouseExited(e -> {
            // Reset style
            button.setStyle(originalStyle);

            // Scale back smoothly
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), button);
            scaleOut.setToX(1.0);
            scaleOut.setToY(1.0);
            scaleOut.play();

            // Reset shadow
            DropShadow normalShadow = new DropShadow();
            normalShadow.setColor(Color.rgb(25, 118, 210, 0.3));
            normalShadow.setOffsetY(2);
            normalShadow.setRadius(8);
            button.setEffect(normalShadow);
        });

        // Click ripple effect
        button.setOnMousePressed(e -> {
            ScaleTransition scalePress = new ScaleTransition(Duration.millis(100), button);
            scalePress.setToX(0.98);
            scalePress.setToY(0.98);
            scalePress.play();
        });

        button.setOnMouseReleased(e -> {
            ScaleTransition scaleRelease = new ScaleTransition(Duration.millis(100), button);
            scaleRelease.setToX(1.02);
            scaleRelease.setToY(1.02);
            scaleRelease.play();
        });
    }

    private void setupPremiumInputFieldEffects(TextField field) {
        String originalStyle = field.getStyle();

        // Subtle hover effect
        field.setOnMouseEntered(e -> {
            if (!field.isFocused()) {
                field.setStyle(originalStyle.replace("-fx-border-color: #BDC3C7", "-fx-border-color: #85C1E9"));

                // Subtle glow
                DropShadow subtleGlow = new DropShadow();
                subtleGlow.setColor(Color.rgb(52, 152, 219, 0.2));
                subtleGlow.setRadius(8);
                field.setEffect(subtleGlow);
            }
        });

        field.setOnMouseExited(e -> {
            if (!field.isFocused()) {
                field.setStyle(originalStyle);
                field.setEffect(null);
            }
        });

        // Enhanced focus effect
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // Focus animation
                field.setStyle(originalStyle.replace("-fx-border-color: #BDC3C7", "-fx-border-color: #3498DB")
                        .replace("-fx-border-width: 1", "-fx-border-width: 2"));

                // Enhanced focus glow
                DropShadow focusGlow = new DropShadow();
                focusGlow.setColor(Color.rgb(52, 152, 219, 0.4));
                focusGlow.setRadius(12);
                focusGlow.setSpread(0.2);
                field.setEffect(focusGlow);

                // Subtle scale animation
                ScaleTransition focusScale = new ScaleTransition(Duration.millis(200), field);
                focusScale.setToX(1.01);
                focusScale.setToY(1.01);
                focusScale.play();

            } else {
                // Unfocus animation
                field.setStyle(originalStyle);
                field.setEffect(null);

                ScaleTransition unfocusScale = new ScaleTransition(Duration.millis(200), field);
                unfocusScale.setToX(1.0);
                unfocusScale.setToY(1.0);
                unfocusScale.play();
            }
        });
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        // Start loading animation
        startLoadingAnimation();

        // Validate input
        if (!validateInput()) {
            stopLoadingAnimation();
            return;
        }

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // Authenticate with a smooth loading animation
        Timeline delayTimeline = new Timeline(new KeyFrame(Duration.millis(800), e -> {
            try {
                User user = userDAO.authenticateUser(username, password);

                if (user != null) {
                    // Show success animation before navigation
                    showSuccessAnimation();

                    // Navigate to dashboard after success animation
                    Timeline successDelay = new Timeline(new KeyFrame(Duration.millis(500), ev -> {
                        try {
                            SessionManager.getInstance().login(user);
                            navigateToDashboard(user.getRole());
                            Stage currentStage = (Stage) btnLogin.getScene().getWindow();
                            currentStage.close();
                        } catch (IOException ex) {
                            DialogUtils.showError("Lỗi hệ thống", "Không thể mở dashboard: " + ex.getMessage());
                        }
                    }));
                    successDelay.play();

                } else {
                    stopLoadingAnimation();
                    showErrorAnimation();
                    DialogUtils.showError("Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không chính xác!");
                }

            } catch (Exception ex) {
                stopLoadingAnimation();
                showErrorAnimation();
                DialogUtils.showError("Lỗi hệ thống", "Có lỗi xảy ra khi đăng nhập: " + ex.getMessage());
            }
        }));
        delayTimeline.play();
    }

    private void startLoadingAnimation() {
        btnLogin.setText("ĐANG ĐĂNG NHẬP...");
        btnLogin.setDisable(true);

        // Material Design loading animation with color change
        btnLogin.setStyle(btnLogin.getStyle().replace("#1976D2", "#9E9E9E"));

        // Subtle pulsing animation
        ScaleTransition pulse = new ScaleTransition(Duration.millis(800), btnLogin);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.01);
        pulse.setToY(1.01);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.play();
        btnLogin.setUserData(pulse);

        // Loading glow effect
        DropShadow loadingGlow = new DropShadow();
        loadingGlow.setColor(Color.rgb(158, 158, 158, 0.3));
        loadingGlow.setRadius(10);
        loadingGlow.setSpread(0.2);
        btnLogin.setEffect(loadingGlow);
    }

    private void stopLoadingAnimation() {
        btnLogin.setText("ĐĂNG NHẬP");
        btnLogin.setDisable(false);

        // Stop pulsing animation
        Object userData = btnLogin.getUserData();
        if (userData instanceof ScaleTransition) {
            ((ScaleTransition) userData).stop();
        }
        btnLogin.setScaleX(1.0);
        btnLogin.setScaleY(1.0);

        // Reset button style to Material Design
        btnLogin.setStyle(
                "-fx-background-color: #1976D2; -fx-background-radius: 12; -fx-text-fill: white; -fx-font-family: 'Segoe UI', 'Roboto', sans-serif; -fx-font-size: 16; -fx-font-weight: 600; -fx-cursor: hand; -fx-border-color: transparent; -fx-effect: dropshadow(gaussian, rgba(25,118,210,0.3), 12, 0, 0, 4);");
    }

    private void showSuccessAnimation() {
        btnLogin.setText("✓ ĐĂNG NHẬP THÀNH CÔNG");
        btnLogin.setStyle(btnLogin.getStyle().replace("#9E9E9E", "#4CAF50"));

        // Success bounce animation
        ScaleTransition successBounce = new ScaleTransition(Duration.millis(300), btnLogin);
        successBounce.setFromX(1.0);
        successBounce.setFromY(1.0);
        successBounce.setToX(1.05);
        successBounce.setToY(1.05);
        successBounce.setAutoReverse(true);
        successBounce.setCycleCount(2);
        successBounce.play();

        // Success glow
        DropShadow successGlow = new DropShadow();
        successGlow.setColor(Color.rgb(76, 175, 80, 0.5));
        successGlow.setRadius(12);
        successGlow.setSpread(0.3);
        btnLogin.setEffect(successGlow);
    }

    private void showErrorAnimation() {
        btnLogin.setText("✗ ĐĂNG NHẬP THẤT BẠI");
        btnLogin.setStyle(btnLogin.getStyle().replace("#9E9E9E", "#F44336"));

        // Enhanced shake animation
        TranslateTransition shake = new TranslateTransition(Duration.millis(80), btnLogin);
        shake.setFromX(0);
        shake.setToX(10);
        shake.setAutoReverse(true);
        shake.setCycleCount(6);

        // Error glow
        DropShadow errorGlow = new DropShadow();
        errorGlow.setColor(Color.rgb(244, 67, 54, 0.5));
        errorGlow.setRadius(12);
        errorGlow.setSpread(0.3);
        btnLogin.setEffect(errorGlow);

        shake.setOnFinished(e -> {
            // Reset after error display
            Timeline resetDelay = new Timeline(new KeyFrame(Duration.millis(1500), ev -> {
                stopLoadingAnimation();
            }));
            resetDelay.play();
        });
        shake.play();
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/register.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Đăng ký tài khoản");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            DialogUtils.showError("Lỗi", "Không thể mở màn hình đăng ký!");
        }
    }

    private boolean validateInput() {
        if (txtUsername.getText().trim().isEmpty()) {
            DialogUtils.showWarning("Thiếu thông tin", "Vui lòng nhập tên đăng nhập!");
            txtUsername.requestFocus();
            return false;
        }

        if (txtPassword.getText().isEmpty()) {
            DialogUtils.showWarning("Thiếu thông tin", "Vui lòng nhập mật khẩu!");
            txtPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void navigateToDashboard(UserRole role) throws IOException {
        // Load main layout instead of role-specific dashboards
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/main-layout.fxml"));
        Parent root = loader.load();

        // Get the controller and set current user
        MainLayoutController controller = loader.getController();
        User currentUser = SessionManager.getCurrentUser();
        controller.setCurrentUser(currentUser);

        // Create new scene with Material Design CSS
        Scene scene = new Scene(root, 1400, 900);
        scene.getStylesheets().add(
                getClass().getResource("/com/pocitaco/oopsh/styles/material-design.css").toExternalForm());

        // Set up the stage
        Stage stage = new Stage();
        stage.setTitle("OOPSH - Driving License Examination System");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
