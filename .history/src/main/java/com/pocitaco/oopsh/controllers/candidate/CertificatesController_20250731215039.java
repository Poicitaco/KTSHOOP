package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.CertificateDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.Certificate;
import com.pocitaco.oopsh.models.ExamType;
import com.pocitaco.oopsh.models.User;
import com.pocitaco.oopsh.utils.SessionManager;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class CertificatesController extends BaseController {

    @FXML
    private TableView<Certificate> tableView;
    @FXML
    private TableColumn<Certificate, Integer> colId;
    @FXML
    private TableColumn<Certificate, String> colCertificateNumber;
    @FXML
    private TableColumn<Certificate, String> colExamType;
    @FXML
    private TableColumn<Certificate, Double> colScore;
    @FXML
    private TableColumn<Certificate, String> colGrade;
    @FXML
    private TableColumn<Certificate, String> colIssuedDate;

    private CertificateDAO certificateDAO;
    private ExamTypeDAO examTypeDAO;
    private User currentUser;

    @Override
    protected void initializeComponents() {
        certificateDAO = new CertificateDAO();
        examTypeDAO = new ExamTypeDAO();
        setupTableColumns();
        loadCertificates();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadCertificates();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCertificateNumber.setCellValueFactory(new PropertyValueFactory<>("certificateNumber"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colIssuedDate.setCellValueFactory(new PropertyValueFactory<>("issuedDate"));
    }

    private void loadCertificates() {
        List<Certificate> certificates = certificateDAO.findAll();
        
        // Add exam type names to certificates
        for (Certificate cert : certificates) {
            examTypeDAO.get(cert.getExamId()).ifPresent(examType -> {
                cert.setExamTypeName(examType.getName());
            });
        }
        
        tableView.getItems().clear();
        tableView.getItems().addAll(certificates);
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

