package com.pocitaco.oopsh.controllers.examiner;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.UserDAO;
import com.pocitaco.oopsh.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class CandidatesController extends BaseController {

    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User, Integer> colId;
    @FXML
    private TableColumn<User, String> colName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, String> colPhone;
    @FXML
    private TableColumn<User, String> colStatus;

    private UserDAO userDAO;

    @Override
    protected void initializeComponents() {
        userDAO = new UserDAO();
        setupTableColumns();
        loadCandidates();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadCandidates();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadCandidates() {
        List<User> allUsers = userDAO.getAllUsers();
        // Filter for candidates only
        List<User> candidates = allUsers.stream()
                .filter(user -> "CANDIDATE".equals(user.getRole().toString()))
                .collect(Collectors.toList());
        tableView.getItems().clear();
        tableView.getItems().addAll(candidates);
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

