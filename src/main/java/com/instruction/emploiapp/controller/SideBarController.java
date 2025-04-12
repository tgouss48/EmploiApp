package com.instruction.emploiapp.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SideBarController {

    @FXML private AnchorPane contentPane;
    @FXML private VBox sideMenu,pageCenter;
    @FXML private Button toggleButton, home, settings, subject, teacher, suivi, generateur, closeButton, minimizeButton;
    @FXML private HBox toggleBox;

    private boolean menuVisible = true;
    private Button activeButton;

    @FXML
    public void initialize() {
        loadHome();
        activeButton = home;
        setActiveStyle(activeButton);
        addToolTipPopups();
    }

    private void addToolTipPopups() {

        for (Node node : sideMenu.getChildren()) {
            if (node instanceof Button btn && node.getStyleClass().contains("menu-button")) {

                Popup tooltip = new Popup();
                Label label = new Label(btn.getText());
                label.getStylesheets().add(getClass().getResource("/com/instruction/emploiapp/styles/SideBar.css").toExternalForm());
                label.getStyleClass().add("tooltip-custom");
                tooltip.getContent().add(label);

                btn.setOnMouseEntered(e -> {
                    if (sideMenu.getStyleClass().contains("collapsed")) {

                        Bounds bounds = btn.localToScreen(btn.getBoundsInLocal());

                        double labelHeight = label.prefHeight(-1);
                        double centerY = bounds.getMinY() + (bounds.getHeight() / 2) - (labelHeight / 2);

                        tooltip.show(btn, bounds.getMaxX() + 10, centerY);
                    }
                });

                btn.setOnMouseExited(e -> tooltip.hide());
            }
        }
    }

    @FXML
    public void toggleSidebar() {
        boolean collapsing = menuVisible;
        menuVisible = !menuVisible;

        double startWidth = collapsing ? 180 : 40;
        double endWidth = collapsing ? 40 : 180;

        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(sideMenu.prefWidthProperty(), endWidth);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue);
        timeline.getKeyFrames().add(keyFrame);

        timeline.setOnFinished(event -> {
            if (collapsing) {
                toggleButton.setText("☰");
                if (!sideMenu.getStyleClass().contains("collapsed")) {
                    sideMenu.getStyleClass().add("collapsed");
                }
                StackPane.setAlignment(toggleButton, Pos.CENTER);

            } else {
                toggleButton.setText("✖");
                sideMenu.getStyleClass().remove("collapsed");
                StackPane.setAlignment(toggleButton, Pos.TOP_RIGHT);
            }
        });

        timeline.play();
    }

    @FXML
    private void loadHome(){
        loadView("/com/instruction/emploiapp/views/accueil.fxml");
        setActiveButton(home);
    }

    @FXML
    private void loadParametres(){
        loadView("/com/instruction/emploiapp/views/parametre.fxml");
        setActiveButton(settings);
    }

    @FXML
    private void loadMatieres() {
        loadView("/com/instruction/emploiapp/views/table/matiere.fxml");
        setActiveButton(subject);
    }

    @FXML
    private void loadInstructeurs() {
        loadView("/com/instruction/emploiapp/views/table/instructeur.fxml");
        setActiveButton(teacher);
    }

    @FXML
    private void loadSuivi(){
        loadView("/com/instruction/emploiapp/views/table/suivi.fxml");
        setActiveButton(suivi);
    }

    @FXML
    private void loadEmploiDuTemps() {
        loadView("/com/instruction/emploiapp/views/emploi.fxml");
        setActiveButton(generateur);
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);

            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue : " + fxmlPath);
        }
    }

    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
        }
        activeButton = button;
        setActiveStyle(activeButton);
    }

    private void setActiveStyle(Button button) {
        if (!button.getStyleClass().contains("active")) {
            button.getStyleClass().add("active");
        }
    }

    @FXML
    private void minimizeWindow() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}