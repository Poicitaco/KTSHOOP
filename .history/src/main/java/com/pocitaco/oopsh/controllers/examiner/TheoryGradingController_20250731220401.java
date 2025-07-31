package com.pocitaco.oopsh.controllers.examiner;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.models.Result;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;

import java.util.List;

public class TheoryGradingController extends BaseController {

    @FXML
    private TableView<Result> tableView;
    @FXML
    private TableColumn<Result, Integer> colId;
    @FXML
    private TableColumn<Result, String> colCandidate;
    @FXML
    private TableColumn<Result, String> colExamType;
    @FXML
    private TableColumn<Result, Double> colScore;
    @FXML
    private TableColumn<Result, String> colStatus;
    @FXML
    private TextField txtScore;

    private ResultDAO resultDAO;
    private Result selectedResult;

    @Override
    protected void initializeComponents() {
        resultDAO = new ResultDAO();
        setupTableColumns();
        loadTheoryResults();
    }

    @Override
    protected void setupEventHandlers() {
        // Event handlers are set in FXML
    }

    @Override
    protected void loadInitialData() {
        loadTheoryResults();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCandidate.setCellValueFactory(new PropertyValueFactory<>("candidateName"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadTheoryResults() {
        List<Result> results = resultDAO.findAll();
        // Filter for theory results (examTypeId = 1 for theory)
        List<Result> theoryResults = results.stream()
                .filter(result -> result.getExamTypeId() == 1)
                .toList();
        tableView.getItems().clear();
        tableView.getItems().addAll(theoryResults);
    }

    @FXML
    private void handleSelectResult() {
        selectedResult = tableView.getSelectionModel().getSelectedItem();
        if (selectedResult != null) {
            txtScore.setText(String.valueOf(selectedResult.getScore()));
        }
    }

    @FXML
    private void handleSaveGrade() {
        if (selectedResult != null) {
            try {
                double score = Double.parseDouble(txtScore.getText());
                selectedResult.setScore(score);
                resultDAO.updateResult(selectedResult);
                showInfo("Thành công", "Đã cập nhật điểm lý thuyết!");
                loadTheoryResults();
            } catch (NumberFormatException e) {
                showError("Lỗi", "Vui lòng nhập điểm hợp lệ!");
            }
        } else {
            showError("Lỗi", "Vui lòng chọn kết quả để chấm điểm!");
        }
    }

    @Override
    protected void clearForm() {
        txtScore.clear();
        selectedResult = null;
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        txtScore.setDisable(!enabled);
    }
}

