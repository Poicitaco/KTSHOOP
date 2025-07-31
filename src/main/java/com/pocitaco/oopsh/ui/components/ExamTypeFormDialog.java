package com.pocitaco.oopsh.ui.components;

import com.pocitaco.oopsh.models.ExamType;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Colors;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Typography;
import com.pocitaco.oopsh.ui.MaterialDesignManager.Buttons;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.Optional;

public class ExamTypeFormDialog extends Stage {
    private final ExamType existingExamType;
    private ExamType result;

    // Form fields
    private TextField nameField;
    private TextArea descriptionArea;
    private TextField feeField;
    private TextField durationField;
    private TextField passingScoreField;

    public ExamTypeFormDialog(Stage parent, String title, ExamType examType) {
        this.existingExamType = examType;
        this.result = null;

        initModality(Modality.WINDOW_MODAL);
        initOwner(parent);
        setTitle(title);
        setResizable(false);

        Scene scene = createScene();
        setScene(scene);

        // Populate fields if editing
        if (existingExamType != null) {
            populateFields();
        }

        Platform.runLater(() -> nameField.requestFocus());
    }

    public Optional<ExamType> showDialog() {
        super.showAndWait();
        return Optional.ofNullable(result);
    }

    private Scene createScene() {
        VBox root = new VBox(24);
        root.setStyle(Colors.BACKGROUND_SURFACE);
        root.setPadding(new Insets(32));
        root.setPrefWidth(500);

        // Header
        VBox headerSection = createHeaderSection();

        // Form
        VBox formSection = createFormSection();

        // Buttons
        HBox buttonSection = createButtonSection();

        root.getChildren().addAll(headerSection, formSection, buttonSection);

        return new Scene(root);
    }

    private VBox createHeaderSection() {
        VBox headerSection = new VBox(8);

        Label titleLabel = new Label(getTitle());
        titleLabel.setStyle(Typography.HEADLINE_SMALL + Colors.TEXT_PRIMARY);

        Label subtitleLabel = new Label("Configure exam type details and scoring parameters");
        subtitleLabel.setStyle(Typography.BODY_MEDIUM + Colors.TEXT_SECONDARY);

        headerSection.getChildren().addAll(titleLabel, subtitleLabel);
        return headerSection;
    }

    private VBox createFormSection() {
        VBox formSection = new VBox(20);

        // Name Field
        VBox nameContainer = createFieldContainer("Exam Type Name *",
                nameField = createTextField("Enter exam type name"));

        // Description Field
        VBox descriptionContainer = createFieldContainer("Description *",
                descriptionArea = createTextArea("Enter detailed description"));

        // Fee Field
        VBox feeContainer = createFieldContainer("Fee (VND) *",
                feeField = createTextField("Enter exam fee in VND"));

        // Duration Field
        VBox durationContainer = createFieldContainer("Duration (minutes) *",
                durationField = createTextField("Enter exam duration"));

        // Passing Score Field
        VBox passingScoreContainer = createFieldContainer("Passing Score (%) *",
                passingScoreField = createTextField("Enter minimum passing score"));

        formSection.getChildren().addAll(
                nameContainer,
                descriptionContainer,
                feeContainer,
                durationContainer,
                passingScoreContainer);

        return formSection;
    }

    private VBox createFieldContainer(String labelText, Control field) {
        VBox container = new VBox(8);

        Label label = new Label(labelText);
        label.setStyle(Typography.LABEL_MEDIUM + Colors.TEXT_PRIMARY);

        container.getChildren().addAll(label, field);
        return container;
    }

    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle(
                "-fx-padding: 12 16;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #79747E;" +
                        "-fx-border-width: 1;" +
                        "-fx-background-color: #FEF7FF;" +
                        "-fx-font-size: 14px;");

        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                textField.setStyle(
                        "-fx-padding: 12 16;" +
                                "-fx-border-radius: 12;" +
                                "-fx-background-radius: 12;" +
                                "-fx-border-color: #6750A4;" +
                                "-fx-border-width: 2;" +
                                "-fx-background-color: #FEF7FF;" +
                                "-fx-font-size: 14px;");
            } else {
                textField.setStyle(
                        "-fx-padding: 12 16;" +
                                "-fx-border-radius: 12;" +
                                "-fx-background-radius: 12;" +
                                "-fx-border-color: #79747E;" +
                                "-fx-border-width: 1;" +
                                "-fx-background-color: #FEF7FF;" +
                                "-fx-font-size: 14px;");
            }
        });

        return textField;
    }

    private TextArea createTextArea(String promptText) {
        TextArea textArea = new TextArea();
        textArea.setPromptText(promptText);
        textArea.setPrefRowCount(3);
        textArea.setWrapText(true);
        textArea.setStyle(
                "-fx-padding: 12 16;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #79747E;" +
                        "-fx-border-width: 1;" +
                        "-fx-background-color: #FEF7FF;" +
                        "-fx-font-size: 14px;");

        textArea.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                textArea.setStyle(
                        "-fx-padding: 12 16;" +
                                "-fx-border-radius: 12;" +
                                "-fx-background-radius: 12;" +
                                "-fx-border-color: #6750A4;" +
                                "-fx-border-width: 2;" +
                                "-fx-background-color: #FEF7FF;" +
                                "-fx-font-size: 14px;");
            } else {
                textArea.setStyle(
                        "-fx-padding: 12 16;" +
                                "-fx-border-radius: 12;" +
                                "-fx-background-radius: 12;" +
                                "-fx-border-color: #79747E;" +
                                "-fx-border-width: 1;" +
                                "-fx-background-color: #FEF7FF;" +
                                "-fx-font-size: 14px;");
            }
        });

        return textArea;
    }

    private HBox createButtonSection() {
        HBox buttonSection = new HBox(12);
        buttonSection.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Button cancelButton = Buttons.createTextButton("Cancel", null);
        cancelButton.setOnAction(e -> close());

        Button saveButton = Buttons.createFilledButton("Save", null);
        saveButton.setOnAction(e -> handleSave());

        buttonSection.getChildren().addAll(cancelButton, saveButton);
        return buttonSection;
    }

    private void populateFields() {
        nameField.setText(existingExamType.getName());
        descriptionArea.setText(existingExamType.getDescription());
        feeField.setText(String.valueOf(existingExamType.getFee()));
        durationField.setText(String.valueOf(existingExamType.getDuration()));
        passingScoreField.setText(String.valueOf(existingExamType.getPassingScore()));
    }

    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        result = createExamTypeFromForm();
        close();
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText().trim().isEmpty()) {
            errors.append("• Name is required\n");
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            errors.append("• Description is required\n");
        }

        try {
            double fee = Double.parseDouble(feeField.getText().trim());
            if (fee < 0) {
                errors.append("• Fee must be positive\n");
            }
        } catch (NumberFormatException e) {
            errors.append("• Fee must be a valid number\n");
        }

        try {
            int duration = Integer.parseInt(durationField.getText().trim());
            if (duration <= 0) {
                errors.append("• Duration must be positive\n");
            }
        } catch (NumberFormatException e) {
            errors.append("• Duration must be a valid number\n");
        }

        try {
            int passingScore = Integer.parseInt(passingScoreField.getText().trim());
            if (passingScore < 0 || passingScore > 100) {
                errors.append("• Passing score must be between 0 and 100\n");
            }
        } catch (NumberFormatException e) {
            errors.append("• Passing score must be a valid number\n");
        }

        if (errors.length() > 0) {
            showValidationError(errors.toString());
            return false;
        }

        return true;
    }

    private ExamType createExamTypeFromForm() {
        ExamType examType = existingExamType != null ? existingExamType : new ExamType();

        examType.setName(nameField.getText().trim());
        examType.setDescription(descriptionArea.getText().trim());
        examType.setFee(Double.parseDouble(feeField.getText().trim()));
        examType.setDuration(Integer.parseInt(durationField.getText().trim()));
        examType.setPassingScore(Integer.parseInt(passingScoreField.getText().trim()));

        if (existingExamType == null) {
            examType.setCreatedDate(LocalDateTime.now());
        }

        return examType;
    }

    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(this);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Please fix the following errors:");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
