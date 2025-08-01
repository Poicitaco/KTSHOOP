package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.enums.UserStatus;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Colors;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Typography;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Icons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Buttons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Cards;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * User Management Screen - Material Design 3.0 with Sidebar
 * Giao diện quản lý người dùng hoàn chỉnh
 */
public class UserManagementScreen {

    private final Stage primaryStage;
    private final UserDAO userDAO;
    private final ObservableList<User> userList;
    private TableView<User> table;
    private TextField searchField;
    private ComboBox<String> roleFilter;
    private ComboBox<String> statusFilter;

    // Statistics
    private Label totalUsersLabel;
    private Label activeUsersLabel;
    private Label candidatesLabel;
    private Label examinersLabel;

    // UI Components
    private BorderPane mainLayout;
    private VBox navigationRail;
    private StackPane contentArea;

    public UserManagementScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.userDAO = new UserDAO();
        this.userList = FXCollections.observableArrayList();
    }

    public Scene createScene() {
        // Main layout with sidebar
        mainLayout = new BorderPane();
        mainLayout.setBackground(new Background(new BackgroundFill(
                Colors.BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        // Create components
        HBox appBar = createAppBar();
        navigationRail = createNavigationRail();
        contentArea = createContentArea();

        // Layout setup
        mainLayout.setTop(appBar);
        mainLayout.setLeft(navigationRail);
        mainLayout.setCenter(contentArea);

        // Load user management content
        loadUserManagementContent();

        return new Scene(mainLayout, 1200, 800);
    }

    public void show() {
        Scene scene = createScene();
        primaryStage.setTitle("Quản lý người dùng - OOPSH");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createAppBar() {
        HBox appBar = new HBox();
        appBar.setAlignment(Pos.CENTER_LEFT);
        appBar.setSpacing(16);
        appBar.setPadding(new Insets(0, 24, 0, 24));
        appBar.setPrefHeight(64);
        appBar.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Title
        Label title = new Label("👥 Quản lý người dùng - OOPSH");
        title.setFont(Typography.TITLE_LARGE);
        title.setTextFill(Colors.PRIMARY);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Back to Dashboard button
        Button backButton = Buttons.createIconButton(Icons.createDashboardIcon());
        backButton.setOnAction(e -> backToDashboard());

        appBar.getChildren().addAll(title, spacer, backButton);

        // Add elevation
        appBar.setEffect(createElevation(2));

        return appBar;
    }

    private VBox createNavigationRail() {
        VBox navRail = new VBox();
        navRail.setAlignment(Pos.TOP_CENTER);
        navRail.setSpacing(8);
        navRail.setPadding(new Insets(16, 8, 16, 8));
        navRail.setPrefWidth(80);
        navRail.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Navigation items
        navRail.getChildren().addAll(
                createNavItem("Dashboard", Icons.createDashboardIcon(), false),
                createNavItem("Người dùng", Icons.createAccountGroupIcon(), true),
                createNavItem("Loại thi", Icons.createFileDocumentIcon(), false),
                createNavItem("Lịch thi", Icons.createCalendarIcon(), false),
                createNavItem("Báo cáo", Icons.createChartBarIcon(), false));

        return navRail;
    }

    private VBox createNavItem(String text, FontIcon icon, boolean selected) {
        VBox navItem = new VBox();
        navItem.setAlignment(Pos.CENTER);
        navItem.setSpacing(4);
        navItem.setPadding(new Insets(8));
        navItem.setPrefWidth(64);
        navItem.setPrefHeight(64);

        // Set background and styling based on selection
        if (selected) {
            navItem.setBackground(new Background(new BackgroundFill(
                    Colors.PRIMARY_CONTAINER, new CornerRadii(16), Insets.EMPTY)));
            icon.setIconColor(Colors.ON_PRIMARY_CONTAINER);
        } else {
            navItem.setBackground(Background.EMPTY);
            icon.setIconColor(Colors.ON_SURFACE_VARIANT);
        }

        icon.setIconSize(24);

        Label label = new Label(text);
        label.setFont(Typography.LABEL_SMALL);
        label.setTextFill(selected ? Colors.ON_PRIMARY_CONTAINER : Colors.ON_SURFACE_VARIANT);

        navItem.getChildren().addAll(icon, label);

        // Add click handler
        navItem.setOnMouseClicked(e -> handleNavItemClick(text));
        navItem.setStyle("-fx-cursor: hand;");

        return navItem;
    }

    private StackPane createContentArea() {
        StackPane content = new StackPane();
        content.setPadding(new Insets(24));
        content.setBackground(new Background(new BackgroundFill(
                Colors.BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        return content;
    }

    private void loadUserManagementContent() {
        VBox managementContent = new VBox(24);
        managementContent.setAlignment(Pos.TOP_LEFT);

        // Header Section
        VBox headerSection = createHeaderSection();

        // Statistics Cards
        HBox statsSection = createStatsSection();

        // Filters Section
        VBox filtersSection = createFiltersSection();

        // Table Section
        VBox tableSection = createTableSection();

        managementContent.getChildren().addAll(headerSection, statsSection, filtersSection, tableSection);

        // Load data
        loadUsers();

        contentArea.getChildren().clear();
        contentArea.getChildren().add(managementContent);
    }

    private VBox createHeaderSection() {
        VBox headerSection = new VBox(8);

        Label titleLabel = new Label("👥 Quản lý người dùng");
        titleLabel.setFont(Typography.DISPLAY_SMALL);
        titleLabel.setTextFill(Colors.PRIMARY);

        Label subtitleLabel = new Label("Quản lý tài khoản người dùng, phân quyền và theo dõi hoạt động");
        subtitleLabel.setFont(Typography.BODY_LARGE);
        subtitleLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        headerSection.getChildren().addAll(titleLabel, subtitleLabel);
        return headerSection;
    }

    private HBox createStatsSection() {
        HBox statsSection = new HBox(16);
        statsSection.setAlignment(Pos.CENTER_LEFT);

        // Total Users Card
        VBox totalCard = createStatCard(
                "👥 Tổng người dùng",
                "0",
                Icons.createAccountGroupIcon(),
                Colors.PRIMARY);
        totalUsersLabel = (Label) ((HBox) totalCard.getChildren().get(0)).getChildren().get(1);

        // Active Users Card
        VBox activeCard = createStatCard(
                "✅ Đang hoạt động",
                "0",
                Icons.createAccountGroupIcon(),
                Colors.SUCCESS);
        activeUsersLabel = (Label) ((HBox) activeCard.getChildren().get(0)).getChildren().get(1);

        // Candidates Card
        VBox candidatesCard = createStatCard(
                "🎓 Thí sinh",
                "0",
                Icons.createAccountGroupIcon(),
                Colors.WARNING);
        candidatesLabel = (Label) ((VBox) candidatesCard.getChildren().get(0)).getChildren().get(1);

        // Examiners Card
        VBox examinersCard = createStatCard(
                "👨‍🏫 Giám khảo",
                "0",
                Icons.createAccountGroupIcon(),
                Colors.ERROR);
        examinersLabel = (Label) ((VBox) examinersCard.getChildren().get(0)).getChildren().get(1);

        statsSection.getChildren().addAll(totalCard, activeCard, candidatesCard, examinersCard);
        return statsSection;
    }

    private VBox createStatCard(String title, String value, FontIcon icon, Color accentColor) {
        VBox card = Cards.createCard();
        card.setPrefWidth(280);
        card.setSpacing(16);

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(16);

        icon.setIconSize(32);
        icon.setIconColor(accentColor);

        VBox textInfo = new VBox();
        textInfo.setSpacing(4);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Typography.BODY_MEDIUM);
        titleLabel.setTextFill(Colors.ON_SURFACE_VARIANT);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Typography.HEADLINE_SMALL);
        valueLabel.setTextFill(Colors.ON_SURFACE);

        textInfo.getChildren().addAll(titleLabel, valueLabel);
        header.getChildren().addAll(icon, textInfo);

        card.getChildren().add(header);
        return card;
    }

    private VBox createFiltersSection() {
        VBox filtersSection = new VBox(16);

        Label sectionTitle = new Label("🔍 Bộ lọc và tìm kiếm");
        sectionTitle.setFont(Typography.TITLE_MEDIUM);
        sectionTitle.setTextFill(Colors.ON_SURFACE);

        // Filter controls
        HBox filtersRow = new HBox(16);
        filtersRow.setAlignment(Pos.CENTER_LEFT);

        // Search field
        VBox searchContainer = new VBox(8);
        Label searchLabel = new Label("Tìm kiếm");
        searchLabel.setFont(Typography.LABEL_MEDIUM);
        searchLabel.setTextFill(Colors.ON_SURFACE);

        searchField = new TextField();
        searchField.setPromptText("Tìm theo tên, email, username...");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterUsers());

        searchContainer.getChildren().addAll(searchLabel, searchField);

        // Role filter
        VBox roleContainer = new VBox(8);
        Label roleLabel = new Label("Vai trò");
        roleLabel.setFont(Typography.LABEL_MEDIUM);
        roleLabel.setTextFill(Colors.ON_SURFACE);

        roleFilter = new ComboBox<>();
        roleFilter.getItems().addAll("Tất cả", "Quản trị viên", "Giám khảo", "Thí sinh");
        roleFilter.setValue("Tất cả");
        roleFilter.setPrefWidth(150);
        roleFilter.setOnAction(e -> filterUsers());

        roleContainer.getChildren().addAll(roleLabel, roleFilter);

        // Status filter
        VBox statusContainer = new VBox(8);
        Label statusLabel = new Label("Trạng thái");
        statusLabel.setFont(Typography.LABEL_MEDIUM);
        statusLabel.setTextFill(Colors.ON_SURFACE);

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Tất cả", "Hoạt động", "Tạm khóa");
        statusFilter.setValue("Tất cả");
        statusFilter.setPrefWidth(150);
        statusFilter.setOnAction(e -> filterUsers());

        statusContainer.getChildren().addAll(statusLabel, statusFilter);

        // Action buttons
        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        Button addButton = Buttons.createFilledButton("➕ Thêm người dùng", Icons.createAccountGroupIcon());
        addButton.setOnAction(e -> showAddUserDialog());

        Button refreshButton = Buttons.createOutlinedButton("🔄 Làm mới", Icons.createFileDocumentIcon());
        refreshButton.setOnAction(e -> loadUsers());

        actionButtons.getChildren().addAll(addButton, refreshButton);

        filtersRow.getChildren().addAll(searchContainer, roleContainer, statusContainer, actionButtons);

        filtersSection.getChildren().addAll(sectionTitle, filtersRow);
        return filtersSection;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox(16);

        Label tableLabel = new Label("📋 Danh sách người dùng");
        tableLabel.setFont(Typography.TITLE_MEDIUM);
        tableLabel.setTextFill(Colors.ON_SURFACE);

        table = createUserTable();

        tableSection.getChildren().addAll(tableLabel, table);
        return tableSection;
    }

    private TableView<User> createUserTable() {
        TableView<User> table = new TableView<>();
        table.setItems(userList);

        // Style the table
        table.setStyle(
                "-fx-background-color: " + Colors.SURFACE + ";" +
                        "-fx-border-color: " + Colors.OUTLINE_VARIANT + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;");

        // ID Column
        TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(60);

        // Username Column
        TableColumn<User, String> usernameColumn = new TableColumn<>("Tên đăng nhập");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameColumn.setPrefWidth(150);

        // Full Name Column
        TableColumn<User, String> fullNameColumn = new TableColumn<>("Họ và tên");
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        fullNameColumn.setPrefWidth(200);

        // Email Column
        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setPrefWidth(200);

        // Phone Column
        TableColumn<User, String> phoneColumn = new TableColumn<>("Số điện thoại");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneColumn.setPrefWidth(130);

        // Role Column
        TableColumn<User, String> roleColumn = new TableColumn<>("Vai trò");
        roleColumn.setCellValueFactory(cellData -> {
            UserRole role = cellData.getValue().getRole();
            String roleText = switch (role) {
                case ADMIN -> "Quản trị viên";
                case EXAMINER -> "Giám khảo";
                case CANDIDATE -> "Thí sinh";
            };
            return new SimpleStringProperty(roleText);
        });
        roleColumn.setPrefWidth(120);

        // Status Column
        TableColumn<User, String> statusColumn = new TableColumn<>("Trạng thái");
        statusColumn.setCellValueFactory(cellData -> {
            UserStatus status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status == UserStatus.ACTIVE ? "Hoạt động" : "Tạm khóa");
        });
        statusColumn.setPrefWidth(100);

        // Created Date Column
        TableColumn<User, String> createdDateColumn = new TableColumn<>("Ngày tạo");
        createdDateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getCreatedDate();
            return new SimpleStringProperty(
                    date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A");
        });
        createdDateColumn.setPrefWidth(100);

        // Actions Column
        TableColumn<User, Void> actionsColumn = new TableColumn<>("Thao tác");
        actionsColumn.setPrefWidth(180);
        actionsColumn.setCellFactory(column -> new TableCell<User, Void>() {
            private final HBox actionBox = new HBox(8);
            private final Button editButton = Buttons.createIconButton(Icons.createFileDocumentIcon());
            private final Button toggleButton = Buttons.createIconButton(Icons.createFileDocumentIcon());
            private final Button deleteButton = Buttons.createIconButton(Icons.createFileDocumentIcon());

            {
                editButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showEditUserDialog(user);
                });

                toggleButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    toggleUserStatus(user);
                });

                deleteButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(user);
                });

                actionBox.setAlignment(Pos.CENTER);
                actionBox.getChildren().addAll(editButton, toggleButton, deleteButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });

        table.getColumns().addAll(idColumn, usernameColumn, fullNameColumn, emailColumn,
                phoneColumn, roleColumn, statusColumn, createdDateColumn, actionsColumn);

        return table;
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            userList.clear();
            userList.addAll(users);
            updateStats();
            System.out.println("✅ Loaded " + users.size() + " users");
        } catch (Exception e) {
            System.err.println("❌ Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterUsers() {
        try {
            List<User> allUsers = userDAO.getAllUsers();
            String searchText = searchField.getText().toLowerCase();
            String selectedRole = roleFilter.getValue();
            String selectedStatus = statusFilter.getValue();

            List<User> filteredUsers = allUsers.stream()
                    .filter(user -> {
                        // Search filter
                        boolean matchesSearch = searchText.isEmpty() ||
                                user.getFullName().toLowerCase().contains(searchText) ||
                                user.getEmail().toLowerCase().contains(searchText) ||
                                user.getUsername().toLowerCase().contains(searchText);

                        // Role filter
                        boolean matchesRole = selectedRole.equals("Tất cả") ||
                                (selectedRole.equals("Quản trị viên") && user.getRole() == UserRole.ADMIN) ||
                                (selectedRole.equals("Giám khảo") && user.getRole() == UserRole.EXAMINER) ||
                                (selectedRole.equals("Thí sinh") && user.getRole() == UserRole.CANDIDATE);

                        // Status filter
                        boolean matchesStatus = selectedStatus.equals("Tất cả") ||
                                (selectedStatus.equals("Hoạt động") && user.getStatus() == UserStatus.ACTIVE) ||
                                (selectedStatus.equals("Tạm khóa") && user.getStatus() != UserStatus.ACTIVE);

                        return matchesSearch && matchesRole && matchesStatus;
                    })
                    .toList();

            userList.clear();
            userList.addAll(filteredUsers);
            System.out.println("🔍 Filtered " + filteredUsers.size() + " users");
        } catch (Exception e) {
            System.err.println("❌ Error filtering users: " + e.getMessage());
        }
    }

    private void updateStats() {
        int total = userList.size();
        long active = userList.stream().filter(u -> u.getStatus() == UserStatus.ACTIVE).count();
        long candidates = userList.stream().filter(u -> u.getRole() == UserRole.CANDIDATE).count();
        long examiners = userList.stream().filter(u -> u.getRole() == UserRole.EXAMINER).count();

        totalUsersLabel.setText(String.valueOf(total));
        activeUsersLabel.setText(String.valueOf(active));
        candidatesLabel.setText(String.valueOf(candidates));
        examinersLabel.setText(String.valueOf(examiners));
    }

    private void showAddUserDialog() {
        showUserDialog(null, "➕ Thêm người dùng mới");
    }

    private void showEditUserDialog(User user) {
        showUserDialog(user, "✏️ Chỉnh sửa người dùng");
    }

    private void showUserDialog(User user, String title) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle(title);

        VBox layout = new VBox(16);
        layout.setPadding(new Insets(24));
        layout.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Form fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Tên đăng nhập");
        if (user != null)
            usernameField.setText(user.getUsername());

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Họ và tên");
        if (user != null)
            fullNameField.setText(user.getFullName());

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        if (user != null)
            emailField.setText(user.getEmail());

        TextField phoneField = new TextField();
        phoneField.setPromptText("Số điện thoại");
        if (user != null)
            phoneField.setText(user.getPhone());

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(user == null ? "Mật khẩu" : "Mật khẩu mới (để trống nếu không đổi)");

        ComboBox<UserRole> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(UserRole.values());
        if (user != null)
            roleComboBox.setValue(user.getRole());
        else
            roleComboBox.setValue(UserRole.CANDIDATE);

        CheckBox activeCheckBox = new CheckBox("Tài khoản hoạt động");
        if (user != null)
            activeCheckBox.setSelected(user.getStatus() == UserStatus.ACTIVE);
        else
            activeCheckBox.setSelected(true);

        // Buttons
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelButton = Buttons.createOutlinedButton("Hủy", null);
        cancelButton.setOnAction(e -> dialog.close());

        Button saveButton = Buttons.createFilledButton(user == null ? "Thêm" : "Lưu", null);
        saveButton.setOnAction(e -> {
            try {
                if (user == null) {
                    // Create new user
                    User newUser = new User();
                    newUser.setUsername(usernameField.getText());
                    newUser.setPassword(passwordField.getText()); // In real app, hash this
                    newUser.setFullName(fullNameField.getText());
                    newUser.setEmail(emailField.getText());
                    newUser.setPhone(phoneField.getText());
                    newUser.setRole(roleComboBox.getValue());
                    newUser.setStatus(activeCheckBox.isSelected() ? UserStatus.ACTIVE : UserStatus.INACTIVE);
                    newUser.setCreatedDate(LocalDate.now());

                    userDAO.addUser(newUser);
                    showSuccessMessage("Thêm người dùng thành công!");
                } else {
                    // Update existing user
                    user.setUsername(usernameField.getText());
                    if (!passwordField.getText().isEmpty()) {
                        user.setPassword(passwordField.getText()); // In real app, hash this
                    }
                    user.setFullName(fullNameField.getText());
                    user.setEmail(emailField.getText());
                    user.setPhone(phoneField.getText());
                    user.setRole(roleComboBox.getValue());
                    user.setStatus(activeCheckBox.isSelected() ? UserStatus.ACTIVE : UserStatus.INACTIVE);

                    userDAO.updateUser(user);
                    showSuccessMessage("Cập nhật người dùng thành công!");
                }

                loadUsers();
                dialog.close();
            } catch (Exception ex) {
                showErrorMessage("Lỗi: " + ex.getMessage());
            }
        });

        buttonBox.getChildren().addAll(cancelButton, saveButton);

        layout.getChildren().addAll(
                new Label("Tên đăng nhập:"), usernameField,
                new Label("Họ và tên:"), fullNameField,
                new Label("Email:"), emailField,
                new Label("Số điện thoại:"), phoneField,
                new Label("Mật khẩu:"), passwordField,
                new Label("Vai trò:"), roleComboBox,
                activeCheckBox,
                buttonBox);

        Scene scene = new Scene(layout, 400, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void toggleUserStatus(User user) {
        try {
            UserStatus newStatus = (user.getStatus() == UserStatus.ACTIVE) ? UserStatus.INACTIVE : UserStatus.ACTIVE;
            user.setStatus(newStatus);
            userDAO.updateUser(user);
            loadUsers();
            showSuccessMessage("Cập nhật trạng thái người dùng thành công!");
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
    }

    private void showDeleteConfirmation(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("🗑️ Xóa người dùng");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa người dùng này?");
        alert.setContentText("Tên: " + user.getFullName() + "\nEmail: " + user.getEmail());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userDAO.deleteUser(user.getId());
                loadUsers();
                showSuccessMessage("Xóa người dùng thành công!");
            } catch (Exception e) {
                showErrorMessage("Lỗi khi xóa người dùng: " + e.getMessage());
            }
        }
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleNavItemClick(String itemName) {
        System.out.println("🔄 Navigating to: " + itemName);

        switch (itemName) {
            case "Dashboard":
                backToDashboard();
                break;
            case "Loại thi":
                openExamTypeManagement();
                break;
            case "Lịch thi":
                openExamScheduleManagement();
                break;
            case "Báo cáo":
                openReports();
                break;
            case "Người dùng":
                // Stay on current screen
                break;
            default:
                System.out.println("🔍 Unknown navigation item: " + itemName);
                break;
        }
    }

    private void backToDashboard() {
        AdminDashboardScreen adminDashboard = new AdminDashboardScreen(primaryStage, getCurrentUser());
        adminDashboard.show();
    }

    private void openExamTypeManagement() {
        ExamTypeManagementScreen examTypeManagement = new ExamTypeManagementScreen(primaryStage);
        Scene scene = examTypeManagement.createScene();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openExamScheduleManagement() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("📅 Lịch thi");
        alert.setHeaderText(null);
        alert.setContentText("Đang chuyển đến quản lý lịch thi...");
        alert.showAndWait();
    }

    private void openReports() {
                    ReportsScreen reportsScreen = new ReportsScreen();
        Scene scene = reportsScreen.createScene();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private com.pocitaco.oopsh.models.User getCurrentUser() {
        // TODO: Get current user from session/context
        return new com.pocitaco.oopsh.models.User();
    }

    private javafx.scene.effect.DropShadow createElevation(int level) {
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.2));
        shadow.setOffsetY(level * 2);
        shadow.setRadius(level * 4);
        return shadow;
    }
}
