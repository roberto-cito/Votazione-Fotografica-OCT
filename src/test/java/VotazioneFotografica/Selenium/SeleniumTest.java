package VotazioneFotografica.Selenium;

import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraService;
import VotazioneFotografica.Model.Users.CustomUserDetailsService;
import VotazioneFotografica.Model.Users.PasswordUtility;
import VotazioneFotografica.Model.Users.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SeleniumTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private SquadraService squadraService;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Comment this out to see the browser
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");
        
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Clean up possible leftover
        try { customUserDetailsService.remove("testAdmin"); } catch(Exception ignored) {}
        try { squadraService.remove("testSquadra"); } catch(Exception ignored) {}
        try { customUserDetailsService.remove("testSquadra"); } catch(Exception ignored) {}

        // Add test admin and squadra users
        customUserDetailsService.save(new UserEntity("testAdmin", PasswordUtility.hashPassword("pass123"), "ADMIN"));
        customUserDetailsService.save(new UserEntity("testSquadra", PasswordUtility.hashPassword("pass123"), "SQUADRA"));
        squadraService.save(new SquadraEntity("testSquadra", "Squadra Test", "Istituto Test"));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        // Clean up injected users
        try { customUserDetailsService.remove("testAdmin"); } catch(Exception ignored) {}
        try { squadraService.remove("testSquadra"); } catch(Exception ignored) {}
        try { customUserDetailsService.remove("testSquadra"); } catch(Exception ignored) {}
    }

    private void doLogin(String username, String password) {
        driver.get("http://localhost:" + port + "/votazione-fotografica/");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector(".btn-login"));

        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        loginButton.click();
        
        // Wait until login completes and browser is redirected to a success page
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.or(
                        org.openqa.selenium.support.ui.ExpectedConditions.urlContains("home"),
                        org.openqa.selenium.support.ui.ExpectedConditions.urlContains("admin"),
                        org.openqa.selenium.support.ui.ExpectedConditions.urlContains("squadra"),
                        org.openqa.selenium.support.ui.ExpectedConditions.urlContains("giudice")
                ));
    }

    @Test
    void testLoginPageLoads() {
        driver.get("http://localhost:" + port + "/votazione-fotografica/");
        
        // Verify title
        String title = driver.getTitle();
        assertTrue(title.contains("Votazione"), "Title should contain 'Votazione'");

        // Verify login form elements
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector(".btn-login"));

        assertTrue(usernameInput.isDisplayed());
        assertTrue(passwordInput.isDisplayed());
        assertTrue(loginButton.isDisplayed());
    }

    @Test
    void testLoginWithInvalidCredentials() {
        driver.get("http://localhost:" + port + "/votazione-fotografica/");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.cssSelector(".btn-login"));

        usernameInput.sendKeys("wrongUser");
        passwordInput.sendKeys("wrongPass");
        loginButton.click();

        assertTrue(driver.getCurrentUrl().contains("error"), "URL should contain 'error' param");
        
        WebElement errorMessage = driver.findElement(By.xpath("//div[contains(text(), 'Username o password errati')]"));
        assertTrue(errorMessage.isDisplayed());
    }

    @Test
    void testAdminUserListFiltering() {
        doLogin("testAdmin", "pass123");
        
        driver.get("http://localhost:" + port + "/votazione-fotografica/admin/manage-users");
        
        WebElement filterUsername = driver.findElement(By.id("filterUsername"));
        WebElement filterRole = driver.findElement(By.id("filterRole"));
        
        // Filter by username "testSquadra"
        filterUsername.sendKeys("testSquadra");
        List<WebElement> visibleRows = driver.findElements(By.xpath("//tbody/tr[not(contains(@style, 'display: none'))]"));
        
        boolean foundTestSquadra = false;
        for (WebElement row : visibleRows) {
            if (row.getText().contains("testSquadra")) {
                foundTestSquadra = true;
                break;
            }
        }
        assertTrue(foundTestSquadra, "Should find 'testSquadra' row after filtering by username");
        
        // Clear username filter
        filterUsername.clear();
        
        // Filter by role "SQUADRA"
        Select roleSelect = new Select(filterRole);
        roleSelect.selectByValue("SQUADRA");
        
        visibleRows = driver.findElements(By.xpath("//tbody/tr[not(contains(@style, 'display: none'))]"));
        boolean noAdmins = true;
        for (WebElement row : visibleRows) {
            if (row.getText().contains("ADMIN")) {
                noAdmins = false;
                break;
            }
        }
        assertTrue(noAdmins, "Should not show ADMIN users when filtered by SQUADRA");
    }

    @Test
    void testLogoutFlow() {
        doLogin("testSquadra", "pass123");
        
        driver.get("http://localhost:" + port + "/votazione-fotografica/home");
        
        WebElement logoutButton = driver.findElement(By.cssSelector("form[action*='/logout'] button"));
        assertTrue(logoutButton.isDisplayed(), "Logout button should be visible");
        
        logoutButton.click();
        
        // Wait for redirect to complete
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/login"));
        
        assertTrue(driver.getCurrentUrl().contains("login"), "Should acturally route back to login page");
    }
}
