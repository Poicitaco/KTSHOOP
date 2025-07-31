package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.utils.ValidationHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.util.List;
import java.util.Optional;

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
    private ComboBox<UserRole> cbRoleFilter;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnRefresh;

    private UserDAO userDAO;
    private User selectedUser;

    @Override
    protected void initializeComponents() {
        userDAO = new UserDAO();
        setupTableColumns();
        setupFilters();
        loadUsers();
    }

    @Override
    protected void setupEventHandlers() {
        btnAdd.setOnAction(event -> handleAddUser());
        btnRefresh.setOnAction(event -> loadUsers());

        // Search functionality
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filterUsers(newVal);
        });

        // Role filter
        cbRoleFilter.setOnAction(event -> handleRoleFilter());

        // Table selection
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedUser = newSelection;
                });
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
        colActions.setCellFactory(param -> new TableCell<User, String>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
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

    private void setupFilters() {
        ObservableList<UserRole> roles = FXCollections.observableArrayList(UserRole.values());
        roles.add(0, null); // Add null option for "All"
        cbRoleFilter.setItems(roles);
        cbRoleFilter.setCellFactory(param -> new ListCell<UserRole>() {
            @Override
            protected void updateItem(UserRole item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Tất cả vai trò");
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        cbRoleFilter.setButtonCell(cbRoleFilter.getCellFactory().call(null));
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

    private void handleRoleFilter() {
        UserRole selectedRole = cbRoleFilter.getValue();
        if (selectedRole == null) {
            loadUsers();
        } else {
            List<User> allUsers = userDAO.getAllUsers();
            List<User> filteredUsers = allUsers.stream()
                    .filter(user -> user.getRole() == selectedRole)
                    .toList();
            tableView.getItems().clear();
            tableView.getItems().addAll(filteredUsers);
        }
    }

    private void handleAddUser() {
        showUserDialog(null);
    }

    private void handleEditUser(User user) {
        if (user != null) {
            showUserDialog(user);
        } else {
            showError("Lỗi", "Vui lòng chọn người dùng để sửa!");
        }
    }

    private void handleDeleteUser(User user) {
        if (user == null) {
            showError("Lỗi", "Vui lòng chọn người dùng để xóa!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa người dùng");
        alert.setContentText("Bạn có chắc muốn xóa người dùng '" + user.getFullName() + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userDAO.deleteUser(user.getId());
                showInfo("Thành công", "Đã xóa người dùng thành công!");
                loadUsers();
            } catch (Exception e) {
                showError("Lỗi", "Không thể xóa người dùng: " + e.getMessage());
            }
        }
    }

    private void showUserDialog(User user) {
        // Create dialog
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(user == null ? "Thêm người dùng mới" : "Sửa người dùng");
        dialog.setHeaderText(user == null ? "Nhập thông tin người dùng mới" : "Cập nhật thông tin người dùng");

        // Set dialog modality
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(tableView.getScene().getWindow());

        // Create form
        VBox form = new VBox(15);
        form.setPadding(new javafx.geometry.Insets(20));

        // Username field
        Label lblUsername = new Label("Tên đăng nhập:");
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Nhập tên đăng nhập");
        if (user != null) {
            txtUsername.setText(user.getUsername());
            txtUsername.setDisable(true); // Cannot change username when editing
        }

        // Full name field
        Label lblFullName = new Label("Họ và tên:");
        TextField txtFullName = new TextField();
        txtFullName.setPromptText("Nhập họ và tên");
        if (user != null) {
            txtFullName.setText(user.getFullName());
        }

        // Email field
        Label lblEmail = new Label("Email:");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Nhập email");
        if (user != null) {
            txtEmail.setText(user.getEmail());
        }

        // Phone field
        Label lblPhone = new Label("Số điện thoại:");
        TextField txtPhone = new TextField();
        txtPhone.setPromptText("Nhập số điện thoại");
        if (user != null) {
            txtPhone.setText(user.getPhone());
        }

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
        if (user != null) {
            cbRole.setValue(user.getRole());
        } else {
            cbRole.setValue(UserRole.CANDIDATE); // Default role
        }

        // Password field (only for new users)
        Label lblPassword = new Label("Mật khẩu:");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Nhập mật khẩu");
        if (user != null) {
            lblPassword.setText("Mật khẩu (để trống nếu không đổi):");
        }

        // Add fields to form
        form.getChildren().addAll(
                lblUsername, txtUsername,
                lblFullName, txtFullName,
                lblEmail, txtEmail,
                lblPhone, txtPhone,
                lblRole, cbRole,
                lblPassword, txtPassword);

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
                if (txtUsername.getText().trim().isEmpty()) {
                    showError("Lỗi", "Tên đăng nhập không được để trống!");
                    return null;
                }
                if (txtFullName.getText().trim().isEmpty()) {
                    showError("Lỗi", "Họ và tên không được để trống!");
                    return null;
                }
                if (cbRole.getValue() == null) {
                    showError("Lỗi", "Vui lòng chọn vai trò!");
                    return null;
                }
                if (user == null && txtPassword.getText().trim().isEmpty()) {
                    showError("Lỗi", "Mật khẩu không được để trống!");
                    return null;
                }

                // Validate email if provided
                if (!txtEmail.getText().trim().isEmpty() &&
                        !ValidationHelper.isValidEmail(txtEmail.getText().trim())) {
                    showError("Lỗi", "Email không hợp lệ!");
                    return null;
                }

                // Validate phone if provided
                if (!txtPhone.getText().trim().isEmpty() &&
                        !ValidationHelper.isValidPhone(txtPhone.getText().trim())) {
                    showError("Lỗi", "Số điện thoại không hợp lệ!");
                    return null;
                }

                // Check username uniqueness for new users
                if (user == null) {
                    if (ValidationHelper.isUsernameExists(txtUsername.getText().trim(), userDAO)) {
                        showError("Lỗi", "Tên đăng nhập đã tồn tại!");
                        return null;
                    }
                }

                // Create or update user
                try {
                    if (user == null) {
                        // Create new user
                        User newUser = new User();
                        newUser.setUsername(txtUsername.getText().trim());
                        newUser.setFullName(txtFullName.getText().trim());
                        newUser.setEmail(txtEmail.getText().trim());
                        newUser.setPhone(txtPhone.getText().trim());
                        newUser.setRole(cbRole.getValue());
                        newUser.setPassword(txtPassword.getText().trim());

                        userDAO.addUser(newUser);
                        showInfo("Thành công", "Đã thêm người dùng mới thành công!");
                    } else {
                        // Update existing user
                        user.setFullName(txtFullName.getText().trim());
                        user.setEmail(txtEmail.getText().trim());
                        user.setPhone(txtPhone.getText().trim());
                        user.setRole(cbRole.getValue());

                        // Update password if provided
                        if (!txtPassword.getText().trim().isEmpty()) {
                            user.setPassword(txtPassword.getText().trim());
                        }

                        userDAO.updateUser(user);
                        showInfo("Thành công", "Đã cập nhật người dùng thành công!");
                    }

                    loadUsers();
                    return user;
                } catch (Exception e) {
                    showError("Lỗi", "Không thể lưu người dùng: " + e.getMessage());
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
        txtSearch.clear();
        cbRoleFilter.setValue(null);
        selectedUser = null;
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        btnAdd.setDisable(!enabled);
        btnRefresh.setDisable(!enabled);
        txtSearch.setDisable(!enabled);
        cbRoleFilter.setDisable(!enabled);
    }
}
