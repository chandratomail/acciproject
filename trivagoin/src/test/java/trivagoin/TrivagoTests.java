package trivagoin;

import static org.testng.Assert.assertTrue;
import static trivagoin.Constants.CARDHOLDER_NAME_ERROR;
import static trivagoin.Constants.CONTACTNAME_ERROR;
import static trivagoin.Constants.DEBITCREDITCARD_ERROR;
import static trivagoin.Constants.EMAIL_ADDRESS_ERROR;
import static trivagoin.Constants.EXPIRY_MONTHYEAR_ERROR;
import static trivagoin.Constants.PHONENUMBER_ERROR;
import static trivagoin.Constants.SECURITY_CODE_ERROR;
import static trivagoin.PropertiesLoader.getProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TrivagoTests {

	String url;
	WebDriver driver;
	String chromeDriverPath;
	String ieDriverPath;
	Utils utils;

	@BeforeClass
	public void beforeClass() {
		url = getProperty("url");
		if(getProperty("browser").equalsIgnoreCase("chrome")) {
			chromeDriverPath = getProperty("chromeDriverPath");
			System. setProperty("webdriver.chrome.driver", chromeDriverPath);
			driver = new ChromeDriver();

		}
		else if(getProperty("browser").equalsIgnoreCase("firefox")) {
			driver = new FirefoxDriver();	
		}
		else if(getProperty("browser").equalsIgnoreCase("ie")) {
			ieDriverPath = getProperty("ieDriverPath");
			System.setProperty("webdriver.ie.driver", ieDriverPath);
			driver = new InternetExplorerDriver();
		}
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		utils = new Utils(driver);		
		utils.wait = new WebDriverWait(driver, 20);
	}

	@Test(dataProvider="BookingMap")
	public void  testBookingUntilCancelHotelRoom(Map<String, String> map) throws Exception {
		
		String city = map.get("city");
		String hotel = map.get("hotel");
		String sort = map.get("sort");
		int checkinDateForward = Integer.parseInt(map.get("checkinDateForward"));
		int checkoutDateForward = Integer.parseInt(map.get("checkoutDateForward"));;
		String rooms = map.get("rooms");
		String adult = map.get("adult");
		String children = map.get("children");
		
		driver.get(url);
		Reporter.log("<br>Application trivago is started");
		utils.click(By.id("horus-querytext"), "SearchBox");

		utils.sendKeys(By.id("horus-querytext"), city, "SearchText");//data
		utils.click(By.xpath("(//span[@class='ssg-title']/mark[text()='"+city+"'])[1]"), "SearchOption");
		utils.click(By.xpath("//span[text()='Search']"), "SearchButton");
		utils.waitForInvisibilityOfElement(By.xpath("//section[id='js_item_list_section']/div[contains(@class, 'ellipsis-loader-wrapper')]"));

		List<WebElement> priceElements = utils.findElements(By.xpath("//strong[contains(@class,'item__best-price mb-gutter')]"), "Prices");
		int price = 0;
		for(WebElement element:priceElements) {
			price = Integer.parseInt(element.getText().replaceAll("[^0-9]", ""));
			if(price>=1500 && price<=1600) {
				utils.scrollToElement(element, "price element");
				break;
			}
		}

		utils.click(By.id("horus-querytext"), "SearchBox");
		utils.sendKeys(By.id("horus-querytext"), hotel, "SearchValue");
		utils.click(By.xpath("//span[text()='Search']"), "SearchButton");
		utils.selectByVisibleText(By.xpath("//select[@id='mf-select-sortby']"), sort);

		utils.waitForInvisibilityOfElement(By.xpath("//section[id='js_item_list_section']/div[contains(@class, 'ellipsis-loader-wrapper')]"));

		List<WebElement> resortNames = utils.findElements(By.xpath("//ol[@id='js_itemlist']/li//h3"), hotel);
		Reporter.log("<br>*** Printing Resort Names started ***");
		for(WebElement element : resortNames) {
			System.out.println(element.getText());
			Reporter.log("<br>"+element.getText());
			
		}
		Reporter.log("<br>*** Printing Resort Names is done ***");
		for(int i=0;i<400;i++) {
			List<WebElement> resortNames2 = driver.findElements(By.xpath("//ol[@id='js_itemlist']/li//h3"));
			for(WebElement element : resortNames2) {
				if(element.getText().equals("Goa Marriott Resort & Spa")) {
					utils.scrollToElement(element, "Goa Marriott Resort & Spa");
					utils.click(By.xpath("//ol[@id='js_itemlist']/li//h3[text()='Goa Marriott Resort & Spa']/ancestor::li//section//button[text()='View Deal']"), "Goa Marriott Resort & Spa");
					i=400;
					break;
				}
			}
			utils.click(By.xpath("//button[@class='btn btn--pagination btn--small btn--page-arrow btn--next']"), "Next Button");
			resortNames2 = null;
		}

		Set<String> windows = driver.getWindowHandles();
		Iterator<String> iterator = windows.iterator();
		String parentWindow = iterator.next();
		String childWindow = iterator.next();
		driver.switchTo().window(childWindow);

		utils.sendKeys(By.xpath("//input[@id='availability-check-in']"), utils.getDateByForwardDays(checkinDateForward), "Checkin Date");//checkin date
		utils.sendKeys(By.xpath("//input[@id='availability-check-out']"), utils.getDateByForwardDays(checkoutDateForward), "Checkout Date");//checkout date
		utils.selectByVisibleText(By.xpath("//select[@class='rooms-selector']"), rooms);
		utils.selectByVisibleText(By.xpath("//select[@class='adult-selector']"), adult);
		utils.selectByVisibleText(By.xpath("//select[@class='children-selector']"), children);
		utils.click(By.id("update-availability-button"), "Update Button");

		utils.waitForInvisibilityOfElement(By.xpath("//*[@id='rooms-and-rates' and @aria-busy='true']"));
		utils.click(By.xpath("//button[contains(@class,'book-button btn-pwa')]"), "Book Button");

		utils.click(By.id("complete-booking"), "Complete Booking Button");

		String[] errors = {CONTACTNAME_ERROR, PHONENUMBER_ERROR, CARDHOLDER_NAME_ERROR, DEBITCREDITCARD_ERROR, EXPIRY_MONTHYEAR_ERROR, SECURITY_CODE_ERROR, EMAIL_ADDRESS_ERROR};

		List<WebElement> errorElements = utils.findElements(By.xpath("//p[@class='uitk-validation-error']"), "Errors");
		int counter = 0;
		List<String> errorValues = new ArrayList<String>();
		for(WebElement element : errorElements) {
			errorValues.add(element.getText());
		}

		for(String s : errorValues) {
			assertTrue(errorValues.contains(errors[counter]), "The error message is not displayed"+errors[counter]);
			counter++;
		}

		utils.sendKeys(By.xpath("//input[@data-tealeaf-name='contactName']"), "Customer name", "Customer name");
		utils.sendKeys(By.xpath("//input[@data-tealeaf-name='phoneNumber']"), "9632963296", "Customer phone number");
		utils.sendKeys(By.xpath("//input[@data-tealeaf-name='cardHolderName_1']"), "Customer name", "Card holder name");
		utils.sendKeys(By.xpath("//input[@id='creditCardInput']"), "12344567812345678", "Card number");
		utils.sendKeys(By.xpath("//input[@id='new_cc_security_code']"), "123", "Security Code");
		utils.sendKeys(By.xpath("//input[@type='email' and @name='email']"), "test@gmail.com", "Email");
		utils.click(By.id("complete-booking"), "Complete Booking");

		List<WebElement> errorsElements = utils.findElements(By.xpath("//p[@class='uitk-validation-error']"), "Errors");
		for(WebElement element : errorsElements) {
			if(element.getText().equals(DEBITCREDITCARD_ERROR)){
				assertTrue(element.getText().equals(DEBITCREDITCARD_ERROR), "The error message is not displayed"+element.getText());
			}
			else if(element.getText().equals(EXPIRY_MONTHYEAR_ERROR)) {
				assertTrue(element.getText().equals(EXPIRY_MONTHYEAR_ERROR), "The error message is not displayed"+element.getText());
			}
			else {
				throw new Exception("There are extra errors on page");
			}
		}

		driver.switchTo().window(parentWindow);

	}

	@AfterClass
	public void tearDown() {
		if(driver!=null) {
			driver.quit();
		}
	}

	//Not using it as my ms office is expired. Need to buy new one.
	@DataProvider(name = "BookingExcel")
	public Object[][] dataSupplier() throws IOException {
	    File file = new File("src//test//resources//TestData.xlsx");
	    FileInputStream fis = new FileInputStream(file);
	    Workbook workbook = new XSSFWorkbook(fis);
	    Sheet sheet = workbook.getSheetAt(0);
	    fis.close();
	    int lastRowNum = sheet.getLastRowNum() ;
	    int lastCellNum = sheet.getRow(0).getLastCellNum();
	    Object[][] object = new Object[lastRowNum][1];
	    for (int i = 0; i < lastRowNum; i++) {
	      Map<Object, Object> datamap = new HashMap<Object, Object>();
	      for (int j = 0; j < lastCellNum; j++) {
	        datamap.put(sheet.getRow(0).getCell(j).toString(), sheet.getRow(i+1).getCell(j).toString());
	      }
	      object[i][0] = datamap;
	    }
	    return  object;
	  }

	
	@DataProvider(name = "BookingMap")
	public static Object[][] credentials() {
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("city", "Goa");
		map.put("hotel", "Resort");
		map.put("sort", "Rating & Popularity");
		map.put("checkinDateForward", "3");
		map.put("checkoutDateForward", "5");
		map.put("rooms", "1");
		map.put("adult", "1");
		map.put("children", "0");
		
		Object[][] object = new Object[1][1];
		object[0][0] = map;
		
		return object;
		

	}
}