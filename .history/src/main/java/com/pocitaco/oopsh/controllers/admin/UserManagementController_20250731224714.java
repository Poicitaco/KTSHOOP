package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class UserManagementController extends BaseController {

    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User, Integer> colId;
    @FXML
    private TableColumn<User, String> colUsername;
    @FXML
    private TableColumn<User, String> colFullName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, String> colRole;
    @FXML
    private TableColumn<User, String> colActions;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnAddUser;
    @FXML
    private Button btnRefresh;

    private UserDAO userDAO;

    @Override
    protected void initializeComponents() {
        userDAO = new UserDAO();
        setupTableColumns();
        loadUsers();
    }

    @Override
    protected void setupEventHandlers() {
        btnAddUser.setOnAction(event -> handleAddUser());
        btnRefresh.setOnAction(event -> loadUsers());
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> filterUsers(newVal));
    }

    @Override
    protected void loadInitialData() {
        loadUsers();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Setup actions column
        colActions.setCellFactory(param -> new javafx.scene.control.TableCell<User, String>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox buttons = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleEditUser(user);
                });
                btnDelete.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUser(user);
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

    private void loadUsers() {
        List<User> users = userDAO.getAllUsers();
        tableView.getItems().clear();
        tableView.getItems().addAll(users);
    }

    private void filterUsers(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            loadUsers();
        } else {
            List<User> allUsers = userDAO.getAllUsers();
            List<User> filteredUsers = allUsers.stream()
                    .filter(user -> {
                        // Fuzzy search - search in multiple fields
                        String searchLower = searchText.toLowerCase();
                        return user.getFullName().toLowerCase().contains(searchLower) ||
                                user.getUsername().toLowerCase().contains(searchLower) ||
                                (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchLower)) ||
                                user.getRole().toString().toLowerCase().contains(searchLower);
                    })
                    .toList();
            tableView.getItems().clear();
            tableView.getItems().addAll(filteredUsers);
        }
    }

    private void handleAddUser() {
        showInfo("Add User", "Add user functionality will be implemented here");
        // TODO: Implement add user dialog
    }

    private void handleEditUser(User user) {
        showInfo("Edit User", "Edit user functionality will be implemented here for: " + user.getFullName());
        // TODO: Implement edit user dialog
    }

    private void handleDeleteUser(User user) {
        if (showConfirmation("Delete User", "Are you sure you want to delete user: " + user.getFullName() + "?")) {
            userDAO.deleteUser(user.getId());
            loadUsers();
            showInfo("User Deleted", "User has been deleted successfully");
        }
    }

    @Override
    protected void clearForm() {
        txtSearch.clear();
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        txtSearch.setDisable(!enabled);
        btnAddUser.setDisable(!enabled);
        btnRefresh.setDisable(!enabled);
        tableView.setDisable(!enabled);
    }
}
