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

public class UserCreateController extends BaseController {

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
        // Set default values if needed
        cmbRole.selectFirst();
        cmbStatus.selectFirst();
    }

    private void handleSaveUser() {
        if (validateInput()) {
            User newUser = new User();
            newUser.setFullName(txtFullName.getText());
            newUser.setEmail(txtEmail.getText());
            newUser.setPassword(PasswordUtils.hashPassword(txtPassword.getText()));
            newUser.setRole(cmbRole.getValue());
            newUser.setStatus(cmbStatus.getValue());

            userDAO.addUser(newUser);

            showInfo("User Created", "User " + newUser.getFullName() + " has been created successfully.");
            clearForm();
            // Optionally, navigate back to the user management screen
        }
    }

    private void handleCancel() {
        // Logic to navigate back to the user management screen
        showInfo("Cancelled", "Create user operation cancelled.");
    }

    private boolean validateInput() {
        if (txtFullName.getText().isEmpty() || txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty()) {
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

