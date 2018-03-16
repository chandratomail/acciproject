package trivagoin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

public class Utils {
	
	WebDriver driver;
	public WebDriverWait wait = null;
	
	public Utils(WebDriver driver) {
		this.driver = driver;
	}
	
	public void waitForPresenceOfElement(By locator) {
		wait.until(ExpectedConditions.presenceOfElementLocated(locator));
	}

	public void waitForInvisibilityOfElement(By locator) {
		wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}

	public void scrollToElement(WebElement element, String name) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		Reporter.log("<br>Scrolled to "+ name);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void selectByVisibleText(By locator, String text) {
		waitForPresenceOfElement(locator);
		WebElement webElement = driver.findElement(locator);
		Select child = new Select(webElement);
		child.selectByVisibleText(text);
		Reporter.log("<br>Selected option "+ text);
	}

	public void sendKeys(By locator, String text, String name) {
		waitForPresenceOfElement(locator);
		driver.findElement(locator).clear();
		driver.findElement(locator).sendKeys(text);
		Reporter.log("<br>Entered value in "+ name);
	}

	public void click(By locator, String name) {
		waitForPresenceOfElement(locator);
		driver.findElement(locator).click();
		Reporter.log("<br>Clicked on element "+ name);
	}

	public List<WebElement> findElements(By locator, String name) {
		waitForPresenceOfElement(locator);
		Reporter.log("<br>Found elements for "+ name);
		return driver.findElements(locator);

	}

	public WebElement findElement(By locator, String name) {
		waitForPresenceOfElement(locator);
		Reporter.log("<br>Found element "+ name);
		return driver.findElement(locator);

	}

	public String getDateByForwardDays(int days) {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
		c.setTime(new Date());
		c.add(Calendar.DATE, days);
		return formatter.format(c.getTime());
	}
}
