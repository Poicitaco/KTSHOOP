package com.pocitaco.oopsh.controllers.examiner;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamScheduleDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.ExamSchedule;
import com.pocitaco.oopsh.models.ExamType;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TodayScheduleController extends BaseController {

    @FXML
    private TableView<ExamSchedule> tableView;
    @FXML
    private TableColumn<ExamSchedule, Integer> colId;
    @FXML
    private TableColumn<ExamSchedule, String> colExamType;
    @FXML
    private TableColumn<ExamSchedule, String> colTimeSlot;
    @FXML
    private TableColumn<ExamSchedule, String> colCandidates;
    @FXML
    private TableColumn<ExamSchedule, String> colStatus;

    private ExamScheduleDAO examScheduleDAO;
    private ExamTypeDAO examTypeDAO;

    @Override
    protected void initializeComponents() {
        examScheduleDAO = new ExamScheduleDAO();
        examTypeDAO = new ExamTypeDAO();
        setupTableColumns();
        loadTodaySchedule();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadTodaySchedule();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colTimeSlot.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        colCandidates.setCellValueFactory(new PropertyValueFactory<>("registeredCandidates"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadTodaySchedule() {
        LocalDate today = LocalDate.now();
        List<ExamSchedule> allSchedules = examScheduleDAO.getAllSchedules();
        
        // Filter schedules for today
        List<ExamSchedule> todaySchedules = allSchedules.stream()
                .filter(schedule -> schedule.getExamDate().equals(today))
                .collect(Collectors.toList());
        
        // Add exam type names to sessions
        for (ExamSchedule session : todaySchedules) {
            examTypeDAO.get(session.getExamTypeId()).ifPresent(examType -> {
                session.setExamTypeName(examType.getName());
            });
        }
        
        tableView.getItems().clear();
        tableView.getItems().addAll(todaySchedules);
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

