package org.web_scrapping;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public String downloadFile(String fileUrl, String directoryPath) {
        try {
            URL url = new URL(fileUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int currentStatus = connection.getResponseCode();

            if (currentStatus != connection.HTTP_OK) {
                System.out.println("Erro ao baixar arquivo: " + currentStatus);
                return null;
                //throw new Exception("Erro ao baixar arquivo: " + currentStatus);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void zipFiles() {

    }

    public void removeDirectory() {

    }

}
