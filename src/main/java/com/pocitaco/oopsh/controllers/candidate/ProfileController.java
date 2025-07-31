package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.utils.SessionManager;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;

public class ProfileController extends BaseController {

    @FXML
    private MFXTextField txtFullName;

    @FXML
    private MFXTextField txtEmail;

    @FXML
    private MFXPasswordField txtPassword;

    @FXML
    private MFXButton btnSave;

    private UserDAO userDAO;
    private User currentUser;

    @Override
    public void initializeComponents() {
        this.userDAO = new UserDAO();
        this.currentUser = SessionManager.getCurrentUser();
    }

    @Override
    protected void setupEventHandlers() {
        btnSave.setOnAction(event -> handleSaveProfile());
    }

    @Override
    protected void loadInitialData() {
        if (currentUser != null) {
            txtFullName.setText(currentUser.getFullName());
            txtEmail.setText(currentUser.getEmail());
        }
    }

    private void handleSaveProfile() {
        if (validateInput()) {
            currentUser.setFullName(txtFullName.getText());
            currentUser.setEmail(txtEmail.getText());

            if (!txtPassword.getText().isEmpty()) {
                currentUser.setPassword(txtPassword.getText()); // In a real app, hash the password
            }

            userDAO.update(currentUser);

            showInfo("Profile Updated", "Your profile has been updated successfully.");
        }
    }

    private boolean validateInput() {
        if (txtFullName.getText().isEmpty() || txtEmail.getText().isEmpty()) {
            showError("Validation Error", "Please fill in all required fields.");
            return false;
        }
        return true;
    }

    @Override
    protected void clearForm() {
        // Not applicable for this controller
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        txtFullName.setDisable(!enabled);
        txtEmail.setDisable(!enabled);
        txtPassword.setDisable(!enabled);
        btnSave.setDisable(!enabled);
    }
}

