package com.pocitaco.oopsh.ui.screens;

import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.enums.ScheduleStatus;
import com.pocitaco.oopsh.enums.TimeSlot;
import com.pocitaco.oopsh.models.ExamSchedule;
import com.pocitaco.oopsh.models.ExamType;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.utils.DialogUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exam Schedule Management Screen
 * Quản lý lịch thi cử cho admin
 * 
 * @author OOPSH Team
 * @version 1.0
 */
public class ExamScheduleManagementScreen extends BorderPane {

    private User currentUser;
    private ExamScheduleDAO examScheduleDAO;
    private ExamTypeDAO examTypeDAO;
    private ExamSchedule selectedSchedule;

    // UI Components
    private TableView<ExamSchedule> tableView;
    private ComboBox<ExamType> cbExamType;
    private DatePicker dpExamDate;
    private ComboBox<TimeSlot> cbTimeSlot;
    private ComboBox<ScheduleStatus> cbStatus;
    private TextField txtMaxCandidates;
    private TextField txtLocation;
    private Button btnAdd;
    private Button btnUpdate;
    private Button btnClear;
    private Button btnDelete;

    // Search controls
    private TextField txtSearch;
    private DatePicker dpSearchFrom;
    private DatePicker dpSearchTo;
    private ComboBox<ScheduleStatus> cbSearchStatus;
    private Button btnSearch;
    private Button btnReset;

    public ExamScheduleManagementScreen(User currentUser) {
        this.currentUser = currentUser;
        this.examScheduleDAO = new ExamScheduleDAO();
        this.examTypeDAO = new ExamTypeDAO();
        
        initializeComponents();
        setupLayout();
        loadData();
    }

    private void initializeComponents() {
        // Initialize table
        tableView = new TableView<>();
        setupTableColumns();
        
        // Initialize form controls
        cbExamType = new ComboBox<>();
        dpExamDate = new DatePicker();
        cbTimeSlot = new ComboBox<>();
        cbStatus = new ComboBox<>();
        txtMaxCandidates = new TextField();
        txtLocation = new TextField();
        
        // Initialize buttons
        btnAdd = new Button("Thêm");
        btnUpdate = new Button("Cập nhật");
        btnClear = new Button("Xóa");
        btnDelete = new Button("Xóa");
        
        // Initialize search controls
        txtSearch = new TextField();
        dpSearchFrom = new DatePicker();
        dpSearchTo = new DatePicker();
        cbSearchStatus = new ComboBox<>();
        btnSearch = new Button("Tìm kiếm");
        btnReset = new Button("Làm mới");
        
        setupFormControls();
        setupEventHandlers();
    }

    private void setupTableColumns() {
        TableColumn<ExamSchedule, Integer> colId = new TableColumn<>("ID");
        TableColumn<ExamSchedule, String> colExamType = new TableColumn<>("Loại thi");
        TableColumn<ExamSchedule, String> colDate = new TableColumn<>("Ngày thi");
        TableColumn<ExamSchedule, String> colTimeSlot = new TableColumn<>("Khung giờ");
        TableColumn<ExamSchedule, String> colStatus = new TableColumn<>("Trạng thái");
        TableColumn<ExamSchedule, String> colMaxCandidates = new TableColumn<>("Số lượng tối đa");
        TableColumn<ExamSchedule, String> colRegistered = new TableColumn<>("Đã đăng ký");
        TableColumn<ExamSchedule, String> colLocation = new TableColumn<>("Địa điểm");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("examDate"));
        colTimeSlot.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colMaxCandidates.setCellValueFactory(new PropertyValueFactory<>("maxCandidates"));
        colRegistered.setCellValueFactory(new PropertyValueFactory<>("registeredCandidates"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));

        tableView.getColumns().addAll(colId, colExamType, colDate, colTimeSlot, colStatus, colMaxCandidates, colRegistered, colLocation);
        
        // Setup selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedSchedule = newSelection;
                        loadScheduleToForm(newSelection);
                    }
                });
    }

    private void setupFormControls() {
        // Load exam types
        List<ExamType> examTypes = examTypeDAO.getAll();
        cbExamType.setItems(FXCollections.observableArrayList(examTypes));
        cbExamType.setCellFactory(param -> new ListCell<ExamType>() {
            @Override
            protected void updateItem(ExamType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getCode() + ")");
                }
            }
        });
        cbExamType.setButtonCell(cbExamType.getCellFactory().call(null));

        // Load time slots
        ObservableList<TimeSlot> timeSlots = FXCollections.observableArrayList(TimeSlot.values());
        cbTimeSlot.setItems(timeSlots);
        cbTimeSlot.setCellFactory(param -> new ListCell<TimeSlot>() {
            @Override
            protected void updateItem(TimeSlot item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        cbTimeSlot.setButtonCell(cbTimeSlot.getCellFactory().call(null));

        // Load status options
        ObservableList<ScheduleStatus> statuses = FXCollections.observableArrayList(ScheduleStatus.values());
        cbStatus.setItems(statuses);
        cbStatus.setCellFactory(param -> new ListCell<ScheduleStatus>() {
            @Override
            protected void updateItem(ScheduleStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        cbStatus.setButtonCell(cbStatus.getCellFactory().call(null));

        // Setup search status
        cbSearchStatus.setItems(statuses);
        cbSearchStatus.setCellFactory(param -> new ListCell<ScheduleStatus>() {
            @Override
            protected void updateItem(ScheduleStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        cbSearchStatus.setButtonCell(cbSearchStatus.getCellFactory().call(null));

        // Set default values
        dpExamDate.setValue(LocalDate.now());
        cbStatus.setValue(ScheduleStatus.OPEN);
        txtLocation.setPromptText("Nhập địa điểm thi");
        txtMaxCandidates.setPromptText("Số lượng thí sinh tối đa");
    }

    private void setupEventHandlers() {
        btnAdd.setOnAction(event -> handleAdd());
        btnUpdate.setOnAction(event -> handleUpdate());
        btnDelete.setOnAction(event -> handleDelete());
        btnClear.setOnAction(event -> clearForm());
        btnSearch.setOnAction(event -> handleSearch());
        btnReset.setOnAction(event -> resetSearch());
    }

    private void setupLayout() {
        // Header
        Label headerLabel = new Label("Quản lý Lịch Thi");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        HBox header = new HBox(headerLabel);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));

        // Search section
        VBox searchSection = createSearchSection();
        
        // Main content
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        
        // Table
        VBox tableSection = new VBox(10);
        Label tableLabel = new Label("Danh sách lịch thi");
        tableLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        tableSection.getChildren().addAll(tableLabel, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        
        // Form section
        VBox formSection = createFormSection();
        
        mainContent.getChildren().addAll(tableSection, formSection);
        
        // Set layout
        setTop(header);
        setCenter(new VBox(20, searchSection, mainContent));
    }

    private VBox createSearchSection() {
        VBox searchSection = new VBox(10);
        searchSection.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label searchLabel = new Label("Tìm kiếm");
        searchLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        GridPane searchGrid = new GridPane();
        searchGrid.setHgap(10);
        searchGrid.setVgap(10);
        
        searchGrid.add(new Label("Từ khóa:"), 0, 0);
        searchGrid.add(txtSearch, 1, 0);
        txtSearch.setPromptText("Nhập từ khóa tìm kiếm...");
        
        searchGrid.add(new Label("Từ ngày:"), 0, 1);
        searchGrid.add(dpSearchFrom, 1, 1);
        
        searchGrid.add(new Label("Đến ngày:"), 2, 1);
        searchGrid.add(dpSearchTo, 3, 1);
        
        searchGrid.add(new Label("Trạng thái:"), 0, 2);
        searchGrid.add(cbSearchStatus, 1, 2);
        
        HBox searchButtons = new HBox(10, btnSearch, btnReset);
        searchButtons.setAlignment(Pos.CENTER_RIGHT);
        searchGrid.add(searchButtons, 3, 2);
        
        searchSection.getChildren().addAll(searchLabel, searchGrid);
        return searchSection;
    }

    private VBox createFormSection() {
        VBox formSection = new VBox(10);
        formSection.setStyle("-fx-background-color: #f0f8ff; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label formLabel = new Label("Thông tin lịch thi");
        formLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        
        formGrid.add(new Label("Loại thi:"), 0, 0);
        formGrid.add(cbExamType, 1, 0);
        
        formGrid.add(new Label("Ngày thi:"), 0, 1);
        formGrid.add(dpExamDate, 1, 1);
        
        formGrid.add(new Label("Khung giờ:"), 2, 1);
        formGrid.add(cbTimeSlot, 3, 1);
        
        formGrid.add(new Label("Trạng thái:"), 0, 2);
        formGrid.add(cbStatus, 1, 2);
        
        formGrid.add(new Label("Số lượng tối đa:"), 2, 2);
        formGrid.add(txtMaxCandidates, 3, 2);
        
        formGrid.add(new Label("Địa điểm:"), 0, 3);
        formGrid.add(txtLocation, 1, 3, 3, 1);
        
        HBox formButtons = new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear);
        formButtons.setAlignment(Pos.CENTER_RIGHT);
        formGrid.add(formButtons, 3, 4);
        
        formSection.getChildren().addAll(formLabel, formGrid);
        return formSection;
    }

    private void loadData() {
        try {
            List<ExamSchedule> schedules = examScheduleDAO.getAllSchedules();

            // Add exam type names to schedules
            for (ExamSchedule schedule : schedules) {
                try {
                    examTypeDAO.get(schedule.getExamTypeId()).ifPresent(examType -> {
                        schedule.setExamTypeName(examType.getName());
                    });
                } catch (Exception e) {
                    schedule.setExamTypeName("Không xác định");
                }
            }

            tableView.getItems().clear();
            tableView.getItems().addAll(schedules);
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Lỗi", "Không thể tải danh sách lịch thi: " + e.getMessage());
        }
    }

    private void handleAdd() {
        if (validateForm()) {
            try {
                ExamSchedule schedule = new ExamSchedule();
                schedule.setExamTypeId(cbExamType.getValue().getId());
                schedule.setExamDate(dpExamDate.getValue());
                schedule.setTimeSlot(cbTimeSlot.getValue());
                schedule.setStatus(cbStatus.getValue());
                schedule.setMaxCandidates(Integer.parseInt(txtMaxCandidates.getText()));
                schedule.setLocation(txtLocation.getText());
                schedule.setExaminerId(currentUser.getId());

                examScheduleDAO.addSchedule(schedule);
                loadData();
                clearForm();
                DialogUtils.showInfo("Thành công", "Đã thêm lịch thi mới!");
            } catch (Exception e) {
                e.printStackTrace();
                DialogUtils.showError("Lỗi", "Không thể thêm lịch thi: " + e.getMessage());
            }
        }
    }

    private void handleUpdate() {
        if (selectedSchedule != null && validateForm()) {
            try {
                selectedSchedule.setExamTypeId(cbExamType.getValue().getId());
                selectedSchedule.setExamDate(dpExamDate.getValue());
                selectedSchedule.setTimeSlot(cbTimeSlot.getValue());
                selectedSchedule.setStatus(cbStatus.getValue());
                selectedSchedule.setMaxCandidates(Integer.parseInt(txtMaxCandidates.getText()));
                selectedSchedule.setLocation(txtLocation.getText());

                examScheduleDAO.updateSchedule(selectedSchedule);
                loadData();
                clearForm();
                DialogUtils.showInfo("Thành công", "Đã cập nhật lịch thi!");
            } catch (Exception e) {
                e.printStackTrace();
                DialogUtils.showError("Lỗi", "Không thể cập nhật lịch thi: " + e.getMessage());
            }
        } else {
            DialogUtils.showError("Lỗi", "Vui lòng chọn lịch thi để cập nhật!");
        }
    }

    private void handleDelete() {
        if (selectedSchedule != null) {
            if (DialogUtils.showConfirmation("Xác nhận xóa", "Bạn có chắc muốn xóa lịch thi này?")) {
                try {
                    examScheduleDAO.deleteSchedule(selectedSchedule.getId());
                    loadData();
                    clearForm();
                    DialogUtils.showInfo("Thành công", "Đã xóa lịch thi!");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogUtils.showError("Lỗi", "Không thể xóa lịch thi: " + e.getMessage());
                }
            }
        } else {
            DialogUtils.showError("Lỗi", "Vui lòng chọn lịch thi để xóa!");
        }
    }

    private void loadScheduleToForm(ExamSchedule schedule) {
        try {
            examTypeDAO.get(schedule.getExamTypeId()).ifPresent(cbExamType::setValue);
            dpExamDate.setValue(schedule.getExamDate());
            cbTimeSlot.setValue(schedule.getTimeSlot());
            cbStatus.setValue(schedule.getStatus());
            txtMaxCandidates.setText(String.valueOf(schedule.getMaxCandidates()));
            txtLocation.setText(schedule.getLocation());
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Lỗi", "Không thể tải thông tin lịch thi: " + e.getMessage());
        }
    }

    private void handleSearch() {
        try {
            String searchText = txtSearch.getText().toLowerCase();
            LocalDate fromDate = dpSearchFrom.getValue();
            LocalDate toDate = dpSearchTo.getValue();
            ScheduleStatus status = cbSearchStatus.getValue();

            List<ExamSchedule> allSchedules = examScheduleDAO.getAllSchedules();

            List<ExamSchedule> filteredSchedules = allSchedules.stream()
                    .filter(schedule -> {
                        // Text search
                        boolean textMatch = searchText.isEmpty() ||
                                (schedule.getExamTypeName() != null && schedule.getExamTypeName().toLowerCase().contains(searchText)) ||
                                schedule.getTimeSlot().getDisplayName().toLowerCase().contains(searchText) ||
                                (schedule.getLocation() != null && schedule.getLocation().toLowerCase().contains(searchText));

                        // Date range search
                        boolean dateMatch = true;
                        if (fromDate != null && toDate != null) {
                            dateMatch = !schedule.getExamDate().isBefore(fromDate) &&
                                    !schedule.getExamDate().isAfter(toDate);
                        } else if (fromDate != null) {
                            dateMatch = !schedule.getExamDate().isBefore(fromDate);
                        } else if (toDate != null) {
                            dateMatch = !schedule.getExamDate().isAfter(toDate);
                        }

                        // Status search
                        boolean statusMatch = status == null || schedule.getStatus() == status;

                        return textMatch && dateMatch && statusMatch;
                    })
                    .collect(Collectors.toList());

            // Add exam type names
            for (ExamSchedule schedule : filteredSchedules) {
                try {
                    examTypeDAO.get(schedule.getExamTypeId()).ifPresent(examType -> {
                        schedule.setExamTypeName(examType.getName());
                    });
                } catch (Exception e) {
                    schedule.setExamTypeName("Không xác định");
                }
            }

            tableView.getItems().clear();
            tableView.getItems().addAll(filteredSchedules);
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Lỗi", "Không thể tìm kiếm: " + e.getMessage());
        }
    }

    private void resetSearch() {
        txtSearch.clear();
        dpSearchFrom.setValue(null);
        dpSearchTo.setValue(null);
        cbSearchStatus.setValue(null);
        loadData();
    }

    private void clearForm() {
        cbExamType.setValue(null);
        dpExamDate.setValue(LocalDate.now());
        cbTimeSlot.setValue(null);
        cbStatus.setValue(ScheduleStatus.OPEN);
        txtMaxCandidates.clear();
        txtLocation.clear();
        selectedSchedule = null;
    }

    private boolean validateForm() {
        if (cbExamType.getValue() == null) {
            DialogUtils.showError("Lỗi", "Vui lòng chọn loại thi!");
            return false;
        }

        if (dpExamDate.getValue() == null) {
            DialogUtils.showError("Lỗi", "Vui lòng chọn ngày thi!");
            return false;
        }

        if (dpExamDate.getValue().isBefore(LocalDate.now())) {
            DialogUtils.showError("Lỗi", "Ngày thi không thể là ngày trong quá khứ!");
            return false;
        }

        if (cbTimeSlot.getValue() == null) {
            DialogUtils.showError("Lỗi", "Vui lòng chọn khung giờ thi!");
            return false;
        }

        if (cbStatus.getValue() == null) {
            DialogUtils.showError("Lỗi", "Vui lòng chọn trạng thái!");
            return false;
        }

        try {
            int maxCandidates = Integer.parseInt(txtMaxCandidates.getText());
            if (maxCandidates <= 0) {
                DialogUtils.showError("Lỗi", "Số lượng thí sinh tối đa phải lớn hơn 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            DialogUtils.showError("Lỗi", "Số lượng thí sinh tối đa phải là số nguyên!");
            return false;
        }

        if (txtLocation.getText().trim().isEmpty()) {
            DialogUtils.showError("Lỗi", "Vui lòng nhập địa điểm thi!");
            return false;
        }

        return true;
    }
}