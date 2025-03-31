package org.web_scrapping;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        try {
            String url = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";
            driver.get(url);
            System.out.println("Acesso ao site: " + driver.getTitle());

            verifyCookieBar(driver);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a.internal-link")));

            WebElement anexoI =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"cfec435d-6921-461f-b85a-b425bc3cb4a5\"]/div/ol/li[1]/a[1]")));
            String urlAnexoI = anexoI.getAttribute("href");
            System.out.println("Anexo I: " + urlAnexoI);

            WebElement anexoII = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"cfec435d-6921-461f-b85a-b425bc3cb4a5\"]/div/ol/li[2]/a")));
            String urlAnexoII = anexoII.getAttribute("href");
            System.out.println("Anexo II: " + urlAnexoII);

            SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
            String directory = "pdf_ans_anexos_" + sdf.format(new Date());
            String directoryPath = "D:Downloads/"+ directory +"/";

            FileHandler fileHandler = new FileHandler();
            fileHandler.createDirectory(directoryPath);
            String pathAnexoI = fileHandler.downloadFile(urlAnexoI, directoryPath);
            String pathAnexoII = fileHandler.downloadFile(urlAnexoII, directoryPath);

            if (pathAnexoI != null && pathAnexoII != null) {
                System.out.println("Arquivos baixados com sucesso!");
                System.out.println("Iniciando compactação em arquivo zip...");

                fileHandler.zipFiles(directoryPath, "D:Downloads/" + directory +".zip");
                System.out.println("Arquivos compactados com sucesso!");
            }

            System.out.println("Procedimentos ANS finalizados com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao acessar o site: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    public static void verifyCookieBar(WebDriver driver) {
        List<WebElement> cookieBars = driver.findElements(By.xpath("//*[contains(@class, 'br-cookiebar') and contains(@class, 'default')]"));

        if (!cookieBars.isEmpty()) {
            List<WebElement> rejectButtons = driver.findElements(By.xpath("//button[contains(@class, 'br-button') and contains(@class, 'secondary') and contains(@class, 'small') and contains(@class, 'reject-all')]"));

            if (!rejectButtons.isEmpty()) {
                rejectButtons.getFirst().click();
                System.out.println("Cookies rejeitados");
            }
        }
    }
}