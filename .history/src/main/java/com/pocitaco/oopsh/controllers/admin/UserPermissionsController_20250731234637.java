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
import com.pocitaco.oopsh.models.UserRole;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Optional;

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

    private void handleEditPermission(Permission permission) {
        if (permission != null) {
            showPermissionDialog(permission);
        } else {
            showError("Lỗi", "Vui lòng chọn quyền để sửa!");
        }
    }

    private void handleDeletePermission(Permission permission) {
        if (permission == null) {
            showError("Lỗi", "Vui lòng chọn quyền để xóa!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa quyền");
        alert.setContentText("Bạn có chắc muốn xóa quyền '" + permission.getFunctionality() + "' cho vai trò '" + permission.getRole().getDisplayName() + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                permissionDAO.deleteById(permission.getId());
                showInfo("Thành công", "Đã xóa quyền thành công!");
                loadPermissions();
            } catch (Exception e) {
                showError("Lỗi", "Không thể xóa quyền: " + e.getMessage());
            }
        }
    }

    private void showPermissionDialog(Permission permission) {
        // Create dialog
        Dialog<Permission> dialog = new Dialog<>();
        dialog.setTitle(permission == null ? "Thêm quyền mới" : "Sửa quyền");
        dialog.setHeaderText(permission == null ? "Nhập thông tin quyền mới" : "Cập nhật thông tin quyền");
        
        // Set dialog modality
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(tableView.getScene().getWindow());

        // Create form
        VBox form = new VBox(15);
        form.setPadding(new javafx.geometry.Insets(20));

        // Role field
        Label lblRole = new Label("Vai trò:");
        ComboBox<UserRole> cbRole = new ComboBox<>();
        cbRole.getItems().addAll(UserRole.values());
        cbRole.setCellFactory(param -> new ListCell<UserRole>() {
            @Override
            protected void updateItem(UserRole item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        cbRole.setButtonCell(cbRole.getCellFactory().call(null));
        if (permission != null) {
            cbRole.setValue(permission.getRole());
        }

        // Functionality field
        Label lblFunctionality = new Label("Chức năng:");
        ComboBox<String> cbFunctionality = new ComboBox<>();
        cbFunctionality.getItems().addAll(
            "USER_MANAGEMENT", "EXAM_MANAGEMENT", "RESULT_MANAGEMENT", 
            "SCHEDULE_MANAGEMENT", "REPORT_VIEW", "SYSTEM_SETTINGS"
        );
        if (permission != null) {
            cbFunctionality.setValue(permission.getFunctionality());
        }

        // Has Access field
        Label lblHasAccess = new Label("Có quyền truy cập:");
        CheckBox chkHasAccess = new CheckBox("Cho phép truy cập");
        if (permission != null) {
            chkHasAccess.setSelected(permission.hasAccess());
        } else {
            chkHasAccess.setSelected(true); // Default to true for new permissions
        }

        // Add fields to form
        form.getChildren().addAll(
            lblRole, cbRole,
            lblFunctionality, cbFunctionality,
            lblHasAccess, chkHasAccess
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
                if (cbRole.getValue() == null) {
                    showError("Lỗi", "Vui lòng chọn vai trò!");
                    return null;
                }
                if (cbFunctionality.getValue() == null) {
                    showError("Lỗi", "Vui lòng chọn chức năng!");
                    return null;
                }

                // Check for duplicate permissions
                if (permission == null) {
                    List<Permission> existingPermissions = permissionDAO.getAll();
                    boolean duplicateExists = existingPermissions.stream()
                        .anyMatch(p -> p.getRole() == cbRole.getValue() && 
                                     p.getFunctionality().equals(cbFunctionality.getValue()));
                    if (duplicateExists) {
                        showError("Lỗi", "Quyền này đã tồn tại cho vai trò!");
                        return null;
                    }
                }

                // Create or update permission
                try {
                    if (permission == null) {
                        // Create new permission
                        Permission newPermission = new Permission();
                        newPermission.setRole(cbRole.getValue());
                        newPermission.setFunctionality(cbFunctionality.getValue());
                        newPermission.setHasAccess(chkHasAccess.isSelected());
                        
                        permissionDAO.addPermission(newPermission);
                        showInfo("Thành công", "Đã thêm quyền mới thành công!");
                    } else {
                        // Update existing permission
                        permission.setRole(cbRole.getValue());
                        permission.setFunctionality(cbFunctionality.getValue());
                        permission.setHasAccess(chkHasAccess.isSelected());
                        
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
