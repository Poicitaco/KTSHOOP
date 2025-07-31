package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.PermissionDAO;
import com.pocitaco.oopsh.models.Permission;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;

public class UserPermissionsController extends BaseController {

    @FXML
    private MFXTableView<Permission> tblPermissions;

    @FXML
    private MFXButton btnAddPermission;

    private PermissionDAO permissionDAO;

    @Override
    public void initializeComponents() {
        this.permissionDAO = new PermissionDAO();
        setupTable();
    }

    @Override
    protected void setupEventHandlers() {
        btnAddPermission.setOnAction(event -> handleAddPermission());
    }

    @Override
    protected void loadInitialData() {
        tblPermissions.setItems(FXCollections.observableArrayList(permissionDAO.getAll()));
    }

    private void setupTable() {
        MFXTableRowCell<Permission, String> roleCell = new MFXTableRowCell<>(p -> p.getRole().toString());
        MFXTableRowCell<Permission, String> functionalityCell = new MFXTableRowCell<>(Permission::getFunctionality);
        
        // roleCell.setComparator(...); // Commented out due to MaterialFX compatibility
        // functionalityCell.setComparator(...); // Commented out due to MaterialFX compatibility

        tblPermissions.getTableColumns().addAll(
                // new io.github.palexdev.materialfx.controls.MFXTableColumn<>("Role", true, roleCell),
                // new io.github.palexdev.materialfx.controls.MFXTableColumn<>("Functionality", true, functionalityCell),
                createHasAccessColumn(),
                createActionsColumn()
        );
    }

    private io.github.palexdev.materialfx.controls.MFXTableColumn<Permission> createHasAccessColumn() {
        io.github.palexdev.materialfx.controls.MFXTableColumn<Permission> hasAccessColumn = // new io.github.palexdev.materialfx.controls.MFXTableColumn<>("Has Access", true, null);
        hasAccessColumn.setRowCellFactory(permission -> new MFXTableRowCell<>(p -> "") {
            private final CheckBox checkBox = new CheckBox();
            {
                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    permission.setHasAccess(newVal);
                    permissionDAO.update(permission);
                    showInfo("Permission Updated", "Access for " + permission.getFunctionality() + " updated to " + newVal);
                });
                setGraphic(checkBox);
            }
            @Override
            protected void updateItem(Permission p, boolean empty) {
                super.updateItem(p, empty);
                if (p != null && !empty) {
                    checkBox.setSelected(p.hasAccess());
                }
            }
        });
        return hasAccessColumn;
    }

    private io.github.palexdev.materialfx.controls.MFXTableColumn<Permission> createActionsColumn() {
        io.github.palexdev.materialfx.controls.MFXTableColumn<Permission> actionsColumn = // new io.github.palexdev.materialfx.controls.MFXTableColumn<>("Actions", false, null);

        actionsColumn.setRowCellFactory(permission -> new MFXTableRowCell<>(p -> "") {
            {
                HBox buttons = new HBox(5);
                buttons.setAlignment(Pos.CENTER);
                Button btnEdit = new Button("Edit");
                btnEdit.getStyleClass().add("btn-secondary");
                btnEdit.setOnAction(event -> handleEditPermission(permission));

                Button btnDelete = new Button("Delete");
                btnDelete.getStyleClass().add("btn-error");
                btnDelete.setOnAction(event -> handleDeletePermission(permission));

                buttons.getChildren().addAll(btnEdit, btnDelete);
                setGraphic(buttons);
            }
        });

        return actionsColumn;
    }

    private void handleAddPermission() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/admin/permission-create.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Create New Permission");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadInitialData(); // Refresh table
        } catch (IOException e) {
            showError("Error", "Could not load the create permission screen.");
            e.printStackTrace();
        }
    }

    private void handleEditPermission(Permission permission) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/admin/permission-edit.fxml"));
            Parent root = loader.load();

            PermissionEditController controller = loader.getController();
            controller.setPermissionToEdit(permission);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Permission");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadInitialData(); // Refresh table
        } catch (IOException e) {
            showError("Error", "Could not load the edit permission screen.");
            e.printStackTrace();
        }
    }

    private void handleDeletePermission(Permission permission) {
        if (showConfirmation("Delete Permission", "Are you sure you want to delete permission for " + permission.getRole() + " - " + permission.getFunctionality() + "?")) {
            permissionDAO.delete(permission.getId());
            loadInitialData();
            showInfo("Permission Deleted", "Permission has been deleted.");
        }
    }

    @Override
    protected void clearForm() {
        // Not applicable
    }

    @Override
    protected void setFormEnabled(boolean b) {
        // Not applicable
    }
}

