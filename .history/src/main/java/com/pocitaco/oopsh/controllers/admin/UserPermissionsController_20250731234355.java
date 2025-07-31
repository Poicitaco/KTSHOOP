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
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import com.pocitaco.oopsh.models.UserPermission;
import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.models.User;

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
    private UserDAO userDAO;

    @Override
    protected void initializeComponents() {
        permissionDAO = new PermissionDAO();
        userDAO = new UserDAO();
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
        showPermissionDialog(null);
    }

    private void handleEditPermission(UserPermission permission) {
        if (permission != null) {
            showPermissionDialog(permission);
        } else {
            showError("Lỗi", "Vui lòng chọn quyền để sửa!");
        }
    }

    private void handleDeletePermission(Permission permission) {
        if (showConfirmation("Delete Permission", "Are you sure you want to delete permission for "
                + permission.getRole() + " - " + permission.getFunctionality() + "?")) {
            permissionDAO.deleteById(permission.getId());
            loadPermissions();
            showInfo("Permission Deleted", "Permission has been deleted.");
        }
    }

    private void showPermissionDialog(UserPermission permission) {
        // Create dialog
        Dialog<UserPermission> dialog = new Dialog<>();
        dialog.setTitle(permission == null ? "Thêm quyền mới" : "Sửa quyền");
        dialog.setHeaderText(permission == null ? "Nhập thông tin quyền mới" : "Cập nhật thông tin quyền");
        
        // Set dialog modality
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(tableView.getScene().getWindow());

        // Create form
        VBox form = new VBox(15);
        form.setPadding(new javafx.geometry.Insets(20));

        // User field
        Label lblUser = new Label("Người dùng:");
        ComboBox<User> cbUser = new ComboBox<>();
        cbUser.getItems().addAll(userDAO.getAllUsers());
        cbUser.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFullName() + " (" + item.getUsername() + ")");
                }
            }
        });
        cbUser.setButtonCell(cbUser.getCellFactory().call(null));
        if (permission != null) {
            cbUser.setValue(userDAO.getUserById(permission.getUserId()));
        }

        // Permission type field
        Label lblPermissionType = new Label("Loại quyền:");
        ComboBox<String> cbPermissionType = new ComboBox<>();
        cbPermissionType.getItems().addAll("READ", "WRITE", "DELETE", "ADMIN");
        if (permission != null) {
            cbPermissionType.setValue(permission.getPermissionType());
        }

        // Resource field
        Label lblResource = new Label("Tài nguyên:");
        ComboBox<String> cbResource = new ComboBox<>();
        cbResource.getItems().addAll("USERS", "EXAMS", "RESULTS", "SCHEDULES", "REPORTS");
        if (permission != null) {
            cbResource.setValue(permission.getResource());
        }

        // Description field
        Label lblDescription = new Label("Mô tả:");
        TextArea txtDescription = new TextArea();
        txtDescription.setPromptText("Nhập mô tả quyền");
        txtDescription.setPrefRowCount(3);
        if (permission != null) {
            txtDescription.setText(permission.getDescription());
        }

        // Add fields to form
        form.getChildren().addAll(
            lblUser, cbUser,
            lblPermissionType, cbPermissionType,
            lblResource, cbResource,
            lblDescription, txtDescription
        );

        // Set dialog content
        dialog.getDialogPane().setContent(form);

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Handle save button
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Validate input
                if (cbUser.getValue() == null) {
                    showError("Lỗi", "Vui lòng chọn người dùng!");
                    return null;
                }
                if (cbPermissionType.getValue() == null) {
                    showError("Lỗi", "Vui lòng chọn loại quyền!");
                    return null;
                }
                if (cbResource.getValue() == null) {
                    showError("Lỗi", "Vui lòng chọn tài nguyên!");
                    return null;
                }

                // Check for duplicate permissions
                if (permission == null) {
                    List<UserPermission> existingPermissions = permissionDAO.getAllPermissions();
                    boolean duplicateExists = existingPermissions.stream()
                        .anyMatch(p -> p.getUserId() == cbUser.getValue().getId() && 
                                     p.getPermissionType().equals(cbPermissionType.getValue()) &&
                                     p.getResource().equals(cbResource.getValue()));
                    if (duplicateExists) {
                        showError("Lỗi", "Quyền này đã tồn tại cho người dùng!");
                        return null;
                    }
                }

                // Create or update permission
                try {
                    if (permission == null) {
                        // Create new permission
                        UserPermission newPermission = new UserPermission();
                        newPermission.setUserId(cbUser.getValue().getId());
                        newPermission.setPermissionType(cbPermissionType.getValue());
                        newPermission.setResource(cbResource.getValue());
                        newPermission.setDescription(txtDescription.getText().trim());
                        
                        permissionDAO.addPermission(newPermission);
                        showInfo("Thành công", "Đã thêm quyền mới thành công!");
                    } else {
                        // Update existing permission
                        permission.setUserId(cbUser.getValue().getId());
                        permission.setPermissionType(cbPermissionType.getValue());
                        permission.setResource(cbResource.getValue());
                        permission.setDescription(txtDescription.getText().trim());
                        
                        permissionDAO.updatePermission(permission);
                        showInfo("Thành công", "Đã cập nhật quyền thành công!");
                    }
                    
                    loadPermissions();
                    return permission;
                } catch (Exception e) {
                    showError("Lỗi", "Không thể lưu quyền: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        // Show dialog
        dialog.showAndWait();
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
