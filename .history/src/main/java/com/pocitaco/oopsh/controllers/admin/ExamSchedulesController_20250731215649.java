package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.ExamSchedule;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ExamSchedulesController extends BaseController {

    @FXML
    private TableView<ExamSchedule> tableView;
    @FXML
    private TableColumn<ExamSchedule, Integer> colId;
    @FXML
    private TableColumn<ExamSchedule, String> colExamType;
    @FXML
    private TableColumn<ExamSchedule, String> colDate;
    @FXML
    private TableColumn<ExamSchedule, String> colTimeSlot;
    @FXML
    private TableColumn<ExamSchedule, String> colStatus;

    private ExamScheduleDAO examScheduleDAO;
    private ExamTypeDAO examTypeDAO;

    @Override
    protected void initializeComponents() {
        examScheduleDAO = new ExamScheduleDAO();
        examTypeDAO = new ExamTypeDAO();
        setupTableColumns();
        loadExamSchedules();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadExamSchedules();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("examDate"));
        colTimeSlot.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadExamSchedules() {
        List<ExamSchedule> schedules = examScheduleDAO.getAllSchedules();

        // Add exam type names to schedules
        for (ExamSchedule schedule : schedules) {
            examTypeDAO.get(schedule.getExamTypeId()).ifPresent(examType -> {
                schedule.setExamTypeName(examType.getName());
            });
        }

        tableView.getItems().clear();
        tableView.getItems().addAll(schedules);
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
