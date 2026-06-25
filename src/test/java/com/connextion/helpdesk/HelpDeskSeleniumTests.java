package com.connextion.helpdesk;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelpDeskSeleniumTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080/frontend/";

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run headless to avoid opening GUI windows
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testClientLogin_CU2() {
        driver.get(BASE_URL + "client-login.html");
        
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitBtn = driver.findElement(By.id("submitBtn"));

        emailInput.sendKeys("maria.lopez@example.com");
        passwordInput.sendKeys("16200122Wqkj!");
        submitBtn.click();

        // Redirect check to dashboard
        wait.until(ExpectedConditions.urlContains("client-dashboard.html"));
        assertTrue(driver.getCurrentUrl().contains("client-dashboard.html"), "Redirección fallida al portal de clientes");
    }

    @Test
    public void testCreateTicket_CU4() {
        testClientLogin_CU2(); // First login

        // Click on "Ingresar Solicitud" action item
        WebElement actionItem = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//h3[contains(text(),'Ingresar Solicitud')]")));
        actionItem.click();

        WebElement descInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("issue-description")));
        WebElement phoneInput = driver.findElement(By.id("issue-phone"));
        WebElement emailInput = driver.findElement(By.id("issue-email"));
        WebElement addressInput = driver.findElement(By.id("issue-address"));
        WebElement submitBtn = driver.findElement(By.id("submit-issue-btn"));

        descInput.sendKeys("Urgente: El internet está totalmente caído, parpadea en rojo.");
        phoneInput.sendKeys("8888-8888");
        emailInput.sendKeys("maria.lopez@example.com");
        addressInput.sendKeys("San Pedro, Montes de Oca");

        // Select first available service option from list
        WebElement serviceSelectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("issue-service")));
        Select serviceSelect = new Select(serviceSelectElement);
        // Wait until services option contains loaded items (not default placeholder)
        wait.until(d -> serviceSelect.getOptions().size() > 1);
        serviceSelect.selectByIndex(1);

        submitBtn.click();

        // Check for success message
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("create-issue-message")));
        assertTrue(successMsg.getText().toLowerCase().contains("creado") || successMsg.getText().toLowerCase().contains("éxito"), "Mensaje de éxito de ticket no se visualizó");
    }

    @Test
    public void testSupportLoginAndDashboard_CU8_CU10() {
        driver.get(BASE_URL + "support-login.html");
        
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitBtn = driver.findElement(By.id("submitBtn"));

        emailInput.sendKeys("supervisor@connextion.com");
        passwordInput.sendKeys("16200122Wqkj!");
        submitBtn.click();

        // Redirect check to support dashboard
        wait.until(ExpectedConditions.urlContains("support-dashboard.html"));
        assertTrue(driver.getCurrentUrl().contains("support-dashboard.html"), "Redirección fallida al dashboard de soporte");

        // Check if ticket table is displayed
        WebElement ticketsTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tickets-table-body")));
        assertTrue(ticketsTable.isDisplayed(), "La bandeja de tickets del dashboard de soporte no se visualizó");
    }
}
