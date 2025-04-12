package com.instruction.emploiapp.algo.excel;


import com.instruction.emploiapp.model.Instructeur;
import org.apache.poi.ss.usermodel.Row;

import java.sql.SQLException;

public class InstructeurRowMapper implements ExcelRowMapper<Instructeur> {

    @Override
    public Instructeur mapRow(Row row) throws SQLException {
        String nom = getCellValueAsString(row.getCell(0));
        String grade = getCellValueAsString(row.getCell(1));
        String matiere = getCellValueAsString(row.getCell(2));
        String compagnies = getCellValueAsString(row.getCell(3));
        return new Instructeur(nom, grade, matiere, compagnies);
    }
}
