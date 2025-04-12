package com.instruction.emploiapp.controller.table;

import com.instruction.emploiapp.algo.excel.ExcelRowMapper;
import com.instruction.emploiapp.algo.excel.InstructeurRowMapper;
import com.instruction.emploiapp.controller.popup.filter.GenericFilter;
import com.instruction.emploiapp.db.GenericDAO;

import com.instruction.emploiapp.db.InstructeurDAO;
import com.instruction.emploiapp.model.Instructeur;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

public class InstructeurController extends GenericController<Instructeur> {
    @FXML private TableColumn<Instructeur, String> nomColumn;
    @FXML private TableColumn<Instructeur, String> gradeColumn;
    @FXML private TableColumn<Instructeur, String> matiereColumn;
    @FXML private TableColumn<Instructeur, String> idCieColumn;

    @Override
    public void initialize() throws SQLException, IOException {
        nomColumn.setCellValueFactory(cell -> cell.getValue().nomProperty());
        gradeColumn.setCellValueFactory(cell -> cell.getValue().gradeProperty());
        matiereColumn.setCellValueFactory(cell -> cell.getValue().matiereProperty());
        idCieColumn.setCellValueFactory(cell -> cell.getValue().idCieProperty());

        fields = FXCollections.observableArrayList("Nom", "Grade", "Matière", "Compagnies(s)");
        controller = "Instructeur";

        super.initialize();
    }

    @Override
    protected GenericFilter createFilter() {
        GenericFilter<Instructeur> filter = new GenericFilter<>(tableView, list);
        filter.registerField("Nom", Instructeur::getNom);
        filter.registerField("Grade", Instructeur::getGrade);
        filter.registerField("Matière", Instructeur::getMatiere);
        filter.registerField("Compagnies(s)", Instructeur::getIdCie);
        return filter;
    }

    @Override
    protected String getFormFxmlPath() {
        return "/com/instruction/emploiapp/views/popup/InstructeurForm.fxml";
    }

    @Override
    protected ExcelRowMapper getExcelRowMapper() {
        return new InstructeurRowMapper();
    }

    @Override
    protected GenericDAO getDAO() {
        return new InstructeurDAO();
    }
}