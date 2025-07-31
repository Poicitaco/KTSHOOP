package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.enums.ScheduleStatus;
import com.pocitaco.oopsh.enums.TimeSlot;
import com.pocitaco.oopsh.models.ExamSchedule;
import com.pocitaco.oopsh.models.ExamType;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.ui.MaterialDesignManager;
import com.pocitaco.oopsh.ui.components.ExamScheduleFormDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Exam Schedule Management Screen with Material Design 3.0
 * Features: Statistics cards, advanced filtering, CRUD operations
 */
public class ExamScheduleManagementScreen extends VBox {

    private final ExamScheduleDAO examScheduleDAO;
    private final ExamTypeDAO examTypeDAO;
    private final UserDAO userDAO;
    private final ObservableList<ExamSchedule> scheduleList;
    private TableView<ExamSchedule> table;
    private TextField searchField;
    private ComboBox<String> statusFilter;
    private ComboBox<String> timeSlotFilter;
    private DatePicker dateFilter;

    // Statistics
    private Label totalSchedulesLabel;
    private Label openSchedulesLabel;
    private Label todaySchedulesLabel;
    private Label occupancyRateLabel;

    public ExamScheduleManagementScreen() {
        this.examScheduleDAO = new ExamScheduleDAO();
        this.examTypeDAO = new ExamTypeDAO();
        this.userDAO = new UserDAO();
        this.scheduleList = FXCollections.observableArrayList();

        initializeUI();
        setupEventHandlers();
        loadSchedules();
    }

    private void initializeUI() {
        setSpacing(24);
        setPadding(new Insets(24));
        setStyle("-fx-background-color: #FFFBFE;");

        // Header
        VBox header = createHeader();

        // Statistics Cards
        HBox statsCards = createStatsCards();

        // Filters Section
        VBox filtersSection = createFiltersSection();

        // Table Section
        VBox tableSection = createTableSection();

        getChildren().addAll(header, statsCards, filtersSection, tableSection);
    }

    private VBox createHeader() {
        VBox header = new VBox(8);

        Label title = new Label("Quản lý Lịch Thi");
        title.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1C1B1F;");

        Label subtitle = new Label("Tạo và quản lý các buổi thi sát hạch");
        subtitle.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #49454F;");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private HBox createStatsCards() {
        HBox statsContainer = new HBox(16);

        // Total Schedules Card
        VBox totalCard = createStatCard("Tổng Lịch Thi", "0", "#6750A4");
        totalSchedulesLabel = (Label) ((VBox) totalCard.getChildren().get(1)).getChildren().get(0);

        // Open Schedules Card
        VBox openCard = createStatCard("Đang Mở", "0", "#0F9D58");
        openSchedulesLabel = (Label) ((VBox) openCard.getChildren().get(1)).getChildren().get(0);

        // Today Schedules Card
        VBox todayCard = createStatCard("Hôm Nay", "0", "#FF9800");
        todaySchedulesLabel = (Label) ((VBox) todayCard.getChildren().get(1)).getChildren().get(0);

        // Occupancy Rate Card
        VBox occupancyCard = createStatCard("Tỷ Lệ Lấp Đầy", "0%", "#F44336");
        occupancyRateLabel = (Label) ((VBox) occupancyCard.getChildren().get(1)).getChildren().get(0);

        statsContainer.getChildren().addAll(totalCard, openCard, todayCard, occupancyCard);
        return statsContainer;
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E7E0EC;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #49454F;" +
                        "-fx-font-weight: 500;");

        VBox valueContainer = new VBox(4);
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + color + ";");

        valueContainer.getChildren().add(valueLabel);
        card.getChildren().addAll(titleLabel, valueContainer);

        return card;
    }

    private VBox createFiltersSection() {
        VBox section = new VBox(16);

        Label sectionTitle = new Label("Bộ Lọc & Tìm Kiếm");
        sectionTitle.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1C1B1F;");

        // First row: Search and Add button
        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Tìm kiếm theo địa điểm, loại thi...");
        searchField.setPrefWidth(300);
        searchField.setPrefHeight(40);
        searchField.setStyle(
                "-fx-background-color: #F7F2FA;" +
                        "-fx-border-color: #79747E;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 0 16;" +
                        "-fx-text-fill: #1C1B1F;" +
                        "-fx-prompt-text-fill: #49454F;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addButton = new Button("Thêm Lịch Thi");
        addButton.setPrefHeight(40);
        addButton.setStyle(
                "-fx-background-color: #6750A4;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 0 24;");

        topRow.getChildren().addAll(searchField, spacer, addButton);

        // Second row: Filters
        HBox filtersRow = new HBox(16);
        filtersRow.setAlignment(Pos.CENTER_LEFT);

        // Status filter
        Label statusLabel = new Label("Trạng thái:");
        statusLabel.setStyle("-fx-font-weight: 500; -fx-text-fill: #1C1B1F;");

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Tất cả", "Đang mở", "Đã lên lịch", "Đang diễn ra", "Hoàn thành", "Đã hủy");
        statusFilter.setValue("Tất cả");
        statusFilter.setPrefWidth(120);
        styleComboBox(statusFilter);

        // Time slot filter
        Label timeSlotLabel = new Label("Ca thi:");
        timeSlotLabel.setStyle("-fx-font-weight: 500; -fx-text-fill: #1C1B1F;");

        timeSlotFilter = new ComboBox<>();
        timeSlotFilter.getItems().addAll("Tất cả", "Sáng", "Chiều", "Tối");
        timeSlotFilter.setValue("Tất cả");
        timeSlotFilter.setPrefWidth(100);
        styleComboBox(timeSlotFilter);

        // Date filter
        Label dateLabel = new Label("Ngày thi:");
        dateLabel.setStyle("-fx-font-weight: 500; -fx-text-fill: #1C1B1F;");

        dateFilter = new DatePicker();
        dateFilter.setPrefWidth(150);
        styleComboBox(dateFilter);

        Button clearFiltersButton = new Button("Xóa bộ lọc");
        clearFiltersButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #6750A4;" +
                        "-fx-border-width: 1;" +
                        "-fx-text-fill: #6750A4;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 8 16;");

        filtersRow.getChildren().addAll(
                statusLabel, statusFilter,
                timeSlotLabel, timeSlotFilter,
                dateLabel, dateFilter,
                clearFiltersButton);

        section.getChildren().addAll(sectionTitle, topRow, filtersRow);
        return section;
    }

    private void styleComboBox(Control comboBox) {
        comboBox.setStyle(
                "-fx-background-color: #F7F2FA;" +
                        "-fx-border-color: #79747E;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-text-fill: #1C1B1F;");
    }

    private VBox createTableSection() {
        VBox section = new VBox(16);

        Label sectionTitle = new Label("Danh Sách Lịch Thi");
        sectionTitle.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1C1B1F;");

        table = new TableView<>();
        table.setItems(scheduleList);
        table.setPrefHeight(500);

        setupTableColumns();

        // Style the table
        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E7E0EC;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;");

        section.getChildren().addAll(sectionTitle, table);
        return section;
    }

    private void setupTableColumns() {
        // ID Column
        TableColumn<ExamSchedule, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(60);
        idColumn.setStyle("-fx-alignment: CENTER;");

        // Exam Type Column
        TableColumn<ExamSchedule, String> examTypeColumn = new TableColumn<>("Loại Thi");
        examTypeColumn.setCellValueFactory(cellData -> {
            ExamSchedule schedule = cellData.getValue();
            Optional<ExamType> examType = examTypeDAO.findById(schedule.getExamTypeId());
            return new SimpleStringProperty(examType.map(ExamType::getName).orElse("N/A"));
        });
        examTypeColumn.setPrefWidth(150);

        // Date Column
        TableColumn<ExamSchedule, String> dateColumn = new TableColumn<>("Ngày Thi");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getExamDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        dateColumn.setPrefWidth(100);
        dateColumn.setStyle("-fx-alignment: CENTER;");

        // Time Slot Column
        TableColumn<ExamSchedule, String> timeSlotColumn = new TableColumn<>("Ca Thi");
        timeSlotColumn.setCellValueFactory(cellData -> {
            TimeSlot timeSlot = cellData.getValue().getTimeSlot();
            String displayText = switch (timeSlot) {
                case MORNING -> "Sáng";
                case AFTERNOON -> "Chiều";
                case EVENING -> "Tối";
            };
            return new SimpleStringProperty(displayText);
        });
        timeSlotColumn.setPrefWidth(80);
        timeSlotColumn.setStyle("-fx-alignment: CENTER;");

        // Location Column
        TableColumn<ExamSchedule, String> locationColumn = new TableColumn<>("Địa Điểm");
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationColumn.setPrefWidth(200);

        // Capacity Column
        TableColumn<ExamSchedule, String> capacityColumn = new TableColumn<>("Sức Chứa");
        capacityColumn.setCellValueFactory(cellData -> {
            ExamSchedule schedule = cellData.getValue();
            return new SimpleStringProperty(
                    schedule.getRegisteredCandidates() + "/" + schedule.getMaxCandidates());
        });
        capacityColumn.setPrefWidth(100);
        capacityColumn.setStyle("-fx-alignment: CENTER;");

        // Status Column
        TableColumn<ExamSchedule, String> statusColumn = new TableColumn<>("Trạng Thái");
        statusColumn.setCellValueFactory(cellData -> {
            ScheduleStatus status = cellData.getValue().getStatus();
            String displayText = switch (status) {
                case OPEN -> "Đang mở";
                case SCHEDULED -> "Đã lên lịch";
                case IN_PROGRESS -> "Đang diễn ra";
                case COMPLETED -> "Hoàn thành";
                case CANCELLED -> "Đã hủy";
            };
            return new SimpleStringProperty(displayText);
        });
        statusColumn.setPrefWidth(100);
        statusColumn.setStyle("-fx-alignment: CENTER;");

        // Examiner Column
        TableColumn<ExamSchedule, String> examinerColumn = new TableColumn<>("Giám Thị");
        examinerColumn.setCellValueFactory(cellData -> {
            ExamSchedule schedule = cellData.getValue();
            Optional<User> examiner = userDAO.findById(schedule.getExaminerId());
            return new SimpleStringProperty(examiner.map(User::getFullName).orElse("N/A"));
        });
        examinerColumn.setPrefWidth(120);

        // Actions Column
        TableColumn<ExamSchedule, Void> actionsColumn = new TableColumn<>("Thao Tác");
        actionsColumn.setPrefWidth(180);
        actionsColumn.setCellFactory(column -> new TableCell<ExamSchedule, Void>() {
            private final HBox actionBox = new HBox(8);
            private final Button viewButton = createActionButton("Chi tiết", "#4CAF50");
            private final Button editButton = createActionButton("Sửa", "#2196F3");
            private final Button deleteButton = createActionButton("Xóa", "#F44336");

            {
                viewButton.setOnAction(e -> {
                    ExamSchedule schedule = getTableView().getItems().get(getIndex());
                    showScheduleDetails(schedule);
                });

                editButton.setOnAction(e -> {
                    ExamSchedule schedule = getTableView().getItems().get(getIndex());
                    showEditScheduleDialog(schedule);
                });

                deleteButton.setOnAction(e -> {
                    ExamSchedule schedule = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(schedule);
                });

                actionBox.getChildren().addAll(viewButton, editButton, deleteButton);
                actionBox.setAlignment(Pos.CENTER);
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

        table.getColumns().addAll(
                idColumn, examTypeColumn, dateColumn, timeSlotColumn,
                locationColumn, capacityColumn, statusColumn, examinerColumn, actionsColumn);
    }

    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 4 8;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;");
        button.setPrefWidth(50);
        return button;
    }

    private void setupEventHandlers() {
        // Search functionality
        searchField.textProperty().addListener((obs, oldText, newText) -> filterSchedules());

        // Filter functionality
        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterSchedules());
        timeSlotFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterSchedules());
        dateFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterSchedules());

        // Add button
        Button addButton = (Button) ((HBox) ((VBox) getChildren().get(2)).getChildren().get(1)).getChildren().get(2);
        addButton.setOnAction(e -> showAddScheduleDialog());

        // Clear filters button
        Button clearButton = (Button) ((HBox) ((VBox) getChildren().get(2)).getChildren().get(2)).getChildren().get(7);
        clearButton.setOnAction(e -> clearFilters());
    }

    private void loadSchedules() {
        try {
            List<ExamSchedule> schedules = examScheduleDAO.getAllSchedules();
            scheduleList.clear();
            scheduleList.addAll(schedules);
            updateStats();
            System.out.println("✅ Loaded " + schedules.size() + " exam schedules");
        } catch (Exception e) {
            System.err.println("❌ Error loading exam schedules: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStats() {
        List<ExamSchedule> allSchedules = scheduleList;

        // Total schedules
        totalSchedulesLabel.setText(String.valueOf(allSchedules.size()));

        // Open schedules
        long openCount = allSchedules.stream()
                .filter(s -> s.getStatus() == ScheduleStatus.OPEN)
                .count();
        openSchedulesLabel.setText(String.valueOf(openCount));

        // Today's schedules
        LocalDate today = LocalDate.now();
        long todayCount = allSchedules.stream()
                .filter(s -> s.getExamDate().equals(today))
                .count();
        todaySchedulesLabel.setText(String.valueOf(todayCount));

        // Occupancy rate
        double totalCapacity = allSchedules.stream()
                .mapToInt(ExamSchedule::getMaxCandidates)
                .sum();
        double totalRegistered = allSchedules.stream()
                .mapToInt(ExamSchedule::getRegisteredCandidates)
                .sum();

        double occupancyRate = totalCapacity > 0 ? (totalRegistered / totalCapacity) * 100 : 0;
        occupancyRateLabel.setText(String.format("%.1f%%", occupancyRate));
    }

    private void filterSchedules() {
        String searchText = searchField.getText();
        String statusText = statusFilter.getValue();
        String timeSlotText = timeSlotFilter.getValue();
        LocalDate selectedDate = dateFilter.getValue();

        try {
            List<ExamSchedule> allSchedules = examScheduleDAO.getAllSchedules();
            List<ExamSchedule> filteredSchedules = allSchedules.stream()
                    .filter(schedule -> {
                        // Search filter
                        if (searchText != null && !searchText.trim().isEmpty()) {
                            String query = searchText.toLowerCase();
                            Optional<ExamType> examType = examTypeDAO.findById(schedule.getExamTypeId());
                            String examTypeName = examType.map(ExamType::getName).orElse("").toLowerCase();

                            if (!schedule.getLocation().toLowerCase().contains(query) &&
                                    !examTypeName.contains(query)) {
                                return false;
                            }
                        }

                        // Status filter
                        if (statusText != null && !statusText.equals("Tất cả")) {
                            ScheduleStatus targetStatus = switch (statusText) {
                                case "Đang mở" -> ScheduleStatus.OPEN;
                                case "Đã lên lịch" -> ScheduleStatus.SCHEDULED;
                                case "Đang diễn ra" -> ScheduleStatus.IN_PROGRESS;
                                case "Hoàn thành" -> ScheduleStatus.COMPLETED;
                                case "Đã hủy" -> ScheduleStatus.CANCELLED;
                                default -> null;
                            };
                            if (targetStatus != null && schedule.getStatus() != targetStatus) {
                                return false;
                            }
                        }

                        // Time slot filter
                        if (timeSlotText != null && !timeSlotText.equals("Tất cả")) {
                            TimeSlot targetTimeSlot = switch (timeSlotText) {
                                case "Sáng" -> TimeSlot.MORNING;
                                case "Chiều" -> TimeSlot.AFTERNOON;
                                case "Tối" -> TimeSlot.EVENING;
                                default -> null;
                            };
                            if (targetTimeSlot != null && schedule.getTimeSlot() != targetTimeSlot) {
                                return false;
                            }
                        }

                        // Date filter
                        if (selectedDate != null && !schedule.getExamDate().equals(selectedDate)) {
                            return false;
                        }

                        return true;
                    })
                    .toList();

            scheduleList.clear();
            scheduleList.addAll(filteredSchedules);

        } catch (Exception e) {
            System.err.println("❌ Error filtering schedules: " + e.getMessage());
        }
    }

    private void clearFilters() {
        searchField.clear();
        statusFilter.setValue("Tất cả");
        timeSlotFilter.setValue("Tất cả");
        dateFilter.setValue(null);
        loadSchedules();
    }

    private void showAddScheduleDialog() {
        ExamScheduleFormDialog dialog = new ExamScheduleFormDialog(null);
        Optional<ExamSchedule> result = dialog.showDialog();

        if (result.isPresent()) {
            ExamSchedule newSchedule = result.get();
            examScheduleDAO.addSchedule(newSchedule);
            loadSchedules();

            showSuccessAlert("Thành công", "Đã thêm lịch thi mới!");
        }
    }

    private void showEditScheduleDialog(ExamSchedule schedule) {
        ExamScheduleFormDialog dialog = new ExamScheduleFormDialog(schedule);
        Optional<ExamSchedule> result = dialog.showDialog();

        if (result.isPresent()) {
            ExamSchedule updatedSchedule = result.get();
            examScheduleDAO.updateSchedule(updatedSchedule);
            loadSchedules();

            showSuccessAlert("Thành công", "Đã cập nhật lịch thi!");
        }
    }

    private void showScheduleDetails(ExamSchedule schedule) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi Tiết Lịch Thi");
        alert.setHeaderText("Thông tin chi tiết lịch thi #" + schedule.getId());

        Optional<ExamType> examType = examTypeDAO.findById(schedule.getExamTypeId());
        Optional<User> examiner = userDAO.findById(schedule.getExaminerId());

        String details = String.format(
                "Loại thi: %s\n" +
                        "Ngày thi: %s\n" +
                        "Ca thi: %s\n" +
                        "Địa điểm: %s\n" +
                        "Sức chứa: %d/%d\n" +
                        "Trạng thái: %s\n" +
                        "Giám thị: %s\n" +
                        "Ngày tạo: %s",
                examType.map(ExamType::getName).orElse("N/A"),
                schedule.getExamDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                switch (schedule.getTimeSlot()) {
                    case MORNING -> "Sáng";
                    case AFTERNOON -> "Chiều";
                    case EVENING -> "Tối";
                },
                schedule.getLocation(),
                schedule.getRegisteredCandidates(),
                schedule.getMaxCandidates(),
                switch (schedule.getStatus()) {
                    case OPEN -> "Đang mở";
                    case SCHEDULED -> "Đã lên lịch";
                    case IN_PROGRESS -> "Đang diễn ra";
                    case COMPLETED -> "Hoàn thành";
                    case CANCELLED -> "Đã hủy";
                },
                examiner.map(User::getFullName).orElse("N/A"),
                schedule.getCreatedDate() != null
                        ? schedule.getCreatedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "N/A");

        alert.setContentText(details);
        alert.showAndWait();
    }

    private void showDeleteConfirmation(ExamSchedule schedule) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác Nhận Xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa lịch thi này?");
        alert.setContentText("Lịch thi: " + schedule.getLocation() + " - " +
                schedule.getExamDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            examScheduleDAO.deleteSchedule(schedule.getId());
            loadSchedules();
            showSuccessAlert("Thành công", "Đã xóa lịch thi!");
        }
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
