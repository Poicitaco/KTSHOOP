package com.pocitaco.oopsh.controllers;

import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.models.User;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            DialogUtils.showError("Login Error", "Username and password are required.");
            return;
        }

        userDAO.findByUsername(username).ifPresentOrElse(user -> {
            if (PasswordUtils.verifyPassword(password, user.getPassword())) {
                SessionManager.setCurrentUser(user);
                navigateToDashboard();
            } else {
                DialogUtils.showError("Login Error", "Invalid username or password.");
            }
        }, () -> {
            DialogUtils.showError("Login Error", "Invalid username or password.");
        });
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/main-layout.fxml"));
            Parent root = loader.load();

            MainLayoutController controller = loader.getController();
            controller.setCurrentUser(SessionManager.getCurrentUser());

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("OOPSH - Main");
        } catch (IOException e) {
            DialogUtils.showError("Navigation Error", "Could not load the main application layout.");
            e.printStackTrace();
        }
    }
}


