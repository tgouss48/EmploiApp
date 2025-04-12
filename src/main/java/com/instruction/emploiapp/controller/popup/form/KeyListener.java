package com.instruction.emploiapp.controller.popup.form;

import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

public class KeyListener {
    public static void keyListener(VBox root, Runnable enterAction, Runnable escAction) {
        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                enterAction.run();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                escAction.run();
                event.consume();
            }
        });
    }
}
