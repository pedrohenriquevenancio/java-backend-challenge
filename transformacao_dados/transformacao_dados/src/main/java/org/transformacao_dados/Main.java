package org.transformacao_dados;

import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String pathAnexoI = "D:/Downloads/Anexo_I_Rol_2021RN_465.2021_RN627L.2024.pdf";
        String csvPath = "D:/Downloads/Anexo_I_Rol_2021RN_465.2021_RN627L.2024.csv";

        try {
            PDDocument document = PDDocument.load(new File(pathAnexoI));
            int totalPages = document.getNumberOfPages();
            document.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(csvPath));

            for (int i = 3; i <= totalPages; i++) {
                List<Table> tables = extractTablesFromPage(pathAnexoI, i);
                for (Table table : tables) {
                    List<List<RectangularTextContainer>> rows = table.getRows();

                    for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
                        List<RectangularTextContainer> row = rows.get(rowIndex);
                        StringBuilder rowText = new StringBuilder();

                        for (RectangularTextContainer cell : row) {
                            String text = cell.getText().trim();

                            if (text.isEmpty()) {
                                text = "N/A";
                            }

                            if (rowIndex == 0) {
                                if (text.equalsIgnoreCase("OD")) {
                                    text = "Seg. Odontológica";
                                } else if (text.equalsIgnoreCase("AMB")) {
                                    text = "Seg. Ambulatorial";
                                }
                            }

                            rowText.append(text).append(";");
                        }

                        writer.write(rowText.toString().replaceAll(";$", "") + "\n");
                    }
                }
            }

            writer.close();
            System.out.println("O anexo I em formato CSV foi gerado com sucesso!");

            FileHandler fileHandler = new FileHandler();
            fileHandler.zipFiles(csvPath, "D:/Downloads/Teste_Pedro_Henrique_Venancio_Clotilde.zip");
            fileHandler.removeDirectory(csvPath);
            System.out.println("O arquivo foi compactado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Table> extractTablesFromPage(String pdfPath, int pageNumber) throws IOException {
        PDDocument document = PDDocument.load(new File(pdfPath));
        ObjectExtractor extractor = new ObjectExtractor(document);
        Page page = extractor.extract(pageNumber);

        SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
        List<Table> tables = sea.extract(page);

        document.close();
        return tables;
    }
}