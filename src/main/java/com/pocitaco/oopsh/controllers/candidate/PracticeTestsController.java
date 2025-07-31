package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.PracticeTestDAO;
import com.pocitaco.oopsh.models.PracticeTest;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

public class PracticeTestsController extends BaseController {

    @FXML
    private MFXListView<PracticeTest> lstPracticeTests;

    @FXML
    private MFXButton btnStartTest;

    private PracticeTestDAO practiceTestDAO;

    @Override
    public void initializeComponents() {
        this.practiceTestDAO = new PracticeTestDAO();
    }

    @Override
    protected void setupEventHandlers() {
        btnStartTest.setOnAction(event -> handleStartTest());
    }

    @Override
    protected void loadInitialData() {
        lstPracticeTests.setItems(FXCollections.observableArrayList(practiceTestDAO.getAll()));
    }

    private void handleStartTest() {
        PracticeTest selectedTest = lstPracticeTests.getSelectionModel().getSelectedValues().get(0);
        if (selectedTest != null) {
            showInfo("Start Test", "Starting practice test: " + selectedTest.getName());
            // Logic to load and start the practice test
        } else {
            showError("No Test Selected", "Please select a practice test to start.");
        }
    }

    @Override
    protected void clearForm() {

    }

    @Override
    protected void setFormEnabled(boolean b) {

    }
}

