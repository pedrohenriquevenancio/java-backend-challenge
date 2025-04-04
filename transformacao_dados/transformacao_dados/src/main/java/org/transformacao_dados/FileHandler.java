package org.transformacao_dados;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileHandler {

    public void createDirectory(String directoryPath) {
        try {
            File file = new File(directoryPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeDirectory(String directoryPath) {
        try {
            File file = new File(directoryPath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null) {
                        for (File f : files) {
                            f.delete();
                        }
                    }
                }
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void zipFiles(String directoryPath, String zipFilePath) {
        try {
            File directory = new File(directoryPath);

            if (!directory.exists()) {
                System.err.println("Arquivo ou diretório de origem não encontrado");
                return;
            }

            FileOutputStream outputStream = new FileOutputStream(zipFilePath);
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

            List<File> files = new ArrayList<>();
            if (directory.isDirectory()) {
                File[] filesInDirectory = directory.listFiles();
                if (filesInDirectory != null) {
                    files.addAll(List.of(filesInDirectory));
                }
            } else {
                files.add(directory);
            }

            for (File currentFile : files) {
                FileInputStream inputStream = new FileInputStream(currentFile);
                ZipEntry zipEntry = new ZipEntry(currentFile.getName());
                zipOutputStream.putNextEntry(zipEntry);

                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    zipOutputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                zipOutputStream.closeEntry();
            }

            this.removeDirectory(directoryPath);

            zipOutputStream.close();
            outputStream.close();
        } catch (IOException e) {
            System.out.println("Erro ao compactar arquivos: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
