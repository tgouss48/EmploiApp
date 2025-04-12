package com.instruction.emploiapp.controller;

import com.instruction.emploiapp.controller.popup.toast.ToastController;
import com.instruction.emploiapp.controller.popup.toast.ToastType;
import com.instruction.emploiapp.serialisation.SerialisationManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.instruction.emploiapp.algo.AlgoG;
import com.instruction.emploiapp.model.Emploi;

import static com.instruction.emploiapp.algo.Valeurs.periode;

@SuppressWarnings("BusyWait")
public class EmploiController {

    @FXML private ProgressBar progressBar;
    @FXML private Button btnSave;
    @FXML private Label progressLabel,errorLabel;
    private File sauvegardeDirectory;

    @FXML
    private void choisirChemin(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choisir un dossier pour sauvegarder");
        Stage stage = (Stage) progressBar.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            sauvegardeDirectory = selectedDirectory;

            btnSave.getStyleClass().remove("error-field");
            errorLabel.setText("Chemin sélectionné");
            errorLabel.getStyleClass().add("label-path");
            errorLabel.setVisible(true);

            ToastController.showToast((Stage) progressBar.getScene().getWindow(), ToastType.SUCCESS,
                    "Chemin sélectionné", "Vous pouvez maintenant générer votre emploi du temps.", 5000);
        }
    }

    @FXML
    private void generer(ActionEvent event) {
        if (sauvegardeDirectory == null) {
            Platform.runLater(() -> {
                errorLabel.setText("Veuillez sélectionner un dossier de sauvegarde");
                errorLabel.getStyleClass().add("label-error");
                btnSave.getStyleClass().add("error-field");
                errorLabel.setVisible(true);
            });
            return;
        }
        final String chemin = sauvegardeDirectory.getAbsolutePath();
        int semaine = SerialisationManager.charger("semaine.ser");

        // 14 pour FCB et 12 pour spécifique car on commence le compte des semaines par 0
        if ((periode.equals("FCB") && semaine > 14) || (periode.equals("Spécifique") && semaine > 12)) {
            ToastController.showToast((Stage) progressBar.getScene().getWindow(), ToastType.INFO,
                    "Information", "Vous avez déjà généré l'emploi du temps de toutes les semaines" +
                            "\nVeuillez réinitialiser l'application", 5000);
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                int sem = periode.equals("FCB") ? 15 : 13;

                for (int i = 1; i <= sem; i++) {
                    String sousChemin = chemin + File.separator + "Semaine_" + i;
                    File sousDossier = new File(sousChemin);
                    if (!sousDossier.exists()) {
                        if (!sousDossier.mkdirs()) {
                            ToastController.showToast((Stage) progressBar.getScene().getWindow(), ToastType.ERROR,
                                    "Erreur", "Échec lors de la création du dossier", 5000);
                            continue;
                        }
                    }

                    final double baseProgress = (i - 1) / (double) sem;
                    final double nextProgress = i / (double) sem;

                    AlgoG algoG = new AlgoG();
                    final Emploi[] emploiHolder = new Emploi[1];
                    Thread algoThread = new Thread(() -> {
                        try {
                            emploiHolder[0] = algoG.executer(sousChemin);
                        } catch (SQLException | IOException e) {
                            e.printStackTrace();
                        }
                    });
                    algoThread.start();

                    double progress = baseProgress;
                    while (algoThread.isAlive()) {
                        progress += 0.005;
                        if (progress > nextProgress) {
                            progress = nextProgress;
                        }

                        int percent = (int) (progress * 100);
                        Platform.runLater(() -> progressLabel.setText(percent + "%"));

                        updateProgress(progress, 1.0);
                        Thread.sleep(50);
                    }

                    int finalPercent = (int) (nextProgress * 100);
                    Platform.runLater(() -> progressLabel.setText(finalPercent + "%"));
                    updateProgress(nextProgress, 1.0);

                }

                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();

        task.setOnSucceeded(workerStateEvent -> Platform.runLater(() -> {
            errorLabel.setVisible(false);
            ToastController.showToast((Stage) progressBar.getScene().getWindow(), ToastType.SUCCESS,
                    "Opération effectuée", "Consultez le dossier choisi pour voir les emplois du temps.", 5000);
        }));
    }

    public File getSauvegardeDirectory() {
        return sauvegardeDirectory;
    }
}