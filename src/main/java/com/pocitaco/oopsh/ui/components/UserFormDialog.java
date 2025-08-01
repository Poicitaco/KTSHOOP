package com.pocitaco.oopsh.ui.components;

import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.enums.UserStatus;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Colors;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Typography;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Buttons;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Optional;

/**
 * User Form Dialog - Material Design 3.0
 * Dialog for creating and editing users
 */
public class UserFormDialog extends Stage {

    private TextField usernameField;
    private PasswordField passwordField;
    private TextField fullNameField;
    private TextField emailField;
    private TextField phoneField;
    private DatePicker birthDatePicker;
    private TextField addressField;
    private ComboBox<UserRole> roleComboBox;
    private ComboBox<UserStatus> statusComboBox;

    private User user;
    private boolean confirmed = false;

    public UserFormDialog(Stage owner, String title, User user) {
        this.user = user;

        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle(title);
        setResizable(false);

        createContent();

        if (user != null) {
            populateFields();
        }
    }

    private void createContent() {
        VBox root = new VBox();
        root.setSpacing(24);
        root.setPadding(new Insets(24));
        root.setPrefWidth(500);
        root.setBackground(new Background(new BackgroundFill(
                Colors.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Title
        Label titleLabel = new Label(user == null ? "Thêm người dùng mới" : "Chỉnh sửa người dùng");
        titleLabel.setFont(Typography.HEADLINE_SMALL);
        titleLabel.setTextFill(Colors.ON_SURFACE);

        // Form
        VBox formContainer = createForm();

        // Buttons
        HBox buttonContainer = createButtonContainer();

        root.getChildren().addAll(titleLabel, formContainer, buttonContainer);

        Scene scene = new Scene(root);
        setScene(scene);
    }

    private VBox createForm() {
        VBox form = new VBox();
        form.setSpacing(16);

        // Username
        usernameField = new TextField();
        usernameField.setPromptText("Tên đăng nhập");
        styleTextField(usernameField);

        // Password
        passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu");
        styleTextField(passwordField);

        // Full name
        fullNameField = new TextField();
        fullNameField.setPromptText("Họ và tên");
        styleTextField(fullNameField);

        // Email
        emailField = new TextField();
        emailField.setPromptText("Email");
        styleTextField(emailField);

        // Phone
        phoneField = new TextField();
        phoneField.setPromptText("Số điện thoại");
        styleTextField(phoneField);

        // Birth date
        birthDatePicker = new DatePicker();
        birthDatePicker.setPromptText("Ngày sinh");
        styleTextField(birthDatePicker);

        // Address
        addressField = new TextField();
        addressField.setPromptText("Địa chỉ");
        styleTextField(addressField);

        // Role
        roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(UserRole.values());
        roleComboBox.setValue(UserRole.CANDIDATE);
        styleComboBox(roleComboBox);

        // Status
        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(UserStatus.values());
        statusComboBox.setValue(UserStatus.ACTIVE);
        styleComboBox(statusComboBox);

        form.getChildren().addAll(
                createFieldContainer("Tên đăng nhập *", usernameField),
                createFieldContainer("Mật khẩu *", passwordField),
                createFieldContainer("Họ và tên *", fullNameField),
                createFieldContainer("Email", emailField),
                createFieldContainer("Số điện thoại", phoneField),
                createFieldContainer("Ngày sinh", birthDatePicker),
                createFieldContainer("Địa chỉ", addressField),
                createFieldContainer("Vai trò", roleComboBox),
                createFieldContainer("Trạng thái", statusComboBox));

        return form;
    }

    private VBox createFieldContainer(String labelText, Control field) {
        VBox container = new VBox();
        container.setSpacing(4);

        Label label = new Label(labelText);
        label.setFont(Typography.BODY_SMALL);
        label.setTextFill(Colors.ON_SURFACE_VARIANT);

        container.getChildren().addAll(label, field);
        return container;
    }

    private void styleTextField(Control field) {
        field.setStyle(
                "-fx-background-color: " + toHexString(Colors.SURFACE) + ";" +
                        "-fx-border-color: " + toHexString(Colors.ON_SURFACE_VARIANT) + ";" +
                        "-fx-border-radius: 4px;" +
                        "-fx-padding: 12px;" +
                        "-fx-font-size: 14px;");
    }

    private void styleComboBox(ComboBox<?> comboBox) {
        comboBox.setStyle(
                "-fx-background-color: " + toHexString(Colors.SURFACE) + ";" +
                        "-fx-border-color: " + toHexString(Colors.ON_SURFACE_VARIANT) + ";" +
                        "-fx-border-radius: 4px;" +
                        "-fx-padding: 8px;");
    }

    private HBox createButtonContainer() {
        HBox container = new HBox();
        container.setSpacing(12);
        container.setAlignment(Pos.CENTER_RIGHT);

        Button cancelButton = Buttons.createTextButton("Hủy", null);
        cancelButton.setOnAction(e -> close());

        Button saveButton = Buttons.createFilledButton("Lưu", null);
        saveButton.setOnAction(e -> handleSave());

        container.getChildren().addAll(cancelButton, saveButton);
        return container;
    }

    private void populateFields() {
        if (user != null) {
            usernameField.setText(user.getUsername());
            passwordField.setText(user.getPassword());
            fullNameField.setText(user.getFullName());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone());
            if (user.getBirthday() != null) {
                birthDatePicker.setValue(user.getBirthday());
            }
            addressField.setText(user.getAddress());
            roleComboBox.setValue(user.getRole());
            statusComboBox.setValue(user.getStatus());
        }
    }

    private void handleSave() {
        // Validate required fields
        if (usernameField.getText().trim().isEmpty() ||
                passwordField.getText().trim().isEmpty() ||
                fullNameField.getText().trim().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Thông tin thiếu");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng điền đầy đủ các trường bắt buộc (*)");
            alert.showAndWait();
            return;
        }

        confirmed = true;
        close();
    }

    public Optional<User> getResult() {
        if (confirmed) {
            User resultUser = user != null ? user : new User();

            resultUser.setUsername(usernameField.getText().trim());
            resultUser.setPassword(passwordField.getText().trim());
            resultUser.setFullName(fullNameField.getText().trim());
            resultUser.setEmail(emailField.getText().trim());
            resultUser.setPhone(phoneField.getText().trim());
            resultUser.setBirthday(birthDatePicker.getValue());
            resultUser.setAddress(addressField.getText().trim());
            resultUser.setRole(roleComboBox.getValue());
            resultUser.setStatus(statusComboBox.getValue());

            if (user == null) {
                resultUser.setId((int) (System.currentTimeMillis() % Integer.MAX_VALUE));
                resultUser.setCreatedDate(LocalDate.now());
            }

            return Optional.of(resultUser);
        }

        return Optional.empty();
    }

    private String toHexString(javafx.scene.paint.Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}