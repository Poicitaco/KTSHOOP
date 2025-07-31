package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.PaymentDAO;
import com.pocitaco.oopsh.models.Payment;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class PaymentHistoryController extends BaseController {

    @FXML
    private TableView<Payment> tableView;
    @FXML
    private TableColumn<Payment, Integer> colId;
    @FXML
    private TableColumn<Payment, String> colAmount;
    @FXML
    private TableColumn<Payment, String> colStatus;
    @FXML
    private TableColumn<Payment, String> colDate;

    private PaymentDAO paymentDAO;

    @Override
    protected void initializeComponents() {
        paymentDAO = new PaymentDAO();
        setupTableColumns();
        loadPaymentHistory();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadPaymentHistory();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
    }

    private void loadPaymentHistory() {
        List<Payment> payments = paymentDAO.findAll();
        tableView.getItems().clear();
        tableView.getItems().addAll(payments);
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

