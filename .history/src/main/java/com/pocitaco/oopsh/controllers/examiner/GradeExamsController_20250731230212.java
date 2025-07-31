package com.pocitaco.oopsh.controllers.examiner;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ResultDAO;
import com.pocitaco.oopsh.dao.RegistrationDAO;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.enums.ResultStatus;
import com.pocitaco.oopsh.models.Result;
import com.pocitaco.oopsh.models.Registration;
import com.pocitaco.oopsh.models.ExamType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class GradeExamsController extends BaseController {

    @FXML
    private TableView<Registration> tableView;
    @FXML
    private TableColumn<Registration, Integer> colId;
    @FXML
    private TableColumn<Registration, String> colCandidateName;
    @FXML
    private TableColumn<Registration, String> colExamType;
    @FXML
    private TableColumn<Registration, String> colRegistrationDate;
    @FXML
    private TableColumn<Registration, String> colStatus;
    @FXML
    private TableColumn<Registration, String> colActions;

    @FXML
    private TextField txtScore;
    @FXML
    private TextArea txtComments;
    @FXML
    private Button btnSaveGrade;
    @FXML
    private Button btnClear;
    @FXML
    private ComboBox<ExamType> cbExamTypeFilter;
    @FXML
    private ComboBox<String> cbStatusFilter;
    @FXML
    private Button btnFilter;
    @FXML
    private Button btnReset;

    private ResultDAO resultDAO;
    private RegistrationDAO registrationDAO;
    private ExamTypeDAO examTypeDAO;
    private Registration selectedRegistration;

    @Override
    protected void initializeComponents() {
        resultDAO = new ResultDAO();
        registrationDAO = new RegistrationDAO();
        examTypeDAO = new ExamTypeDAO();
        setupTableColumns();
        setupFilters();
        loadPendingExams();
    }

    @Override
    protected void setupEventHandlers() {
        btnSaveGrade.setOnAction(event -> handleSaveGrade());
        btnClear.setOnAction(event -> clearForm());
        btnFilter.setOnAction(event -> handleFilter());
        btnReset.setOnAction(event -> resetFilter());

        // Table selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedRegistration = newSelection;
                        loadRegistrationDetails(newSelection);
                    }
                });
    }

    @Override
    protected void loadInitialData() {
        loadPendingExams();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCandidateName.setCellValueFactory(new PropertyValueFactory<>("candidateName"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examTypeName"));
        colRegistrationDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Setup actions column
        colActions.setCellFactory(param -> new TableCell<Registration, String>() {
            private final Button btnGrade = new Button("Chấm điểm");
            private final Button btnView = new Button("Xem");
            private final HBox buttons = new HBox(5, btnGrade, btnView);

            {
                btnGrade.setOnAction(event -> {
                    Registration registration = getTableView().getItems().get(getIndex());
                    handleGradeExam(registration);
                });
                btnView.setOnAction(event -> {
                    Registration registration = getTableView().getItems().get(getIndex());
                    handleViewDetails(registration);
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

    private void setupFilters() {
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
        ObservableList<String> statusList = FXCollections.observableArrayList("PENDING", "COMPLETED", "CANCELLED");
        statusList.add(0, null); // Add null option for "All"
        cbStatusFilter.setItems(statusList);
        cbStatusFilter.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Tất cả trạng thái");
                } else {
                    setText(item);
                }
            }
        });
        cbStatusFilter.setButtonCell(cbStatusFilter.getCellFactory().call(null));
    }

    private void loadPendingExams() {
        List<Registration> registrations = registrationDAO.findAll();

        // Add candidate names and exam type names
        for (Registration registration : registrations) {
            // Set exam type name
            examTypeDAO.get(registration.getExamTypeId()).ifPresent(examType -> {
                registration.setExamTypeName(examType.getName());
            });

            // Set candidate name (placeholder - would need UserDAO in real implementation)
            registration.setCandidateName("Thí sinh " + registration.getUserId());
        }

        tableView.getItems().clear();
        tableView.getItems().addAll(registrations);
    }

    private void handleGradeExam(Registration registration) {
        selectedRegistration = registration;
        loadRegistrationDetails(registration);

        // Check if already graded
        List<Result> existingResults = resultDAO.findAll();
        boolean alreadyGraded = existingResults.stream()
                .anyMatch(result -> result.getUserId() == registration.getUserId() &&
                        result.getExamTypeId() == registration.getExamTypeId());

        if (alreadyGraded) {
            showInfo("Thông báo", "Thí sinh này đã được chấm điểm!");
            return;
        }

        // Enable grading form
        setFormEnabled(true);
        txtScore.requestFocus();
    }

    private void loadRegistrationDetails(Registration registration) {
        if (registration != null) {
            // Load exam type details
            examTypeDAO.get(registration.getExamTypeId()).ifPresent(examType -> {
                String details = String.format(
                        "Thông tin đăng ký:\n" +
                                "• Mã đăng ký: %d\n" +
                                "• Thí sinh: %s\n" +
                                "• Loại thi: %s\n" +
                                "• Ngày đăng ký: %s\n" +
                                "• Trạng thái: %s\n" +
                                "• Điểm đậu: %d",
                        registration.getId(),
                        registration.getCandidateName(),
                        examType.getName(),
                        registration.getRegistrationDate()
                                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        registration.getStatus(),
                        examType.getPassingScore());

                // You could display this in a label or text area
                System.out.println(details);
            });
        }
    }

    private void handleSaveGrade() {
        if (selectedRegistration == null) {
            showError("Lỗi", "Vui lòng chọn thí sinh để chấm điểm!");
            return;
        }

        String scoreText = txtScore.getText().trim();
        if (scoreText.isEmpty()) {
            showError("Lỗi", "Vui lòng nhập điểm!");
            return;
        }

        try {
            double score = Double.parseDouble(scoreText);
            if (score < 0 || score > 100) {
                showError("Lỗi", "Điểm phải từ 0 đến 100!");
                return;
            }

            // Get exam type to check passing score
            Optional<ExamType> examTypeOpt = examTypeDAO.get(selectedRegistration.getExamTypeId());
            if (examTypeOpt.isEmpty()) {
                showError("Lỗi", "Không tìm thấy thông tin loại thi!");
                return;
            }

            ExamType examType = examTypeOpt.get();
            ResultStatus status = score >= examType.getPassingScore() ? ResultStatus.PASSED : ResultStatus.FAILED;

            // Create result
            Result result = new Result();
            result.setUserId(selectedRegistration.getUserId());
            result.setExamTypeId(selectedRegistration.getExamTypeId());
            result.setScore(score);
            result.setExamDate(LocalDate.now());
            result.setStatus(status);
            result.setCandidateName(selectedRegistration.getCandidateName());
            result.setExamTypeName(examType.getName());

            // Save result
            resultDAO.create(result);

            // Update registration status
            selectedRegistration.setStatus("COMPLETED");
            registrationDAO.update(selectedRegistration);

            showInfo("Thành công", String.format(
                    "Đã chấm điểm thành công!\n" +
                            "Thí sinh: %s\n" +
                            "Điểm: %.1f\n" +
                            "Kết quả: %s",
                    selectedRegistration.getCandidateName(),
                    score,
                    status.getDisplayName()));

            clearForm();
            loadPendingExams();

        } catch (NumberFormatException e) {
            showError("Lỗi", "Điểm phải là số hợp lệ!");
        } catch (Exception e) {
            showError("Lỗi", "Không thể lưu điểm: " + e.getMessage());
        }
    }

    private void handleViewDetails(Registration registration) {
        String details = String.format(
                "Chi tiết đăng ký:\n" +
                        "Mã đăng ký: %d\n" +
                        "Thí sinh: %s\n" +
                        "Loại thi: %s\n" +
                        "Ngày đăng ký: %s\n" +
                        "Trạng thái: %s",
                registration.getId(),
                registration.getCandidateName(),
                registration.getExamTypeName(),
                registration.getRegistrationDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                registration.getStatus());

        showInfo("Chi tiết đăng ký", details);
    }

    private void handleFilter() {
        ExamType selectedExamType = cbExamTypeFilter.getValue();
        String selectedStatus = cbStatusFilter.getValue();

        List<Registration> allRegistrations = registrationDAO.findAll();

        List<Registration> filteredRegistrations = allRegistrations.stream()
                .filter(registration -> {
                    // Exam type filter
                    boolean examTypeMatch = selectedExamType == null ||
                            registration.getExamTypeId() == selectedExamType.getId();

                    // Status filter
                    boolean statusMatch = selectedStatus == null ||
                            registration.getStatus().equals(selectedStatus);

                    return examTypeMatch && statusMatch;
                })
                .toList();

        // Add candidate names and exam type names
        for (Registration registration : filteredRegistrations) {
            examTypeDAO.get(registration.getExamTypeId()).ifPresent(examType -> {
                registration.setExamTypeName(examType.getName());
            });
            registration.setCandidateName("Thí sinh " + registration.getUserId());
        }

        tableView.getItems().clear();
        tableView.getItems().addAll(filteredRegistrations);

        showInfo("Lọc dữ liệu", String.format("Tìm thấy %d kết quả", filteredRegistrations.size()));
    }

    private void resetFilter() {
        cbExamTypeFilter.setValue(null);
        cbStatusFilter.setValue(null);
        loadPendingExams();
    }

    @Override
    protected void clearForm() {
        txtScore.clear();
        txtComments.clear();
        selectedRegistration = null;
        setFormEnabled(false);
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        txtScore.setDisable(!enabled);
        txtComments.setDisable(!enabled);
        btnSaveGrade.setDisable(!enabled);
        btnClear.setDisable(!enabled);
    }
}
