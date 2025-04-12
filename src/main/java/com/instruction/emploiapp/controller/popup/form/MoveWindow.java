package com.instruction.emploiapp.controller.popup.form;

import javafx.scene.Node;
import javafx.stage.Stage;

public class MoveWindow {
    public static void makeStageDraggable(Stage stage, Node root) {
        final double[] offsetX = new double[1];
        final double[] offsetY = new double[1];

        root.setOnMousePressed(event -> {
            offsetX[0] = event.getSceneX();
            offsetY[0] = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - offsetX[0]);
            stage.setY(event.getScreenY() - offsetY[0]);
        });
    }
}