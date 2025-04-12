package com.instruction.emploiapp.controller.popup.form;

import com.instruction.emploiapp.controller.popup.toast.ToastController;
import com.instruction.emploiapp.controller.popup.toast.ToastType;
import com.instruction.emploiapp.db.MatiereDAO;
import com.instruction.emploiapp.model.Matiere;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

@SuppressWarnings("DuplicatedCode")
public class MatiereFormController {

    @FXML private VBox root;

    @FXML private Label headerTitle;

    @FXML private TextField matiereField, VolumeField;

    @FXML private Label errorLabel;

    @FXML private Button btnSave, btnCancel;

    private Stage dialogStage;
    private boolean saved = false;
    private boolean editMode = false;
    private Matiere matiere;
    private MatiereDAO matiereDAO;

    @FXML
    private void initialize() {
        btnSave.setOnAction(e -> handleSave());
        btnCancel.setOnAction(e -> {
            if (dialogStage != null) {
                dialogStage.close();
            }
        });
        errorLabel.setVisible(false);

        CustomContextMenu.attachCustomContextMenu(matiereField);
        CustomContextMenu.attachCustomContextMenu(VolumeField);
    }

    public void setData(Stage owner, MatiereDAO matiereDAO, Matiere matiere) {
        this.matiereDAO = matiereDAO;
        if (matiere == null) {
            this.editMode = false;
            this.matiere = new Matiere();
        } else {
            this.editMode = true;
            this.matiere = matiere;
        }

        this.dialogStage = new Stage();
        Node reference = owner.getScene().lookup("#pageCenter");
        CreateScene.dialog(owner,dialogStage,root,reference);

        KeyListener.keyListener(root, this::handleSave, () -> dialogStage.close());

        if (editMode) {
            if (this.matiere.getNom() != null) {
                matiereField.setText(this.matiere.getNom());
            }
            VolumeField.setText(String.valueOf(this.matiere.getVolumeHoraire()));
        }

        addValidationListeners();
    }

    public void showForm() {
        if (dialogStage != null) {
            headerTitle.setText(editMode ? "Modifier" : "Ajouter");
            dialogStage.showAndWait();
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Matiere getMatiere() {
        return saved ? matiere : null;
    }

    private void handleSave() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        clearErrorStyles();

        if (validateFields()) {
            errorLabel.setText("Tous les champs doivent être remplis");
            errorLabel.setVisible(true);
            return;
        }

        String temp = matiere.getNom();

        matiere.setNom(matiereField.getText());
        matiere.setVolumeHoraire(Float.parseFloat(VolumeField.getText().trim()));

        try {
            if (!editMode) {
                if (!matiereDAO.exists(matiere)) {
                    matiereDAO.insert(matiere);
                    saved = true;
                    ToastController.showToast((Stage) dialogStage.getOwner(), ToastType.SUCCESS,
                            "Opération réussie", "L'élément a été ajouté avec succès", 5000);
                } else {
                    ToastController.showToast((Stage) dialogStage.getOwner(), ToastType.INFO,
                            "Elément existant", "L'élément existe déjà dans la base de données", 5000);
                }
            } else {
                matiereDAO.update(matiere,temp);
                saved = true;
                ToastController.showToast((Stage) dialogStage.getOwner(), ToastType.SUCCESS,
                        "Opération réussie", "L'élément a été mis à jour avec succès", 5000);
            }
            dialogStage.close();
        } catch (SQLException e) {
            ToastController.showToast((Stage) dialogStage.getOwner(), ToastType.ERROR,
                    "Erreur", "Problème de Base de Donnée. Veuillez contacter le responsable", 5000);
        }
    }

    private boolean validateFields() {
        boolean hasError = false;
        if (matiereField.getText().trim().isEmpty()) {
            matiereField.getStyleClass().add("error-field");
            hasError = true;
        }
        if (VolumeField.getText().trim().isEmpty()) {
            VolumeField.getStyleClass().add("error-field");
            hasError = true;
        }
        return hasError;
    }

    private void addValidationListeners() {
        matiereField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                matiereField.getStyleClass().remove("error-field");
            }
        });
        VolumeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                VolumeField.getStyleClass().remove("error-field");
            }
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                VolumeField.setText(oldVal);
            }
        });
    }

    private void clearErrorStyles() {
        matiereField.getStyleClass().remove("error-field");
        VolumeField.getStyleClass().remove("error-field");
    }
}