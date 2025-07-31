package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.enums.ResultStatus;
import com.pocitaco.oopsh.models.Result;
import com.pocitaco.oopsh.models.ExamType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ExamResultsController extends BaseController {

    @FXML private TableView<Result> tableView;
    @FXML private TableColumn<Result, Integer> colId;
    @FXML private TableColumn<Result, String> colCandidateName;
    @FXML private TableColumn<Result, String> colExamType;
    @FXML private TableColumn<Result, Double> colScore;
    @FXML private TableColumn<Result, String> colExamDate;
    @FXML private TableColumn<Result, String> colStatus;
    @FXML private TableColumn<Result, String> colActions;
    
    // Search controls
    @FXML private TextField txtSearch;
    @FXML private ComboBox<ExamType> cbExamTypeFilter;
    @FXML private ComboBox<ResultStatus> cbStatusFilter;
    @FXML private TextField txtMinScore;
    @FXML private TextField txtMaxScore;
    @FXML private DatePicker dpFromDate;
    @FXML private DatePicker dpToDate;
    @FXML private Button btnSearch;
    @FXML private Button btnReset;
    @FXML private Button btnExport;

    private ResultDAO resultDAO;
    private ExamTypeDAO examTypeDAO;

    @Override
    protected void initializeComponents() {
        resultDAO = new ResultDAO();
        examTypeDAO = new ExamTypeDAO();
        setupTableColumns();
        setupSearchControls();
        loadExamResults();
    }

    @Override
    protected void setupEventHandlers() {
        btnSearch.setOnAction(event -> handleSearch());
        btnReset.setOnAction(event -> resetSearch());
        btnExport.setOnAction(event -> handleExport());
        
        // Real-time search
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                loadExamResults();
            }
        });
    }

    @Override
    protected void loadInitialData() {
        loadExamResults();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCandidateName.setCellValueFactory(new PropertyValueFactory<>("candidateName"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        colExamDate.setCellValueFactory(new PropertyValueFactory<>("examDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Format score column
        colScore.setCellFactory(param -> new TableCell<Result, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f", item));
                }
            }
        });
        
        // Format date column
        colExamDate.setCellFactory(param -> new TableCell<Result, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });
        
        // Setup actions column
        colActions.setCellFactory(param -> new TableCell<Result, String>() {
            private final Button btnView = new Button("Xem");
            private final Button btnEdit = new Button("Sửa");
            private final HBox buttons = new HBox(5, btnView, btnEdit);
            
            {
                btnView.setOnAction(event -> {
                    Result result = getTableView().getItems().get(getIndex());
                    handleViewResult(result);
                });
                btnEdit.setOnAction(event -> {
                    Result result = getTableView().getItems().get(getIndex());
                    handleEditResult(result);
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

    private void setupSearchControls() {
        // Load exam types for filter
        List<ExamType> examTypes = examTypeDAO.getAll();
        ObservableList<ExamType> examTypeList = FXCollections.observableArrayList(examTypes);
        examTypeList.add(0, null); // Add null option for "All"
        cbExamTypeFilter.setItems(examTypeList);
        cbExamTypeFilter.setCellFactory(param -> new ListCell<ExamType>() {
            @Override
            protected void updateItem(ExamType item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Tất cả loại thi");
                } else {
                    setText(item.getName());
                }
            }
        });
        cbExamTypeFilter.setButtonCell(cbExamTypeFilter.getCellFactory().call(null));
        
        // Load status options for filter
        ObservableList<ResultStatus> statusList = FXCollections.observableArrayList(ResultStatus.values());
        statusList.add(0, null); // Add null option for "All"
        cbStatusFilter.setItems(statusList);
        cbStatusFilter.setCellFactory(param -> new ListCell<ResultStatus>() {
            @Override
            protected void updateItem(ResultStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Tất cả trạng thái");
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        cbStatusFilter.setButtonCell(cbStatusFilter.getCellFactory().call(null));
    }

    private void loadExamResults() {
        List<Result> results = resultDAO.findAll();
        
        // Add exam type names to results
        for (Result result : results) {
            examTypeDAO.get(result.getExamTypeId()).ifPresent(examType -> {
                result.setExamTypeName(examType.getName());
            });
        }
        
        tableView.getItems().clear();
        tableView.getItems().addAll(results);
    }

    private void handleSearch() {
        String searchText = txtSearch.getText().toLowerCase();
        ExamType selectedExamType = cbExamTypeFilter.getValue();
        ResultStatus selectedStatus = cbStatusFilter.getValue();
        
        // Parse score range
        final Double minScore;
        final Double maxScore;
        try {
            if (!txtMinScore.getText().isEmpty()) {
                minScore = Double.parseDouble(txtMinScore.getText());
            } else {
                minScore = null;
            }
            if (!txtMaxScore.getText().isEmpty()) {
                maxScore = Double.parseDouble(txtMaxScore.getText());
            } else {
                maxScore = null;
            }
        } catch (NumberFormatException e) {
            showError("Lỗi", "Điểm số phải là số hợp lệ!");
            return;
        }
        
        final LocalDate fromDate = dpFromDate.getValue();
        final LocalDate toDate = dpToDate.getValue();
        
        List<Result> allResults = resultDAO.findAll();
        
        List<Result> filteredResults = allResults.stream()
            .filter(result -> {
                // Text search (fuzzy search)
                boolean textMatch = searchText.isEmpty() || 
                    result.getCandidateName().toLowerCase().contains(searchText) ||
                    result.getExamTypeName().toLowerCase().contains(searchText);
                
                // Exam type filter
                boolean examTypeMatch = selectedExamType == null || 
                    result.getExamTypeId() == selectedExamType.getId();
                
                // Status filter
                boolean statusMatch = selectedStatus == null || 
                    result.getStatus() == selectedStatus;
                
                // Score range filter
                boolean scoreMatch = true;
                if (minScore != null && result.getScore() < minScore) {
                    scoreMatch = false;
                }
                if (maxScore != null && result.getScore() > maxScore) {
                    scoreMatch = false;
                }
                
                // Date range filter
                boolean dateMatch = true;
                if (fromDate != null && result.getExamDate().isBefore(fromDate)) {
                    dateMatch = false;
                }
                if (toDate != null && result.getExamDate().isAfter(toDate)) {
                    dateMatch = false;
                }
                
                return textMatch && examTypeMatch && statusMatch && scoreMatch && dateMatch;
            })
            .collect(Collectors.toList());
        
        // Add exam type names
        for (Result result : filteredResults) {
            examTypeDAO.get(result.getExamTypeId()).ifPresent(examType -> {
                result.setExamTypeName(examType.getName());
            });
        }
        
        tableView.getItems().clear();
        tableView.getItems().addAll(filteredResults);
        
        // Show search results count
        showInfo("Tìm kiếm", String.format("Tìm thấy %d kết quả", filteredResults.size()));
    }

    private void resetSearch() {
        txtSearch.clear();
        cbExamTypeFilter.setValue(null);
        cbStatusFilter.setValue(null);
        txtMinScore.clear();
        txtMaxScore.clear();
        dpFromDate.setValue(null);
        dpToDate.setValue(null);
        loadExamResults();
    }

    private void handleViewResult(Result result) {
        String message = String.format(
            "Chi tiết kết quả:\n" +
            "Thí sinh: %s\n" +
            "Loại thi: %s\n" +
            "Điểm: %.1f\n" +
            "Ngày thi: %s\n" +
            "Trạng thái: %s",
            result.getCandidateName(),
            result.getExamTypeName(),
            result.getScore(),
            result.getExamDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            result.getStatus().getDisplayName()
        );
        showInfo("Chi tiết kết quả", message);
    }

    private void handleEditResult(Result result) {
        showInfo("Sửa kết quả", "Chức năng sửa kết quả sẽ được triển khai ở đây cho: " + result.getCandidateName());
        // TODO: Implement edit result dialog
    }

    private void handleExport() {
        List<Result> results = tableView.getItems();
        if (results.isEmpty()) {
            showError("Lỗi", "Không có dữ liệu để xuất!");
            return;
        }
        
        // TODO: Implement export to Excel/CSV
        showInfo("Xuất dữ liệu", String.format("Đã xuất %d kết quả thi", results.size()));
    }

    @Override
    protected void clearForm() {
        resetSearch();
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        txtSearch.setDisable(!enabled);
        cbExamTypeFilter.setDisable(!enabled);
        cbStatusFilter.setDisable(!enabled);
        txtMinScore.setDisable(!enabled);
        txtMaxScore.setDisable(!enabled);
        dpFromDate.setDisable(!enabled);
        dpToDate.setDisable(!enabled);
        btnSearch.setDisable(!enabled);
        btnReset.setDisable(!enabled);
        btnExport.setDisable(!enabled);
        tableView.setDisable(!enabled);
    }
}
