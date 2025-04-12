package com.instruction.emploiapp.controller.popup.form;

import com.instruction.emploiapp.controller.popup.toast.ToastController;
import com.instruction.emploiapp.controller.popup.toast.ToastType;
import com.instruction.emploiapp.db.InstructeurDAO;
import com.instruction.emploiapp.db.MatiereDAO;
import com.instruction.emploiapp.model.Instructeur;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

@SuppressWarnings("DuplicatedCode")
public class InstructeurFormController {
    @FXML private VBox root;

    @FXML private TextField nomField, gradeField, idCieField;

    @FXML private ComboBox<String> matiereCombo;

    @FXML private Label errorLabel,headerTitle;

    @FXML private Button btnSave, btnCancel;

    private Stage dialogStage;
    private boolean saved = false;
    private boolean editMode = false;
    private Instructeur instructeur;
    private InstructeurDAO instructeurDAO;

    @FXML
    private void initialize() {
        btnSave.setOnAction(e -> handleSave());
        btnCancel.setOnAction(e -> {
            if (dialogStage != null) {
                dialogStage.close();
            }
        });

        errorLabel.setVisible(false);

        CustomContextMenu.attachCustomContextMenu(nomField);
        CustomContextMenu.attachCustomContextMenu(gradeField);
        CustomContextMenu.attachCustomContextMenu(idCieField);
    }

    public void setData(Stage owner, InstructeurDAO instructeurDAO, Instructeur instructeur) {
        this.instructeurDAO = instructeurDAO;
        if (instructeur == null) {
            this.editMode = false;
            this.instructeur = new Instructeur();

        } else {
            this.editMode = true;
            this.instructeur = instructeur;
        }

        this.dialogStage = new Stage();
        Node reference = owner.getScene().lookup("#pageCenter");
        CreateScene.dialog(owner,dialogStage,root,reference);

        KeyListener.keyListener(root, this::handleSave, () -> dialogStage.close());

        try {
            ObservableList<String> matieres = MatiereDAO.getDistinctMatiereNames();
            matiereCombo.setItems(matieres);

            matiereCombo.setCellFactory(listView -> new ListCell<>() {
                private final Label label = new Label();

                {
                    label.setStyle("-fx-text-fill: #454444;");
                    label.setPrefWidth(265);
                    label.setWrapText(true);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        label.setText(item);
                        label.setMaxWidth(300);
                        setGraphic(label);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (editMode) {
            if (this.instructeur.getNom() != null) {
                nomField.setText(this.instructeur.getNom());
            }
            if (this.instructeur.getGrade() != null) {
                gradeField.setText(this.instructeur.getGrade());
            }
            if (this.instructeur.getMatiere() != null) {
                matiereCombo.getSelectionModel().select(this.instructeur.getMatiere());
            }
            if (this.instructeur.getIdCie() != null) {
                idCieField.setText(this.instructeur.getIdCie());
            }
        }

        addValidationListeners();
    }

    public void showForm() {
        if (dialogStage != null) {
            if(editMode)
                headerTitle.setText("Modifier");
            else
                headerTitle.setText("Ajouter");
            dialogStage.showAndWait();
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Instructeur getInstructeur() {
        return saved ? instructeur : null;
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

        instructeur.setNom(nomField.getText());
        instructeur.setGrade(gradeField.getText());
        instructeur.setMatiere(matiereCombo.getSelectionModel().getSelectedItem());
        instructeur.setIdCie(idCieField.getText());

        try {
            if (!editMode) {
                if(!instructeurDAO.exists(instructeur)) {
                    instructeurDAO.insert(instructeur);
                    saved = true;
                    ToastController.showToast((Stage) dialogStage.getOwner(),ToastType.SUCCESS,
                            "Opération réussie","L'élément e été ajouté avec succès",5000);
                }
                else
                    ToastController.showToast((Stage) dialogStage.getOwner(),ToastType.INFO,
                            "Elément existant","L'élément que vous essayez d'ajouter existe déjà dans la base de données",5000);
            } else {
                instructeurDAO.update(instructeur);
                saved = true;
                ToastController.showToast((Stage) dialogStage.getOwner(),ToastType.SUCCESS,
                        "Opération réussie","L'élément e été mis à jour avec succès",5000);
            }

            dialogStage.close();
        } catch (SQLException e) {
            ToastController.showToast((Stage) dialogStage.getOwner(), ToastType.ERROR,
                    "Erreur","Problème de Base de Donnée. Veuillez contacter le responsable",5000);
        }
    }

    private boolean validateFields() {
        boolean hasError = false;
        if (nomField.getText().trim().isEmpty()) {
            nomField.getStyleClass().add("error-field");
            hasError = true;
        }
        if (gradeField.getText().trim().isEmpty()) {
            gradeField.getStyleClass().add("error-field");
            hasError = true;
        }
        if (matiereCombo.getSelectionModel().isEmpty()) {
            matiereCombo.getStyleClass().add("error-field");
            hasError = true;
        }
        if (idCieField.getText().trim().isEmpty()) {
            idCieField.getStyleClass().add("error-field");
            hasError = true;
        }
        return hasError;
    }

    private void addValidationListeners() {
        nomField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                nomField.getStyleClass().remove("error-field");
            }
        });
        gradeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                gradeField.getStyleClass().remove("error-field");
            }
        });
        idCieField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                idCieField.getStyleClass().remove("error-field");
            }
        });
        matiereCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                matiereCombo.getStyleClass().remove("error-field");
            }
        });
    }

    private void clearErrorStyles() {
        nomField.getStyleClass().remove("error-field");
        gradeField.getStyleClass().remove("error-field");
        matiereCombo.getStyleClass().remove("error-field");
        idCieField.getStyleClass().remove("error-field");
    }
}