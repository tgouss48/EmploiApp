package com.instruction.emploiapp.controller;

import com.instruction.emploiapp.controller.popup.form.CustomContextMenu;
import com.instruction.emploiapp.controller.popup.form.KeyListener;
import com.instruction.emploiapp.controller.popup.toast.ToastController;
import com.instruction.emploiapp.controller.popup.toast.ToastType;
import com.instruction.emploiapp.model.Parametre;
import com.instruction.emploiapp.serialisation.SerialisationManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@SuppressWarnings("DuplicatedCode")
public class ParametreController {

    @FXML private TextField nbCompagniesField;
    @FXML private TextField indispoField;
    @FXML private ComboBox<String> periode;
    @FXML private Label errorLabel;
    @FXML private VBox root;

    private Parametre parametre;

    @FXML
    public void initialize() {
        parametre = SerialisationManager.charger("parametres.ser");
        if (parametre == null) {
            parametre = new Parametre(14, 2, "FCB");
        }
        nbCompagniesField.setText(String.valueOf(parametre.getNbCompagnies()));
        indispoField.setText(String.valueOf(parametre.getNbCompagniesNonDisponibles()));
        periode.setValue(parametre.getPeriode());

        addValidationListeners();

        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sauvegarderParametres();
            }
        });

        CustomContextMenu.attachCustomContextMenu(nbCompagniesField);
        CustomContextMenu.attachCustomContextMenu(indispoField);
    }

    @FXML
    public void sauvegarderParametres() {

        clearErrorStyles();
        if (validateFields()){
            errorLabel.setText("Veuillez remplir tous les champs");
            errorLabel.setVisible(true);
            return;
        }

        int nbCompagnies = Integer.parseInt(nbCompagniesField.getText());
        int nbCompagniesNonDisponibles = Integer.parseInt(indispoField.getText());
        String selectedPeriode = periode.getValue();

        parametre = new Parametre(nbCompagnies, nbCompagniesNonDisponibles, selectedPeriode);
        SerialisationManager.sauvegarder(parametre, "parametres.ser");
        errorLabel.setVisible(false);

        ToastController.showToast((Stage) indispoField.getScene().getWindow(), ToastType.SUCCESS,
                "Opération réussie", "Les paramètres ont été bien sauvegardés", 5000);
    }

    private boolean validateFields() {
        boolean hasError = false;
        if (nbCompagniesField.getText().trim().isEmpty()) {
            nbCompagniesField.getStyleClass().add("error-field");
            hasError = true;
        }
        if (indispoField.getText().trim().isEmpty()) {
            indispoField.getStyleClass().add("error-field");
            hasError = true;
        }
        if (periode.getSelectionModel().isEmpty()) {
            periode.getStyleClass().add("error-field");
            hasError = true;
        }
        return hasError;
    }

    private void addValidationListeners() {
        nbCompagniesField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                nbCompagniesField.getStyleClass().remove("error-field");
            }
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                nbCompagniesField.setText(oldVal);
            }
        });
        indispoField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                indispoField.getStyleClass().remove("error-field");
            }
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                indispoField.setText(oldVal);
            }
        });
        periode.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                periode.getStyleClass().remove("error-field");
            }
        });
    }

    private void clearErrorStyles() {
        nbCompagniesField.getStyleClass().remove("error-field");
        indispoField.getStyleClass().remove("error-field");
        periode.getStyleClass().remove("error-field");
    }
}