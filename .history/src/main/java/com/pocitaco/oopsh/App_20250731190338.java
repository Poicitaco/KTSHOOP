package com.pocitaco.oopsh;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App - Driving License Examination System
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load login screen initially
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pocitaco/oopsh/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 600);

        // Apply Material Design CSS
        try {
            String materialCssPath = getClass().getResource("/com/pocitaco/oopsh/styles/material-design.css")
                    .toExternalForm();
            String loginCssPath = getClass().getResource("/com/pocitaco/oopsh/styles/login.css").toExternalForm();
            scene.getStylesheets().addAll(materialCssPath, loginCssPath);
        } catch (Exception e) {
            System.out.println("Warning: Could not load CSS file - " + e.getMessage());
        }

        primaryStage.setTitle("Hệ thống sát hạch bằng lái xe - Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}