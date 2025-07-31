package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.dao.RegistrationDAO;
import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.enums.RegistrationStatus;
import com.pocitaco.oopsh.models.ExamType;
import com.pocitaco.oopsh.models.Registration;
import com.pocitaco.oopsh.models.ExamSchedule;
import com.pocitaco.oopsh.utils.ValidationHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RegisterExamController extends BaseController {

    @FXML private ComboBox<ExamType> cbExamType;
    @FXML private ComboBox<ExamSchedule> cbExamSchedule;
    @FXML private Label lblExamInfo;
    @FXML private Label lblScheduleInfo;
    @FXML private Button btnRegister;
    @FXML private Button btnClear;

    private ExamTypeDAO examTypeDAO;
    private RegistrationDAO registrationDAO;
    private ExamScheduleDAO examScheduleDAO;
    private int currentUserId;

    @Override
    protected void initializeComponents() {
        examTypeDAO = new ExamTypeDAO();
        registrationDAO = new RegistrationDAO();
        examScheduleDAO = new ExamScheduleDAO();
        setupComboBoxes();
        loadExamTypes();
    }

    @Override
    protected void setupEventHandlers() {
        btnRegister.setOnAction(event -> handleRegister());
        btnClear.setOnAction(event -> clearForm());
        
        // Exam type selection listener
        cbExamType.setOnAction(event -> handleExamTypeSelection());
        
        // Exam schedule selection listener
        cbExamSchedule.setOnAction(event -> handleScheduleSelection());
    }

    @Override
    protected void loadInitialData() {
        loadExamTypes();
    }

    private void setupComboBoxes() {
        // Setup exam type ComboBox
        cbExamType.setCellFactory(param -> new ListCell<ExamType>() {
            @Override
            protected void updateItem(ExamType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getCode() + ") - " + 
                           ValidationHelper.formatCurrency(item.getFee()));
                }
            }
        });
        cbExamType.setButtonCell(cbExamType.getCellFactory().call(null));
        
        // Setup exam schedule ComboBox
        cbExamSchedule.setCellFactory(param -> new ListCell<ExamSchedule>() {
            @Override
            protected void updateItem(ExamSchedule item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s - %s", 
                        item.getExamDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        item.getTimeSlot().getDisplayName(),
                        item.getStatus().getDisplayName()));
                }
            }
        });
        cbExamSchedule.setButtonCell(cbExamSchedule.getCellFactory().call(null));
    }

    private void loadExamTypes() {
        List<ExamType> examTypes = examTypeDAO.getAll();
        ObservableList<ExamType> examTypeList = FXCollections.observableArrayList(examTypes);
        cbExamType.setItems(examTypeList);
    }

    private void handleExamTypeSelection() {
        ExamType selectedExamType = cbExamType.getValue();
        if (selectedExamType != null) {
            // Display exam information
            lblExamInfo.setText(String.format(
                "Thông tin kỳ thi:\n" +
                "• Tên: %s\n" +
                "• Mã: %s\n" +
                "• Mô tả: %s\n" +
                "• Thời gian: %d phút\n" +
                "• Điểm đậu: %d\n" +
                "• Phí thi: %s",
                selectedExamType.getName(),
                selectedExamType.getCode(),
                selectedExamType.getDescription(),
                selectedExamType.getDuration(),
                selectedExamType.getPassingScore(),
                ValidationHelper.formatCurrency(selectedExamType.getFee())
            ));
            
            // Load available schedules for this exam type
            loadAvailableSchedules(selectedExamType.getId());
        } else {
            lblExamInfo.setText("Vui lòng chọn loại thi");
            cbExamSchedule.getItems().clear();
        }
    }

    private void loadAvailableSchedules(int examTypeId) {
        List<ExamSchedule> allSchedules = examScheduleDAO.getAllSchedules();
        List<ExamSchedule> availableSchedules = allSchedules.stream()
            .filter(schedule -> schedule.getExamTypeId() == examTypeId)
            .filter(schedule -> schedule.getStatus().toString().equals("OPEN"))
            .filter(schedule -> schedule.getRegisteredCandidates() < schedule.getMaxCandidates())
            .filter(schedule -> schedule.getExamDate().isAfter(LocalDate.now()) || 
                               schedule.getExamDate().isEqual(LocalDate.now()))
            .toList();
        
        ObservableList<ExamSchedule> scheduleList = FXCollections.observableArrayList(availableSchedules);
        cbExamSchedule.setItems(scheduleList);
        
        if (availableSchedules.isEmpty()) {
            lblScheduleInfo.setText("Không có lịch thi khả dụng cho loại thi này");
        } else {
            lblScheduleInfo.setText(String.format("Có %d lịch thi khả dụng", availableSchedules.size()));
        }
    }

    private void handleScheduleSelection() {
        ExamSchedule selectedSchedule = cbExamSchedule.getValue();
        if (selectedSchedule != null) {
            lblScheduleInfo.setText(String.format(
                "Thông tin lịch thi:\n" +
                "• Ngày thi: %s\n" +
                "• Khung giờ: %s\n" +
                "• Địa điểm: %s\n" +
                "• Số lượng đã đăng ký: %d/%d\n" +
                "• Trạng thái: %s",
                selectedSchedule.getExamDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                selectedSchedule.getTimeSlot().getDisplayName(),
                selectedSchedule.getLocation() != null ? selectedSchedule.getLocation() : "Chưa cập nhật",
                selectedSchedule.getRegisteredCandidates(),
                selectedSchedule.getMaxCandidates(),
                selectedSchedule.getStatus().getDisplayName()
            ));
        }
    }

    private void handleRegister() {
        ExamType selectedExamType = cbExamType.getValue();
        ExamSchedule selectedSchedule = cbExamSchedule.getValue();
        
        if (selectedExamType == null) {
            showError("Lỗi", "Vui lòng chọn loại thi!");
            return;
        }
        
        if (selectedSchedule == null) {
            showError("Lỗi", "Vui lòng chọn lịch thi!");
            return;
        }
        
        // Check if user is already registered for this exam type
        List<Registration> existingRegistrations = registrationDAO.findAll();
        boolean alreadyRegistered = existingRegistrations.stream()
            .anyMatch(reg -> reg.getUserId() == currentUserId && 
                           reg.getExamTypeId() == selectedExamType.getId() &&
                           !reg.getStatus().toString().equals("CANCELLED"));
        
        if (alreadyRegistered) {
            showError("Lỗi", "Bạn đã đăng ký cho loại thi này rồi!");
            return;
        }
        
        // Check if schedule is still available
        if (selectedSchedule.getRegisteredCandidates() >= selectedSchedule.getMaxCandidates()) {
            showError("Lỗi", "Lịch thi đã đầy, vui lòng chọn lịch khác!");
            return;
        }
        
        // Confirm registration
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đăng ký");
        alert.setHeaderText("Xác nhận đăng ký thi");
        alert.setContentText(String.format(
            "Bạn có chắc muốn đăng ký thi?\n\n" +
            "Loại thi: %s\n" +
            "Ngày thi: %s\n" +
            "Khung giờ: %s\n" +
            "Phí thi: %s",
            selectedExamType.getName(),
            selectedSchedule.getExamDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            selectedSchedule.getTimeSlot().getDisplayName(),
            ValidationHelper.formatCurrency(selectedExamType.getFee())
        ));
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Create registration
            Registration registration = new Registration();
            registration.setUserId(currentUserId);
            registration.setExamTypeId(selectedExamType.getId());
            registration.setRegistrationDate(LocalDate.now());
            registration.setStatus("PENDING");
            registration.setExamTypeName(selectedExamType.getName());
            
            try {
                registrationDAO.add(registration);
                
                // Update schedule registered count
                selectedSchedule.setRegisteredCandidates(selectedSchedule.getRegisteredCandidates() + 1);
                examScheduleDAO.updateSchedule(selectedSchedule);
                
                showInfo("Thành công", "Đăng ký thi thành công!\n\n" +
                    "Mã đăng ký: " + registration.getId() + "\n" +
                    "Vui lòng thanh toán phí thi để hoàn tất đăng ký.");
                
                clearForm();
                
            } catch (Exception e) {
                showError("Lỗi", "Không thể đăng ký thi: " + e.getMessage());
            }
        }
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    @Override
    protected void clearForm() {
        cbExamType.setValue(null);
        cbExamSchedule.setValue(null);
        lblExamInfo.setText("Vui lòng chọn loại thi");
        lblScheduleInfo.setText("Vui lòng chọn lịch thi");
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        cbExamType.setDisable(!enabled);
        cbExamSchedule.setDisable(!enabled);
        btnRegister.setDisable(!enabled);
        btnClear.setDisable(!enabled);
    }
}
