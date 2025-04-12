package com.instruction.emploiapp.algo.excel;

import com.instruction.emploiapp.model.Matiere;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class MatiereRowMapper implements ExcelRowMapper<Matiere> {
    @Override
    public Matiere mapRow(Row row) {
        String nom = getCellValueAsString(row.getCell(0));
        double volumeHoraire = 0.0;
        Cell cell = row.getCell(1);
        if(cell != null) {
            if(cell.getCellType() == CellType.NUMERIC) {
                volumeHoraire = cell.getNumericCellValue();
            } else {
                String str = getCellValueAsString(cell);
                try {
                    volumeHoraire = Double.parseDouble(str);
                } catch(NumberFormatException e) {
                    volumeHoraire = 0.0;
                }
            }
        }
        return new Matiere(nom, (float) volumeHoraire);
    }
}
