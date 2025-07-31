package com.pocitaco.oopsh;

import com.pocitaco.oopsh.ui.screens.LoginScreen;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App - Driving License Examination System
 * Material Design 3.0 Implementation
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Thiết lập cửa sổ chính
        primaryStage.setTitle("OOPSH - Hệ thống sát hạch bằng lái xe");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();

        // Khởi tạo và hiển thị màn hình đăng nhập Material Design
        LoginScreen loginScreen = new LoginScreen(primaryStage);
        loginScreen.show();

        System.out.println("🎨 OOPSH Material Design 3.0 Application đã khởi động!");
        System.out.println("📋 Backend hoàn thành 100% - UI Material Design mới!");
        System.out.println("� Sử dụng Ikonli Material Design Icons");
    }

    public static void main(String[] args) {
        launch(args);
    }
}