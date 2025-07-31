package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.PermissionDAO;
import com.pocitaco.oopsh.models.Permission;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class UserPermissionsController extends BaseController {

    @FXML
    private TableView<Permission> tableView;
    @FXML
    private TableColumn<Permission, Integer> colId;
    @FXML
    private TableColumn<Permission, String> colRole;
    @FXML
    private TableColumn<Permission, String> colFunctionality;
    @FXML
    private TableColumn<Permission, Boolean> colHasAccess;
    @FXML
    private TableColumn<Permission, String> colActions;
    @FXML
    private Button btnAddPermission;
    @FXML
    private Button btnRefresh;

    private PermissionDAO permissionDAO;

    @Override
    protected void initializeComponents() {
        permissionDAO = new PermissionDAO();
        setupTableColumns();
        loadPermissions();
    }

    @Override
    protected void setupEventHandlers() {
        btnAddPermission.setOnAction(event -> handleAddPermission());
        btnRefresh.setOnAction(event -> loadPermissions());
    }

    @Override
    protected void loadInitialData() {
        loadPermissions();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colFunctionality.setCellValueFactory(new PropertyValueFactory<>("functionality"));

        // Setup has access column with checkbox
        colHasAccess.setCellFactory(param -> new javafx.scene.control.TableCell<Permission, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    Permission permission = getTableView().getItems().get(getIndex());
                    if (permission != null) {
                        permission.setHasAccess(newVal);
                        permissionDAO.update(permission);
                        showInfo("Permission Updated",
                                "Access for " + permission.getFunctionality() + " updated to " + newVal);
                    }
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Permission permission = getTableView().getItems().get(getIndex());
                    if (permission != null) {
                        checkBox.setSelected(permission.hasAccess());
                    }
                    setGraphic(checkBox);
                }
            }
        });

        // Setup actions column
        colActions.setCellFactory(param -> new javafx.scene.control.TableCell<Permission, String>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox buttons = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setOnAction(event -> {
                    Permission permission = getTableView().getItems().get(getIndex());
                    handleEditPermission(permission);
                });
                btnDelete.setOnAction(event -> {
                    Permission permission = getTableView().getItems().get(getIndex());
                    handleDeletePermission(permission);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadPermissions() {
        List<Permission> permissions = permissionDAO.getAll();
        tableView.getItems().clear();
        tableView.getItems().addAll(permissions);
    }

    private void handleAddPermission() {
        showInfo("Add Permission", "Add permission functionality will be implemented here");
        // TODO: Implement add permission dialog
    }

    private void handleEditPermission(Permission permission) {
        showInfo("Edit Permission",
                "Edit permission functionality will be implemented here for: " + permission.getFunctionality());
        // TODO: Implement edit permission dialog
    }

    private void handleDeletePermission(Permission permission) {
        if (showConfirmation("Delete Permission", "Are you sure you want to delete permission for "
                + permission.getRole() + " - " + permission.getFunctionality() + "?")) {
            permissionDAO.deleteById(permission.getId());
            loadPermissions();
            showInfo("Permission Deleted", "Permission has been deleted.");
        }
    }

    @Override
    protected void clearForm() {
        // Not applicable
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        btnAddPermission.setDisable(!enabled);
        btnRefresh.setDisable(!enabled);
        tableView.setDisable(!enabled);
    }
}
