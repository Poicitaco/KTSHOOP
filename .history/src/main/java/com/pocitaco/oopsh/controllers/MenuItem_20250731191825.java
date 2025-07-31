package com.pocitaco.oopsh.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

import java.util.List;
import java.util.function.Supplier;

/**
 * Represents a menu item in the navigation
 */
public class MenuItem {
    private String title;
    private String category;
    private String fxmlPath;
    private String iconLiteral;
    private Supplier<Node> contentSupplier;
    private List<ActionButtonConfig> actionButtons;

    public MenuItem(String title, String category) {
        this.title = title;
        this.category = category;
    }

    public MenuItem(String title, String category, String fxmlPath) {
        this.title = title;
        this.category = category;
        this.fxmlPath = fxmlPath;
    }

    public MenuItem(String title, String category, String fxmlPath, String iconLiteral) {
        this.title = title;
        this.category = category;
        this.fxmlPath = fxmlPath;
        this.iconLiteral = iconLiteral;
    }

    public MenuItem(String title, String category, Supplier<Node> contentSupplier) {
        this.title = title;
        this.category = category;
        this.contentSupplier = contentSupplier;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public void setFxmlPath(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public Supplier<Node> getContentSupplier() {
        return contentSupplier;
    }

    public void setContentSupplier(Supplier<Node> contentSupplier) {
        this.contentSupplier = contentSupplier;
    }

    public List<ActionButtonConfig> getActionButtons() {
        return actionButtons;
    }

    public void setActionButtons(List<ActionButtonConfig> actionButtons) {
        this.actionButtons = actionButtons;
    }

    public String getIconLiteral() {
        return iconLiteral;
    }

    public void setIconLiteral(String iconLiteral) {
        this.iconLiteral = iconLiteral;
    }
}
