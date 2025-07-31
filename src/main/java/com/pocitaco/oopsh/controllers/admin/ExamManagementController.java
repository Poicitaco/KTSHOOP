package com.pocitaco.oopsh.controllers.admin;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.ExamTypeDAO;
import com.pocitaco.oopsh.models.ExamType;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;

public class ExamManagementController extends BaseController {

    @FXML
    private MFXTextField txtSearch;

    @FXML
    private MFXButton btnSearch;

    @FXML
    private MFXButton btnAddExam;

    @FXML
    private MFXTableView<ExamType> tblExams;

    private ExamTypeDAO examTypeDAO;

    @Override
    public void initializeComponents() {
        this.examTypeDAO = new ExamTypeDAO();
        setupTable();
    }

    @Override
    protected void setupEventHandlers() {
        btnAddExam.setOnAction(event -> handleAddExam());
        btnSearch.setOnAction(event -> handleSearch());
    }

    @Override
    protected void loadInitialData() {
        tblExams.setItems(FXCollections.observableArrayList(examTypeDAO.getAll()));
    }

    private void setupTable() {
        io.github.palexdev.materialfx.controls.MFXTableColumn<ExamType> codeColumn = new io.github.palexdev.materialfx.controls.MFXTableColumn<>("Code", true);
        codeColumn.setRowCellFactory(examType -> new MFXTableRowCell<>(ExamType::getCode));

        io.github.palexdev.materialfx.controls.MFXTableColumn<ExamType> nameColumn = new io.github.palexdev.materialfx.controls.MFXTableColumn<>("Name", true);
        nameColumn.setRowCellFactory(examType -> new MFXTableRowCell<>(ExamType::getName));

        io.github.palexdev.materialfx.controls.MFXTableColumn<ExamType> feeColumn = new io.github.palexdev.materialfx.controls.MFXTableColumn<>("Fee", true);
        feeColumn.setRowCellFactory(examType -> new MFXTableRowCell<>(ExamType::getFee));

        io.github.palexdev.materialfx.controls.MFXTableColumn<ExamType> statusColumn = new io.github.palexdev.materialfx.controls.MFXTableColumn<>("Status", true);
        statusColumn.setRowCellFactory(examType -> new MFXTableRowCell<>(ExamType::getStatus));

        tblExams.getTableColumns().addAll(
                codeColumn,
                nameColumn,
                feeColumn,
                statusColumn,
                createActionsColumn()
        );
    }

    private io.github.palexdev.materialfx.controls.MFXTableColumn<ExamType> createActionsColumn() {
        io.github.palexdev.materialfx.controls.MFXTableColumn<ExamType> actionsColumn = new io.github.palexdev.materialfx.controls.MFXTableColumn<>("Actions", false);
        actionsColumn.setRowCellFactory(examType -> {
            HBox buttons = new HBox(5);
            buttons.setAlignment(Pos.CENTER);
            Button btnEdit = new Button("Edit");
            btnEdit.getStyleClass().add("btn-secondary");
            btnEdit.setOnAction(event -> handleEditExam(examType));

            Button btnDelete = new Button("Delete");
            btnDelete.getStyleClass().add("btn-error");
            btnDelete.setOnAction(event -> handleDeleteExam(examType));

            buttons.getChildren().addAll(btnEdit, btnDelete);
            return new MFXTableRowCell<>(et -> "") {{ setGraphic(buttons); }};
        });

        return actionsColumn;
    }

    private void handleAddExam() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/admin/exam-create.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Create New Exam Type");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            // Refresh table after closing
            loadInitialData();
        } catch (IOException e) {
            showError("Error", "Could not load the create exam screen.");
            e.printStackTrace();
        }
    }

    private void handleEditExam(ExamType examType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/admin/exam-edit.fxml"));
            Parent root = loader.load();

            ExamEditController controller = loader.getController();
            controller.setExamTypeToEdit(examType);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Exam Type");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            // Refresh table after closing
            loadInitialData();
        } catch (IOException e) {
            showError("Error", "Could not load the edit exam screen.");
            e.printStackTrace();
        }
    }

    private void handleDeleteExam(ExamType examType) {
        if (showConfirmation("Delete Exam", "Are you sure you want to delete " + examType.getName() + "?")) {
            examTypeDAO.deleteById(examType.getId());
            loadInitialData();
            showInfo("Exam Deleted", examType.getName() + " has been deleted.");
        }
    }

    private void handleSearch() {
        String searchTerm = txtSearch.getText().toLowerCase();
        tblExams.setItems(FXCollections.observableArrayList(examTypeDAO.getAll().stream()
                .filter(exam -> exam.getName().toLowerCase().contains(searchTerm) ||
                               exam.getCode().toLowerCase().contains(searchTerm))
                .toList()));
    }

    @Override
    protected void clearForm() {

    }

    @Override
    protected void setFormEnabled(boolean b) {

    }
}

