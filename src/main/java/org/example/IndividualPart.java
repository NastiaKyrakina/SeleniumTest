package org.example;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

public class IndividualPart {
    private static WebDriver chromeDriver;
    private static final String pageURL = "https://rozetka.com.ua/ua/";
    private static final long durationSeconds = 15;
    private static final Duration timeOutInSeconds = Duration.ofSeconds(durationSeconds);
    private static WebDriverWait wait;
    private static final String screenshotPath = "src/test/screenshots/IndividualPart/";
    public static WebDriver getDriver(){
        System.setProperty("webdriver.chrome.driver", "/home/west/Documents/drivers/chromedriver");
        return new ChromeDriver();
    }
    public static void takeSnapShot(WebDriver webdriver, String fileWithPath) throws Exception {

        //Convert web driver object to TakeScreenshot
        TakesScreenshot scrShot =((TakesScreenshot)webdriver);

        //Call getScreenshotAs method to create image file
        File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);

        //Move image file to new destination
        File DestFile = new File(fileWithPath);

        //Copy file at destination
        FileUtils.copyFile(SrcFile, DestFile);
    }

    @BeforeClass
    public static void setUp(){
        //run driver
        log("Individual task log");
        log("Starting driver");
        chromeDriver = getDriver();
        wait = new WebDriverWait(chromeDriver, timeOutInSeconds);
    }

    public void preconditions() {
        //open main page
        log(String.format("Open page: %s", pageURL));
        chromeDriver.get(pageURL);
    }

    @AfterClass
    public static void tearDown(){
        log("Exit");
        chromeDriver.quit();
    }

    @Test
    public void searchForProduct(){
        try {
            preconditions();

            takeSnapShot(chromeDriver, screenshotPath.concat("MainPage.png"));

            String searchFieldClass = "search-form__input";
            String productForSearch = "Навушники Razer Barracuda X";
            String firstSearchResultXPath = "/html/body/app-root/div/div/rz-header/rz-main-header/header/div/div/div/form/div/div[2]/ul/li[2]/a";
            String buyBtnXPath = "//*[@id=\"#scrollArea\"]/div[1]/div[2]/rz-product-main-info/div[1]/div[1]/rz-product-buy-btn/app-buy-button/button";
            String productTagName = "rz-product";
            String productPriceClass = "product-price__big";
            String productNameClass = "product__title";
            String productAvailableClass = "status-label";
            String modalWindow = "rz-single-modal-window";

            // get search field
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(searchFieldClass)));
            WebElement searchField = chromeDriver.findElement(By.className(searchFieldClass));
            log("Input in search field: " + productForSearch);
            searchField.sendKeys(productForSearch);
            takeSnapShot(chromeDriver, screenshotPath.concat("Search.png"));

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(firstSearchResultXPath)));

            log("Click on search result");
            WebElement firstProduct = chromeDriver.findElement(By.xpath(firstSearchResultXPath));
            takeSnapShot(chromeDriver, screenshotPath.concat("SearchResult.png"));
            firstProduct.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName(productTagName)));

            log("Open product page");
            WebElement productPriceBlock = chromeDriver.findElement(By.className(productPriceClass));
            WebElement productNameBlock = chromeDriver.findElement(By.className(productNameClass));
            String productStatus = chromeDriver.findElement(By.className(productAvailableClass)).getText();

            log(String.format("Product Name: %s", productNameBlock.getText()));
            log(String.format("Product Price: %s", productPriceBlock.getText()));
            log(String.format("Product status: %s", productStatus));

            takeSnapShot(chromeDriver, screenshotPath.concat("ProductPage.png"));

            if (productStatus.trim().equals("Є в наявності")) {
                WebElement bueButton = chromeDriver.findElement(By.xpath(buyBtnXPath));
                bueButton.click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName(modalWindow)));
                takeSnapShot(chromeDriver, screenshotPath.concat("Bye.png"));
                log("Bye product");
            }
        }
        catch(Exception e){
            this.logException(e, "searchForProduct");
        }
    }

    void logException(Exception e, String testName) {
        System.out.println(String.format("Test %s\n", testName));
        System.out.println(e.getMessage());
    }

    static void log(String text) {
        System.out.println(text);
    }
}
