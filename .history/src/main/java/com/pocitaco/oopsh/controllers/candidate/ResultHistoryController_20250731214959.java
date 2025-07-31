package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.models.Result;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ResultHistoryController extends BaseController {

    @FXML
    private TableView<Result> tableView;
    @FXML
    private TableColumn<Result, Integer> colId;
    @FXML
    private TableColumn<Result, String> colExamType;
    @FXML
    private TableColumn<Result, Double> colScore;
    @FXML
    private TableColumn<Result, String> colDate;
    @FXML
    private TableColumn<Result, String> colStatus;

    private ResultDAO resultDAO;

    @Override
    protected void initializeComponents() {
        resultDAO = new ResultDAO();
        setupTableColumns();
        loadResultHistory();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadResultHistory();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("examDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadResultHistory() {
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

