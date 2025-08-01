package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.enums.ScheduleStatus;
import com.pocitaco.oopsh.enums.TimeSlot;
import com.pocitaco.oopsh.models.ExamSchedule;
import com.pocitaco.oopsh.models.ExamType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ExamScheduleController extends BaseController {

    @FXML
    private TableView<ExamSchedule> tableView;
    @FXML
    private TableColumn<ExamSchedule, Integer> colId;
    @FXML
    private TableColumn<ExamSchedule, String> colExamType;
    @FXML
    private TableColumn<ExamSchedule, String> colDate;
    @FXML
    private TableColumn<ExamSchedule, String> colTimeSlot;
    @FXML
    private TableColumn<ExamSchedule, String> colStatus;
    @FXML
    private TableColumn<ExamSchedule, String> colActions;

    // Form controls
    @FXML
    private ComboBox<ExamType> cbExamType;
    @FXML
    private DatePicker dpExamDate;
    @FXML
    private ComboBox<TimeSlot> cbTimeSlot;
    @FXML
    private ComboBox<ScheduleStatus> cbStatus;
    @FXML
    private TextField txtMaxCandidates;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnClear;

    // Search controls
    @FXML
    private TextField txtSearch;
    @FXML
    private DatePicker dpSearchFrom;
    @FXML
    private DatePicker dpSearchTo;
    @FXML
    private ComboBox<ScheduleStatus> cbSearchStatus;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnReset;

    private ExamScheduleDAO examScheduleDAO;
    private ExamTypeDAO examTypeDAO;
    private ExamSchedule selectedSchedule;

    @Override
    protected void initializeComponents() {
        examScheduleDAO = new ExamScheduleDAO();
        examTypeDAO = new ExamTypeDAO();
        setupTableColumns();
        setupFormControls();
        setupSearchControls();
        loadExamSchedules();
    }

    @Override
    protected void setupEventHandlers() {
        btnAdd.setOnAction(event -> handleAdd());
        btnUpdate.setOnAction(event -> handleUpdate());
        btnClear.setOnAction(event -> clearForm());
        btnSearch.setOnAction(event -> handleSearch());
        btnReset.setOnAction(event -> resetSearch());

        // Table selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedSchedule = newSelection;
                        loadScheduleToForm(newSelection);
                    }
                });
    }

    @Override
    protected void loadInitialData() {
        loadExamSchedules();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("examDate"));
        colTimeSlot.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Setup actions column
        colActions.setCellFactory(param -> new TableCell<ExamSchedule, String>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
            private final HBox buttons = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setOnAction(event -> {
                    ExamSchedule schedule = getTableView().getItems().get(getIndex());
                    handleEdit(schedule);
                });
                btnDelete.setOnAction(event -> {
                    ExamSchedule schedule = getTableView().getItems().get(getIndex());
                    handleDelete(schedule);
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

    private void setupFormControls() {
        try {
            // Load exam types to ComboBox
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
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi", "Không thể tải danh sách loại thi: " + e.getMessage());
        }

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

        // Set default values
        dpExamDate.setValue(LocalDate.now());
        cbStatus.setValue(ScheduleStatus.OPEN);
    }

    private void setupSearchControls() {
        // Load status options for search
        ObservableList<ScheduleStatus> statuses = FXCollections.observableArrayList(ScheduleStatus.values());
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
    }

    private void loadExamSchedules() {
        try {
            List<ExamSchedule> schedules = examScheduleDAO.getAllSchedules();

            // Add exam type names to schedules
            for (ExamSchedule schedule : schedules) {
                try {
                    examTypeDAO.get(schedule.getExamTypeId()).ifPresent(examType -> {
                        schedule.setExamTypeName(examType.getName());
                    });
                } catch (Exception e) {
                    // If exam type not found, set a default name
                    schedule.setExamTypeName("Không xác định");
                }
            }

            tableView.getItems().clear();
            tableView.getItems().addAll(schedules);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi", "Không thể tải danh sách lịch thi: " + e.getMessage());
        }
    }

    private void handleAdd() {
        if (validateForm()) {
            ExamSchedule schedule = new ExamSchedule();
            schedule.setExamTypeId(cbExamType.getValue().getId());
            schedule.setExamDate(dpExamDate.getValue());
            schedule.setTimeSlot(cbTimeSlot.getValue());
            schedule.setStatus(cbStatus.getValue());
            schedule.setMaxCandidates(Integer.parseInt(txtMaxCandidates.getText()));

            examScheduleDAO.addSchedule(schedule);
            loadExamSchedules();
            clearForm();
            showInfo("Thành công", "Đã thêm lịch thi mới!");
        }
    }

    private void handleUpdate() {
        if (selectedSchedule != null && validateForm()) {
            selectedSchedule.setExamTypeId(cbExamType.getValue().getId());
            selectedSchedule.setExamDate(dpExamDate.getValue());
            selectedSchedule.setTimeSlot(cbTimeSlot.getValue());
            selectedSchedule.setStatus(cbStatus.getValue());
            selectedSchedule.setMaxCandidates(Integer.parseInt(txtMaxCandidates.getText()));

            examScheduleDAO.updateSchedule(selectedSchedule);
            loadExamSchedules();
            clearForm();
            showInfo("Thành công", "Đã cập nhật lịch thi!");
        } else {
            showError("Lỗi", "Vui lòng chọn lịch thi để cập nhật!");
        }
    }

    private void handleEdit(ExamSchedule schedule) {
        selectedSchedule = schedule;
        loadScheduleToForm(schedule);
    }

    private void handleDelete(ExamSchedule schedule) {
        if (showConfirmation("Xác nhận xóa", "Bạn có chắc muốn xóa lịch thi này?")) {
            examScheduleDAO.deleteSchedule(schedule.getId());
            loadExamSchedules();
            showInfo("Thành công", "Đã xóa lịch thi!");
        }
    }

    private void loadScheduleToForm(ExamSchedule schedule) {
        try {
            examTypeDAO.get(schedule.getExamTypeId()).ifPresent(cbExamType::setValue);
            dpExamDate.setValue(schedule.getExamDate());
            cbTimeSlot.setValue(schedule.getTimeSlot());
            cbStatus.setValue(schedule.getStatus());
            txtMaxCandidates.setText(String.valueOf(schedule.getMaxCandidates()));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi", "Không thể tải thông tin lịch thi: " + e.getMessage());
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
                                schedule.getTimeSlot().getDisplayName().toLowerCase().contains(searchText);

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
            showError("Lỗi", "Không thể tìm kiếm: " + e.getMessage());
        }
    }

    private void resetSearch() {
        txtSearch.clear();
        dpSearchFrom.setValue(null);
        dpSearchTo.setValue(null);
        cbSearchStatus.setValue(null);
        loadExamSchedules();
    }

    private boolean validateForm() {
        if (cbExamType.getValue() == null) {
            showError("Lỗi", "Vui lòng chọn loại thi!");
            return false;
        }

        if (dpExamDate.getValue() == null) {
            showError("Lỗi", "Vui lòng chọn ngày thi!");
            return false;
        }

        if (dpExamDate.getValue().isBefore(LocalDate.now())) {
            showError("Lỗi", "Ngày thi không thể là ngày trong quá khứ!");
            return false;
        }

        if (cbTimeSlot.getValue() == null) {
            showError("Lỗi", "Vui lòng chọn khung giờ thi!");
            return false;
        }

        if (cbStatus.getValue() == null) {
            showError("Lỗi", "Vui lòng chọn trạng thái!");
            return false;
        }

        try {
            int maxCandidates = Integer.parseInt(txtMaxCandidates.getText());
            if (maxCandidates <= 0) {
                showError("Lỗi", "Số lượng thí sinh tối đa phải lớn hơn 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Lỗi", "Số lượng thí sinh tối đa phải là số nguyên!");
            return false;
        }

        return true;
    }

    @Override
    protected void clearForm() {
        cbExamType.setValue(null);
        dpExamDate.setValue(LocalDate.now());
        cbTimeSlot.setValue(null);
        cbStatus.setValue(ScheduleStatus.OPEN);
        txtMaxCandidates.clear();
        selectedSchedule = null;
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        cbExamType.setDisable(!enabled);
        dpExamDate.setDisable(!enabled);
        cbTimeSlot.setDisable(!enabled);
        cbStatus.setDisable(!enabled);
        txtMaxCandidates.setDisable(!enabled);
        btnAdd.setDisable(!enabled);
        btnUpdate.setDisable(!enabled);
        btnClear.setDisable(!enabled);
    }
}