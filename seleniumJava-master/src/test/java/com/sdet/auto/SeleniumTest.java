package com.sdet.auto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paulhammant.ngwebdriver.ByAngular;
import com.paulhammant.ngwebdriver.NgWebDriver;
import com.sdet.auto.PageObjects.*;
import com.sdet.auto.TestHelper.ConfigSettings;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v108.network.Network;
import org.openqa.selenium.devtools.v108.network.model.RequestId;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.fail;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SeleniumTest extends TestBaseClass{

    @Test
    public void TC0000_SmokeTest() {
        GuiHelper.openWebBrowser();
        Navigation.navToWebPageUnderTest();
        GuiHelper.closeWebBrowser();
    }

    @Test
    public void TC0001_PageLogin() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Vatsala\\Desktop\\seleniumJava-master\\src\\main\\resources\\chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get(ConfigSettings.getWebUrl());
        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");
        driver.findElement(By.className("login-button")).click();
        Thread.sleep(2000);
        driver.close();
    }

    @Test
    public void TC0002_LoginBadInfo() throws InterruptedException {

        final String loginId = "sdetAutomatiom";
        final String password = "pass@word";
        final String expectedMsg = "Invalid username or password!";

        GuiHelper.openWebBrowser();
        Navigation.navToWebPageUnderTest();
        LoginPage.enterCredentials(loginId, password);
        LoginPage.verifyMessage(testAssert, expectedMsg);
        Thread.sleep(2000);
        GuiHelper.closeWebBrowser();
    }
    @Test
    public void TC0003_LogOutCheck() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get(ConfigSettings.getWebUrl());
        driver.manage().window().maximize();
        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");
        driver.findElement(By.className("login-button")).click();
        Thread.sleep(2000);
        List<WebElement> elementA = driver.findElements(By.tagName("a"));

        for (WebElement element1 : elementA){
//            System.out.println("Elements : "+element1.getAttribute("class"));
            if(element1.getAttribute("class").contains("mat-menu-trigger nav-link")){
                System.out.println("ICON PRESSED");
                element1.click();
                Thread.sleep(2000);
                driver.findElement(By.cssSelector("div#mat-menu-panel-0>div>button[class='mat-focus-indicator btn-action btn-logout mat-menu-item ng-tns-c42-2']")).click();
                System.out.println("LogOut button pressed!");
                Thread.sleep(2000);
                break;
            }
        }
        driver.close();
    }

    @Test
    public void TC0004_ProfileCheck() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get(ConfigSettings.getWebUrl());
        driver.manage().window().maximize();
        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");
        driver.findElement(By.className("login-button")).click();
        Thread.sleep(2000);
        List<WebElement> elementA = driver.findElements(By.tagName("a"));

        for (WebElement element1 : elementA){
//            System.out.println("Elements : "+element1.getAttribute("class"));
            if(element1.getAttribute("class").contains("mat-menu-trigger nav-link")){
                System.out.println("ICON PRESSED");
                element1.click();
                Thread.sleep(2000);
                driver.findElement(By.cssSelector("div#mat-menu-panel-0>div>button[class='mat-focus-indicator btn-profile btn-action mat-menu-item ng-tns-c42-2']")).click();
                System.out.println("Profile button pressed!");
                Thread.sleep(2000);
                break;
            }
        }
        driver.close();
    }
    @Test
    public void TC0005_SideNavCheck() throws InterruptedException {

        AtomicReference<String> responseBody= new AtomicReference<>("");
        File file = new File("D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");

//        Level logLevel = Level.INFO;
//        System.setProperty("webdriver.chrome.logfile", "selenium.log");
//        System.setProperty("webdriver.chrome.verboseLogging", "true");
//        Logger logger = Logger.getLogger("");
//        logger.setLevel(logLevel);

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
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        DevTools finalDevTools = devTools;

        devTools.addListener(Network.requestWillBeSent(), request -> {

            System.out.println("Request: " + request.getRequest().getUrl());

        });
        devTools.addListener(Network.responseReceived(), responseReceived -> {
            System.out.println("Network event :"+ responseReceived.getRequestId());
            requestIds[0] = responseReceived.getRequestId();
            String url = responseReceived.getResponse().getUrl();

            int status = responseReceived.getResponse().getStatus();
            String type = responseReceived.getType().toJson();
            String headers = responseReceived.getResponse().getHeaders().toString();
            if(url.contains("/VPService/v1/User/login")) {

                responseBody.set(finalDevTools.send(Network.getResponseBody(requestIds[0])).getBody());
                System.out.println("Response Body :"+responseBody);

            }
        });
        
        driver.get(ConfigSettings.getWebUrl());
        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");

        testLoginResponseValidation(driver);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-button")));
        loginButton.click();

        Thread.sleep(5000);

        try {
            List<String> accessValues = getAccessData(responseBody.get());
//                    driver.get("https://demo2.progate.mobi/#/app/dashboard");
//                    System.out.println("SystemNames : "+driver.getScreenshotAs(OutputType.FILE));
            List<WebElement> sidebarElements = driver.findElements(By.tagName("li"));
//                    WebDriverExtensions.screenshot(driver);
//                    System.out.println("Find ele : "+ driver.findElements(By.tagName("li")));
            if(sidebarElements.isEmpty()){
//                        fail("Unexpected error");
                Assert.assertTrue(true);
            }
            for (WebElement element : sidebarElements) {
                System.out.println("Attribute name : "+element.getAttribute("class"));
                if (element.getAttribute("class").contains("dashboard nav-item ng-star-inserted active")) {
                    System.out.println("DASHBOARD");
                    if(!accessValues.contains("dashboard"))
                        fail();
                }
                if (element.getAttribute("class").contains("customer_management nav-item ng-star-inserted")) {
                    System.out.println("CUSTOMER MANAGEMENT");
                    if(!accessValues.contains("customer_management"))
                        fail();
                }
                if (element.getAttribute("class").contains("my_users nav-item ng-star-inserted")) {
                    System.out.println("MY GROUPS");
                    if(!accessValues.contains("my_users"))
                        fail();
                }
                if (element.getAttribute("class").contains("my_sessions nav-item ng-star-inserted")) {
                    System.out.println("MY SESSIONS");
                    if(!accessValues.contains("my_sessions"))
                        fail();
                }
                if (element.getAttribute("class").contains("dynamic_links nav-item ng-star-inserted")) {
                    System.out.println("DYNAMIC LINKS");
                    if(!accessValues.contains("dynamic_links"))
                        fail();
                }
                if (element.getAttribute("class").contains("my_reports nav-item ng-star-inserted")) {
                    System.out.println("MY REPORTS");
                    if(!accessValues.contains("my_reports"))
                        fail();
                }
                if (element.getAttribute("class").contains("nav-item platform_access ng-star-inserted")) {
                    System.out.println("PLATFORM ACCESS");
                    if(!accessValues.contains("manage_platform_access"))
                        fail();
                }
                if (element.getAttribute("class").contains("nav-item platform_feature ng-star-inserted")) {
                    System.out.println("PLATFORM FEATURE");
                    if(!accessValues.contains("manage_platform_feature"))
                        fail();
                }
                if (element.getAttribute("class").contains("feedform_form nav-item ng-star-inserted")) {
                    System.out.println("FEEDBACK");
                    if(!accessValues.contains("feedback")){
                        System.out.println("FEEDBACK FAIL");
                        fail();
                        break;
                    }
                }
            }
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            fail("Unexpected error");
        }

        driver.close();
    }

    @Test
    public void TC0006_CustomerManagement() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");
        WebDriver driver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        NgWebDriver ngWebDriver = new NgWebDriver(js);
        driver.get(ConfigSettings.getWebUrl());
        driver.manage().window().maximize();
        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");
        driver.findElement(By.className("login-button")).click();
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("div.sidebar-wrapper>ul[class='nav ng-star-inserted']>li[class='customer_management nav-item ng-star-inserted']")).click();
        Thread.sleep(1000);
        driver.findElement(ByAngular.partialButtonText("Create")).click();
//        driver.findElement(By.cssSelector("button[class='mat-focus-indicator create-btn mat-raised-button mat-button-base ng-star-inserted']")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("mat-tab-label-0-0")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("mat-tab-label-0-1")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("mat-tab-label-0-2")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("mat-tab-label-0-3")).click();
        Thread.sleep(1000);
        driver.findElement(ByAngular.partialButtonText("Submit")).click();
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("button[class='mat-focus-indicator close-btn mat-icon-button mat-button-base']")).click();
        driver.close();

    }

    @Test
    public void TC0006_MyGroups() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get(ConfigSettings.getWebUrl());
        driver.manage().window().maximize();
        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");
        driver.findElement(By.className("login-button")).click();
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("div.sidebar-wrapper>ul[class='nav ng-star-inserted']>li[class='my_users nav-item ng-star-inserted']")).click();
        Thread.sleep(1000);
        driver.findElement(ByAngular.partialButtonText("Create")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("mat-tab-label-0-0")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("mat-tab-label-0-1")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("mat-tab-label-0-2")).click();
        Thread.sleep(1000);
        driver.findElement(By.id("mat-tab-label-0-3")).click();
        Thread.sleep(1000);
        driver.findElement(ByAngular.partialButtonText("Submit")).click();
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("button[class='mat-focus-indicator close-btn mat-icon-button mat-button-base']")).click();
        driver.close();

    }

    @Test
    public void TC0007_SendLink() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get(ConfigSettings.getWebUrl());
        driver.manage().window().maximize();
        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");
        driver.findElement(By.className("login-button")).click();
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("div.sidebar-wrapper>ul[class='nav ng-star-inserted']>li[class='dynamic_links nav-item ng-star-inserted']")).click();
        Thread.sleep(1000);
        driver.close();
    }

    @Test
    public void TC0008_CustomerData() throws InterruptedException {
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
//            System.out.println("Network event :"+ responseReceived.getRequestId());
            requestIds[0] = responseReceived.getRequestId();
            String url = responseReceived.getResponse().getUrl();
            System.out.println("Url : "+url);
            if(url.contains("https://demo2.progate.mobi/VPService/v1/Account/GetAll")) {

                String responseBody = finalDevTools.send(Network.getResponseBody(requestIds[0])).getBody();
                System.out.println("Response Body :" + responseBody);

            }});
        JavascriptExecutor js = (JavascriptExecutor) driver;
        NgWebDriver ngWebDriver = new NgWebDriver(js);
        driver.get(ConfigSettings.getWebUrl());
        driver.manage().window().maximize();
        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");
        driver.findElement(By.className("login-button")).click();
        Thread.sleep(5000);
        driver.findElement(By.cssSelector("div.sidebar-wrapper>ul[class='nav ng-star-inserted']>li[class='customer_management nav-item ng-star-inserted']")).click();
        Thread.sleep(10000);
        List<WebElement> tableRows = driver.findElements(By.tagName("tr"));
        for (WebElement element : tableRows) {
            System.out.println("Attribute name : " + element.getAttribute("class"));

        }
        Thread.sleep(2000);
        driver.close();
    }

    public Boolean testLoginResponseValidation(ChromeDriver driver){
        System.out.println("Driver : "+driver.getCurrentUrl());
        return true;
    }
//    @Test
//    public void getLogs() {
//
//        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Vatsala\\Desktop\\seleniumJava-master\\src\\main\\resources\\chromedriver");
//        WebDriver driver = new ChromeDriver();
//
//        DevTools devTools = ((ChromeDriver) driver).getDevTools();
//        devTools.createSession();
//
//        devTools.send(Network.enable(Optional.of(1000000), Optional.empty(), Optional.empty()));

//        devTools.addListener(Network.requestWillBeSent(), request -> {
//            if (request.getRequest().getMethod().equalsIgnoreCase("POST")) {
//                System.out.println("Request URL : " + request.getRequest().getUrl());
//                System.out.println("Request body: " + request.getRequest().getPostData());
//                System.out.println("Request headers: " + request.getRequest().getHeaders().toString());
//            }
//            System.out.println("Request Method : " + request.getRequest().getMethod().equalsIgnoreCase("POST"));
//            System.out.println("Request isPostDataPresent : " + request.getRequest().getHasPostData());
//        });

//        devTools.addListener(Network.loadingFinished(), response ->
//        {
//            String body = devTools.send(Network.getResponseBody(response.getRequestId())).getBody();
//            System.out.println(body);
//        });
//
//        driver.get(ConfigSettings.getWebUrl());
//        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
//        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");
//        driver.findElement(By.className("login-button")).click();


//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//            WebElement liElement = wait.until(ExpectedConditions.elementToBeClickable(By.className("dashboard")));
//            liElement.click();


//    }

    public List<String> getAccessData(String responseBody) throws IOException {
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(responseBody,JsonObject.class);
        System.out.println("Params : "+params);
        ObjectMapper objectMapper=new ObjectMapper();
        if(params.isJsonNull()) return null;

       List<String>sysName = new ArrayList<>();
        if(!params.get("Access").isJsonNull()){
            List<HashMap> dataAsMap = objectMapper.readValue(params.get("Access").toString(), List.class);
            for(Map<String,String>map : dataAsMap){
                sysName.add(map.get("systemName"));
            }
        }
        return sysName;
    }


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
//@Test
//public void getLogs() {
//
//    // pass the path of the chromedriver location in the second argument
////    System.setProperty("webdriver.chrome.driver", "");
//
//    WebDriver driver = new ChromeDriver();
//    driver.get("https://demo2.progate.mobi/#/");
//
//    LogEntries entry = driver.manage().logs().get(LogType.BROWSER);
//
//    // Retrieving all logs
//    List<LogEntry> logs = entry.getAll();
//
//    // Printing details separately
//    for (LogEntry e : logs) {
//        System.out.println("Message: " + e.getMessage());
//        System.out.println("Level: " + e.getLevel());
//        System.out.println("Timestamp: " + e.getTimestamp());
//    }
//
//}


}
