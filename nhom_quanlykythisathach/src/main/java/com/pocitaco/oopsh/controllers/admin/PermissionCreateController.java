package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.PermissionDAO;
import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.models.Permission;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class PermissionCreateController extends BaseController {

    @FXML
    private MFXComboBox<UserRole> cmbRole;

    @FXML
    private MFXTextField txtFunctionality;

    @FXML
    private MFXButton btnCancel;

    @FXML
    private MFXButton btnSave;

    private PermissionDAO permissionDAO;

    @Override
    public void initializeComponents() {
        this.permissionDAO = new PermissionDAO();
        cmbRole.setItems(FXCollections.observableArrayList(UserRole.values()));
    }

    @Override
    protected void setupEventHandlers() {
        btnSave.setOnAction(event -> handleSavePermission());
        btnCancel.setOnAction(event -> handleCancel());
    }

    @Override
    protected void loadInitialData() {
        cmbRole.selectFirst();
    }

    private void handleSavePermission() {
        if (validateInput()) {
            Permission newPermission = new Permission();
            newPermission.setRole(cmbRole.getValue());
            newPermission.setFunctionality(txtFunctionality.getText());
            newPermission.setHasAccess(true); // New permissions are active by default

            permissionDAO.create(newPermission);

            showInfo("Permission Created", "Permission for " + newPermission.getRole() + " - " + newPermission.getFunctionality() + " has been created successfully.");
            clearForm();
        }
    }

    private void handleCancel() {
        // Close the stage
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    private boolean validateInput() {
        if (cmbRole.getValue() == null || txtFunctionality.getText().isEmpty()) {
            showError("Validation Error", "Please select a role and enter functionality.");
            return false;
        }
        return true;
    }

    @Override
    protected void clearForm() {
        cmbRole.clearSelection();
        txtFunctionality.clear();
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        cmbRole.setDisable(!enabled);
        txtFunctionality.setDisable(!enabled);
        btnSave.setDisable(!enabled);
        btnCancel.setDisable(!enabled);
    }
}

