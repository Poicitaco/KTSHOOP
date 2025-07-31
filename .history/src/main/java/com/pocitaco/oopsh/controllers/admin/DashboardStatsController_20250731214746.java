package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardStatsController extends BaseController {

    @FXML
    private Label lblTotalUsers;
    @FXML
    private Label lblTotalExams;
    @FXML
    private Label lblTotalRegistrations;
    @FXML
    private Label lblTotalRevenue;

    @Override
    protected void initializeComponents() {
        // Initialize components
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadStatistics();
    }

    private void loadStatistics() {
        // TODO: Load actual statistics from DAOs
        lblTotalUsers.setText("1,234");
        lblTotalExams.setText("567");
        lblTotalRegistrations.setText("890");
        lblTotalRevenue.setText("123,456,789 VNĐ");
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

