package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.enums.UserStatus;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Colors;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Typography;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Icons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Buttons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Cards;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.Optional;

public class ExaminerManagementScreen {
    private Stage primaryStage;
    private Scene scene;
    private BorderPane mainLayout;
    private VBox navigationRail;
    private StackPane contentArea;

    private final UserDAO userDAO;
    private final ObservableList<User> examiners;

    private TableView<User> examinersTable;
    private TextField searchField;
    private ComboBox<String> statusFilter;

    private Label totalExaminersLabel;
    private Label activeExaminersLabel;
    private Label inactiveExaminersLabel;

    public ExaminerManagementScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.userDAO = new UserDAO();
        this.examiners = FXCollections.observableArrayList();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        mainLayout = new BorderPane();
        mainLayout.setBackground(new Background(new BackgroundFill(
                Colors.BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox appBar = createAppBar();
        navigationRail = createNavigationRail();
        contentArea = createContentArea();

        mainLayout.setTop(appBar);
        mainLayout.setLeft(navigationRail);
        mainLayout.setCenter(contentArea);

        scene = new Scene(mainLayout, 1200, 800);
    }

    private HBox createAppBar() {
        HBox appBar = new HBox();
        appBar.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));
        appBar.setPadding(new Insets(16, 24, 16, 24));
        appBar.setAlignment(Pos.CENTER_LEFT);
        appBar.setSpacing(16);

        FontIcon menuIcon = Icons.createMenuIcon();
        menuIcon.setIconSize(24);
        menuIcon.setIconColor(Colors.ON_SURFACE);

        Label title = new Label("Quản lý giám thị");
        title.setFont(Typography.HEADLINE_MEDIUM);
        title.setTextFill(Colors.ON_SURFACE);

        appBar.getChildren().addAll(menuIcon, title);
        return appBar;
    }

    private VBox createNavigationRail() {
        VBox rail = new VBox();
        rail.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));
        rail.setPrefWidth(80);
        rail.setPadding(new Insets(16, 8, 16, 8));
        rail.setSpacing(8);

        rail.getChildren().addAll(
                createNavItem("Dashboard", Icons.createHomeIcon(), false),
                createNavItem("Người dùng", Icons.createAccountGroupIcon(), false),
                createNavItem("Giám thị", Icons.createAccountGroupIcon(), true),
                createNavItem("Thí sinh", Icons.createAccountGroupIcon(), false),
                createNavItem("Loại thi", Icons.createFileDocumentIcon(), false),
                createNavItem("Lịch thi", Icons.createCalendarIcon(), false),
                createNavItem("Báo cáo", Icons.createChartBarIcon(), false));

        return rail;
    }

    private VBox createNavItem(String text, FontIcon icon, boolean selected) {
        VBox navItem = new VBox(4);
        navItem.setAlignment(Pos.CENTER);
        navItem.setPadding(new Insets(12, 8, 12, 8));
        navItem.setBackground(new Background(new BackgroundFill(
                selected ? Colors.PRIMARY_CONTAINER : Color.TRANSPARENT,
                new CornerRadii(8), Insets.EMPTY)));
        navItem.setPrefHeight(72);

        icon.setIconSize(24);
        icon.setIconColor(selected ? Colors.ON_PRIMARY_CONTAINER : Colors.ON_SURFACE_VARIANT);

        Label label = new Label(text);
        label.setFont(Typography.LABEL_SMALL);
        label.setTextFill(selected ? Colors.ON_PRIMARY_CONTAINER : Colors.ON_SURFACE_VARIANT);
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);

        navItem.getChildren().addAll(icon, label);

        navItem.setOnMouseClicked(e -> handleNavItemClick(text));

        return navItem;
    }

    private StackPane createContentArea() {
        StackPane content = new StackPane();
        content.setPadding(new Insets(24));
        loadExaminersContent();
        return content;
    }

    private void loadExaminersContent() {
        VBox examinersContent = new VBox();
        examinersContent.setSpacing(24);
        examinersContent.setAlignment(Pos.TOP_LEFT);

        Label pageTitle = new Label("Quản lý giám thị");
        pageTitle.setFont(Typography.HEADLINE_MEDIUM);
        pageTitle.setTextFill(Colors.ON_BACKGROUND);

        HBox statsSection = createStatsSection();
        VBox filtersSection = createFiltersSection();
        VBox tableSection = createTableSection();

        examinersContent.getChildren().addAll(pageTitle, statsSection, filtersSection, tableSection);

        contentArea.getChildren().clear();
        contentArea.getChildren().add(examinersContent);
    }

    private HBox createStatsSection() {
        HBox statsSection = new HBox(16);
        statsSection.setAlignment(Pos.CENTER_LEFT);

        totalExaminersLabel = new Label("0");
        totalExaminersLabel.setFont(Typography.HEADLINE_LARGE);
        totalExaminersLabel.setTextFill(Colors.PRIMARY);

        Label totalExaminersDesc = new Label("Tổng số giám thị");
        totalExaminersDesc.setFont(Typography.BODY_MEDIUM);
        totalExaminersDesc.setTextFill(Colors.ON_SURFACE_VARIANT);

        VBox totalExaminersBox = new VBox(4);
        totalExaminersBox.setAlignment(Pos.CENTER_LEFT);
        totalExaminersBox.getChildren().addAll(totalExaminersLabel, totalExaminersDesc);

        activeExaminersLabel = new Label("0");
        activeExaminersLabel.setFont(Typography.HEADLINE_LARGE);
        activeExaminersLabel.setTextFill(Colors.SECONDARY);

        Label activeExaminersDesc = new Label("Giám thị hoạt động");
        activeExaminersDesc.setFont(Typography.BODY_MEDIUM);
        activeExaminersDesc.setTextFill(Colors.ON_SURFACE_VARIANT);

        VBox activeExaminersBox = new VBox(4);
        activeExaminersBox.setAlignment(Pos.CENTER_LEFT);
        activeExaminersBox.getChildren().addAll(activeExaminersLabel, activeExaminersDesc);

        inactiveExaminersLabel = new Label("0");
        inactiveExaminersLabel.setFont(Typography.HEADLINE_LARGE);
        inactiveExaminersLabel.setTextFill(Colors.TERTIARY);

        Label inactiveExaminersDesc = new Label("Giám thị không hoạt động");
        inactiveExaminersDesc.setFont(Typography.BODY_MEDIUM);
        inactiveExaminersDesc.setTextFill(Colors.ON_SURFACE_VARIANT);

        VBox inactiveExaminersBox = new VBox(4);
        inactiveExaminersBox.setAlignment(Pos.CENTER_LEFT);
        inactiveExaminersBox.getChildren().addAll(inactiveExaminersLabel, inactiveExaminersDesc);

        statsSection.getChildren().addAll(totalExaminersBox, activeExaminersBox, inactiveExaminersBox);
        return statsSection;
    }

    private VBox createFiltersSection() {
        VBox filtersSection = Cards.createCard();
        filtersSection.setSpacing(16);
        filtersSection.setPadding(new Insets(20));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(12);

        FontIcon filterIcon = Icons.createFileDocumentIcon();
        filterIcon.setIconSize(24);
        filterIcon.setIconColor(Colors.PRIMARY);

        Label filterLabel = new Label("Lọc giám thị");
        filterLabel.setFont(Typography.TITLE_MEDIUM);
        filterLabel.setTextFill(Colors.ON_SURFACE);

        header.getChildren().addAll(filterIcon, filterLabel);

        HBox filterControls = new HBox(16);
        filterControls.setAlignment(Pos.CENTER_LEFT);

        // Search field
        VBox searchBox = new VBox(4);
        Label searchLabel = new Label("Tìm kiếm");
        searchLabel.setFont(Typography.LABEL_MEDIUM);
        searchLabel.setTextFill(Colors.ON_SURFACE);

        searchField = new TextField();
        searchField.setPromptText("Tìm theo tên, email...");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterExaminers());

        searchBox.getChildren().addAll(searchLabel, searchField);

        // Status filter
        VBox statusBox = new VBox(4);
        Label statusLabel = new Label("Trạng thái");
        statusLabel.setFont(Typography.LABEL_MEDIUM);
        statusLabel.setTextFill(Colors.ON_SURFACE);

        statusFilter = new ComboBox<>();
        statusFilter.setPromptText("Tất cả trạng thái");
        statusFilter.setPrefWidth(200);
        statusFilter.getItems().addAll("Tất cả", "Hoạt động", "Không hoạt động");
        statusFilter.setValue("Tất cả");
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterExaminers());

        statusBox.getChildren().addAll(statusLabel, statusFilter);

        filterControls.getChildren().addAll(searchBox, statusBox);

        filtersSection.getChildren().addAll(header, filterControls);
        return filtersSection;
    }

    private VBox createTableSection() {
        VBox tableSection = Cards.createCard();
        tableSection.setSpacing(16);
        tableSection.setPadding(new Insets(20));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(16);

        Label tableLabel = new Label("Danh sách giám thị");
        tableLabel.setFont(Typography.TITLE_MEDIUM);
        tableLabel.setTextFill(Colors.ON_SURFACE);

        Button addButton = Buttons.createFilledButton("Thêm giám thị", Icons.createAccountPlusIcon());
        addButton.setOnAction(e -> showAddExaminerDialog());

        Button refreshButton = Buttons.createOutlinedButton("Làm mới", Icons.createFileDocumentIcon());
        refreshButton.setOnAction(e -> loadData());

        header.getChildren().addAll(tableLabel, addButton, refreshButton);

        examinersTable = createExaminersTable();

        tableSection.getChildren().addAll(header, examinersTable);
        return tableSection;
    }

    private TableView<User> createExaminersTable() {
        TableView<User> table = new TableView<>();
        table.setPlaceholder(new Label("Không có giám thị nào"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ID column
        TableColumn<User, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        // Full name column
        TableColumn<User, String> nameCol = new TableColumn<>("Họ tên");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFullName()));

        // Email column
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        // Phone column
        TableColumn<User, String> phoneCol = new TableColumn<>("Số điện thoại");
        phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));

        // Status column
        TableColumn<User, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(data -> {
            UserStatus status = data.getValue().getStatus();
            String statusText = status == UserStatus.ACTIVE ? "Hoạt động" : "Không hoạt động";
            return new SimpleStringProperty(statusText);
        });

        // Actions column
        TableColumn<User, Void> actionsCol = new TableColumn<>("Thao tác");
        actionsCol.setCellFactory(col -> new TableCell<User, Void>() {
            private final HBox actionBox = new HBox(8);
            private final Button editButton = Buttons.createIconButton(Icons.createFileDocumentIcon());
            private final Button deleteButton = Buttons.createIconButton(Icons.createFileDocumentIcon());

            {
                editButton.setTooltip(new Tooltip("Sửa"));
                editButton.setOnAction(e -> {
                    User examiner = getTableView().getItems().get(getIndex());
                    showEditExaminerDialog(examiner);
                });

                deleteButton.setTooltip(new Tooltip("Xóa"));
                deleteButton.setOnAction(e -> {
                    User examiner = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(examiner);
                });

                actionBox.getChildren().addAll(editButton, deleteButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBox);
            }
        });

        table.getColumns().clear();
        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, statusCol, actionsCol);
        table.setItems(examiners);

        return table;
    }

    private void loadData() {
        try {
            List<User> allUsers = userDAO.getAll();
            examiners.clear();
            
            // Filter only examiners
            for (User user : allUsers) {
                if (user.getRole() == UserRole.EXAMINER) {
                    examiners.add(user);
                }
            }

            updateStats();
        } catch (Exception e) {
            showError("Lỗi", "Không thể tải dữ liệu: " + e.getMessage());
        }
    }

    private void filterExaminers() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = statusFilter.getValue();

        List<User> allUsers = userDAO.getAll();
        examiners.clear();

        for (User user : allUsers) {
            if (user.getRole() != UserRole.EXAMINER) {
                continue;
            }

            boolean matchesSearch = true;
            boolean matchesStatus = true;

            // Search filter
            if (!searchText.isEmpty()) {
                String fullName = user.getFullName() != null ? user.getFullName().toLowerCase() : "";
                String email = user.getEmail() != null ? user.getEmail().toLowerCase() : "";
                matchesSearch = fullName.contains(searchText) || email.contains(searchText);
            }

            // Status filter
            if (selectedStatus != null && !selectedStatus.equals("Tất cả")) {
                if (selectedStatus.equals("Hoạt động")) {
                    matchesStatus = user.getStatus() == UserStatus.ACTIVE;
                } else if (selectedStatus.equals("Không hoạt động")) {
                    matchesStatus = user.getStatus() == UserStatus.INACTIVE;
                }
            }

            if (matchesSearch && matchesStatus) {
                examiners.add(user);
            }
        }
    }

    private void updateStats() {
        int total = examiners.size();
        int active = 0;
        int inactive = 0;

        for (User examiner : examiners) {
            if (examiner.getStatus() == UserStatus.ACTIVE) {
                active++;
            } else {
                inactive++;
            }
        }

        totalExaminersLabel.setText(String.valueOf(total));
        activeExaminersLabel.setText(String.valueOf(active));
        inactiveExaminersLabel.setText(String.valueOf(inactive));
    }

    private void showAddExaminerDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Thêm giám thị");
        dialog.setHeaderText("Nhập thông tin giám thị mới");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Họ tên");
        nameField.setPrefWidth(300);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefWidth(300);

        TextField phoneField = new TextField();
        phoneField.setPromptText("Số điện thoại");
        phoneField.setPrefWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu");
        passwordField.setPrefWidth(300);

        ComboBox<UserStatus> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll(UserStatus.ACTIVE, UserStatus.INACTIVE);
        statusCombo.setValue(UserStatus.ACTIVE);
        statusCombo.setPrefWidth(300);

        content.getChildren().addAll(
                new Label("Họ tên:"), nameField,
                new Label("Email:"), emailField,
                new Label("Số điện thoại:"), phoneField,
                new Label("Mật khẩu:"), passwordField,
                new Label("Trạng thái:"), statusCombo
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String password = passwordField.getText().trim();
                    UserStatus status = statusCombo.getValue();

                    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        showError("Lỗi", "Vui lòng điền đầy đủ thông tin bắt buộc");
                        return null;
                    }

                    User newExaminer = new User();
                    newExaminer.setFullName(name);
                    newExaminer.setEmail(email);
                    newExaminer.setPhone(phone);
                    newExaminer.setPassword(password);
                    newExaminer.setRole(UserRole.EXAMINER);
                    newExaminer.setStatus(status);

                    return newExaminer;
                } catch (Exception e) {
                    showError("Lỗi", "Thông tin không hợp lệ: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(examiner -> {
            try {
                userDAO.addUser(examiner);
                loadData();
                showSuccess("Thành công", "Đã thêm giám thị thành công");
            } catch (Exception e) {
                showError("Lỗi", "Không thể thêm giám thị: " + e.getMessage());
            }
        });
    }

    private void showEditExaminerDialog(User examiner) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Sửa giám thị");
        dialog.setHeaderText("Cập nhật thông tin giám thị");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        TextField nameField = new TextField(examiner.getFullName());
        nameField.setPromptText("Họ tên");
        nameField.setPrefWidth(300);

        TextField emailField = new TextField(examiner.getEmail());
        emailField.setPromptText("Email");
        emailField.setPrefWidth(300);

        TextField phoneField = new TextField(examiner.getPhone());
        phoneField.setPromptText("Số điện thoại");
        phoneField.setPrefWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu mới (để trống nếu không đổi)");
        passwordField.setPrefWidth(300);

        ComboBox<UserStatus> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll(UserStatus.ACTIVE, UserStatus.INACTIVE);
        statusCombo.setValue(examiner.getStatus());
        statusCombo.setPrefWidth(300);

        content.getChildren().addAll(
                new Label("Họ tên:"), nameField,
                new Label("Email:"), emailField,
                new Label("Số điện thoại:"), phoneField,
                new Label("Mật khẩu mới:"), passwordField,
                new Label("Trạng thái:"), statusCombo
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String password = passwordField.getText().trim();
                    UserStatus status = statusCombo.getValue();

                    if (name.isEmpty() || email.isEmpty()) {
                        showError("Lỗi", "Vui lòng điền đầy đủ thông tin bắt buộc");
                        return null;
                    }

                    examiner.setFullName(name);
                    examiner.setEmail(email);
                    examiner.setPhone(phone);
                    examiner.setStatus(status);

                    if (!password.isEmpty()) {
                        examiner.setPassword(password);
                    }

                    return examiner;
                } catch (Exception e) {
                    showError("Lỗi", "Thông tin không hợp lệ: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        result.ifPresent(updatedExaminer -> {
            try {
                userDAO.updateUser(updatedExaminer);
                loadData();
                showSuccess("Thành công", "Đã cập nhật giám thị thành công");
            } catch (Exception e) {
                showError("Lỗi", "Không thể cập nhật giám thị: " + e.getMessage());
            }
        });
    }

    private void showDeleteConfirmation(User examiner) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa giám thị");
        alert.setContentText("Bạn có chắc chắn muốn xóa giám thị '" + examiner.getFullName() + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userDAO.deleteUser(examiner.getId());
                loadData();
                showSuccess("Thành công", "Đã xóa giám thị thành công");
            } catch (Exception e) {
                showError("Lỗi", "Không thể xóa giám thị: " + e.getMessage());
            }
        }
    }

    private void handleNavItemClick(String itemName) {
        System.out.println("ExaminerManagement navigating to: " + itemName);
        switch (itemName) {
            case "Dashboard":
                handleBack();
                break;
            case "Người dùng":
                showInfo("Thông báo", "Chức năng quản lý người dùng đang được phát triển");
                break;
            case "Giám thị":
                // Stay on current screen
                break;
            case "Thí sinh":
                showInfo("Thông báo", "Chức năng quản lý thí sinh đang được phát triển");
                break;
            case "Loại thi":
                showInfo("Thông báo", "Chức năng quản lý loại thi đang được phát triển");
                break;
            case "Lịch thi":
                showInfo("Thông báo", "Chức năng quản lý lịch thi đang được phát triển");
                break;
            case "Báo cáo":
                showInfo("Thông báo", "Chức năng báo cáo đang được phát triển");
                break;
            default:
                System.out.println("Unknown navigation item: " + itemName);
                break;
        }
    }

    private void handleBack() {
        System.out.println("Going back to admin dashboard");
        // TODO: Navigate back to admin dashboard
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene createScene() {
        return scene;
    }
} 