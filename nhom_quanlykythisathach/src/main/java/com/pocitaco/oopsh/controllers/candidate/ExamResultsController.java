package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.models.ExamType;
import com.pocitaco.oopsh.models.Result;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.utils.SessionManager;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExamResultsController extends BaseController {

    @FXML
    private MFXTableView<Result> tblResults;

    private ResultDAO resultDAO;
    private ExamTypeDAO examTypeDAO;
    private User currentUser;

    @Override
    public void initializeComponents() {
        this.resultDAO = new ResultDAO();
        this.examTypeDAO = new ExamTypeDAO();
        this.currentUser = SessionManager.getCurrentUser();
        setupTable();
    }

    @Override
    protected void setupEventHandlers() {
        // No event handlers needed for this view
    }

    @Override
    protected void loadInitialData() {
        if (currentUser != null) {
            List<Result> allResults = resultDAO.findAll();
            List<Result> myResults = allResults.stream()
                    .filter(result -> result.getUserId() == currentUser.getId())
                    .collect(Collectors.toList());
            tblResults.setItems(FXCollections.observableArrayList(myResults));
        }
    }

    private void setupTable() {
        // Create columns with simple approach to avoid compatibility issues
        MFXTableColumn<Result> examNameColumn = new MFXTableColumn<>("Exam Name");
        MFXTableColumn<Result> dateColumn = new MFXTableColumn<>("Date");
        MFXTableColumn<Result> theoryScoreColumn = new MFXTableColumn<>("Theory Score");
        MFXTableColumn<Result> practiceScoreColumn = new MFXTableColumn<>("Practice Score");
        MFXTableColumn<Result> statusColumn = new MFXTableColumn<>("Status");

        // Add columns to table
        tblResults.getTableColumns().addAll(
                examNameColumn, dateColumn, theoryScoreColumn, practiceScoreColumn, statusColumn);
    }

    private String getExamName(int examTypeId) {
        Optional<ExamType> examType = examTypeDAO.findById(examTypeId);
        return examType.map(ExamType::getName).orElse("Unknown");
    }

    @Override
    protected void clearForm() {
        // No form to clear
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        // No form to enable/disable
    }
}
