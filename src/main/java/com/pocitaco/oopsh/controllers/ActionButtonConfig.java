package com.pocitaco.oopsh.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Configuration for action buttons
 */
public class ActionButtonConfig {
    private String text;
    private EventHandler<ActionEvent> action;
    private String styleClass;

    public ActionButtonConfig(String text, EventHandler<ActionEvent> action) {
        this.text = text;
        this.action = action;
        this.styleClass = "btn-primary";
    }

    public ActionButtonConfig(String text, EventHandler<ActionEvent> action, String styleClass) {
        this.text = text;
        this.action = action;
        this.styleClass = styleClass;
    }

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public EventHandler<ActionEvent> getAction() {
        return action;
    }

    public void setAction(EventHandler<ActionEvent> action) {
        this.action = action;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
}

