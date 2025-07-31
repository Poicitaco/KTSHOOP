package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

public class DashboardStatsController extends BaseController {

    @FXML
    private AreaChart<String, Number> userRegistrationsChart;

    @FXML
    private PieChart examRegistrationsChart;

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
        loadUserRegistrationsChart();
        loadExamRegistrationsChart();
    }

    private void loadUserRegistrationsChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("New Users");

        // Dummy data
        series.getData().add(new XYChart.Data<>("Jan", 100));
        series.getData().add(new XYChart.Data<>("Feb", 200));
        series.getData().add(new XYChart.Data<>("Mar", 150));
        series.getData().add(new XYChart.Data<>("Apr", 300));
        series.getData().add(new XYChart.Data<>("May", 250));
        series.getData().add(new XYChart.Data<>("Jun", 400));

        userRegistrationsChart.getData().add(series);
    }

    private void loadExamRegistrationsChart() {
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("A1", 13),
                        new PieChart.Data("A2", 25),
                        new PieChart.Data("B1", 10),
                        new PieChart.Data("B2", 22),
                        new PieChart.Data("C", 30));
        examRegistrationsChart.setData(pieChartData);
    }

    @Override
    protected void clearForm() {

    }

    @Override
    protected void setFormEnabled(boolean b) {

    }
}

