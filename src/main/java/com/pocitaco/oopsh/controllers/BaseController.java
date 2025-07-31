package com.pocitaco.oopsh.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Abstract base controller class providing common functionality for all
 * controllers
 * Implements Template Method pattern and common UI operations
 */
public abstract class BaseController implements Initializable {

    protected static final String SUCCESS_STYLE = "-fx-text-fill: #4CAF50;";
    protected static final String ERROR_STYLE = "-fx-text-fill: #f44336;";
    protected static final String WARNING_STYLE = "-fx-text-fill: #FF9800;";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComponents();
        setupEventHandlers();
        loadInitialData();
    }

    /**
     * Template method for initializing UI components
     * Subclasses should override this method to setup their specific components
     */
    protected abstract void initializeComponents();

    /**
     * Template method for setting up event handlers
     * Subclasses should override this method to bind their event handlers
     */
    protected abstract void setupEventHandlers();

    /**
     * Template method for loading initial data
     * Subclasses should override this method to load their required data
     */
    protected abstract void loadInitialData();

    /**
     * Common method to show information dialog
     */
    protected void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Common method to show error dialog
     */
    protected void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Common method to show confirmation dialog
     */
    protected boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Common method to validate required fields
     */
    protected boolean validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            showError("Validation Error", fieldName + " is required");
            return false;
        }
        return true;
    }

    /**
     * Common method to clear form fields
     * Subclasses should override this method to clear their specific fields
     */
    protected abstract void clearForm();

    /**
     * Common method to enable/disable form fields
     * Subclasses should override this method to handle their specific fields
     */
    protected abstract void setFormEnabled(boolean enabled);
}

