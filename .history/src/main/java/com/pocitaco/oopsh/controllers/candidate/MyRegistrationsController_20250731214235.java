package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.RegistrationDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.Registration;
import com.pocitaco.oopsh.models.ExamType;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MyRegistrationsController extends BaseController {

    @FXML
    private TableView<Registration> tableView;
    @FXML
    private TableColumn<Registration, Integer> colId;
    @FXML
    private TableColumn<Registration, String> colExamType;
    @FXML
    private TableColumn<Registration, String> colRegistrationDate;
    @FXML
    private TableColumn<Registration, String> colStatus;

    private RegistrationDAO registrationDAO;
    private ExamTypeDAO examTypeDAO;

    @Override
    protected void initializeComponents() {
        registrationDAO = new RegistrationDAO();
        examTypeDAO = new ExamTypeDAO();
        setupTableColumns();
        loadMyRegistrations();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadMyRegistrations();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colRegistrationDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadMyRegistrations() {
        List<Registration> registrations = registrationDAO.getAll();
        
        // Add exam type names to registrations
        for (Registration registration : registrations) {
            examTypeDAO.get(registration.getExamTypeId()).ifPresent(examType -> {
                registration.setExamTypeName(examType.getName());
            });
        }
        
        tableView.getItems().clear();
        tableView.getItems().addAll(registrations);
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

