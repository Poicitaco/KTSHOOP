package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.ExamType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;

public class ExamInfoController extends BaseController {

    @FXML
    private Label lblExamName;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblDuration;
    @FXML
    private Label lblPassingScore;
    @FXML
    private Label lblFee;

    private ExamTypeDAO examTypeDAO;
    private int examTypeId;

    @Override
    protected void initializeComponents() {
        examTypeDAO = new ExamTypeDAO();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        // Load exam information
        if (examTypeId > 0) {
            examTypeDAO.get(examTypeId).ifPresent(this::displayExamInfo);
        }
    }

    public void setExamTypeId(int examTypeId) {
        this.examTypeId = examTypeId;
    }

    private void displayExamInfo(ExamType examType) {
        lblExamName.setText(examType.getName());
        lblDescription.setText(examType.getDescription());
        lblDuration.setText(examType.getDuration() + " phút");
        lblPassingScore.setText(examType.getPassingScore() + " điểm");
        lblFee.setText(String.format("%,.0f VNĐ", examType.getFee()));
    }

    @Override
    protected void clearForm() {
        lblExamName.setText("");
        lblDescription.setText("");
        lblDuration.setText("");
        lblPassingScore.setText("");
        lblFee.setText("");
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        // No form to enable/disable
    }
}

