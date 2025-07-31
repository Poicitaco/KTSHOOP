package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.ExamType;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AvailableExamsController extends BaseController {

    @FXML
    private TableView<ExamType> tableView;
    @FXML
    private TableColumn<ExamType, Integer> colId;
    @FXML
    private TableColumn<ExamType, String> colName;
    @FXML
    private TableColumn<ExamType, String> colDescription;
    @FXML
    private TableColumn<ExamType, Double> colFee;
    @FXML
    private TableColumn<ExamType, String> colStatus;

    private ExamTypeDAO examTypeDAO;

    @Override
    protected void initializeComponents() {
        examTypeDAO = new ExamTypeDAO();
        setupTableColumns();
        loadAvailableExams();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadAvailableExams();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colFee.setCellValueFactory(new PropertyValueFactory<>("fee"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadAvailableExams() {
        List<ExamType> examTypes = examTypeDAO.getAll();
        tableView.getItems().clear();
        tableView.getItems().addAll(examTypes);
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

