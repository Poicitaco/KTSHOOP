package com.pocitaco.oopsh.controllers.examiner;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.models.Result;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class GradeExamsController extends BaseController {

    @FXML
    private TableView<Result> tableView;
    @FXML
    private TableColumn<Result, Integer> colId;
    @FXML
    private TableColumn<Result, String> colCandidate;
    @FXML
    private TableColumn<Result, String> colExamType;
    @FXML
    private TableColumn<Result, Double> colScore;
    @FXML
    private TableColumn<Result, String> colStatus;

    private ResultDAO resultDAO;

    @Override
    protected void initializeComponents() {
        resultDAO = new ResultDAO();
        setupTableColumns();
        loadPendingExams();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadPendingExams();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCandidate.setCellValueFactory(new PropertyValueFactory<>("candidateName"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadPendingExams() {
        List<Result> results = resultDAO.findAll();
        // Filter for pending results
        List<Result> pendingResults = results.stream()
                .filter(result -> "PENDING".equals(result.getStatus().toString()))
                .toList();
        tableView.getItems().clear();
        tableView.getItems().addAll(pendingResults);
    }

    @FXML
    private void handleGradeExam() {
        Result selectedResult = tableView.getSelectionModel().getSelectedItem();
        if (selectedResult != null) {
            // TODO: Implement grading logic
            showInfo("Chấm thi", "Đã chấm thi cho thí sinh: " + selectedResult.getCandidateName());
        } else {
            showError("Lỗi", "Vui lòng chọn bài thi để chấm!");
        }
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
