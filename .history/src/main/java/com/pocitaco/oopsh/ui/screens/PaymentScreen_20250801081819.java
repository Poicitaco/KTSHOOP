package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.*;
import com.pocitaco.oopsh.models.*;
import com.pocitaco.oopsh.enums.PaymentStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Payment Management Screen for Candidates
 * Màn hình quản lý thanh toán cho thí sinh
 */
public class PaymentScreen extends BorderPane {

    private final UserDAO userDAO;
    private final PaymentDAO paymentDAO;
    private final RegistrationDAO registrationDAO;
    private final ExamScheduleDAO examScheduleDAO;
    private final ExamTypeDAO examTypeDAO;

    private TableView<Payment> paymentsTable;
    private ObservableList<Payment> paymentsData;

    private ComboBox<String> statusFilter;
    private ComboBox<String> methodFilter;
    private DatePicker dateFromPicker;
    private DatePicker dateToPicker;
    private User currentUser;

    // Statistics labels
    private Label totalPaymentsLabel;
    private Label paidAmountLabel;
    private Label pendingAmountLabel;
    private Label overduePaymentsLabel;

    public PaymentScreen(User currentUser) {
        this.currentUser = currentUser;
        this.userDAO = new UserDAO();
        this.paymentDAO = new PaymentDAO();
        this.registrationDAO = new RegistrationDAO();
        this.examScheduleDAO = new ExamScheduleDAO();
        this.examTypeDAO = new ExamTypeDAO();
        this.paymentsData = FXCollections.observableArrayList();

        setupUI();
        loadData();
        updateStatistics();
    }

    private void setupUI() {
        // Create sidebar
        VBox sidebar = createSidebar();

        // Create main content
        VBox mainContent = createMainContent();

        // Set layout
        setLeft(sidebar);
        setCenter(mainContent);

        // Apply styling
        setStyle("-fx-background-color: #f5f5f5;");
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");

        // Title
        Label titleLabel = new Label("PAYMENTS");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        // Navigation buttons
        Button backButton = new Button("[BACK] Back to Dashboard");
        Button registerExamButton = new Button("[PLUS] Register for Exam");
        Button examResultsButton = new Button("[CHART] Exam Results");
        Button practiceTestsButton = new Button("[TEST] Practice Tests");

        // Style buttons
        Button[] buttons = { backButton, registerExamButton, examResultsButton, practiceTestsButton };
        for (Button btn : buttons) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setStyle(
                    "-fx-background-color: #34495e; -fx-text-fill: white; -fx-border-color: #2c3e50; -fx-padding: 10px;");
            btn.setOnMouseEntered(e -> btn.setStyle(
                    "-fx-background-color: #4a6741; -fx-text-fill: white; -fx-border-color: #2c3e50; -fx-padding: 10px;"));
            btn.setOnMouseExited(e -> btn.setStyle(
                    "-fx-background-color: #34495e; -fx-text-fill: white; -fx-border-color: #2c3e50; -fx-padding: 10px;"));
        }

        // Button actions
        backButton.setOnAction(e -> navigateToDashboard());
        registerExamButton.setOnAction(e -> openExamRegistration());
        examResultsButton.setOnAction(e -> openExamResults());
        practiceTestsButton.setOnAction(e -> openPracticeTests());

        sidebar.getChildren().addAll(titleLabel, new Separator(),
                backButton, registerExamButton, examResultsButton, practiceTestsButton);

        return sidebar;
    }

    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Payment Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));

        // Statistics cards
        HBox statsContainer = createStatisticsCards();

        // Filters
        HBox filtersContainer = createFiltersContainer();

        // Payments table
        paymentsTable = createPaymentsTable();

        // Action buttons
        HBox actionButtons = createActionButtons();

        content.getChildren().addAll(titleLabel, statsContainer, filtersContainer, paymentsTable, actionButtons);
        return content;
    }

    private HBox createStatisticsCards() {
        HBox container = new HBox(20);
        container.setAlignment(Pos.CENTER);

        // Total payments card
        VBox totalCard = createStatCard("Total Payments", "0", "#3498db");
        totalPaymentsLabel = (Label) totalCard.getChildren().get(1);

        // Paid amount card
        VBox paidCard = createStatCard("Paid Amount", "$0.00", "#2ecc71");
        paidAmountLabel = (Label) paidCard.getChildren().get(1);

        // Pending amount card
        VBox pendingCard = createStatCard("Pending Amount", "$0.00", "#f39c12");
        pendingAmountLabel = (Label) pendingCard.getChildren().get(1);

        // Overdue payments card
        VBox overdueCard = createStatCard("Overdue", "0", "#e74c3c");
        overduePaymentsLabel = (Label) overdueCard.getChildren().get(1);

        container.getChildren().addAll(totalCard, paidCard, pendingCard, overdueCard);
        return container;
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPrefWidth(200);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        titleLabel.setTextFill(Color.web("#7f8c8d"));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setTextFill(Color.web(color));

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private HBox createFiltersContainer() {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(10));

        // Status filter
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "PENDING", "PAID", "FAILED", "REFUNDED");
        statusFilter.setValue("All");
        statusFilter.setPromptText("Status");
        statusFilter.setPrefWidth(120);
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterPayments());

        // Payment method filter
        methodFilter = new ComboBox<>();
        methodFilter.getItems().addAll("All", "CREDIT_CARD", "BANK_TRANSFER", "PAYPAL", "CASH");
        methodFilter.setValue("All");
        methodFilter.setPromptText("Payment Method");
        methodFilter.setPrefWidth(150);
        methodFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterPayments());

        // Date range filters
        dateFromPicker = new DatePicker();
        dateFromPicker.setPromptText("From Date");
        dateFromPicker.setPrefWidth(130);
        dateFromPicker.valueProperty().addListener((obs, oldVal, newVal) -> filterPayments());

        dateToPicker = new DatePicker();
        dateToPicker.setPromptText("To Date");
        dateToPicker.setPrefWidth(130);
        dateToPicker.valueProperty().addListener((obs, oldVal, newVal) -> filterPayments());

        Button clearFiltersButton = new Button("Clear Filters");
        clearFiltersButton.setOnAction(e -> clearFilters());

        container.getChildren().addAll(
                new Label("Status:"), statusFilter,
                new Label("Method:"), methodFilter,
                new Label("From:"), dateFromPicker,
                new Label("To:"), dateToPicker,
                clearFiltersButton);

        return container;
    }

    private TableView<Payment> createPaymentsTable() {
        TableView<Payment> table = new TableView<>();
        table.setItems(paymentsData);

        // Columns
        TableColumn<Payment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Payment, String> examCol = new TableColumn<>("Exam");
        examCol.setCellValueFactory(cellData -> {
            try {
                Registration registration = registrationDAO.findById(cellData.getValue().getRegistrationId())
                        .orElse(null);
                if (registration != null) {
                    ExamType examType = examTypeDAO.findById(registration.getExamTypeId()).orElse(null);
                    return new javafx.beans.property.SimpleStringProperty(
                            examType != null ? examType.getName() : "Unknown Exam");
                }
                return new javafx.beans.property.SimpleStringProperty("Unknown");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });
        examCol.setPrefWidth(150);

        TableColumn<Payment, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setCellFactory(column -> new TableCell<Payment, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });
        amountCol.setPrefWidth(100);

        TableColumn<Payment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> {
            PaymentStatus status = cellData.getValue().getStatus();
            return new javafx.beans.property.SimpleStringProperty(status != null ? status.toString() : "UNKNOWN");
        });
        statusCol.setCellFactory(column -> new TableCell<Payment, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "PAID":
                            setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                            break;
                        case "PENDING":
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                            break;
                        case "FAILED":
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        case "REFUNDED":
                            setStyle("-fx-text-fill: #9b59b6; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        statusCol.setPrefWidth(100);

        TableColumn<Payment, String> methodCol = new TableColumn<>("Payment Method");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        methodCol.setPrefWidth(130);

        TableColumn<Payment, String> dateCol = new TableColumn<>("Payment Date");
        dateCol.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getPaymentDate();
            return new javafx.beans.property.SimpleStringProperty(
                    dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });
        dateCol.setPrefWidth(120);

        TableColumn<Payment, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDueDate();
            return new javafx.beans.property.SimpleStringProperty(
                    date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });
        dueDateCol.setPrefWidth(100);

        TableColumn<Payment, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<Payment, Void>() {
            private final Button payButton = new Button("Pay Now");
            private final Button viewButton = new Button("View");
            private final Button receiptButton = new Button("Receipt");
            private final HBox container = new HBox(5);

            {
                payButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 10px;");
                viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 10px;");
                receiptButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 10px;");

                payButton.setOnAction(e -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    processPayment(payment);
                });

                viewButton.setOnAction(e -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    viewPaymentDetails(payment);
                });

                receiptButton.setOnAction(e -> {
                    Payment payment = getTableView().getItems().get(getIndex());
                    downloadReceipt(payment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Payment payment = getTableView().getItems().get(getIndex());
                    container.getChildren().clear();

                    container.getChildren().add(viewButton);

                    if ("PENDING".equals(payment.getStatus())) {
                        container.getChildren().add(payButton);
                    } else if ("PAID".equals(payment.getStatus())) {
                        container.getChildren().add(receiptButton);
                    }

                    setGraphic(container);
                }
            }
        });
        actionCol.setPrefWidth(180);

        table.getColumns().addAll(idCol, examCol, amountCol, statusCol, methodCol, dateCol, dueDateCol, actionCol);
        return table;
    }

    private HBox createActionButtons() {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);

        Button refreshButton = new Button("[REFRESH] Refresh");
        refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10px 20px;");
        refreshButton.setOnAction(e -> {
            loadData();
            updateStatistics();
        });

        Button exportButton = new Button("[EXPORT] Export Report");
        exportButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10px 20px;");
        exportButton.setOnAction(e -> exportPaymentReport());

        container.getChildren().addAll(refreshButton, exportButton);
        return container;
    }

    private void loadData() {
        try {
            List<Payment> payments = paymentDAO.findByUserId(currentUser.getId());
            paymentsData.clear();
            paymentsData.addAll(payments);
        } catch (Exception e) {
            showAlert("Error", "Cannot load payments: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateStatistics() {
        try {
            int totalPayments = paymentsData.size();
            double paidAmount = paymentsData.stream()
                    .filter(p -> "PAID".equals(p.getStatus()))
                    .mapToDouble(Payment::getAmount)
                    .sum();
            double pendingAmount = paymentsData.stream()
                    .filter(p -> "PENDING".equals(p.getStatus()))
                    .mapToDouble(Payment::getAmount)
                    .sum();
            long overduePayments = paymentsData.stream()
                    .filter(p -> "PENDING".equals(p.getStatus()) &&
                            p.getDueDate() != null &&
                            p.getDueDate().isBefore(LocalDate.now()))
                    .count();

            totalPaymentsLabel.setText(String.valueOf(totalPayments));
            paidAmountLabel.setText(String.format("$%.2f", paidAmount));
            pendingAmountLabel.setText(String.format("$%.2f", pendingAmount));
            overduePaymentsLabel.setText(String.valueOf(overduePayments));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterPayments() {
        try {
            List<Payment> allPayments = paymentDAO.findByUserId(currentUser.getId());
            List<Payment> filteredPayments = allPayments.stream()
                    .filter(this::matchesFilters)
                    .toList();

            paymentsData.clear();
            paymentsData.addAll(filteredPayments);
        } catch (Exception e) {
            showAlert("Error", "Cannot filter payments: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean matchesFilters(Payment payment) {
        // Status filter
        String selectedStatus = statusFilter.getValue();
        if (selectedStatus != null && !"All".equals(selectedStatus)) {
            if (!selectedStatus.equals(payment.getStatus())) {
                return false;
            }
        }

        // Payment method filter
        String selectedMethod = methodFilter.getValue();
        if (selectedMethod != null && !"All".equals(selectedMethod)) {
            if (!selectedMethod.equals(payment.getPaymentMethod())) {
                return false;
            }
        }

        // Date range filter
        LocalDate fromDate = dateFromPicker.getValue();
        LocalDate toDate = dateToPicker.getValue();
        LocalDateTime paymentDateTime = payment.getPaymentDate();
        LocalDate paymentDate = paymentDateTime != null ? paymentDateTime.toLocalDate() : null;

        if (fromDate != null && paymentDate != null && paymentDate.isBefore(fromDate)) {
            return false;
        }

        if (toDate != null && paymentDate != null && paymentDate.isAfter(toDate)) {
            return false;
        }

        return true;
    }

    private void clearFilters() {
        statusFilter.setValue("All");
        methodFilter.setValue("All");
        dateFromPicker.setValue(null);
        dateToPicker.setValue(null);
        loadData();
    }

    private void processPayment(Payment payment) {
        Dialog<String> paymentDialog = new Dialog<>();
        paymentDialog.setTitle("Process Payment");
        paymentDialog.setHeaderText("Payment for Exam Registration");

        ButtonType payButtonType = new ButtonType("Pay Now", ButtonBar.ButtonData.OK_DONE);
        paymentDialog.getDialogPane().getButtonTypes().addAll(payButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField amountField = new TextField(String.format("%.2f", payment.getAmount()));
        amountField.setEditable(false);
        ComboBox<String> methodComboBox = new ComboBox<>();
        methodComboBox.getItems().addAll("CREDIT_CARD", "BANK_TRANSFER", "PAYPAL");
        methodComboBox.setValue("CREDIT_CARD");

        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("Card Number");
        TextField cvvField = new TextField();
        cvvField.setPromptText("CVV");
        TextField expiryField = new TextField();
        expiryField.setPromptText("MM/YY");

        grid.add(new Label("Amount:"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Payment Method:"), 0, 1);
        grid.add(methodComboBox, 1, 1);
        grid.add(new Label("Card Number:"), 0, 2);
        grid.add(cardNumberField, 1, 2);
        grid.add(new Label("CVV:"), 0, 3);
        grid.add(cvvField, 1, 3);
        grid.add(new Label("Expiry (MM/YY):"), 0, 4);
        grid.add(expiryField, 1, 4);

        paymentDialog.getDialogPane().setContent(grid);

        paymentDialog.setResultConverter(dialogButton -> {
            if (dialogButton == payButtonType) {
                return methodComboBox.getValue();
            }
            return null;
        });

        Optional<String> result = paymentDialog.showAndWait();
        result.ifPresent(paymentMethod -> {
            try {
                payment.setStatus(PaymentStatus.PAID);
                payment.setPaymentMethod(paymentMethod);
                payment.setPaymentDate(LocalDateTime.now());
                paymentDAO.update(payment);

                showAlert("Success", "Payment processed successfully!", Alert.AlertType.INFORMATION);
                loadData();
                updateStatistics();
            } catch (Exception e) {
                showAlert("Error", "Payment processing failed: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void viewPaymentDetails(Payment payment) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Payment Details");
        dialog.setHeaderText("Payment ID: " + payment.getId());

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        try {
            Registration registration = registrationDAO.findById(payment.getRegistrationId()).orElse(null);
            String examInfo = "Unknown Exam";
            if (registration != null) {
                ExamType examType = examTypeDAO.findById(registration.getExamTypeId()).orElse(null);
                examInfo = examType != null ? examType.getName() : "Unknown Exam";
            }

            content.getChildren().addAll(
                    new Label("Exam: " + examInfo),
                    new Label("Amount: $" + String.format("%.2f", payment.getAmount())),
                    new Label("Status: " + payment.getStatus()),
                    new Label("Payment Method: "
                            + (payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "Not specified")),
                    new Label("Payment Date: " + (payment.getPaymentDate() != null
                            ? payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "Not paid")),
                    new Label("Due Date: " + (payment.getDueDate() != null
                            ? payment.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "No due date")),
                    new Separator(),
                    new Label("Transaction Notes:"),
                    new TextArea(
                            payment.getTransactionId() != null ? payment.getTransactionId() : "No additional notes"));
        } catch (Exception e) {
            content.getChildren().add(new Label("Error loading payment details"));
        }

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private void downloadReceipt(Payment payment) {
        showAlert("Info", "Receipt download functionality would be implemented here.\n" +
                "Receipt for Payment ID: " + payment.getId(), Alert.AlertType.INFORMATION);
    }

    private void exportPaymentReport() {
        showAlert("Info", "Payment report export functionality would be implemented here.\n" +
                "Report will include all payment history.", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Navigation methods
    private void navigateToDashboard() {
        showAlert("Info", "Navigating back to dashboard...", Alert.AlertType.INFORMATION);
    }

    private void openExamRegistration() {
        try {
            ExamRegistrationScreen registrationScreen = new ExamRegistrationScreen(currentUser);
            getScene().setRoot(registrationScreen);
        } catch (Exception e) {
            showAlert("Error", "Cannot open exam registration: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void openExamResults() {
        try {
            ExamResultsScreen resultsScreen = new ExamResultsScreen((Stage) getScene().getWindow(), currentUser);
            Scene scene = resultsScreen.createScene();
            ((Stage) getScene().getWindow()).setScene(scene);
        } catch (Exception e) {
            showAlert("Error", "Cannot open exam results: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void openPracticeTests() {
        try {
            PracticeTestsScreen practiceScreen = new PracticeTestsScreen(currentUser);
            getScene().setRoot(practiceScreen);
        } catch (Exception e) {
            showAlert("Error", "Cannot open practice tests: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
