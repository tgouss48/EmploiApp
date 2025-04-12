package com.instruction.emploiapp.algo.excel;

import com.instruction.emploiapp.algo.AlgoG;
import com.instruction.emploiapp.model.Emploi;
import com.instruction.emploiapp.model.Seance;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.instruction.emploiapp.algo.Valeurs.CRENEAUX;
import static com.instruction.emploiapp.algo.Valeurs.JOURS;

public class ExportExcel {

    public static void exporter(Emploi emploi, String outputFolder) throws IOException {
        Set<Integer> compagnies = listerToutesLesCompagnies(emploi);

        for (Integer cie : compagnies) {
            String filePath = outputFolder + "/Compagnie_" + cie + ".xlsx";
            exporterExcel(emploi, cie, filePath);
        }
    }

    private static Set<Integer> listerToutesLesCompagnies(Emploi emploi) {
        Set<Integer> result = new HashSet<>();
        for (Seance s : emploi.getSeances()) {
            int cie = extraireCompagnie(s.getCie());
            result.add(cie);
        }
        return result;
    }

    private static void exporterExcel(Emploi emploi, int compagnie, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Emploi");

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle hourStyle = createHourDayStyle(workbook);
        CellStyle dayStyle = createCellStyle(workbook);
        CellStyle cellStyle = createCellStyle(workbook);
        CellStyle indispoStyle = createIndispoStyle(workbook);

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Compagnie " + compagnie);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, CRENEAUX.length));

        Row headerRow = sheet.createRow(2);
        Cell cellJour = headerRow.createCell(0);
        cellJour.setCellValue("");
        cellJour.setCellStyle(hourStyle);
        for (int i = 0; i < CRENEAUX.length; i++) {
            Cell cell = headerRow.createCell(i + 1);
            cell.setCellValue(CRENEAUX[i]);
            cell.setCellStyle(hourStyle);
        }

        for (int i = 0; i < JOURS.length; i++) {
            Row row = sheet.createRow(i + 3);
            Cell dayCell = row.createCell(0);
            dayCell.setCellValue(JOURS[i]);
            dayCell.setCellStyle(dayStyle);

            if (AlgoG.estIndisponible("Compagnie " + compagnie, JOURS[i])) {
                sheet.addMergedRegion(new CellRangeAddress(i + 3, i + 3, 1, CRENEAUX.length));
                Cell indispoCell = row.createCell(1);
                indispoCell.setCellValue("Indisponible");
                indispoCell.setCellStyle(indispoStyle);
            } else {
                for (int j = 0; j < CRENEAUX.length; j++) {
                    Cell cell = row.createCell(j + 1);
                    cell.setCellStyle(cellStyle);
                    String horaire = CRENEAUX[j];
                    Seance seance = trouverSeance(emploi, compagnie, JOURS[i], horaire);
                    if (seance != null) {
                        cell.setCellValue(
                                seance.getMatiere() + "\n" +
                                        (seance.getInstructeur() != null ? seance.getInstructeur() : "")
                        );
                    }
                }
            }
        }

        for (int i = 0; i <= CRENEAUX.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    private static Seance trouverSeance(Emploi emploi, int compagnie, String jour, String creneau) {
        for (Seance s : emploi.getSeances()) {
            boolean matchCie = s.getCie().equals("Compagnie " + compagnie);
            boolean matchHoraire = s.getHoraire().contains(jour) && s.getHoraire().contains(creneau);
            if (matchCie && matchHoraire) {
                return s;
            }
        }
        return null;
    }

    private static int extraireCompagnie(String cie) {
        return Integer.parseInt(cie.replace("Compagnie ", ""));
    }

    private static CellStyle createTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createHourDayStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorders(style);
        return style;
    }

    private static CellStyle createCellStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(style);
        return style;
    }

    private static CellStyle createIndispoStyle(Workbook wb) {
        CellStyle style = createCellStyle(wb);
        Font font = wb.createFont();
        font.setBold(true);
        font.setItalic(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.CORAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBorders(style);
        return style;
    }

    private static void setBorders(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
}