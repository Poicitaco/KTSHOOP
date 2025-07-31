package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.MockExamDAO;
import com.pocitaco.oopsh.models.MockExam;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

public class MockExamsController extends BaseController {

    @FXML
    private MFXListView<MockExam> lstMockExams;

    @FXML
    private MFXButton btnStartMockExam;

    private MockExamDAO mockExamDAO;

    @Override
    public void initializeComponents() {
        this.mockExamDAO = new MockExamDAO();
    }

    @Override
    protected void setupEventHandlers() {
        btnStartMockExam.setOnAction(event -> handleStartMockExam());
    }

    @Override
    protected void loadInitialData() {
        lstMockExams.setItems(FXCollections.observableArrayList(mockExamDAO.getAll()));
    }

    private void handleStartMockExam() {
        MockExam selectedMockExam = lstMockExams.getSelectionModel().getSelectedValues().get(0);
        if (selectedMockExam != null) {
            showInfo("Start Mock Exam", "Starting mock exam: " + selectedMockExam.getName());
            // Logic to load and start the mock exam
        } else {
            showError("No Mock Exam Selected", "Please select a mock exam to start.");
        }
    }

    @Override
    protected void clearForm() {

    }

    @Override
    protected void setFormEnabled(boolean b) {

    }
}

