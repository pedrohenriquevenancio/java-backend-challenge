package org.web_scrapping;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private void removeDirectory(String directoryPath) {
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

    public String downloadFile(String fileUrl, String directoryPath) {
        try {
            URL url = new URL(fileUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int currentStatus = connection.getResponseCode();

            if (currentStatus != connection.HTTP_OK) {
                System.err.println("Erro ao baixar arquivo: " + currentStatus);
                return null;
            }

            if (!directoryPath.endsWith("/")) directoryPath += "/";
            
            InputStream inputStream = connection.getInputStream();
            String filePath = directoryPath + fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            FileOutputStream outputStream = new FileOutputStream(filePath);

            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            connection.disconnect();

            return filePath;
        } catch (IOException e) {
            System.out.println("Erro ao realizar downloads dos arquivos: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
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
