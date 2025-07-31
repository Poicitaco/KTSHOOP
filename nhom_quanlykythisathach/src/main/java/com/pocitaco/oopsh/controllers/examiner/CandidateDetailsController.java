package com.pocitaco.oopsh.controllers.examiner;

import com.pocitaco.oopsh.controllers.BaseController;
import com.pocitaco.oopsh.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;

public class CandidateDetailsController extends BaseController {

    @FXML
    private Label lblFullName;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblCccd;

    @FXML
    private Label lblBirthday;

    @FXML
    private Label lblPhone;

    @FXML
    private Label lblAddress;

    private User currentCandidate;

    @Override
    public void initializeComponents() {
        // No specific initialization needed here
    }

    public void setCandidate(User candidate) {
        this.currentCandidate = candidate;
        loadCandidateDetails();
    }

    private void loadCandidateDetails() {
        if (currentCandidate != null) {
            lblFullName.setText(currentCandidate.getFullName());
            lblEmail.setText(currentCandidate.getEmail());
            lblCccd.setText(currentCandidate.getCccd());
            lblBirthday.setText(currentCandidate.getBirthday() != null ? currentCandidate.getBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A");
            lblPhone.setText(currentCandidate.getPhone());
            lblAddress.setText(currentCandidate.getAddress());
        }
    }

    @Override
    protected void setupEventHandlers() {

    }

    @Override
    protected void loadInitialData() {

    }

    @Override
    protected void clearForm() {

    }

    @Override
    protected void setFormEnabled(boolean b) {

    }
}

