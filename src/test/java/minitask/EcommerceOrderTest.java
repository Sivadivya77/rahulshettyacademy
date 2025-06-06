package minitask;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

// ... your other imports

public class EcommerceOrderTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Utility method for safe clicking
    public void safeClick(WebElement element) {
        // Wait for overlay/spinner to disappear before clicking
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".ngx-spinner-overlay")));

        // Scroll element into view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

        // Wait until clickable
        wait.until(ExpectedConditions.elementToBeClickable(element));

        // Use JS click to avoid interception issues
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    @Test
    public void placeOrderSuccessfully() throws InterruptedException {
        driver.get("https://rahulshettyacademy.com/client");

        // Login
        driver.findElement(By.id("userEmail")).sendKeys("csiva801@gmail.com");
        driver.findElement(By.id("userPassword")).sendKeys("Divyasiva@77");
        driver.findElement(By.id("login")).click();

        // Wait until products load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".card-body")));
        Thread.sleep(5000);

        // Add first product to cart
        driver.findElement(By.xpath("//button[contains(text(),'Add To Cart')]")).click();

        // Wait for spinner to disappear
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".ngx-spinner-overlay")));

        // Go to cart - use safeClick
        WebElement cartButton = driver.findElement(By.cssSelector("button[routerlink='/dashboard/cart']"));
        safeClick(cartButton);

        // Assert product is in cart
        WebElement cartProduct = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cartSection h3")));
        Assert.assertTrue(cartProduct.isDisplayed(), "Product is not added to the cart.");

        // Checkout
        driver.findElement(By.xpath("//button[normalize-space()='Checkout']")).click();

        // Country selection
        WebElement countryInput = driver.findElement(By.xpath("//input[@placeholder='Select Country']"));
        countryInput.sendKeys("ind");

        // Wait for country dropdown and select second item
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ta-results")));
        driver.findElement(By.xpath("(//button[@type='button'])[2]")).click();

        // Place Order - use safeClick
        WebElement placeOrderBtn = driver.findElement(By.xpath("//a[normalize-space()='Place Order']"));
        safeClick(placeOrderBtn);

        // Validate order success message
        WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[normalize-space()='Thankyou for the order.']")));
        Assert.assertEquals(confirmationMessage.getText().trim(), "THANKYOU FOR THE ORDER.");

        // Logout (if available)
        try {
            WebElement logoutButton = driver.findElement(By.cssSelector("button[routerlink='/auth/login']"));
            if (logoutButton.isDisplayed()) {
                logoutButton.click();
            }
        } catch (NoSuchElementException e) {
            System.out.println("Logout button not found or already logged out.");
        }
    }
}
