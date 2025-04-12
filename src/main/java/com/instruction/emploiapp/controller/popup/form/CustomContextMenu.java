package com.instruction.emploiapp.controller.popup.form;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.control.TextField;

public class CustomContextMenu {

    public static void attachCustomContextMenu(TextField textField) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem cut = new MenuItem("Couper", createIcon("/com/instruction/emploiapp/images/cut.png"));
        MenuItem copy = new MenuItem("Copier", createIcon("/com/instruction/emploiapp/images/copy.png"));
        MenuItem paste = new MenuItem("Coller", createIcon("/com/instruction/emploiapp/images/paste.png"));
        MenuItem delete = new MenuItem("Supprimer", createIcon("/com/instruction/emploiapp/images/supprimer.png"));
        MenuItem selectAll = new MenuItem("Sélectionner tout", createIcon("/com/instruction/emploiapp/images/select.png"));

        contextMenu.getItems().addAll(
                cut, copy, paste, delete,
                new SeparatorMenuItem(),
                selectAll
        );

        cut.setOnAction(e -> textField.cut());
        copy.setOnAction(e -> textField.copy());
        paste.setOnAction(e -> textField.paste());
        delete.setOnAction(e -> textField.deleteText(textField.getSelection()));
        selectAll.setOnAction(e -> textField.selectAll());

        contextMenu.setOnShowing(e -> {
            boolean noSelection = textField.getSelectedText().isEmpty();
            cut.setDisable(noSelection);
            copy.setDisable(noSelection);
            delete.setDisable(noSelection);

            boolean noClipboard = !Clipboard.getSystemClipboard().hasString();
            paste.setDisable(noClipboard);

        });

        textField.setContextMenu(contextMenu);
    }

    private static ImageView createIcon(String resourcePath) {
        Image image = new Image(CustomContextMenu.class.getResourceAsStream(resourcePath));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        imageView.setPreserveRatio(true);
        return imageView;
    }
}