package com.pocitaco.oopsh.controllers.examiner;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.SessionReportDAO;
import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.models.SessionReport;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class SessionReportsController extends BaseController {

    @FXML
    private TableView<SessionReport> tableView;
    @FXML
    private TableColumn<SessionReport, Integer> colId;
    @FXML
    private TableColumn<SessionReport, String> colSessionId;
    @FXML
    private TableColumn<SessionReport, String> colContent;
    @FXML
    private TableColumn<SessionReport, String> colDate;

    private SessionReportDAO sessionReportDAO;
    private ExamScheduleDAO examScheduleDAO;

    @Override
    protected void initializeComponents() {
        sessionReportDAO = new SessionReportDAO();
        examScheduleDAO = new ExamScheduleDAO();
        setupTableColumns();
        loadSessionReports();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadSessionReports();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSessionId.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        colContent.setCellValueFactory(new PropertyValueFactory<>("reportContent"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
    }

    private void loadSessionReports() {
        List<SessionReport> reports = sessionReportDAO.findAll();
        tableView.getItems().clear();
        tableView.getItems().addAll(reports);
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
