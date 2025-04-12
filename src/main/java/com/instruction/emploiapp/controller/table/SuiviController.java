package com.instruction.emploiapp.controller.table;

import com.instruction.emploiapp.algo.excel.ExcelRowMapper;
import com.instruction.emploiapp.controller.popup.filter.GenericFilter;
import com.instruction.emploiapp.db.GenericDAO;

import com.instruction.emploiapp.db.SuiviDAO;
import com.instruction.emploiapp.model.Suivi;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

public class SuiviController extends GenericController<Suivi> {
    @FXML private TableColumn<Suivi, Integer> idCieColumn;
    @FXML private TableColumn<Suivi, String> matiereColumn;
    @FXML private TableColumn<Suivi, Float> volumeHoraireColumn;

    @Override
    public void initialize() throws SQLException, IOException {
        idCieColumn.setCellValueFactory(cellData -> cellData.getValue().idCieProperty().asObject());
        matiereColumn.setCellValueFactory(cellData -> cellData.getValue().matiereProperty());
        volumeHoraireColumn.setCellValueFactory(cellData -> cellData.getValue().volumeHoraireProperty().asObject());

        fields = FXCollections.observableArrayList("Compagnie", "Matière", "Volume Horaire");

        btnAjouter.setVisible(false);
        btnImport.setVisible(false);

        super.initialize();
    }

    @Override
    protected GenericFilter createFilter() {
        GenericFilter<Suivi> filter = new GenericFilter<>(tableView, list);
        filter.registerField("Compagnie", Suivi::getIdCie);
        filter.registerField("Matière", Suivi::getMatiere);
        filter.registerField("Volume Horaire", Suivi::getVolumeHoraire);
        return filter;
    }

    @Override
    protected String getFormFxmlPath() {
        return "";
    }

    @Override
    protected ExcelRowMapper<Suivi> getExcelRowMapper() {
        return null;
    }

    @Override
    protected GenericDAO<Suivi> getDAO() {
        return new SuiviDAO();
    }
}