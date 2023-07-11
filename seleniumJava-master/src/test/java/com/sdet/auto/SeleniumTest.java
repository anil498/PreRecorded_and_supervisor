package com.sdet.auto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paulhammant.ngwebdriver.ByAngular;
import com.paulhammant.ngwebdriver.NgWebDriver;
import com.sdet.auto.PageObjects.*;
import com.sdet.auto.TestHelper.ConfigSettings;
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
        ChromeDriver driver = new ChromeDriver();
        driver.get(ConfigSettings.getWebUrl());
        login(driver);
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
        ChromeDriver driver = new ChromeDriver();
        driver.get(ConfigSettings.getWebUrl());
        driver.manage().window().maximize();
        login(driver);
        Thread.sleep(1000);
        List<WebElement> elementA = driver.findElements(By.tagName("a"));

        for (WebElement element1 : elementA){
//            System.out.println("Elements : "+element1.getAttribute("class"));
            if(element1.getAttribute("class").contains("mat-menu-trigger nav-link")){
                System.out.println("ICON PRESSED");
                element1.click();
                Thread.sleep(1000);
                driver.findElement(By.cssSelector("div#mat-menu-panel-0>div>button[class='mat-focus-indicator btn-action btn-logout mat-menu-item ng-tns-c42-2']")).click();
                System.out.println("LogOut button pressed!");
                Thread.sleep(1000);
                break;
            }
        }
        driver.close();
    }

    @Test
    public void TC0004_ProfileCheck() throws InterruptedException, IOException {
        AtomicReference<String> responseBody= new AtomicReference<>("");
        File file = new File("D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");

        ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(file).usingAnyFreePort().build();
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

        devTools.addListener(Network.responseReceived(), responseReceived -> {
//            System.out.println("Network event :"+ responseReceived.getRequestId());
            requestIds[0] = responseReceived.getRequestId();
            String url = responseReceived.getResponse().getUrl();
            if(url.contains("/VPService/v1/User/login")) {
                responseBody.set(finalDevTools.send(Network.getResponseBody(requestIds[0])).getBody());
                System.out.println("Response Body :"+responseBody);
            }
        });
        driver.get(ConfigSettings.getWebUrl());
        login(driver);
        Thread.sleep(2000);
        driver.findElement(By.cssSelector("#user-menu")).click();
        Thread.sleep(1000);
        driver.findElement(ByAngular.partialButtonText("Profile")).click();
        Thread.sleep(1000);
        System.out.println("Profile button pressed!");
        Thread.sleep(1000);
        List<WebElement> elementA = driver.findElements(By.tagName("h6"));
        checkAccessAndFeatures(responseBody,elementA);
        driver.close();
    }

    @Test
    public void TC0005_SideNavCheck() throws InterruptedException {

        AtomicReference<String> responseBody= new AtomicReference<>("");
        File file = new File("D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");

        ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(file).usingAnyFreePort().build();
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

        devTools.addListener(Network.responseReceived(), responseReceived -> {
            System.out.println("Network event :"+ responseReceived.getRequestId());
            requestIds[0] = responseReceived.getRequestId();
            String url = responseReceived.getResponse().getUrl();

            if(url.contains("/VPService/v1/User/login")) {
                responseBody.set(finalDevTools.send(Network.getResponseBody(requestIds[0])).getBody());
                System.out.println("Response Body :"+responseBody);
            }
        });
        driver.get(ConfigSettings.getWebUrl());
        login(driver);
        Thread.sleep(5000);
        checkAccess(driver,responseBody);
        driver.close();
    }

    @Test
    public void TC0006_CustomerManagement() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");
        ChromeDriver driver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        NgWebDriver ngWebDriver = new NgWebDriver(js);
        driver.get(ConfigSettings.getWebUrl());
        driver.manage().window().maximize();
        login(driver);
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

//                String responseBody = finalDevTools.send(Network.getResponseBody(requestIds[0])).getBody();
                String responseBody = finalDevTools.send(Network.getResponseBody(requestIds[0])).getBody();
                int chunkSize = 500;

                for (int i = 0; i < responseBody.length(); i += chunkSize) {
                    int endIndex = Math.min(i + chunkSize, responseBody.length());
                    String chunk = responseBody.substring(i, endIndex);
                    System.out.println("Chunk: " + chunk);
                }
                System.out.println("Response Body :" + responseBody);
            }});
        JavascriptExecutor js = (JavascriptExecutor) driver;
        NgWebDriver ngWebDriver = new NgWebDriver(js);
        driver.get(ConfigSettings.getWebUrl());
        driver.manage().window().maximize();
        login(driver);
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

    @Test
    public void TC0009_CustomerRolesCheck() throws InterruptedException, IOException {

        AtomicReference<String> responseBody= new AtomicReference<>("");
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
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        DevTools finalDevTools = devTools;

        devTools.addListener(Network.responseReceived(), responseReceived -> {
//            System.out.println("Network event :"+ responseReceived.getRequestId());
            requestIds[0] = responseReceived.getRequestId();
            String url = responseReceived.getResponse().getUrl();
            if(url.contains("/VPService/v1/User/login")) {
                responseBody.set(finalDevTools.send(Network.getResponseBody(requestIds[0])).getBody());
//                System.out.println("Response Body :"+responseBody);
            }
        });

        JavascriptExecutor js = (JavascriptExecutor) driver;
        NgWebDriver ngWebDriver = new NgWebDriver(js);
        driver.get(ConfigSettings.getWebUrl());
        driver.manage().window().maximize();
        login(driver);
        Thread.sleep(3000);
        driver.findElement(By.cssSelector("div.sidebar-wrapper>ul[class='nav ng-star-inserted']>li[class='customer_management nav-item ng-star-inserted']")).click();
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("div.card-body>div>table>tbody>tr:nth-child(1)>td.mat-cell.cdk-cell.cdk-column-Action.mat-column-Action.ng-star-inserted>button")).click();
        Thread.sleep(1000);
        checkRoles(driver,responseBody);
        driver.findElement(ByAngular.partialButtonText("View")).click();
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("#mat-tab-label-5-1")).click();
        Thread.sleep(1000);
        driver.close();
    }
    public void dataPreProcessing() throws IOException {

    }
    public List<String> getAccessData(String responseBody) throws IOException {
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(responseBody,JsonObject.class);
//        System.out.println("Params : "+params);
        ObjectMapper objectMapper=new ObjectMapper();
        if(params.isJsonNull()) return null;

       List<String>name = new ArrayList<>();
        if(!params.get("Access").isJsonNull()){
            List<HashMap> dataAsMap = objectMapper.readValue(params.get("Access").toString(), List.class);
            for(Map<String,String>map : dataAsMap){
                name.add(map.get("name"));
            }
        }
        return name;
    }
    public List<String> getFeatureData(String responseBody) throws IOException {
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(responseBody,JsonObject.class);
        System.out.println("Params : "+params);
        ObjectMapper objectMapper=new ObjectMapper();
        if(params.isJsonNull()) return null;
        List<String>name = new ArrayList<>();
        if(!params.get("Features").isJsonNull()){
            List<HashMap> dataAsMap = objectMapper.readValue(params.get("Features").toString(), List.class);
            for(Map<String,String>map : dataAsMap){
                name.add(map.get("name"));
            }
        }
        return name;
    }

    public void login(ChromeDriver driver){
        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-button")));
        loginButton.click();
    }
    public void checkRoles(ChromeDriver driver,AtomicReference<String> responseBody) {
        try {
            List<String> accessValues = getAccessData(responseBody.get());
                if (driver.findElement(ByAngular.partialButtonText("Edit")).isDisplayed()) {
                    System.out.println("EDIT");
                    if (!accessValues.contains("customer_update")) fail();
                }
            if (driver.findElement(ByAngular.partialButtonText("Delete")).isDisplayed()) {
                System.out.println("DELETE");
                if (!accessValues.contains("customer_delete")) fail();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void checkAccess(ChromeDriver driver,AtomicReference<String> responseBody){
        try {
            List<String> accessValues = getAccessData(responseBody.get());
            List<WebElement> sidebarElements = driver.findElements(By.tagName("li"));
            if(sidebarElements.isEmpty()){ fail(); }
            for (WebElement element : sidebarElements) {
                System.out.println("Attribute name : "+element.getAttribute("class"));
                if (element.getAttribute("class").contains("dashboard nav-item ng-star-inserted active")) { if(!accessValues.contains("Dashboard")) fail(); }
                else if (element.getAttribute("class").contains("customer_management nav-item ng-star-inserted")) { if(!accessValues.contains("Customer Management")) fail(); }
                else if (element.getAttribute("class").contains("my_users nav-item ng-star-inserted")) { if(!accessValues.contains("My Groups")) fail(); }
                else if (element.getAttribute("class").contains("my_sessions nav-item ng-star-inserted")) { if(!accessValues.contains("My Sessions")) fail(); }
                else if (element.getAttribute("class").contains("dynamic_links nav-item ng-star-inserted")) { if(!accessValues.contains("Send Link")) fail(); }
                else if (element.getAttribute("class").contains("my_reports nav-item ng-star-inserted")) { if(!accessValues.contains("My Reports")) fail(); }
                else if (element.getAttribute("class").contains("nav-item platform_access ng-star-inserted")) { if(!accessValues.contains("Platform Access")) fail(); }
                else if (element.getAttribute("class").contains("nav-item platform_feature ng-star-inserted")) { if(!accessValues.contains("Platform Feature")) fail(); }
                else if (element.getAttribute("class").contains("feedform_form nav-item ng-star-inserted")) { if(!accessValues.contains("Feedback")) fail(); }
                }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            fail("Unexpected error");
        }
    }

    private void checkAccessAndFeatures(AtomicReference<String> responseBody, List<WebElement> elements) {
        try{
            List<String> accessValues = getAccessData(responseBody.get());
            List<String> featureValues = getFeatureData(responseBody.get());
            if(elements.isEmpty()){ fail(); }
            for (WebElement element : elements) {
                if (element.getText().equalsIgnoreCase("dashboard")) { if(!accessValues.contains("Dashboard")) fail(); }
                else if (element.getText().equalsIgnoreCase("Customer Management")) { if(!accessValues.contains("Customer Management")) fail(); }
                else if (element.getText().equalsIgnoreCase("My Groups")) { if(!accessValues.contains("My Groups")) fail(); }
                else if (element.getText().equalsIgnoreCase("My Sessions")) { if(!accessValues.contains("My Sessions")) fail(); }
                else if (element.getText().equalsIgnoreCase("Send Link")) { if(!accessValues.contains("Send Link")) fail(); }
                else if (element.getText().equalsIgnoreCase("MY Reports")) { if(!accessValues.contains("My Reports")) fail(); }
                else if (element.getText().equalsIgnoreCase("Platform Access")) { if(!accessValues.contains("Platform Access")) fail(); }
                else if (element.getText().equalsIgnoreCase("Platform Feature")) { if(!accessValues.contains("Platform Feature")) fail(); }
                else if (element.getText().equalsIgnoreCase("Customer Creation")) { if(!accessValues.contains("Customer Creation")) fail(); }
                else if (element.getText().equalsIgnoreCase("Customer Update")) { if(!accessValues.contains("Customer Update")) fail(); }
                else if (element.getText().equalsIgnoreCase("Customer Delete")) { if(!accessValues.contains("Customer Delete")) fail(); }
                else if (element.getText().equalsIgnoreCase("Group Creation")) { if(!accessValues.contains("Group Creation")) fail(); }
                else if (element.getText().equalsIgnoreCase("Group Delete")) { if(!accessValues.contains("Group Delete")) fail(); }
                else if (element.getText().equalsIgnoreCase("Session Create")) { if(!accessValues.contains("Session Create")) fail(); }
                else if (element.getText().equalsIgnoreCase("Whatsapp")) { if(!accessValues.contains("Whatsapp")) fail(); }
                else if (element.getText().equalsIgnoreCase("Send Notification")) { if(!accessValues.contains("Send Notification")) fail(); }
                else if (element.getText().equalsIgnoreCase("SMS")) { if(!accessValues.contains("SMS")) fail(); }
                else if (element.getText().equalsIgnoreCase("Report Review")) { if(!accessValues.contains("Report Review")) fail(); }
                else if (element.getText().equalsIgnoreCase("Report Download")) { if(!accessValues.contains("Report Download")) fail(); }

                else if (element.getText().equalsIgnoreCase("Recording")) { if(!featureValues.contains("Recording")) fail(); }
                else if (element.getText().equalsIgnoreCase("Screen Share")) { if(!featureValues.contains("Screen Share")) fail(); }
                else if (element.getText().equalsIgnoreCase("Chat")) { if(!featureValues.contains("Chat")) fail(); }
                else if (element.getText().equalsIgnoreCase("Pre Recorded Video")) { if(!featureValues.contains("Pre Recorded Video")) fail(); }
                else if (element.getText().equalsIgnoreCase("Participant Ticker")) { if(!featureValues.contains("Participant Ticker")) fail(); }
                else if (element.getText().equalsIgnoreCase("Customer Info To Agent")) { if(!featureValues.contains("Customer info to Agent")) fail(); }
                else if (element.getText().equalsIgnoreCase("Customised Layout")) { if(!featureValues.contains("Customised Layout")) fail(); }
                else if (element.getText().equalsIgnoreCase("Add Supervisor")) { if(!featureValues.contains("Add Supervisor")) fail(); }
                else if (element.getText().equalsIgnoreCase("Session Timer")) { if(!featureValues.contains("Session Timer")) fail(); }
                else if (element.getText().equalsIgnoreCase("Session Activities")) { if(!featureValues.contains("Session Activities")) fail(); }
                else if (element.getText().equalsIgnoreCase("Session Participants")) { if(!featureValues.contains("Session Participants")) fail(); }
                else if (element.getText().equalsIgnoreCase("Agent Landing Page URL")) { if(!featureValues.contains("Agent Landing Page URL")) fail(); }
                else if (element.getText().equalsIgnoreCase("Participant Landing Page URL")) { if(!featureValues.contains("Participant Landing Page URL")) fail(); }
                else if (element.getText().equalsIgnoreCase("In Call Data Collection")) { if(!featureValues.contains("In Call Data Collection")) fail(); }
            }
            Thread.sleep(1000);
    } catch (IOException | InterruptedException e) {
        e.printStackTrace();
    } catch (Exception e) {
        fail("Unexpected error");
    }
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

}
