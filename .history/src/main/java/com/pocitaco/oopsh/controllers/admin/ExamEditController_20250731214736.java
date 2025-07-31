package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.ExamType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ExamEditController extends BaseController {
    @Override
    protected void loadInitialData() {
        // Load initial data for exam editing
        loadExamTypes();
    }

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
        private void loadExamTypes() {
        cbExamType.getItems().clear();
        cbExamType.getItems().addAll(examTypeDAO.getAll());
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

