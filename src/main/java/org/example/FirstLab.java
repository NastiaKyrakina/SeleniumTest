package org.example;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import java.io.File;
import java.time.Duration;
import java.util.List;

public class FirstLab {
    private static WebDriver chromeDriver;
    private static final String pageURL = "https://www.nmu.org.ua/ua/";
    private static final String libraryPageURL = "https://lib.nmu.org.ua/";
    private static final long durationSeconds = 15;
    private static final Duration timeOutInSeconds = Duration.ofSeconds(durationSeconds);
    private static WebDriverWait wait;
    private static String filename;
    private static final String screenshotPath = "src/test/screenshots/FirstLab/";
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
        log("Common task log");
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
    public void checkHeaderById(){
        try {
            preconditions();
            filename = "MainPage.png";
            takeSnapShot(chromeDriver, screenshotPath.concat(filename));
            String id = "header";
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));

            //find element by ID
            List<WebElement> targetElements = chromeDriver.findElements(By.id(id));
            if (targetElements.size() > 0){
                WebElement header = targetElements.get(0);

                // check if header exist
                Assert.assertNotNull(header);
            }
            else {
                log(String.format("Test %s \n There is no element with id %s", "testHeaderExists", id));
            }
        }
        catch(Exception e){
            this.logException(e, "testHeaderExists");
        }
    }

    @Test
    public void testOpenLibraryPage(){
        String testName = "Open Library Page with XPath";
        try {
            preconditions();

            String libraryLink = "//*[@id=\"main_vertikal2\"]/li[4]/a";
            String libraryBaseLink = "/html/body/center/div[3]/div/div[5]/div[2]/ul/li[1]/a";

            // open library menu option

            // wait and get library option by XPath
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(libraryLink)));
            WebElement forStudentsButton = chromeDriver.findElement(By.xpath(libraryLink));

            // check if exist
            Assert.assertNotNull(forStudentsButton);

            forStudentsButton.click();


            // wait and get library page link by XPath
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(libraryBaseLink)));

            WebElement baseLibraryLink = chromeDriver.findElement(By.xpath(libraryBaseLink));
            baseLibraryLink.click();


            // wait until page loaded
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("site-loader")));

            //verification page changed
            Assert.assertEquals(chromeDriver.getCurrentUrl(), libraryPageURL);

            takeSnapShot(chromeDriver, screenshotPath.concat(testName + ".png"));

            log(String.format("Test %s \n Successfully entered the site 'Library'", testName));
        }
        catch(Exception e){
            this.logException(e, testName);
        }
    }

    @Test
    public void testSearchField(){

        String testName = "Input in search field";

        try {
            String studentPageURL = "content/study/admission/umovi_vstupy/ngu/";
            chromeDriver.get(pageURL + studentPageURL);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("input")));

            // find element by TagName
            WebElement searchButton = chromeDriver.findElement(By.tagName("input"));

            // verification
            Assert.assertNotNull(searchButton);

            // different params of searchField
            log(String.format("Test %s\n", testName));
            log(String.format("Name attribute: %s\n",
            searchButton.getAttribute("name")) + String.format("ID attribute: %s\n",
            searchButton.getAttribute("id")) + String.format("Type attribute: %s\n",
            searchButton.getAttribute("id")) + String.format("Value attribute: %s\n",
            searchButton.getAttribute("type")) + String.format("Position: (%d;%d)\n",
            searchButton.getLocation().x, searchButton.getLocation().y) + String.format("Size: %dx%d\n", searchButton.getSize().height, searchButton.getSize().width));
            searchButton.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gsc-i-id1")));
            log(String.format("Navigate to student's search page"));

            // input value
            String valueForSearch = "About";

            log(String.format("Enter search condition %s", valueForSearch));

            WebElement searchField = chromeDriver.findElement(By.id("gsc-i-id1"));
            searchField.sendKeys(valueForSearch);

            takeSnapShot(chromeDriver, screenshotPath.concat(testName + "1.png"));

            log(String.format("Execute search"));

            //click enter
            searchField.sendKeys(Keys.ENTER);//verification text
            Assert.assertEquals(searchField.getAttribute("value"), valueForSearch);


            //verification page changed
            Assert.assertNotEquals(chromeDriver.getCurrentUrl(), studentPageURL);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".gsc-resultsbox-visible")));

            takeSnapShot(chromeDriver, screenshotPath.concat(testName + "2.png"));
        }
        catch(Exception e){
             this.logException(e, testName);
        }
    }

    @Test
    public void testSlider() {

        String testName = "Test Slider";

        try {
            log(String.format("Test %s \n", testName));
            preconditions();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("next")));

            //find element by class name
            WebElement nextBtn = chromeDriver.findElement(By.className("next"));

            //verification equalityAssert.assertEquals(nextButton, nextButtonByCss);
            WebElement prevBtn = chromeDriver.findElement(By.className("prev"));

            for (int i = 0; i < 20; i++) {
                filename = Integer.toString(i).concat(".png");

                //change count of iterations
                if
                (nextBtn.getAttribute("class").contains("disabled")) {
                    prevBtn.click();
                    takeSnapShot(chromeDriver, screenshotPath.concat(filename));
                    log(String.format("Going backwards"));
                } else {
                    nextBtn.click();
                    takeSnapShot(chromeDriver, screenshotPath.concat(filename));
                    log(String.format("Going forward"));
                }
            }

            Assert.assertFalse(nextBtn.getAttribute("class").contains("disabled"));
            Assert.assertFalse(prevBtn.getAttribute("class").contains("disabled"));
            log(String.format("Stopped at the penultimate image"));
        }
        catch(Exception e){
            this.logException(e, testName);
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
