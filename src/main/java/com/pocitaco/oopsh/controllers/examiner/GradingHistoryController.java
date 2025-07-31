package com.pocitaco.oopsh.controllers.examiner;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.models.Result;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class GradingHistoryController extends BaseController {

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
        loadGradingHistory();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadGradingHistory();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCandidate.setCellValueFactory(new PropertyValueFactory<>("candidateName"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadGradingHistory() {
        List<Result> results = resultDAO.findAll();
        tableView.getItems().clear();
        tableView.getItems().addAll(results);
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
