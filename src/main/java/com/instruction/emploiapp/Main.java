package com.instruction.emploiapp;

import com.instruction.emploiapp.controller.popup.form.MoveWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/instruction/emploiapp/views/SideBar.fxml"));
        Parent root = fxmlLoader.load();
        MoveWindow.makeStageDraggable(stage, root);
        Scene scene = new Scene(root, 1200, 650);
        Image icon = new Image(Main.class.getResourceAsStream("/com/instruction/emploiapp/images/logo.png"));
        stage.getIcons().add(icon);
        stage.setTitle("Générateur");
        scene.getStylesheets().add(getClass().getResource("/com/instruction/emploiapp/styles/context-menu.css").toExternalForm());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
         launch();
    }
}