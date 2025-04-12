package com.instruction.emploiapp.controller.popup.filter;

import com.instruction.emploiapp.controller.popup.form.CreateScene;
import com.instruction.emploiapp.controller.popup.form.KeyListener;
import com.instruction.emploiapp.controller.popup.toast.ToastController;
import com.instruction.emploiapp.controller.popup.toast.ToastType;
import com.instruction.emploiapp.model.FilterCondition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class FilterDialog extends Stage {

    @FXML private Button okButton,resetButton,addFilterButton,closeButton;
    @FXML private ComboBox<String> fieldCombo,operatorCombo;
    @FXML private TextField valueField;
    @FXML private FlowPane activeFiltersPane;
    @FXML private Label errorLabel;
    @FXML private VBox window;

    private final Stage owner;

    private final ObservableList<FilterCondition> filters = FXCollections.observableArrayList();

    private boolean confirmed = false;

    private ObservableList<String> fields = FXCollections.observableArrayList("Nom", "Grade", "Matière", "Compagnies(s)");
    private final ObservableList<String> operators = FXCollections.observableArrayList("Contient", "=", ">", "<");

    public FilterDialog(Stage owner) {
        this.owner = owner;
        this.initOwner(owner);
        initFXML();
    }

    public FilterDialog(Stage owner, ObservableList<String> fields) {
        this.owner = owner;
        this.fields = fields;
        initFXML();
    }

    private void initFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/instruction/emploiapp/views/popup/FilterDialog.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            Node reference = this.getOwner().getScene().lookup("#pageCenter");
            CreateScene.dialog((Stage) this.getOwner(), this, window, reference);

            KeyListener.keyListener(window, () -> addFilterButton.fire(), this::close);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        fieldCombo.setItems(fields);
        operatorCombo.setItems(operators);

        errorLabel.setVisible(false);

        closeButton.setOnAction(e -> close());

        okButton.setGraphic(new ImageView(new Image(
                getClass().getResourceAsStream("/com/instruction/emploiapp/images/done.png"), 16, 16, true, true
        )));
        resetButton.setGraphic(new ImageView(new Image(
                getClass().getResourceAsStream("/com/instruction/emploiapp/images/reset.png"), 16, 16, true, true
        )));
        addFilterButton.setGraphic(new ImageView(new Image(
                getClass().getResourceAsStream("/com/instruction/emploiapp/images/add.png"), 16, 16, true, true
        )));

        addFilterButton.setOnAction(e -> {
            boolean hasError = validateFields();

            if (hasError) {
                errorLabel.setText("  Tous les champs doivent être remplis.");
                errorLabel.setVisible(true);
                return;
            }
            errorLabel.setText("");
            errorLabel.setVisible(false);
            clearErrorStyles();

            String field = fieldCombo.getValue();
            String operator = operatorCombo.getValue();
            String value = valueField.getText();

            FilterCondition condition = new FilterCondition(field, operator, value);

            if (filters.stream().anyMatch(f -> f.equals(condition))) {
                ToastController.showToast((Stage) this.getOwner(), ToastType.INFO,
                        "Filtre existant","Le filtre que vous essayez d'ajouter est déja appliqué",5000);

                return;
            }

            filters.add(condition);

            HBox chip = createFilterChip(condition);
            activeFiltersPane.getChildren().add(chip);
            this.sizeToScene();

            clear();
        });
        addValidationListeners();

        okButton.setOnAction(e -> {
            confirmed = true;
            close();
        });

        resetButton.setOnAction(e -> {
            clear();

            filters.clear();
            activeFiltersPane.getChildren().clear();

            clearErrorStyles();
            errorLabel.setVisible(false);
            this.sizeToScene();
        });
    }

    private boolean validateFields() {
        boolean hasError = false;

        if (fieldCombo.getSelectionModel().isEmpty()) {
            fieldCombo.getStyleClass().add("error");
            hasError = true;
        }

        if (operatorCombo.getSelectionModel().isEmpty()) {
            operatorCombo.getStyleClass().add("error");
            hasError = true;
        }

        if (valueField.getText().trim().isEmpty()) {
            valueField.getStyleClass().add("error");
            hasError = true;
        }

        return hasError;
    }

    private void addValidationListeners() {
        fieldCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                fieldCombo.getStyleClass().remove("error");
            }
        });

        operatorCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                operatorCombo.getStyleClass().remove("error");
            }
        });

        valueField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                valueField.getStyleClass().remove("error");
            }
        });
    }


    private void clearErrorStyles() {
        fieldCombo.getStyleClass().remove("error");
        operatorCombo.getStyleClass().remove("error");
        valueField.getStyleClass().remove("error");
    }

    private void clear() {
        fieldCombo.getSelectionModel().clearSelection();
        operatorCombo.getSelectionModel().clearSelection();
        valueField.clear();
    }

    public void setExistingFilters(ObservableList<FilterCondition> existing) {
        filters.clear();
        activeFiltersPane.getChildren().clear();

        for (FilterCondition fc : existing) {
            filters.add(fc);
            activeFiltersPane.getChildren().add(createFilterChip(fc));
        }
    }

    private HBox createFilterChip(FilterCondition condition) {
        HBox chip = new HBox(5);
        chip.getStyleClass().add("chip");

        Label label = new Label(condition.toString());
        label.getStyleClass().add("chip-label");

        // Bouton "x" pour fermer le chip
        Button closeBtn = new Button("x");
        closeBtn.getStyleClass().add("chip-close-button");
        closeBtn.setOnAction(evt -> {
            filters.remove(condition);
            activeFiltersPane.getChildren().remove(chip);
            this.sizeToScene();
        });

        chip.getChildren().addAll(label, closeBtn);
        return chip;
    }

    public ObservableList<FilterCondition> showDialog(Stage owner) {
        initOwner(owner);
        showAndWait();
        return confirmed ? filters : null;
    }

    public void setFields(ObservableList<String> fields) {
        this.fields = fields;
        if (fieldCombo != null) {
            fieldCombo.setItems(fields);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public ObservableList<FilterCondition> getFilters() {
        return filters;
    }

}