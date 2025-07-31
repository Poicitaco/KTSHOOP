package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.ExamSchedule;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class UpcomingExamsController extends BaseController {

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
        loadUpcomingExams();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadUpcomingExams();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("examDate"));
        colTimeSlot.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadUpcomingExams() {
        List<ExamSchedule> allSchedules = examScheduleDAO.getAllSchedules();
        
        // Filter for upcoming schedules
        List<ExamSchedule> upcomingSchedules = allSchedules.stream()
                .filter(schedule -> schedule.getExamDate().isAfter(LocalDate.now()) || 
                                  schedule.getExamDate().isEqual(LocalDate.now()))
                .collect(Collectors.toList());
        
        // Add exam type names to schedules
        for (ExamSchedule schedule : upcomingSchedules) {
            examTypeDAO.get(schedule.getExamTypeId()).ifPresent(examType -> {
                schedule.setExamTypeName(examType.getName());
            });
        }
        
        tableView.getItems().clear();
        tableView.getItems().addAll(upcomingSchedules);
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
