package com.pocitaco.oopsh.controllers;

import java.util.List;

/**
 * Represents a category of menu items
 */
public class MenuCategory {
    private String name;
    private List<MenuItem> items;

    public MenuCategory(String name, List<MenuItem> items) {
        this.name = name;
        this.items = items;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }
}

