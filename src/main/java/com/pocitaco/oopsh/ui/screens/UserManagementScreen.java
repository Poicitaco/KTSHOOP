package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Colors;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Typography;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Icons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Buttons;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Cards;
import com.pocitaco.oopsh.ui.components.UserFormDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * User Management Screen - Material Design
 * Màn hình quản lý người dùng theo chuẩn Material Design 3.0
 */
public class UserManagementScreen {

    private Stage primaryStage;
    private Scene scene;
    private BorderPane mainLayout;
    private VBox navigationRail;
    private VBox contentArea;
    private TableView<User> userTable;
    private ObservableList<User> userList;
    private UserDAO userDAO;
    private TextField searchField;

    public UserManagementScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.userDAO = new UserDAO();
        this.userList = FXCollections.observableArrayList();
        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        // Main layout
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

        // Create scene
        scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setScene(scene);
    }

    private HBox createAppBar() {
        HBox appBar = new HBox();
        appBar.setAlignment(Pos.CENTER_LEFT);
        appBar.setSpacing(16);
        appBar.setPadding(new Insets(0, 24, 0, 24));
        appBar.setPrefHeight(64);
        appBar.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Back button
        Button backButton = Buttons.createIconButton(Icons.createMenuIcon());
        backButton.setOnAction(e -> goBackToDashboard());

        // Title
        Label title = new Label("User Management");
        title.setFont(Typography.TITLE_LARGE);
        title.setTextFill(Colors.ON_SURFACE);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        appBar.getChildren().addAll(backButton, title, spacer);

        // Add elevation
        appBar.setEffect(createElevation(2));

        return appBar;
    }

    private TextField createSearchField() {
        TextField searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setPrefWidth(300);
        searchField.setFont(Typography.BODY_MEDIUM);

        // Style search field
        searchField.setStyle(
                "-fx-background-color: " + toHexString(Colors.SURFACE_VARIANT) + ";" +
                        "-fx-border-color: " + toHexString(Colors.ON_SURFACE_VARIANT) + ";" +
                        "-fx-border-radius: 20px;" +
                        "-fx-background-radius: 20px;" +
                        "-fx-padding: 8px 16px;");

        // Search functionality
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterUsers(newText);
        });

        return searchField;
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
                createNavItem("Users", Icons.createAccountGroupIcon(), true),
                createNavItem("Exams", Icons.createFileDocumentIcon(), false),
                createNavItem("Schedule", Icons.createCalendarIcon(), false),
                createNavItem("Reports", Icons.createChartBarIcon(), false),
                createNavItem("Payments", Icons.createCreditCardIcon(), false));

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

        // Click handler
        if (!selected) {
            navItem.setOnMouseClicked(e -> handleNavItemClick(text));
        }

        // Hover effects
        if (!selected) {
            navItem.setOnMouseEntered(e -> navItem.setBackground(new Background(new BackgroundFill(
                    Colors.ON_SURFACE_VARIANT.deriveColor(0, 1, 1, 0.08),
                    new CornerRadii(16), Insets.EMPTY))));
            navItem.setOnMouseExited(e -> navItem.setBackground(Background.EMPTY));
        }

        return navItem;
    }

    private VBox createContentArea() {
        VBox content = new VBox();
        content.setSpacing(24);
        content.setPadding(new Insets(24));
        content.setBackground(new Background(new BackgroundFill(
                Colors.BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        // Statistics cards
        HBox statsRow = createStatsRow();

        // User table
        VBox tableSection = createTableSection();

        content.getChildren().addAll(statsRow, tableSection);

        return content;
    }

    private HBox createStatsRow() {
        HBox statsRow = new HBox();
        statsRow.setSpacing(16);
        statsRow.setAlignment(Pos.CENTER);

        VBox totalUsersCard = createStatCard("Total Users", String.valueOf(userDAO.getAllUsers().size()),
                Icons.createAccountGroupIcon(), Colors.PRIMARY);
        VBox adminCard = createStatCard("Admins",
                String.valueOf(userDAO.getUsersByRole(UserRole.ADMIN).size()),
                Icons.createShieldAccountIcon(), Colors.ERROR);
        VBox examinersCard = createStatCard("Examiners",
                String.valueOf(userDAO.getUsersByRole(UserRole.EXAMINER).size()),
                Icons.createSchoolIcon(), Colors.WARNING);
        VBox candidatesCard = createStatCard("Candidates",
                String.valueOf(userDAO.getUsersByRole(UserRole.CANDIDATE).size()),
                Icons.createAccountIcon(), Colors.SUCCESS);

        statsRow.getChildren().addAll(totalUsersCard, adminCard, examinersCard, candidatesCard);
        return statsRow;
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

    private VBox createTableSection() {
        VBox tableSection = new VBox();
        tableSection.setSpacing(16);

        // Section header with title
        Label sectionTitle = new Label("User List");
        sectionTitle.setFont(Typography.TITLE_LARGE);
        sectionTitle.setTextFill(Colors.ON_BACKGROUND);

        // Controls row (search + actions)
        HBox controlsRow = new HBox();
        controlsRow.setAlignment(Pos.CENTER_LEFT);
        controlsRow.setSpacing(16);

        // Search field
        searchField = createSearchField();

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action buttons group
        HBox actionButtons = new HBox();
        actionButtons.setSpacing(8);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button addUserButton = Buttons.createFilledButton("Add User", Icons.createAccountPlusIcon());
        addUserButton.setOnAction(e -> showAddUserDialog());

        Button refreshButton = Buttons.createOutlinedButton("Refresh", Icons.createMenuIcon());
        refreshButton.setOnAction(e -> loadUsers());

        Button exportButton = Buttons.createTextButton("Export", Icons.createFileDocumentIcon());
        exportButton.setOnAction(e -> exportUsers());

        actionButtons.getChildren().addAll(addUserButton, refreshButton, exportButton);
        controlsRow.getChildren().addAll(searchField, spacer, actionButtons);

        // Table
        userTable = createUserTable();

        // Table card
        VBox tableCard = Cards.createCard();
        tableCard.getChildren().add(userTable);

        tableSection.getChildren().addAll(sectionTitle, controlsRow, tableCard);
        return tableSection;
    }

    private TableView<User> createUserTable() {
        TableView<User> table = new TableView<>();
        table.setItems(userList);
        table.setPrefHeight(400);

        // ID Column
        TableColumn<User, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(100);

        // Username Column
        TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameColumn.setPrefWidth(150);

        // Full Name Column
        TableColumn<User, String> fullNameColumn = new TableColumn<>("Full Name");
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        fullNameColumn.setPrefWidth(200);

        // Email Column
        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setPrefWidth(200);

        // Role Column
        TableColumn<User, UserRole> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleColumn.setPrefWidth(100);

        // Created Date Column
        TableColumn<User, LocalDate> createdColumn = new TableColumn<>("Created");
        createdColumn.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        createdColumn.setPrefWidth(150);
        createdColumn.setCellFactory(column -> new TableCell<User, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
            }
        });

        // Actions Column
        TableColumn<User, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setPrefWidth(150);
        actionsColumn.setCellFactory(column -> new TableCell<User, Void>() {
            private final HBox actionBox = new HBox();
            private final Button editButton = Buttons.createIconButton(Icons.createEditIcon());
            private final Button deleteButton = Buttons.createIconButton(Icons.createDeleteIcon());

            {
                actionBox.setSpacing(8);
                actionBox.setAlignment(Pos.CENTER);

                editButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showEditUserDialog(user);
                });

                deleteButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(user);
                });

                actionBox.getChildren().addAll(editButton, deleteButton);
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

        table.getColumns().add(idColumn);
        table.getColumns().add(usernameColumn);
        table.getColumns().add(fullNameColumn);
        table.getColumns().add(emailColumn);
        table.getColumns().add(roleColumn);
        table.getColumns().add(createdColumn);
        table.getColumns().add(actionsColumn);

        return table;
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            userList.clear();
            userList.addAll(users);
            System.out.println("✅ Loaded " + users.size() + " users");
        } catch (Exception e) {
            System.err.println("❌ Error loading users: " + e.getMessage());
            showErrorAlert("Error", "Failed to load users: " + e.getMessage());
        }
    }

    private void filterUsers(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadUsers();
            return;
        }

        try {
            List<User> allUsers = userDAO.getAllUsers();
            List<User> filteredUsers = allUsers.stream()
                    .filter(user -> user.getUsername().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getFullName().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                            user.getRole().toString().toLowerCase().contains(searchText.toLowerCase()))
                    .toList();

            userList.clear();
            userList.addAll(filteredUsers);
        } catch (Exception e) {
            System.err.println("❌ Error filtering users: " + e.getMessage());
        }
    }

    private void showAddUserDialog() {
        UserFormDialog dialog = new UserFormDialog(primaryStage, "Add User", null);
        dialog.showAndWait().ifPresent(user -> {
            try {
                userDAO.addUser(user);
                loadUsers();
                showSuccessAlert("Success", "User added successfully!");
                System.out.println("✅ User added: " + user.getUsername());
            } catch (Exception e) {
                System.err.println("❌ Error adding user: " + e.getMessage());
                showErrorAlert("Error", "Failed to add user: " + e.getMessage());
            }
        });
    }

    private void showEditUserDialog(User user) {
        UserFormDialog dialog = new UserFormDialog(primaryStage, "Edit User", user);
        dialog.showAndWait().ifPresent(updatedUser -> {
            try {
                userDAO.updateUser(updatedUser);
                loadUsers();
                showSuccessAlert("Success", "User updated successfully!");
                System.out.println("✅ User updated: " + updatedUser.getUsername());
            } catch (Exception e) {
                System.err.println("❌ Error updating user: " + e.getMessage());
                showErrorAlert("Error", "Failed to update user: " + e.getMessage());
            }
        });
    }

    private void exportUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("ID,Username,Full Name,Email,Role,Created Date\n");

            for (User user : users) {
                csvContent.append(String.format("%d,%s,%s,%s,%s,%s\n",
                        user.getId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole().toString(),
                        user.getCreatedDate() != null ? user.getCreatedDate().toString() : "N/A"));
            }

            // Show content in console for now (in real app, would save to file)
            System.out.println("📁 Exported Users CSV:\n" + csvContent.toString());
            showSuccessAlert("Export", "User data exported to console (CSV format)");
        } catch (Exception e) {
            System.err.println("❌ Error exporting users: " + e.getMessage());
            showErrorAlert("Error", "Failed to export users: " + e.getMessage());
        }
    }

    private void showDeleteConfirmation(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Delete User: " + user.getUsername());
        alert.setContentText("Are you sure you want to delete this user? This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteUser(user);
            }
        });
    }

    private void deleteUser(User user) {
        try {
            userDAO.deleteUser(user.getId());
            userList.remove(user);
            showSuccessAlert("Success", "User deleted successfully.");
            System.out.println("✅ User deleted: " + user.getUsername());
        } catch (Exception e) {
            System.err.println("❌ Error deleting user: " + e.getMessage());
            showErrorAlert("Error", "Failed to delete user: " + e.getMessage());
        }
    }

    private void handleNavItemClick(String itemName) {
        System.out.println("🔄 Navigating to: " + itemName);

        switch (itemName) {
            case "Dashboard":
                goBackToDashboard();
                break;
            default:
                showInfoAlert("Info", itemName + " screen will be implemented next.");
        }
    }

    private void goBackToDashboard() {
        DashboardScreen dashboard = new DashboardScreen(primaryStage);
        dashboard.show();
    }

    // Helper methods
    private javafx.scene.effect.DropShadow createElevation(int level) {
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.2));
        shadow.setOffsetY(level * 2);
        shadow.setRadius(level * 4);
        return shadow;
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
