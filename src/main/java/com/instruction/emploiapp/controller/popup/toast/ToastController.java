package com.instruction.emploiapp.controller.popup.toast;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class ToastController {

    @FXML
    private HBox root;
    @FXML
    private ImageView iconImage;
    @FXML
    private Label titleLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private Button closeBtn;

    private Popup popup;

    public static void showToast(Stage owner, ToastType type, String title, String message, int durationMs) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ToastController.class.getResource("/com/instruction/emploiapp/views/popup/ToastView.fxml")
            );
            Parent root = loader.load();
            ToastController controller = loader.getController();
            controller.setup(type, title, message, durationMs);

            Popup popup = new Popup();
            popup.getContent().add(root);
            controller.popup = popup;

            popup.show(owner);

            double x = owner.getX() + owner.getWidth() - root.prefWidth(-1) - 15;
            double y = owner.getY() + owner.getHeight() - root.prefHeight(-1) - 15;
            popup.setX(x);
            popup.setY(y);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        closeBtn.setOnAction(e -> {
            if (popup != null) {
                popup.hide();
            }
        });
    }

    private void setup(ToastType type, String title, String message, int durationMs) {
        titleLabel.setText(title);
        messageLabel.setText(message);

        switch (type) {
            case SUCCESS:
                root.setStyle("-fx-background-color: #D4EDDA;");
                titleLabel.setStyle("-fx-text-fill: #4caa7b;");
                iconImage.setImage(new Image(getClass().getResourceAsStream("/com/instruction/emploiapp/images/success.png")));
                break;
            case INFO:
                root.setStyle("-fx-background-color: #D1ECF1;");
                titleLabel.setStyle("-fx-text-fill: #169df7;");
                iconImage.setImage(new Image(getClass().getResourceAsStream("/com/instruction/emploiapp/images/info.png")));
                break;
            case ERROR:
                root.setStyle("-fx-background-color: #F8D7DA;");
                titleLabel.setStyle("-fx-text-fill: #bf3e5a;");
                iconImage.setImage(new Image(getClass().getResourceAsStream("/com/instruction/emploiapp/images/error.png")));
                break;
        }

        if (durationMs > 0) {
            fadeOutAfter(durationMs);
        }
    }

    private void fadeOutAfter(int delayMs) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), root);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.millis(delayMs));
        fade.setOnFinished(e -> {
            if (popup != null) {
                popup.hide();
            }
        });
        fade.play();
    }
}
