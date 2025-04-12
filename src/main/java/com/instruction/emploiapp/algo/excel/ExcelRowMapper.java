package com.instruction.emploiapp.algo.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.sql.SQLException;

public interface ExcelRowMapper<T> {

    T mapRow(Row row) throws SQLException;

    default String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        if(cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if(cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else if(cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if(cell.getCellType() == CellType.FORMULA) {
            return cell.getCellFormula();
        }
        return "";
    }
}

