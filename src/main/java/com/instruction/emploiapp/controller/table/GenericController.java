package com.instruction.emploiapp.controller.table;

import com.instruction.emploiapp.algo.excel.ExcelRowMapper;
import com.instruction.emploiapp.algo.excel.ImportExcel;
import com.instruction.emploiapp.controller.popup.form.DeleteConfirmation;
import com.instruction.emploiapp.controller.popup.filter.FilterDialog;
import com.instruction.emploiapp.controller.popup.filter.GenericFilter;
import com.instruction.emploiapp.controller.popup.form.InstructeurFormController;
import com.instruction.emploiapp.controller.popup.form.MatiereFormController;
import com.instruction.emploiapp.controller.popup.toast.ToastController;
import com.instruction.emploiapp.controller.popup.toast.ToastType;
import com.instruction.emploiapp.db.GenericDAO;
import com.instruction.emploiapp.db.InstructeurDAO;
import com.instruction.emploiapp.db.MatiereDAO;
import com.instruction.emploiapp.model.FilterCondition;
import com.instruction.emploiapp.model.Instructeur;
import com.instruction.emploiapp.model.Matiere;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public abstract class GenericController<T> {
    @FXML protected TableView<T> tableView;
    @FXML protected TableColumn<T, Void> actionsColumn;
    @FXML protected Button btnAjouter, btnFilter, btnClearFilter, btnImport;
    @FXML protected Label lblCount;
    protected String controller;
    protected ObservableList<String> fields;

    protected final ObservableList<T> list = FXCollections.observableArrayList();
    protected final ObservableList<FilterCondition> lastFilters = FXCollections.observableArrayList();

    protected abstract GenericFilter<T> createFilter();
    protected abstract String getFormFxmlPath();
    protected abstract ExcelRowMapper<T> getExcelRowMapper();

    protected abstract GenericDAO<T> getDAO();

    @FXML
    public void initialize() throws IOException,SQLException {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        if (actionsColumn != null) {
            ajouterBoutonsActions();
        }

        loadData();

        GenericFilter<T> filter = createFilter();

        btnAjouter.setOnAction(e -> {
            try {
                handleAddEdit(null,controller);
            } catch (IOException ex) {
                ToastController.showToast((Stage) btnAjouter.getScene().getWindow(), ToastType.ERROR,
                        "Erreur", "Problème de Base de Donnée. Veuillez contacter le responsable", 5000);
            }
        });

        btnFilter.setOnAction(e -> handleFilter(filter));
        btnClearFilter.setOnAction(e -> handleClearFilter());
        btnImport.setOnAction(e -> handleImportExcel());
    }

    protected void handleClearFilter(){
        tableView.setItems(list);
        tableView.refresh();
        tableView.getSelectionModel().clearSelection();
        lastFilters.clear();
        updateCount();
    }

    protected void handleFilter(GenericFilter<T> filter) {
        FilterDialog dialog = new FilterDialog((Stage) tableView.getScene().getWindow());
        dialog.setFields(fields);
        dialog.initOwner(btnAjouter.getScene().getWindow());

        if (!lastFilters.isEmpty()) {
            dialog.setExistingFilters(lastFilters);
        }
        ObservableList<FilterCondition> chosenFilters = dialog.showDialog((Stage) btnFilter.getScene().getWindow());
        if (chosenFilters != null) {
            filter.applyFilters(chosenFilters);
            lastFilters.setAll(chosenFilters);
            tableView.getSelectionModel().clearSelection();
        }
        updateCount();
    }

    protected void ajouterBoutonsActions() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            final Button editBtn = new Button();
            final Button deleteBtn = new Button();
            final HBox box = new HBox(10, editBtn, deleteBtn);

            {
                box.setAlignment(Pos.CENTER);

                editBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/com/instruction/emploiapp/images/edit.png"), 12, 12, true, true)));
                deleteBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/com/instruction/emploiapp/images/delete.png"), 12, 12, true, true)));

                editBtn.setOnAction(e -> {
                    try {
                        handleAddEdit(tableView.getItems().get(getIndex()),controller);
                    } catch (IOException ex) {
                        ToastController.showToast((Stage) editBtn.getScene().getWindow(), ToastType.ERROR,
                                "Erreur", "Problème d'I/O. Veuillez contacter le responsable", 5000);
                    }
                });
                deleteBtn.setOnAction(e -> {
                    try {
                        handleDelete(tableView.getItems().get(getIndex()));
                    } catch (IOException ex) {
                        ToastController.showToast((Stage) editBtn.getScene().getWindow(), ToastType.ERROR,
                                "Erreur", "Problème d'I/O. Veuillez contacter le responsable", 5000);
                    }
                });

                editBtn.getStyleClass().add("icon-button-edit");
                deleteBtn.getStyleClass().add("icon-button-delete");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    protected void handleAddEdit(T item,String controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(getFormFxmlPath()));
        Parent root = loader.load();

        if (controller.equals("Instructeur")) {
            InstructeurFormController form = loader.getController();
            InstructeurDAO instructeurDAO = new InstructeurDAO();
            form.setData((Stage) tableView.getScene().getWindow(), instructeurDAO, (Instructeur) item);
            form.showForm();
            if (form.isSaved()) {
                if (item == null) {
                    list.add((T) form.getInstructeur());
                }
                tableView.refresh();
                updateCount();
            }
        } else if (controller.equals("Matiere")) {
            MatiereFormController form = loader.getController();
            MatiereDAO matiereDAO = new MatiereDAO();
            form.setData((Stage) tableView.getScene().getWindow(), matiereDAO, (Matiere) item);
            form.showForm();
            if (form.isSaved()) {
                if (item == null) {
                    list.add((T) form.getMatiere());
                }
                tableView.refresh();
                updateCount();
            }
        }
    }

    protected void handleDelete(T item) throws IOException {
        DeleteConfirmation dialog = new DeleteConfirmation((Stage) tableView.getScene().getWindow());
        dialog.setMessage("Vous êtes sur le point de supprimer cet élément.\nVoulez-vous continuer ?");

        boolean confirmed = dialog.showDialog();
        if (confirmed) {
            try {
                getDAO().delete(item);
                list.remove(item);
                updateCount();
                ToastController.showToast((Stage) tableView.getScene().getWindow(),ToastType.SUCCESS,
                        "Opération réussie","L'élément e été supprimé avec succès",5000);
            } catch (SQLException e) {
                ToastController.showToast((Stage) tableView.getScene().getWindow(), ToastType.ERROR,
                        "Erreur", "Problème de Base de Donnée. Veuillez contacter le responsable", 5000);
            }
        }
    }

    protected void handleImportExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner le fichier Excel");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers Excel", "*.xls", "*.xlsx")
        );
        File selectedFile = fileChooser.showOpenDialog(btnImport.getScene().getWindow());
        if (selectedFile != null) {
            String cheminDuExcel = selectedFile.getAbsolutePath();
            try {
                ExcelRowMapper<T> mapper = getExcelRowMapper();
                ImportExcel<T> importer = new ImportExcel<>(mapper);
                ObservableList<T> excelRows = importer.ReadExcel(cheminDuExcel);

                int insertedCount = 0;
                for (T item : excelRows) {
                    if (!getDAO().exists(item)) {
                        getDAO().insert(item);
                        insertedCount++;
                    }
                }

                if (insertedCount == 0) {
                    ToastController.showToast((Stage) btnImport.getScene().getWindow(), ToastType.INFO,
                            "Données déjà importées", "Toutes les lignes du fichier Excel existent déjà.", 5000);
                } else {
                    ToastController.showToast((Stage) btnImport.getScene().getWindow(), ToastType.SUCCESS,
                            "Importation terminée", insertedCount + " nouvelles lignes ont été importées.", 5000);
                    loadData();
                }
            } catch (SQLException e) {
                ToastController.showToast((Stage) btnImport.getScene().getWindow(), ToastType.ERROR,
                        "Erreur", "Problème de Base de Donnée. Veuillez contacter le responsable", 5000);
                System.out.println(e.getMessage());
            }
        }
    }

    protected void loadData() throws SQLException {
        list.setAll(getDAO().getAll());
        tableView.setItems(list);
        tableView.getSelectionModel().clearSelection();
        updateCount();
    }

    protected void updateCount() {
        lblCount.setText(tableView.getItems().size() + " éléments");
    }
}
