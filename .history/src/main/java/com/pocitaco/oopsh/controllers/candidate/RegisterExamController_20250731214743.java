package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.ExamType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.List;

public class RegisterExamController extends BaseController {

    @FXML
    private ComboBox<ExamType> cbExamType;
    @FXML
    private Label lblExamInfo;
    @FXML
    private Label lblFee;

    private ExamTypeDAO examTypeDAO;

    @Override
    protected void initializeComponents() {
        examTypeDAO = new ExamTypeDAO();
        loadExamTypes();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        // Load exam types for registration
        loadExamTypes();
    }

    private void loadExamTypes() {
        List<ExamType> examTypes = examTypeDAO.getAll();
        cbExamType.getItems().clear();
        cbExamType.getItems().addAll(examTypes);
    }

    @FXML
    private void handleExamTypeSelection() {
        ExamType selectedExam = cbExamType.getValue();
        if (selectedExam != null) {
            lblExamInfo.setText(selectedExam.getDescription());
            lblFee.setText(String.format("%,.0f VNĐ", selectedExam.getFee()));
        }
    }

    @FXML
    private void handleRegister() {
        ExamType selectedExam = cbExamType.getValue();
        if (selectedExam != null) {
            // TODO: Implement registration logic
            showInfo("Đăng ký thành công", "Bạn đã đăng ký thi " + selectedExam.getName() + " thành công!");
        } else {
            showError("Lỗi", "Vui lòng chọn loại thi!");
        }
    }

    @Override
    protected void clearForm() {
        cbExamType.setValue(null);
        lblExamInfo.setText("");
        lblFee.setText("");
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        cbExamType.setDisable(!enabled);
    }
}
