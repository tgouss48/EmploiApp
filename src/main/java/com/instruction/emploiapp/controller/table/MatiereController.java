package com.instruction.emploiapp.controller.table;

import com.instruction.emploiapp.algo.excel.ExcelRowMapper;
import com.instruction.emploiapp.algo.excel.MatiereRowMapper;
import com.instruction.emploiapp.controller.popup.filter.GenericFilter;
import com.instruction.emploiapp.db.GenericDAO;
import com.instruction.emploiapp.db.MatiereDAO;
import com.instruction.emploiapp.model.Matiere;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

public class MatiereController extends GenericController<Matiere> {
    @FXML private TableColumn<Matiere, String> matiereColumn;
    @FXML private TableColumn<Matiere, Float> volumeHoraireColumn;

    @Override
    public void initialize() throws SQLException, IOException {
        matiereColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        volumeHoraireColumn.setCellValueFactory(cellData -> cellData.getValue().volumeHoraireProperty().asObject());

        fields = FXCollections.observableArrayList("Matière", "Volume Horaire");
        controller = "Matiere";

        super.initialize();
    }

    @Override
    protected GenericFilter createFilter() {
        GenericFilter<Matiere> filter = new GenericFilter<>(tableView, list);
        filter.registerField("Matière", Matiere::getNom);
        filter.registerField("Volume Horaire", Matiere::getVolumeHoraire);
        return filter;
    }

    @Override
    protected String getFormFxmlPath() {
        return "/com/instruction/emploiapp/views/popup/MatiereForm.fxml";
    }

    @Override
    protected ExcelRowMapper getExcelRowMapper() {
        return new MatiereRowMapper();
    }

    @Override
    protected GenericDAO getDAO() {
        return new MatiereDAO();
    }
}