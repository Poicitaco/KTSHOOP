package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseDashboardController;
import com.pocitaco.oopsh.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardOverviewController extends BaseDashboardController {

    @FXML
    private Label lblTotalUsers;

    @FXML
    private Label lblTotalExams;

    @FXML
    private Label lblTotalRevenue;

    @FXML
    private Label lblPendingRegistrations;

    private UserDAO userDAO;

    @Override
    public void initializeComponents() {
        super.initializeComponents();
        this.userDAO = new UserDAO();
    }

    @Override
    protected void loadInitialData() {
        loadStatistics();
    }

    private void loadStatistics() {
        // In a real application, you would fetch this data from a database or service
        // For now, we'll use some dummy data
        lblTotalUsers.setText(String.valueOf(userDAO.getAllUsers().size()));
        lblTotalExams.setText("125");
        lblTotalRevenue.setText("$12,345.67");
        lblPendingRegistrations.setText("8");
    }

    @Override
    protected void updateUserInfo() {
        // No user-specific info to update on this dashboard
    }

    @Override
    protected void loadUserSpecificData() {
        // No user-specific data to load on this dashboard
    }

    @Override
    protected void setupNavigation() {
        // Navigation is handled by the MainLayoutController
    }
}

