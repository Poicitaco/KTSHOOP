package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.ExamType;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.List;

public class ExamTypesController extends BaseController {

    @FXML
    private TableView<ExamType> tableView;
    @FXML
    private TableColumn<ExamType, Integer> colId;
    @FXML
    private TableColumn<ExamType, String> colName;
    @FXML
    private TableColumn<ExamType, String> colCode;
    @FXML
    private TableColumn<ExamType, String> colDescription;
    @FXML
    private TableColumn<ExamType, Double> colFee;
    @FXML
    private TableColumn<ExamType, String> colActions;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnAddExamType;
    @FXML
    private Button btnRefresh;

    private ExamTypeDAO examTypeDAO;

    @Override
    protected void initializeComponents() {
        examTypeDAO = new ExamTypeDAO();
        setupTableColumns();
        loadExamTypes();
    }

    @Override
    protected void setupEventHandlers() {
        btnAddExamType.setOnAction(event -> handleAddExamType());
        btnRefresh.setOnAction(event -> loadExamTypes());
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> filterExamTypes(newVal));
    }

    @Override
    protected void loadInitialData() {
        loadExamTypes();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colFee.setCellValueFactory(new PropertyValueFactory<>("fee"));
        
        // Setup actions column
        colActions.setCellFactory(param -> new javafx.scene.control.TableCell<ExamType, String>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox buttons = new HBox(5, btnEdit, btnDelete);
            
            {
                btnEdit.setOnAction(event -> {
                    ExamType examType = getTableView().getItems().get(getIndex());
                    handleEditExamType(examType);
                });
                btnDelete.setOnAction(event -> {
                    ExamType examType = getTableView().getItems().get(getIndex());
                    handleDeleteExamType(examType);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadExamTypes() {
        List<ExamType> examTypes = examTypeDAO.getAll();
        tableView.getItems().clear();
        tableView.getItems().addAll(examTypes);
    }

    private void filterExamTypes(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            loadExamTypes();
        } else {
            List<ExamType> allExamTypes = examTypeDAO.getAll();
            List<ExamType> filteredExamTypes = allExamTypes.stream()
                    .filter(examType -> examType.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                                       examType.getCode().toLowerCase().contains(searchText.toLowerCase()) ||
                                       examType.getDescription().toLowerCase().contains(searchText.toLowerCase()))
                    .toList();
            tableView.getItems().clear();
            tableView.getItems().addAll(filteredExamTypes);
        }
    }

    private void handleAddExamType() {
        showInfo("Add Exam Type", "Add exam type functionality will be implemented here");
        // TODO: Implement add exam type dialog
    }

    private void handleEditExamType(ExamType examType) {
        showInfo("Edit Exam Type", "Edit exam type functionality will be implemented here for: " + examType.getName());
        // TODO: Implement edit exam type dialog
    }

    private void handleDeleteExamType(ExamType examType) {
        if (showConfirmation("Delete Exam Type", "Are you sure you want to delete exam type: " + examType.getName() + "?")) {
            examTypeDAO.deleteById(examType.getId());
            loadExamTypes();
            showInfo("Exam Type Deleted", "Exam type has been deleted successfully");
        }
    }

    @Override
    protected void clearForm() {
        txtSearch.clear();
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        txtSearch.setDisable(!enabled);
        btnAddExamType.setDisable(!enabled);
        btnRefresh.setDisable(!enabled);
        tableView.setDisable(!enabled);
    }
}

