package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.models.Permission;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PermissionEditController extends BaseController {

    @FXML
    private TextField txtPermissionName;
    @FXML
    private TextField txtDescription;
    @FXML
    private TextField txtResource;

    private Permission permissionToEdit;

    @Override
    protected void initializeComponents() {
        // Initialize components
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        // Load initial data for permission editing
        if (permissionToEdit != null) {
            txtPermissionName.setText(permissionToEdit.getFunctionality());
            txtDescription.setText(permissionToEdit.getRole().toString());
            txtResource.setText(String.valueOf(permissionToEdit.hasAccess()));
        }
    }

    public void setPermissionToEdit(Permission permission) {
        this.permissionToEdit = permission;
    }

    @Override
    protected void clearForm() {
        txtPermissionName.clear();
        txtDescription.clear();
        txtResource.clear();
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        txtPermissionName.setDisable(!enabled);
        txtDescription.setDisable(!enabled);
        txtResource.setDisable(!enabled);
    }
}
