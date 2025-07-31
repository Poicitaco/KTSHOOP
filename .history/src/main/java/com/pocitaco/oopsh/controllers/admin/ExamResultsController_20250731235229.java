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

    @FXML
    private TableView<Result> tableView;
    @FXML
    private TableColumn<Result, Integer> colId;
    @FXML
    private TableColumn<Result, String> colCandidateName;
    @FXML
    private TableColumn<Result, String> colExamType;
    @FXML
    private TableColumn<Result, Double> colScore;
    @FXML
    private TableColumn<Result, String> colExamDate;
    @FXML
    private TableColumn<Result, String> colStatus;
    @FXML
    private TableColumn<Result, String> colActions;

    // Search controls
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<ExamType> cbExamTypeFilter;
    @FXML
    private ComboBox<ResultStatus> cbStatusFilter;
    @FXML
    private TextField txtMinScore;
    @FXML
    private TextField txtMaxScore;
    @FXML
    private DatePicker dpFromDate;
    @FXML
    private DatePicker dpToDate;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnReset;
    @FXML
    private Button btnExport;

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
                result.getStatus().getDisplayName());
        showInfo("Chi tiết kết quả", message);
    }

    private void handleEditResult(Result result) {
        if (result == null) {
            showError("Lỗi", "Vui lòng chọn kết quả để sửa!");
            return;
        }
        showEditResultDialog(result);
    }

    private void showEditResultDialog(Result result) {
        // Create dialog
        Dialog<Result> dialog = new Dialog<>();
        dialog.setTitle("Sửa kết quả thi");
        dialog.setHeaderText("Cập nhật thông tin kết quả thi");
        
        // Set dialog modality
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(tableView.getScene().getWindow());

        // Create form
        VBox form = new VBox(15);
        form.setPadding(new javafx.geometry.Insets(20));

        // Candidate name (read-only)
        Label lblCandidate = new Label("Thí sinh:");
        Label lblCandidateValue = new Label(result.getCandidateName());
        lblCandidateValue.setStyle("-fx-font-weight: bold;");

        // Exam type (read-only)
        Label lblExamType = new Label("Loại thi:");
        Label lblExamTypeValue = new Label(result.getExamTypeName());
        lblExamTypeValue.setStyle("-fx-font-weight: bold;");

        // Exam date (read-only)
        Label lblExamDate = new Label("Ngày thi:");
        Label lblExamDateValue = new Label(result.getExamDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblExamDateValue.setStyle("-fx-font-weight: bold;");

        // Score field
        Label lblScore = new Label("Điểm thi:");
        TextField txtScore = new TextField();
        txtScore.setText(String.valueOf(result.getScore()));
        txtScore.setPromptText("Nhập điểm (0-100)");

        // Status field
        Label lblStatus = new Label("Trạng thái:");
        ComboBox<ResultStatus> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll(ResultStatus.values());
        cbStatus.setCellFactory(param -> new ListCell<ResultStatus>() {
            @Override
            protected void updateItem(ResultStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        cbStatus.setButtonCell(cbStatus.getCellFactory().call(null));
        cbStatus.setValue(result.getStatus());

        // Comments field
        Label lblComments = new Label("Ghi chú:");
        TextArea txtComments = new TextArea();
        txtComments.setText(result.getComments() != null ? result.getComments() : "");
        txtComments.setPromptText("Nhập ghi chú (nếu có)");
        txtComments.setPrefRowCount(3);

        // Add fields to form
        form.getChildren().addAll(
            lblCandidate, lblCandidateValue,
            lblExamType, lblExamTypeValue,
            lblExamDate, lblExamDateValue,
            lblScore, txtScore,
            lblStatus, cbStatus,
            lblComments, txtComments
        );

        // Set dialog content
        dialog.getDialogPane().setContent(form);

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Handle save button
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Validate input
                String scoreText = txtScore.getText().trim();
                if (scoreText.isEmpty()) {
                    showError("Lỗi", "Vui lòng nhập điểm!");
                    return null;
                }

                try {
                    double score = Double.parseDouble(scoreText);
                    if (score < 0 || score > 100) {
                        showError("Lỗi", "Điểm phải từ 0 đến 100!");
                        return null;
                    }

                    // Update result
                    result.setScore(score);
                    result.setStatus(cbStatus.getValue());
                    result.setComments(txtComments.getText().trim());
                    
                    // Update in database
                    resultDAO.updateResult(result);
                    showInfo("Thành công", "Đã cập nhật kết quả thi thành công!");
                    
                    loadExamResults();
                    return result;
                } catch (NumberFormatException e) {
                    showError("Lỗi", "Điểm phải là số hợp lệ!");
                    return null;
                } catch (Exception e) {
                    showError("Lỗi", "Không thể cập nhật kết quả: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        // Show dialog
        dialog.showAndWait();
    }

    private void handleExport() {
        List<Result> results = tableView.getItems();
        if (results.isEmpty()) {
            showError("Lỗi", "Không có dữ liệu để xuất!");
            return;
        }

        // Create file chooser
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Xuất kết quả thi");
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new javafx.stage.FileChooser.ExtensionFilter("All Files", "*.*"));
        fileChooser.setInitialFileName("ket_qua_thi_"
                + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")));

        // Show save dialog
        java.io.File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());
        if (file != null) {
            try {
                exportToFile(results, file);
                showInfo("Thành công",
                        String.format("Đã xuất %d kết quả thi thành công!\nFile: %s", results.size(), file.getName()));
            } catch (Exception e) {
                showError("Lỗi", "Không thể xuất file: " + e.getMessage());
            }
        }
    }

    private void exportToFile(List<Result> results, java.io.File file) throws Exception {
        String extension = getFileExtension(file.getName()).toLowerCase();

        switch (extension) {
            case "csv":
                exportToCSV(results, file);
                break;
            case "txt":
                exportToTXT(results, file);
                break;
            default:
                exportToCSV(results, file); // Default to CSV
                break;
        }
    }

    private void exportToCSV(List<Result> results, java.io.File file) throws Exception {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file, "UTF-8")) {
            // Write CSV header
            writer.println("ID,Thí sinh,Loại thi,Điểm,Ngày thi,Trạng thái");

            // Write data rows
            for (Result result : results) {
                String line = String.format("%d,\"%s\",\"%s\",%.1f,\"%s\",\"%s\"",
                        result.getId(),
                        result.getCandidateName(),
                        result.getExamTypeName(),
                        result.getScore(),
                        result.getExamDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        result.getStatus().getDisplayName());
                writer.println(line);
            }
        }
    }

    private void exportToTXT(List<Result> results, java.io.File file) throws Exception {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file, "UTF-8")) {
            // Write header
            writer.println("BÁO CÁO KẾT QUẢ THI");
            writer.println("Ngày xuất: "
                    + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            writer.println("Tổng số kết quả: " + results.size());
            writer.println();

            // Write table header
            writer.println(String.format("%-5s %-30s %-20s %-8s %-12s %-15s",
                    "ID", "Thí sinh", "Loại thi", "Điểm", "Ngày thi", "Trạng thái"));
            writer.println("-".repeat(100));

            // Write data rows
            for (Result result : results) {
                writer.println(String.format("%-5d %-30s %-20s %-8.1f %-12s %-15s",
                        result.getId(),
                        truncateString(result.getCandidateName(), 28),
                        truncateString(result.getExamTypeName(), 18),
                        result.getScore(),
                        result.getExamDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        result.getStatus().getDisplayName()));
            }

            // Write summary
            writer.println();
            writer.println("THỐNG KÊ:");
            long passedCount = results.stream().filter(r -> r.getStatus().toString().equals("PASSED")).count();
            long failedCount = results.stream().filter(r -> r.getStatus().toString().equals("FAILED")).count();
            double averageScore = results.stream().mapToDouble(Result::getScore).average().orElse(0.0);
            double passRate = results.size() > 0 ? (double) passedCount / results.size() * 100 : 0.0;

            writer.println(String.format("Tổng số: %d", results.size()));
            writer.println(String.format("Đậu: %d", passedCount));
            writer.println(String.format("Rớt: %d", failedCount));
            writer.println(String.format("Điểm trung bình: %.1f", averageScore));
            writer.println(String.format("Tỷ lệ đậu: %.1f%%", passRate));
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    private String truncateString(String str, int maxLength) {
        if (str == null)
            return "";
        if (str.length() <= maxLength)
            return str;
        return str.substring(0, maxLength - 3) + "...";
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
