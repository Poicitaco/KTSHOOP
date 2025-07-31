package com.pocitaco.oopsh.controllers.examiner;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.models.Result;
import com.pocitaco.oopsh.models.User;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PracticeGradingController extends BaseController {

    @FXML
    private Label lblExamSession;

    @FXML
    private MFXTableView<Result> tblCandidates;

    @FXML
    private MFXButton btnSaveGrades;

    private ResultDAO resultDAO;
    private UserDAO userDAO;

    @Override
    public void initializeComponents() {
        this.resultDAO = new ResultDAO();
        this.userDAO = new UserDAO();
        setupTable();
    }

    @Override
    protected void setupEventHandlers() {
        btnSaveGrades.setOnAction(event -> handleSaveGrades());
    }

    @Override
    protected void loadInitialData() {
        // In a real application, you would load results for a specific exam session
        // For now, load all results that are pending practice grading
        List<Result> resultsToGrade = resultDAO.findAll().stream()
                .filter(result -> result.getScore() == 0) // Assuming 0 means not graded
                .collect(Collectors.toList());
        tblCandidates.setItems(FXCollections.observableArrayList(resultsToGrade));
    }

    private void setupTable() {
        // Basic MaterialFX table setup - simplified for compatibility
        // The table will be configured through FXML or basic initialization
        // TODO: Implement proper MaterialFX table setup when API is stable
    }

    private void handleSaveGrades() {
        for (Result result : tblCandidates.getItems()) {
            resultDAO.updateResult(result);
        }
        showInfo("Grades Saved", "Practice grades have been saved successfully.");
    }

    private String getCandidateName(int userId) {
        return userDAO.get(userId).map(User::getFullName).orElse("Unknown Candidate");
    }

    @Override
    protected void clearForm() {

    }

    @Override
    protected void setFormEnabled(boolean b) {

    }
}
