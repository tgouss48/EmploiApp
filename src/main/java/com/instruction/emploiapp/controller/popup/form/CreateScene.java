package com.instruction.emploiapp.controller.popup.form;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CreateScene {
    public static void dialog(Stage owner,Stage dialogStage, VBox root, Node reference){
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        dialogStage.setScene(scene);

        dialogStage.sizeToScene();

        Platform.runLater(() -> {
            Bounds bounds = reference.localToScreen(reference.getBoundsInLocal());
            double dialogWidth = dialogStage.getWidth();
            double dialogHeight = dialogStage.getHeight();

            double centerX = bounds.getMinX() + (bounds.getWidth() - dialogWidth) / 2;
            double centerY = bounds.getMinY() + (bounds.getHeight() - dialogHeight) / 2;

            dialogStage.setX(centerX);
            dialogStage.setY(centerY);
        });

        MoveWindow.makeStageDraggable(dialogStage, root);
    }
}
