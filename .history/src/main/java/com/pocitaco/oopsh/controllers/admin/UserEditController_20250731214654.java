package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.enums.UserStatus;
import com.pocitaco.oopsh.models.User;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import com.pocitaco.oopsh.utils.PasswordUtils;
import javafx.fxml.FXML;

public class UserEditController extends BaseController {

    @FXML
    private MFXTextField txtFullName;

    @FXML
    private MFXTextField txtEmail;

    @FXML
    private MFXPasswordField txtPassword;

    @FXML
    private MFXComboBox<UserRole> cmbRole;

    @FXML
    private MFXComboBox<UserStatus> cmbStatus;

    @FXML
    private MFXButton btnCancel;

    @FXML
    private MFXButton btnSave;

    private UserDAO userDAO;
    private User userToEdit;

    @Override
    public void initializeComponents() {
        this.userDAO = new UserDAO();
        cmbRole.setItems(FXCollections.observableArrayList(UserRole.values()));
        cmbStatus.setItems(FXCollections.observableArrayList(UserStatus.values()));
    }

    @Override
    protected void setupEventHandlers() {
        btnSave.setOnAction(event -> handleSaveUser());
        btnCancel.setOnAction(event -> handleCancel());
    }

    @Override
    protected void loadInitialData() {
        // Load initial data for user editing
        if (userToEdit != null) {
            txtFullName.setText(userToEdit.getFullName());
            txtEmail.setText(userToEdit.getEmail());
            cmbRole.setValue(userToEdit.getRole());
            cmbStatus.setValue(userToEdit.getStatus());
        }
    }

    public void setUserToEdit(User user) {
        this.userToEdit = user;
    }

    private void handleSaveUser() {
        if (validateInput()) {
            userToEdit.setFullName(txtFullName.getText());
            userToEdit.setEmail(txtEmail.getText());
            
            // Only update password if a new one is entered
            if (!txtPassword.getText().isEmpty()) {
                userToEdit.setPassword(PasswordUtils.hashPassword(txtPassword.getText()));
            }
            
            userToEdit.setRole(cmbRole.getValue());
            userToEdit.setStatus(cmbStatus.getValue());

            userDAO.updateUser(userToEdit);

            showInfo("User Updated", "User " + userToEdit.getFullName() + " has been updated successfully.");
            // Optionally, navigate back to the user management screen
        }
    }

    private void handleCancel() {
        // Logic to navigate back to the user management screen
        showInfo("Cancelled", "Edit user operation cancelled.");
    }

    private boolean validateInput() {
        if (txtFullName.getText().isEmpty() || txtEmail.getText().isEmpty()) {
            showError("Validation Error", "Please fill in all required fields.");
            return false;
        }
        // Add more validation logic here (e.g., email format)
        return true;
    }

    @Override
    protected void clearForm() {
        txtFullName.clear();
        txtEmail.clear();
        txtPassword.clear();
        cmbRole.selectFirst();
        cmbStatus.selectFirst();
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        txtFullName.setDisable(!enabled);
        txtEmail.setDisable(!enabled);
        txtPassword.setDisable(!enabled);
        cmbRole.setDisable(!enabled);
        cmbStatus.setDisable(!enabled);
        btnSave.setDisable(!enabled);
        btnCancel.setDisable(!enabled);
    }
}

