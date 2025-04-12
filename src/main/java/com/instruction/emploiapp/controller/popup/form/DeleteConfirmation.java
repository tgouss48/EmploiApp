package com.instruction.emploiapp.controller.popup.form;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class DeleteConfirmation extends Stage {

    @FXML private VBox window;
    @FXML private Label messageLabel;
    @FXML private Button ouiButton;
    @FXML private Button nonButton;

    private boolean confirmed = false;

    public DeleteConfirmation(Stage owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/instruction/emploiapp/views/popup/DeleteConfirmation.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        initStyle(StageStyle.UNDECORATED);
        Node reference = owner.getScene().lookup("#pageCenter");
        CreateScene.dialog(owner,this,window,reference);

        Scene scene = this.getScene();
        scene.getStylesheets().add(getClass().getResource("/com/instruction/emploiapp/styles/delete.css").toExternalForm());
    }

    @FXML
    private void initialize() {
        ouiButton.setOnAction(event -> {
            confirmed = true;
            close();
        });

        nonButton.setOnAction(event -> {
            confirmed = false;
            close();
        });

        KeyListener.keyListener(window, () -> {
                confirmed = true;
                close();
            }, () -> {
                confirmed = false;
                close();
        });
    }

    public boolean showDialog() {
        showAndWait();
        return confirmed;
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}