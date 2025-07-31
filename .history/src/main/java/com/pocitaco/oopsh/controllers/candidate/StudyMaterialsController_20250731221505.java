package com.pocitaco.oopsh.controllers.candidate;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.dao.StudyMaterialDAO;
import com.pocitaco.oopsh.models.StudyMaterial;
import io.github.palexdev.materialfx.controls.MFXListView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

public class StudyMaterialsController extends BaseController {

    @FXML
    private MFXListView<StudyMaterial> lstStudyMaterials;

    private StudyMaterialDAO studyMaterialDAO;

    @Override
    protected void initializeComponents() {
        this.studyMaterialDAO = new StudyMaterialDAO();
    }

    @Override
    protected void loadInitialData() {
        lstStudyMaterials.setItems(FXCollections.observableArrayList(studyMaterialDAO.findAll()));
    }

    @Override
    protected void setupEventHandlers() {
        // Add event handler for opening study material (e.g., on double click)
        lstStudyMaterials.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                StudyMaterial selectedMaterial = lstStudyMaterials.getSelectionModel().getSelectedValues().get(0);
                if (selectedMaterial != null) {
                    showInfo("Open Material", "Opening: " + selectedMaterial.getTitle());
                    // Logic to open the study material (e.g., a PDF viewer or web link)
                }
            }
        });
    }

    @Override
    protected void clearForm() {

    }

    @Override
    protected void setFormEnabled(boolean b) {

    }
}

