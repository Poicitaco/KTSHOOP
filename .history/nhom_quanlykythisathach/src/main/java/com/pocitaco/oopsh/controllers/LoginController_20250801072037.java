package com.pocitaco.oopsh.controllers;

import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.ui.screens.AdminDashboardScreen;
import com.pocitaco.oopsh.ui.screens.CandidateDashboardScreen;
import com.pocitaco.oopsh.ui.screens.ExaminerDashboardScreen;
import com.pocitaco.oopsh.utils.DialogUtils;
import com.pocitaco.oopsh.utils.PasswordUtils;
import com.pocitaco.oopsh.utils.SessionManager;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtPasswordVisible;
    @FXML
    private Button btnTogglePassword;
    @FXML
    private FontIcon passwordIcon;
    @FXML
    private Button btnLogin;

    private UserDAO userDAO;
    private boolean isPasswordVisible = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDAO = new UserDAO();
        
        // Bind password fields
        txtPassword.textProperty().bindBidirectional(txtPasswordVisible.textProperty());
        
        // Set up enter key handling
        txtUsername.setOnAction(e -> txtPassword.requestFocus());
        txtPassword.setOnAction(e -> handleLogin(e));
        txtPasswordVisible.setOnAction(e -> handleLogin(e));
    }

    @FXML
    private void togglePasswordVisibility(ActionEvent event) {
        isPasswordVisible = !isPasswordVisible;
        
        if (isPasswordVisible) {
            txtPassword.setVisible(false);
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.requestFocus();
            passwordIcon.setIconLiteral("mdi2e-eye");
        } else {
            txtPassword.setVisible(true);
            txtPasswordVisible.setVisible(false);
            txtPassword.requestFocus();
            passwordIcon.setIconLiteral("mdi2e-eye-off");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = isPasswordVisible ? txtPasswordVisible.getText() : txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            DialogUtils.showError("Lỗi đăng nhập", "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        userDAO.findByUsername(username).ifPresentOrElse(user -> {
            // Check if user is active
            if (!"ACTIVE".equals(user.getStatus())) {
                DialogUtils.showError("Đăng nhập thất bại", "Tài khoản đã bị khóa hoặc không hoạt động!");
                return;
            }

            if (PasswordUtils.verifyPassword(password, user.getPassword())) {
                SessionManager.setCurrentUser(user);
                navigateToDashboard(user);
            } else {
                DialogUtils.showError("Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không đúng!");
            }
        }, () -> {
            DialogUtils.showError("Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không đúng!");
        });
    }

    private void navigateToDashboard(User user) {
        try {
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            
            switch (user.getRole()) {
                case ADMIN:
                    AdminDashboardScreen adminDashboard = new AdminDashboardScreen(stage, user);
                    adminDashboard.show();
                    break;
                case EXAMINER:
                    ExaminerDashboardScreen examinerDashboard = new ExaminerDashboardScreen(stage, user);
                    examinerDashboard.show();
                    break;
                case CANDIDATE:
                    CandidateDashboardScreen candidateDashboard = new CandidateDashboardScreen(stage, user);
                    candidateDashboard.show();
                    break;
                default:
                    DialogUtils.showError("Lỗi điều hướng", "Vai trò không xác định: " + user.getRole());
                    break;
            }
        } catch (Exception e) {
            DialogUtils.showError("Lỗi điều hướng", "Không thể tải dashboard cho vai trò: " + user.getRole());
            e.printStackTrace();
        }
    }
}
