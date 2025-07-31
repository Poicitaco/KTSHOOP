package com.pocitaco.oopsh.controllers;

import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * Utility class for creating MFXTableColumn objects
 * Helps work around issues with type inference
 */
@SuppressWarnings("unused")
public class TableColumnFactory {

    /**
     * Creates a table column with proper type inference
     * 
     * @param <T>           The entity type for the table
     * @param <V>           The value type for the cell
     * @param title         The column title
     * @param sortable      Whether the column is sortable
     * @param valueFunction The function to extract value from entity
     * @return A properly configured MFXTableColumn
     */
    public static <T, V> MFXTableColumn<T> createColumn(String title, boolean sortable, Function<T, V> valueFunction) {
        MFXTableColumn<T> column = new MFXTableColumn<>(title);

        MFXTableRowCell<T, V> cell = new MFXTableRowCell<>(valueFunction);
        column.setRowCellFactory(p -> cell);

        return column;
    }
}
