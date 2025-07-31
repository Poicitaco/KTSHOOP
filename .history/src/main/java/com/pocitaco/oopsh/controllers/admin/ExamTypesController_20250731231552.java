package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.ExamType;
import com.pocitaco.oopsh.utils.ValidationHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;

import java.util.List;
import java.util.Optional;

public class ExamTypesController extends BaseController {

    @FXML private TableView<ExamType> tableView;
    @FXML private TableColumn<ExamType, Integer> colId;
    @FXML private TableColumn<ExamType, String> colCode;
    @FXML private TableColumn<ExamType, String> colName;
    @FXML private TableColumn<ExamType, String> colDescription;
    @FXML private TableColumn<ExamType, Integer> colDuration;
    @FXML private TableColumn<ExamType, Integer> colPassingScore;
    @FXML private TableColumn<ExamType, String> colFee;
    @FXML private TableColumn<ExamType, String> colActions;
    
    @FXML private TextField txtSearch;
    @FXML private Button btnAdd;
    @FXML private Button btnRefresh;

    private ExamTypeDAO examTypeDAO;
    private ExamType selectedExamType;

    @Override
    protected void initializeComponents() {
        examTypeDAO = new ExamTypeDAO();
        setupTableColumns();
        loadExamTypes();
    }

    @Override
    protected void setupEventHandlers() {
        btnAdd.setOnAction(event -> handleAddExamType());
        btnRefresh.setOnAction(event -> loadExamTypes());
        
        // Search functionality
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filterExamTypes(newVal);
        });
        
        // Table selection
        tableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                selectedExamType = newSelection;
            }
        );
    }

    @Override
    protected void loadInitialData() {
        loadExamTypes();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colPassingScore.setCellValueFactory(new PropertyValueFactory<>("passingScore"));
        
        // Format fee column
        colFee.setCellValueFactory(new PropertyValueFactory<>("fee"));
        colFee.setCellFactory(param -> new TableCell<ExamType, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    try {
                        double fee = Double.parseDouble(item);
                        setText(ValidationHelper.formatCurrency(fee));
                    } catch (NumberFormatException e) {
                        setText(item);
                    }
                }
            }
        });
        
        // Setup actions column
        colActions.setCellFactory(param -> new TableCell<ExamType, String>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
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
                    .filter(examType -> {
                        // Fuzzy search - search in multiple fields
                        String searchLower = searchText.toLowerCase();
                        return examType.getName().toLowerCase().contains(searchLower) ||
                               examType.getCode().toLowerCase().contains(searchLower) ||
                               (examType.getDescription() != null && 
                                examType.getDescription().toLowerCase().contains(searchLower));
                    })
                    .toList();
            tableView.getItems().clear();
            tableView.getItems().addAll(filteredExamTypes);
        }
    }

    private void handleAddExamType() {
        showExamTypeDialog(null);
    }

    private void handleEditExamType(ExamType examType) {
        if (examType != null) {
            showExamTypeDialog(examType);
        } else {
            showError("Lỗi", "Vui lòng chọn loại thi để sửa!");
        }
    }

    private void handleDeleteExamType(ExamType examType) {
        if (examType == null) {
            showError("Lỗi", "Vui lòng chọn loại thi để xóa!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa loại thi");
        alert.setContentText("Bạn có chắc muốn xóa loại thi '" + examType.getName() + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                examTypeDAO.deleteById(examType.getId());
                showInfo("Thành công", "Đã xóa loại thi thành công!");
                loadExamTypes();
            } catch (Exception e) {
                showError("Lỗi", "Không thể xóa loại thi: " + e.getMessage());
            }
        }
    }

    private void showExamTypeDialog(ExamType examType) {
        // Create dialog
        Dialog<ExamType> dialog = new Dialog<>();
        dialog.setTitle(examType == null ? "Thêm loại thi mới" : "Sửa loại thi");
        dialog.setHeaderText(examType == null ? "Nhập thông tin loại thi mới" : "Cập nhật thông tin loại thi");
        
        // Set dialog modality
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(tableView.getScene().getWindow());

        // Create form
        VBox form = new VBox(15);
        form.setPadding(new javafx.geometry.Insets(20));

        // Code field
        Label lblCode = new Label("Mã loại thi:");
        TextField txtCode = new TextField();
        txtCode.setPromptText("Nhập mã loại thi (VD: A1, B2)");
        if (examType != null) {
            txtCode.setText(examType.getCode());
            txtCode.setDisable(true); // Cannot change code when editing
        }

        // Name field
        Label lblName = new Label("Tên loại thi:");
        TextField txtName = new TextField();
        txtName.setPromptText("Nhập tên loại thi");
        if (examType != null) {
            txtName.setText(examType.getName());
        }

        // Description field
        Label lblDescription = new Label("Mô tả:");
        TextArea txtDescription = new TextArea();
        txtDescription.setPromptText("Nhập mô tả loại thi");
        txtDescription.setPrefRowCount(3);
        if (examType != null) {
            txtDescription.setText(examType.getDescription());
        }

        // Duration field
        Label lblDuration = new Label("Thời gian thi (phút):");
        TextField txtDuration = new TextField();
        txtDuration.setPromptText("Nhập thời gian thi");
        if (examType != null) {
            txtDuration.setText(String.valueOf(examType.getDuration()));
        }

        // Passing score field
        Label lblPassingScore = new Label("Điểm đậu:");
        TextField txtPassingScore = new TextField();
        txtPassingScore.setPromptText("Nhập điểm đậu (0-100)");
        if (examType != null) {
            txtPassingScore.setText(String.valueOf(examType.getPassingScore()));
        }

        // Fee field
        Label lblFee = new Label("Phí thi (VNĐ):");
        TextField txtFee = new TextField();
        txtFee.setPromptText("Nhập phí thi");
        if (examType != null) {
            txtFee.setText(String.valueOf(examType.getFee()));
        }

        // Add fields to form
        form.getChildren().addAll(
            lblCode, txtCode,
            lblName, txtName,
            lblDescription, txtDescription,
            lblDuration, txtDuration,
            lblPassingScore, txtPassingScore,
            lblFee, txtFee
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
                if (txtCode.getText().trim().isEmpty()) {
                    showError("Lỗi", "Mã loại thi không được để trống!");
                    return null;
                }
                if (txtName.getText().trim().isEmpty()) {
                    showError("Lỗi", "Tên loại thi không được để trống!");
                    return null;
                }
                if (txtDuration.getText().trim().isEmpty()) {
                    showError("Lỗi", "Thời gian thi không được để trống!");
                    return null;
                }
                if (txtPassingScore.getText().trim().isEmpty()) {
                    showError("Lỗi", "Điểm đậu không được để trống!");
                    return null;
                }
                if (txtFee.getText().trim().isEmpty()) {
                    showError("Lỗi", "Phí thi không được để trống!");
                    return null;
                }

                // Validate numeric fields
                try {
                    int duration = Integer.parseInt(txtDuration.getText().trim());
                    if (!ValidationHelper.isValidExamDuration(duration)) {
                        showError("Lỗi", "Thời gian thi phải từ 1-480 phút!");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    showError("Lỗi", "Thời gian thi phải là số!");
                    return null;
                }

                try {
                    int passingScore = Integer.parseInt(txtPassingScore.getText().trim());
                    if (!ValidationHelper.isValidPassingScore(passingScore)) {
                        showError("Lỗi", "Điểm đậu phải từ 0-100!");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    showError("Lỗi", "Điểm đậu phải là số!");
                    return null;
                }

                try {
                    double fee = Double.parseDouble(txtFee.getText().trim());
                    if (!ValidationHelper.isValidExamFee(fee)) {
                        showError("Lỗi", "Phí thi phải lớn hơn 0!");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    showError("Lỗi", "Phí thi phải là số!");
                    return null;
                }

                // Check code uniqueness for new exam types
                if (examType == null) {
                    List<ExamType> existingTypes = examTypeDAO.getAll();
                    boolean codeExists = existingTypes.stream()
                        .anyMatch(type -> type.getCode().equalsIgnoreCase(txtCode.getText().trim()));
                    if (codeExists) {
                        showError("Lỗi", "Mã loại thi đã tồn tại!");
                        return null;
                    }
                }

                // Create or update exam type
                try {
                    if (examType == null) {
                        // Create new exam type
                        ExamType newExamType = new ExamType();
                        newExamType.setCode(txtCode.getText().trim().toUpperCase());
                        newExamType.setName(txtName.getText().trim());
                        newExamType.setDescription(txtDescription.getText().trim());
                        newExamType.setDuration(Integer.parseInt(txtDuration.getText().trim()));
                        newExamType.setPassingScore(Integer.parseInt(txtPassingScore.getText().trim()));
                        newExamType.setFee(Double.parseDouble(txtFee.getText().trim()));
                        
                        examTypeDAO.addExamType(newExamType);
                        showInfo("Thành công", "Đã thêm loại thi mới thành công!");
                    } else {
                        // Update existing exam type
                        examType.setName(txtName.getText().trim());
                        examType.setDescription(txtDescription.getText().trim());
                        examType.setDuration(Integer.parseInt(txtDuration.getText().trim()));
                        examType.setPassingScore(Integer.parseInt(txtPassingScore.getText().trim()));
                        examType.setFee(Double.parseDouble(txtFee.getText().trim()));
                        
                        examTypeDAO.updateExamType(examType);
                        showInfo("Thành công", "Đã cập nhật loại thi thành công!");
                    }
                    
                    loadExamTypes();
                    return examType;
                } catch (Exception e) {
                    showError("Lỗi", "Không thể lưu loại thi: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        // Show dialog
        dialog.showAndWait();
    }

    @Override
    protected void clearForm() {
        txtSearch.clear();
        selectedExamType = null;
    }

    @Override
    protected void setFormEnabled(boolean enabled) {
        btnAdd.setDisable(!enabled);
        btnRefresh.setDisable(!enabled);
        txtSearch.setDisable(!enabled);
    }
}

