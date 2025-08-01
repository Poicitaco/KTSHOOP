package com.pocitaco.oopsh.ui.components;

import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.enums.ScheduleStatus;
import com.pocitaco.oopsh.enums.TimeSlot;
import com.pocitaco.oopsh.enums.UserRole;
import com.pocitaco.oopsh.models.ExamSchedule;
import com.pocitaco.oopsh.models.ExamType;
import com.pocitaco.oopsh.models.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Exam Schedule Form Dialog - Material Design 3.0
 */
public class ExamScheduleFormDialog {

    private final Stage dialog;
    private final ExamSchedule originalSchedule;
    private ExamSchedule result;

    // DAOs
    private final ExamTypeDAO examTypeDAO;
    private final UserDAO userDAO;

    // Form fields
    private ComboBox<ExamType> examTypeComboBox;
    private TextField scheduleNameField;
    private DatePicker scheduleDatePicker;
    private ComboBox<TimeSlot> timeSlotComboBox;
    private TextField venueField;
    private Spinner<Integer> capacitySpinner;
    private ComboBox<User> examinerComboBox;
    private TextField descriptionField;
    private ComboBox<ScheduleStatus> statusComboBox;

    public ExamScheduleFormDialog(Stage parent, String title, ExamSchedule schedule) {
        this.originalSchedule = schedule;
        this.examTypeDAO = new ExamTypeDAO();
        this.userDAO = new UserDAO();
        this.dialog = new Stage();

        initializeDialog(parent, title);
        setupForm();

        if (schedule != null) {
            populateFields(schedule);
        }
    }

    private void initializeDialog(Stage parent, String title) {
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parent);
        dialog.setTitle(title);
        dialog.setResizable(false);
    }

    private void setupForm() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setStyle(
                "-fx-background-color: #FFFBFE;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;");

        // Title
        Label titleLabel = new Label(originalSchedule == null ? "Tạo Lịch Thi Mới" : "Chỉnh Sửa Lịch Thi");
        titleLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1C1B1F;");

        // Form content
        ScrollPane scrollPane = new ScrollPane();
        VBox formContent = createFormContent();
        scrollPane.setContent(formContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Buttons
        HBox buttonBox = createButtonBox();

        root.getChildren().addAll(titleLabel, scrollPane, buttonBox);

        Scene scene = new Scene(root, 600, 700);
        dialog.setScene(scene);
    }

    private VBox createFormContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(0, 12, 0, 0)); // Space for scrollbar

        // Basic Information Section
        Label basicInfoLabel = new Label("Thông Tin Cơ Bản");
        basicInfoLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #6750A4;");

        GridPane basicGrid = new GridPane();
        basicGrid.setHgap(16);
        basicGrid.setVgap(16);
        int row = 0;

        // Exam Type
        basicGrid.add(createFieldLabel("Loại kỳ thi *"), 0, row);
        examTypeComboBox = new ComboBox<>();
        loadExamTypes();
        examTypeComboBox.setPrefWidth(350);
        examTypeComboBox.setConverter(new javafx.util.StringConverter<ExamType>() {
            @Override
            public String toString(ExamType examType) {
                return examType != null ? examType.getName() : "";
            }

            @Override
            public ExamType fromString(String string) {
                return null;
            }
        });
        styleComboBox(examTypeComboBox);
        basicGrid.add(examTypeComboBox, 1, row++);

        // Schedule Name
        basicGrid.add(createFieldLabel("Tên lịch thi *"), 0, row);
        scheduleNameField = new TextField();
        scheduleNameField.setPrefWidth(350);
        scheduleNameField.setPromptText("Nhập tên lịch thi...");
        styleTextField(scheduleNameField);
        basicGrid.add(scheduleNameField, 1, row++);

        // Schedule Date
        basicGrid.add(createFieldLabel("Ngày thi *"), 0, row);
        scheduleDatePicker = new DatePicker();
        scheduleDatePicker.setPrefWidth(350);
        scheduleDatePicker.setValue(LocalDate.now().plusDays(7)); // Default to next week
        styleControl(scheduleDatePicker);
        basicGrid.add(scheduleDatePicker, 1, row++);

        // Time Slot
        basicGrid.add(createFieldLabel("Buổi thi *"), 0, row);
        timeSlotComboBox = new ComboBox<>();
        timeSlotComboBox.getItems().addAll(TimeSlot.values());
        timeSlotComboBox.setPrefWidth(350);
        timeSlotComboBox.setConverter(new javafx.util.StringConverter<TimeSlot>() {
            @Override
            public String toString(TimeSlot timeSlot) {
                return timeSlot != null ? timeSlot.getDisplayName() : "";
            }

            @Override
            public TimeSlot fromString(String string) {
                return null;
            }
        });
        styleComboBox(timeSlotComboBox);
        basicGrid.add(timeSlotComboBox, 1, row++);

        // Details Section
        Label detailsLabel = new Label("Chi Tiết");
        detailsLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #8E24AA;");

        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(16);
        detailsGrid.setVgap(16);
        row = 0;

        // Venue
        detailsGrid.add(createFieldLabel("Địa điểm *"), 0, row);
        venueField = new TextField();
        venueField.setPrefWidth(350);
        venueField.setPromptText("Nhập địa điểm thi...");
        styleTextField(venueField);
        detailsGrid.add(venueField, 1, row++);

        // Capacity
        detailsGrid.add(createFieldLabel("Sức chứa *"), 0, row);
        capacitySpinner = new Spinner<>(1, 200, 30);
        capacitySpinner.setPrefWidth(350);
        capacitySpinner.setEditable(true);
        styleControl(capacitySpinner);
        detailsGrid.add(capacitySpinner, 1, row++);

        // Examiner
        detailsGrid.add(createFieldLabel("Giám thị *"), 0, row);
        examinerComboBox = new ComboBox<>();
        loadExaminers();
        examinerComboBox.setPrefWidth(350);
        examinerComboBox.setConverter(new javafx.util.StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getFullName() + " (" + user.getUsername() + ")" : "";
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });
        styleComboBox(examinerComboBox);
        detailsGrid.add(examinerComboBox, 1, row++);

        // Description
        detailsGrid.add(createFieldLabel("Mô tả"), 0, row);
        descriptionField = new TextField();
        descriptionField.setPrefWidth(350);
        descriptionField.setPromptText("Nhập mô tả (tùy chọn)...");
        styleTextField(descriptionField);
        detailsGrid.add(descriptionField, 1, row++);

        // Status
        detailsGrid.add(createFieldLabel("Trạng thái *"), 0, row);
        statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(ScheduleStatus.values());
        statusComboBox.setPrefWidth(350);
        statusComboBox.setValue(ScheduleStatus.SCHEDULED); // Default status
        statusComboBox.setConverter(new javafx.util.StringConverter<ScheduleStatus>() {
            @Override
            public String toString(ScheduleStatus status) {
                return status != null ? status.getDisplayName() : "";
            }

            @Override
            public ScheduleStatus fromString(String string) {
                return null;
            }
        });
        styleComboBox(statusComboBox);
        detailsGrid.add(statusComboBox, 1, row++);

        content.getChildren().addAll(
                basicInfoLabel,
                basicGrid,
                new Separator(),
                detailsLabel,
                detailsGrid);

        return content;
    }

    private void loadExamTypes() {
        try {
            List<ExamType> examTypes = examTypeDAO.getAll();
            examTypeComboBox.getItems().clear();
            examTypeComboBox.getItems().addAll(examTypes);
        } catch (Exception e) {
            showErrorAlert("Lỗi", "Không thể tải danh sách loại kỳ thi: " + e.getMessage());
        }
    }

    private void loadExaminers() {
        try {
            List<User> allUsers = userDAO.getAll();
            List<User> examiners = allUsers.stream()
                    .filter(user -> user.getRole() == UserRole.EXAMINER || user.getRole() == UserRole.ADMIN)
                    .toList();

            examinerComboBox.getItems().clear();
            examinerComboBox.getItems().addAll(examiners);
        } catch (Exception e) {
            showErrorAlert("Lỗi", "Không thể tải danh sách giám thị: " + e.getMessage());
        }
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: 500;" +
                        "-fx-text-fill: #49454F;" +
                        "-fx-min-width: 120px;");
        return label;
    }

    private void styleTextField(TextField field) {
        field.setStyle(
                "-fx-background-color: #F7F2FA;" +
                        "-fx-border-color: #79747E;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 16;" +
                        "-fx-text-fill: #1C1B1F;" +
                        "-fx-prompt-text-fill: #49454F;" +
                        "-fx-font-size: 14px;");

        // Focus effects
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle(
                        "-fx-background-color: #F7F2FA;" +
                                "-fx-border-color: #6750A4;" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 8;" +
                                "-fx-background-radius: 8;" +
                                "-fx-padding: 12 16;" +
                                "-fx-text-fill: #1C1B1F;" +
                                "-fx-prompt-text-fill: #49454F;" +
                                "-fx-font-size: 14px;");
            } else {
                styleTextField(field);
            }
        });
    }

    private void styleComboBox(ComboBox<?> comboBox) {
        comboBox.setStyle(
                "-fx-background-color: #F7F2FA;" +
                        "-fx-border-color: #79747E;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-text-fill: #1C1B1F;" +
                        "-fx-font-size: 14px;");
    }

    private void styleControl(Control control) {
        control.setStyle(
                "-fx-background-color: #F7F2FA;" +
                        "-fx-border-color: #79747E;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-text-fill: #1C1B1F;" +
                        "-fx-font-size: 14px;");
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(16, 0, 0, 0));

        Button cancelButton = new Button("Hủy");
        cancelButton.setPrefWidth(100);
        cancelButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #79747E;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-text-fill: #6750A4;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: 500;" +
                        "-fx-padding: 10 24;");

        Button saveButton = new Button(originalSchedule == null ? "Tạo" : "Cập Nhật");
        saveButton.setPrefWidth(100);
        saveButton.setStyle(
                "-fx-background-color: #6750A4;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: 500;" +
                        "-fx-padding: 10 24;");

        cancelButton.setOnAction(e -> dialog.close());
        saveButton.setOnAction(e -> handleSave());

        buttonBox.getChildren().addAll(cancelButton, saveButton);
        return buttonBox;
    }

    private void populateFields(ExamSchedule schedule) {
        // Find and set exam type by ID
        ExamType examType = findExamTypeById(schedule.getExamTypeId());
        examTypeComboBox.setValue(examType);

        scheduleNameField.setText(schedule.getExamTypeName()); // Use exam type name as schedule name
        scheduleDatePicker.setValue(schedule.getExamDate());
        timeSlotComboBox.setValue(schedule.getTimeSlot());
        venueField.setText(schedule.getLocation());
        capacitySpinner.getValueFactory().setValue(schedule.getMaxCandidates());

        // Find and set examiner by ID
        User examiner = findUserById(schedule.getExaminerId());
        examinerComboBox.setValue(examiner);

        descriptionField.setText(""); // No description field in model
        statusComboBox.setValue(schedule.getStatus());
    }

    private ExamType findExamTypeById(int id) {
        return examTypeComboBox.getItems().stream()
                .filter(et -> et.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private User findUserById(int id) {
        return examinerComboBox.getItems().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        try {
            ExamSchedule schedule = originalSchedule != null ? originalSchedule : new ExamSchedule();

            // Set exam type ID and name
            ExamType selectedExamType = examTypeComboBox.getValue();
            schedule.setExamTypeId(selectedExamType.getId());
            schedule.setExamTypeName(selectedExamType.getName());

            // Set examiner ID
            User selectedExaminer = examinerComboBox.getValue();
            schedule.setExaminerId(selectedExaminer.getId());

            // Set other fields using correct model methods
            schedule.setExamDate(scheduleDatePicker.getValue());
            schedule.setTimeSlot(timeSlotComboBox.getValue());
            schedule.setLocation(venueField.getText().trim());
            schedule.setMaxCandidates(capacitySpinner.getValue());
            schedule.setStatus(statusComboBox.getValue());

            // Set additional info for new schedules
            if (originalSchedule == null) {
                schedule.setId((int) (System.currentTimeMillis() % 10000));
                schedule.setCreatedDate(LocalDateTime.now());
                schedule.setRegisteredCandidates(0);
            }

            result = schedule;
            dialog.close();

        } catch (Exception e) {
            showErrorAlert("Lỗi", "Có lỗi xảy ra khi lưu lịch thi: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (examTypeComboBox.getValue() == null) {
            errors.append("• Vui lòng chọn loại kỳ thi\n");
        }

        if (scheduleNameField.getText() == null || scheduleNameField.getText().trim().isEmpty()) {
            errors.append("• Vui lòng nhập tên lịch thi\n");
        }

        if (scheduleDatePicker.getValue() == null) {
            errors.append("• Vui lòng chọn ngày thi\n");
        } else if (scheduleDatePicker.getValue().isBefore(LocalDate.now())) {
            errors.append("• Ngày thi không thể là ngày trong quá khứ\n");
        }

        if (timeSlotComboBox.getValue() == null) {
            errors.append("• Vui lòng chọn buổi thi\n");
        }

        if (venueField.getText() == null || venueField.getText().trim().isEmpty()) {
            errors.append("• Vui lòng nhập địa điểm thi\n");
        }

        if (capacitySpinner.getValue() == null || capacitySpinner.getValue() <= 0) {
            errors.append("• Sức chứa phải lớn hơn 0\n");
        }

        if (examinerComboBox.getValue() == null) {
            errors.append("• Vui lòng chọn giám thị\n");
        }

        if (statusComboBox.getValue() == null) {
            errors.append("• Vui lòng chọn trạng thái\n");
        }

        if (errors.length() > 0) {
            showErrorAlert("Lỗi Validation", errors.toString());
            return false;
        }

        return true;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Optional<ExamSchedule> showAndWait() {
        dialog.showAndWait();
        return Optional.ofNullable(result);
    }
}
