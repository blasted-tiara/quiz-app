package ba.fet.rwa.models;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class ExcelGenerator {
    public static String generateExcelFile(List<Player> playerList) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Players");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Player");
        headerRow.createCell(1).setCellValue("Score");

        // Populate data rows
        int rowNum = 1;
        for (Player player : playerList) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(player.getFullName());
            dataRow.createCell(1).setCellValue(player.getScore());
        }

        // Auto-size columns
        for (int i = 0; i < 2; i++) {
            sheet.autoSizeColumn(i);
        }

        // Convert workbook to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        byte[] workbookBytes = outputStream.toByteArray();
        outputStream.close();

        // Encode byte array to Base64
        String base64Data = Base64.getEncoder().encodeToString(workbookBytes);

        return base64Data;
    }
}
