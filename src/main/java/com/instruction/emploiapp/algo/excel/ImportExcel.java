package com.instruction.emploiapp.algo.excel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

public class ImportExcel<T> {
    private final ExcelRowMapper<T> rowMapper;

    public ImportExcel(ExcelRowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    public ObservableList<T> ReadExcel(String filePath) {
        ObservableList<T> list = FXCollections.observableArrayList();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            // Ligne 1 contient l'en-tête
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                T obj = rowMapper.mapRow(row);
                if (obj != null) {
                    list.add(obj);
                }
            }

        } catch (IOException | SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}
