package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.ExamType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ExamEditController extends BaseController {

    @FXML
    private TextField txtExamName;
    @FXML
    private TextField txtExamCode;
    @FXML
    private TextField txtDescription;
    @FXML
    private TextField txtFee;
    @FXML
    private ComboBox<ExamType> cbExamType;

    private ExamTypeDAO examTypeDAO;
    private ExamType examTypeToEdit;

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
        // Load initial data for exam editing
        if (examTypeToEdit != null) {
            txtExamName.setText(examTypeToEdit.getName());
            txtExamCode.setText(examTypeToEdit.getCode());
            txtDescription.setText(examTypeToEdit.getDescription());
            txtFee.setText(String.valueOf(examTypeToEdit.getFee()));
        }
    }

    public void setExamTypeToEdit(ExamType examType) {
        this.examTypeToEdit = examType;
    }

    private void loadExamTypes() {
        // Load exam types for combo box if needed
    }

    @Override
    protected void clearForm() {
        txtExamName.clear();
        txtExamCode.clear();
        txtDescription.clear();
        txtFee.clear();
        cbExamType.setValue(null);
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        txtExamName.setDisable(!enabled);
        txtExamCode.setDisable(!enabled);
        txtDescription.setDisable(!enabled);
        txtFee.setDisable(!enabled);
        cbExamType.setDisable(!enabled);
    }
}
