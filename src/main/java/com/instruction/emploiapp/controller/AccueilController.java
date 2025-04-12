package com.instruction.emploiapp.controller;

import com.instruction.emploiapp.controller.popup.form.DeleteConfirmation;
import com.instruction.emploiapp.controller.popup.toast.ToastController;
import com.instruction.emploiapp.controller.popup.toast.ToastType;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

public class AccueilController {
    @FXML private StackPane root;
    @FXML private ImageView backgroundImage;

    private final String[] FCB = {
            "log_conflits.txt",
            "parametres.ser",
            "semaine.ser",
            "sauvegardeSem1.ser",
            "sauvegardeSem2.ser",
            "compteurReductions.ser",
            "DataBase.db",
    };

    private final String[] All = {
            "log_conflits.txt",
            "parametres.ser",
            "semaine.ser",
            "sauvegardeSem1.ser",
            "sauvegardeSem2.ser",
            "compteurReductions.ser",
            "DataBase.db",
            "samedi.ser",
            "compteurNormal.ser",
    };

    @FXML
    public void initialize() {
        addZoomEffect(backgroundImage);
    }

    public void ResetFCB(ActionEvent actionEvent) {
        functionSupp(FCB);
    }

    public void ResetAll(ActionEvent actionEvent) {
        functionSupp(All);
    }

    private void addZoomEffect(ImageView imageView) {
        ScaleTransition zoomIn = new ScaleTransition(Duration.millis(200), imageView);
        zoomIn.setToX(1.1);
        zoomIn.setToY(1.1);

        ScaleTransition zoomOut = new ScaleTransition(Duration.millis(200), imageView);
        zoomOut.setToX(1.0);
        zoomOut.setToY(1.0);

        imageView.setOnMouseEntered(e -> zoomIn.playFromStart());
        imageView.setOnMouseExited(e -> zoomOut.playFromStart());
    }

    private void functionSupp(String[] aSupprimer) {
        try {
            DeleteConfirmation confirmationPopup = new DeleteConfirmation((Stage) backgroundImage.getScene().getWindow());
            confirmationPopup.setMessage("Cette action supprimera toutes les données.\nVoulez-vous vraiment continuer ?");

            boolean confirmed = confirmationPopup.showDialog();

            if (confirmed) {
                boolean allDeleted = true;

                for (String nomFichier : aSupprimer) {
                    File fichier = new File(System.getProperty("user.dir"), nomFichier);
                    if (fichier.exists()) {
                        if (!fichier.delete()) {
                            allDeleted = false;
                        }
                    }
                }

                if (allDeleted) {
                    ToastController.showToast((Stage) backgroundImage.getScene().getWindow(), ToastType.SUCCESS,
                            "Opération réussie","L'application a été réinitialiser avec succès",5000);
                } else {
                    ToastController.showToast((Stage) backgroundImage.getScene().getWindow(), ToastType.ERROR,
                            "Opération échoué","Certains fichiers n'ont pas pu être supprimés",5000);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
