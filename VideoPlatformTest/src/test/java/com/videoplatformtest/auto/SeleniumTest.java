package com.videoplatformtest.auto;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import com.videoplatformtest.auto.PageObjects.*;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SeleniumTest extends TestBaseClass {

    String relativePath = "src/main/resources/chromedriver";
    File file = new File(relativePath);
    String absolutePath = file.getAbsolutePath();

    public String webUrl = "https://demo2.progate.mobi/#/";
    public ExtentReports extentReports;
    public ExtentSparkReporter extentSparkReporter;
    public ChromeDriver driver;

    @BeforeClass
    public void beforeClass() {
        System.out.println("Before class!");
        System.setProperty("webdriver.chrome.driver", absolutePath);
        driver = new ChromeDriver();
        extentReports = new ExtentReports();
        extentSparkReporter = new ExtentSparkReporter("VideoPlatformTestReport_demo2.html");
        extentReports.setSystemInfo("Environment", "Dev");
        extentReports.setSystemInfo("User Name", "mCarbon");
        extentSparkReporter.config().setDocumentTitle("Video Platform Test Report");
        extentSparkReporter.config().setReportName("Video Platform Test Report");
        extentSparkReporter.config().setTheme(Theme.STANDARD);
        extentSparkReporter.config().setTimelineEnabled(true);
    }

    @AfterClass
    public void afterClass(){ driver.close(); }

    @BeforeMethod
    public void startReport() {
        extentReports.attachReporter(extentSparkReporter);
    }

    @AfterTest
    public void endReport() { extentReports.flush();}

    @Test
    public void TC0001_DashboardTest() throws Exception {
        Dashboard.dashboard(driver,extentReports, "mcarbon", "mcarbon", webUrl);
    }

    @Test
    public void TC0002_LoginTest() throws Exception {
        LoginPage.loginPage(driver, extentReports, webUrl);
    }

    @Test
    public void TC0003_LogOutTest() throws InterruptedException {
        LogoutCheck.logOut(driver, extentReports, "mcarbon", "mcarbon", webUrl);
    }

    @Test
    public void TC0004_ProfileCheck() throws InterruptedException, IOException {
        ProfileCheck.profileCheck(driver, extentReports, "mcarbon", "mcarbon", webUrl);
    }

    @Test
    public void TC0005_SideNavCheck() throws InterruptedException {
        SideNavCheck.sideNavCheck(driver, extentReports, "mcarbon", "mcarbon", webUrl);
    }

    @Test
    public void TC0006_CustomerManagement() throws InterruptedException {
        CustomerManagement.customerManagementCheck(driver, extentReports, "mcarbon", "mcarbon", webUrl);
    }

    @Test
    public void TC0007_MyGroups() throws InterruptedException {
        MyGroups.myGroupsCheck(driver, extentReports, "mcarbon", "mcarbon", webUrl);
    }

    @Test
    public void TC0008_SendLink() throws InterruptedException, IOException {
        SendLink.sendLinkCheck(driver, extentReports, "mcarbon", "mcarbon", webUrl);
    }

    @Test(dependsOnMethods = "TC0006_CustomerManagement")
    public void TC0009_CustomerRolesCheck() throws InterruptedException, IOException {
        CustomerManagement.customerRolesCheck(driver, extentReports, "mcarbon", "mcarbon", webUrl);
    }
}
/*
    @Test
    public void TC0008_CustomerData() throws InterruptedException, IOException {
        parentTest = extentReports.createTest("CUSTOMER DATA VALIDATION");
        final String[] responseBody = {""};
        File file = new File("D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");
        ChromeDriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(file)
                .usingAnyFreePort().build();
        ChromeOptions options = new ChromeOptions().addArguments("--incognito");
        ChromeDriver driver = new ChromeDriver(service, options);
        driver.manage().window().maximize();
        DevTools devTools = driver.getDevTools();

        devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();
        devTools.send(Network.clearBrowserCache());
        devTools.send(Network.setCacheDisabled(true));

        final RequestId[] requestIds = new RequestId[1];
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.of(100000000)));
        DevTools finalDevTools = devTools;
        devTools.addListener(Network.responseReceived(), responseReceived -> {
            requestIds[0] = responseReceived.getRequestId();

            String url = responseReceived.getResponse().getUrl();
            if(url.contains("https://demo2.progate.mobi/VPService/v1/Account/GetAll")) {

//                String responseBody = finalDevTools.send(Network.getResponseBody(requestIds[0])).getBody();
                responseBody[0] = finalDevTools.send(Network.getResponseBody(requestIds[0])).getBody();
                int chunkSize = 500;

                for (int i = 0; i < responseBody[0].length(); i += chunkSize) {
                    int endIndex = Math.min(i + chunkSize, responseBody[0].length());
                    String chunk = responseBody[0].substring(i, endIndex);
                    System.out.println("Chunk: " + chunk);
                }
                System.out.println("Response Body :" + responseBody[0]);
            }});


        JavascriptExecutor js = (JavascriptExecutor) driver;
        NgWebDriver ngWebDriver = new NgWebDriver(js);
        driver.get(webUrl);
        driver.manage().window().maximize();
        login(driver);
        Thread.sleep(5000);
        driver.findElement(By.id("customer_management")).click();
        Thread.sleep(15000);
        if(responseBody.length == 0){
            fail();
        }
        dataPreProcessing(responseBody[0],driver);
        List<WebElement> tableRows = driver.findElements(By.tagName("tr"));
        for (WebElement element : tableRows) {
            System.out.println("Attribute name : " + element.getAttribute("class"));
        }
        Thread.sleep(500);
        driver.close();
    }


*/
//    public void dataPreProcessing(String responseBody, ChromeDriver driver) throws IOException {
//        if(responseBody == ""){
//            fail("Empty response body!");
//        }
//        JSONParser parser = new JSONParser();
//        try {
//            Object object = (Object) parser.parse(responseBody);
//            JSONArray jsonArray = (JSONArray) object;
//
//            for(int i=0; i<1;i++) {
//                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//                Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
//                for(Map.Entry<String, JsonElement> entry : entries) {
////                    System.out.println(entry.getKey() + " -> "+entry.getValue());
//                    checkAccountData(entry,driver);
//                }
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//    }
//    public void checkAccountData(Map.Entry<String, JsonElement> entry, ChromeDriver driver){
//        String name = driver.findElement(By.cssSelector("table>tbody>tr:nth-child(1)>td.mat-cell.cdk-cell.cdk-column-name.mat-column-name.ng-star-inserted")).getText();
//        String expDate = driver.findElement(By.cssSelector("table>tbody>tr:nth-child(1)>td.mat-cell.cdk-cell.cdk-column-expDate.mat-column-expDate.ng-star-inserted")).getText();
//        System.out.println("Name : "+ name);
//        System.out.println("getKey : "+entry.getKey());
//        System.out.println("getValue : "+entry.getValue());
//        if(entry.getKey().equalsIgnoreCase("name")){
//            if(!name.equalsIgnoreCase(String.valueOf(entry.getValue()))){
//                fail("Name not matched!");
//            }
//            System.out.println("Name matched!");
//        }
//        if(entry.getKey().equalsIgnoreCase("expDate")){
//            if(!expDate.equalsIgnoreCase(String.valueOf(entry.getValue()))){
//                fail("ExpDate not matched!");
//            }
//            System.out.println("ExpDate matched!");
//        }
//    }


//    @Test
//    public void getLogs() {
//
//        // pass the path of the chromedriver location in the second argument
//        System.setProperty("webdriver.chrome.driver", "");
//
//        ChromeOptions options = new ChromeOptions();
//        LoggingPreferences logPrefs = new LoggingPreferences();
//        logPrefs.enable(LogType.BROWSER, Level.ALL);
//        options.setCapability(options.LOGGING_PREFS, logPrefs);
//
//        WebDriver driver = new ChromeDriver(options);
//        driver.get("https://testkru.com/TestUrls/TestConsoleLogs");
//
//        LogEntries entry = driver.manage().logs().get(LogType.BROWSER);
//
//        // Retrieving all logs
//        List<LogEntry> logs = entry.getAll();
//
//        // Printing details separately
//        for (LogEntry e : logs) {
//            System.out.println("Message: " + e.getMessage());
//            System.out.println("Level: " + e.getLevel());
//            System.out.println("Timestamp: "+ e.getTimestamp());
//        }
//
//    }


