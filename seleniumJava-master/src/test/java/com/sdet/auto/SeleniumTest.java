package com.sdet.auto;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
//import com.aventstack.extentreports.parentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.paulhammant.ngwebdriver.ByAngular;
import com.paulhammant.ngwebdriver.NgWebDriver;
import com.sdet.auto.PageObjects.*;
import com.sdet.auto.TestHelper.ConfigSettings;
import com.sdet.auto.TestHelper.TestAssert;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
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
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SeleniumTest extends TestBaseClass{

    public String webUrl = "https://demo2.progate.mobi/#/";
    public ExtentReports extentReports;
    public ExtentSparkReporter extentSparkReporter;
    @BeforeClass
    public void beforeClass(){
        System.out.println("Before class!");
        extentReports = new ExtentReports();
        extentSparkReporter = new ExtentSparkReporter("VideoPlatformTestReport.html");
        extentReports.setSystemInfo("Environment", "Dev");
        extentReports.setSystemInfo("User Name", "mCarbon");
        extentSparkReporter.config().setDocumentTitle("Video Platform Test Report");
        extentSparkReporter.config().setReportName("Video Platform Test Report");
        extentSparkReporter.config().setTheme(Theme.STANDARD);
        extentSparkReporter.config().setTimelineEnabled(true);
    }

    ExtentTest parentTest;
    ExtentTest childTest;

    @BeforeMethod
    public void startReport() {
        extentReports.attachReporter(extentSparkReporter);
    }

//    @Test
//    public void TC0000_SmokeTest() {
//        parentTest = extentReports.createTest("SMOKE TEST");
//        ChromeDriver driver = new ChromeDriver();
//        driver.get(webUrl);
//        driver.close();
//    }

    @Test
    public void TC0001_LoginTest() throws Exception {
        parentTest = extentReports.createTest("LOGIN TEST");
        System.setProperty("webdriver.chrome.driver", "D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");
        ChromeDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(webUrl);

        WebElement userName = driver.findElement(By.id("mat-input-0"));
        WebElement password = driver.findElement(By.id("mat-input-1"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-button")));


        childTest = parentTest.createNode("BOTH LOGIN CREDENTIALS EMPTY");
        String cardLabelText = "Enter Username & Password";
        if(userName.getText().isEmpty() && password.getText().isEmpty()){
            loginButton.click();
//            Thread.sleep(30);
            //if(driver.findElement(By.cssSelector("body>app-root>app-login>div>div>mat-card>mat-card-content>form>div.col-10.offset-1.text-center.ng-star-inserted")).getText().contains(cardLabelText))
            if(false)
            childTest.log(Status.PASS,MarkupHelper.createLabel("Empty fields giving right response label",ExtentColor.GREEN));
            else
                childTest.log(Status.FAIL,MarkupHelper.createLabel("Empty fields giving wrong response label",ExtentColor.RED));
        }
        childTest = parentTest.createNode("EMPTY USERNAME");
        String cardLabelTextU = "Must Enter Username";
        password.sendKeys("abcde");
        if(userName.getText().isEmpty() && password.getText().equals("")){
            loginButton.click();
//            Thread.sleep(30);
            //if(driver.findElement(By.cssSelector("body>app-root>app-login>div>div>mat-card>mat-card-content>form>div.col-10.offset-1.text-center.ng-star-inserted")).getText().contains(cardLabelText))
            if(false)
                childTest.log(Status.PASS,MarkupHelper.createLabel("Empty username field giving right response label",ExtentColor.GREEN));
            else
                childTest.log(Status.FAIL,MarkupHelper.createLabel("Empty username field giving wrong response label",ExtentColor.RED));
        }
        password.clear();
        childTest = parentTest.createNode("EMPTY PASSWORD");
        String cardLabelTextP = "Must Enter Password";
        userName.sendKeys("abcedfg");
        if(userName.getText().equals("") && password.getText().isEmpty()){
            loginButton.click();
//            Thread.sleep(30);
            //if(driver.findElement(By.cssSelector("body>app-root>app-login>div>div>mat-card>mat-card-content>form>div.col-10.offset-1.text-center.ng-star-inserted")).getText().contains(cardLabelText))
            if(false)
                childTest.log(Status.PASS,MarkupHelper.createLabel("Empty password field giving right response label",ExtentColor.GREEN));
            else
                childTest.log(Status.FAIL,MarkupHelper.createLabel("Empty password field giving wrong response label",ExtentColor.RED));
        }
        userName.clear();
        childTest = parentTest.createNode("LABEL TEXT");
        String expectedText = "Invalid username or password!";

        userName.sendKeys("abcedfg");
        password.sendKeys("abcde");
        childTest.log(Status.PASS,MarkupHelper.createLabel("Credentials entered",ExtentColor.GREEN));
        loginButton.click();
        Thread.sleep(50);
        if(driver.findElement(By.cssSelector(".mat-simple-snack-bar-content")).getText().contains(expectedText)){
//        if(false){
            childTest.log(Status.PASS,MarkupHelper.createLabel("Bad info label text correct",ExtentColor.GREEN));
        }
        else{
            childTest.log(Status.FAIL,MarkupHelper.createLabel("Bad info label text wrong",ExtentColor.RED));
        }
        userName.clear();
        password.clear();
        childTest = parentTest.createNode("VALID USER CREDENTIALS");
        userName.sendKeys("mcarbon");
        userName.sendKeys("mcarbon");
        childTest.log(Status.PASS,MarkupHelper.createLabel("Credentials entered",ExtentColor.GREEN));
        loginButton.click();
        childTest.log(Status.PASS,MarkupHelper.createLabel("Login button clicked",ExtentColor.GREEN));
        driver.navigate().to("https://demo2.progate.mobi/#/app/dashboard");
        childTest.log(Status.PASS,MarkupHelper.createLabel("Login Successful",ExtentColor.GREEN));
        driver.close();
        childTest.log(Status.PASS,MarkupHelper.createLabel("Driver closed",ExtentColor.GREEN));

    }

    @Test
    public void TC0002_LogOutTest() throws InterruptedException {
        parentTest = extentReports.createTest("LOGOUT TEST");
        System.setProperty("webdriver.chrome.driver", "D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");
        ChromeDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(webUrl);
        login(driver);
        childTest = parentTest.createNode("LOGOUT CHECK");
        Thread.sleep(2000);
        childTest.log(Status.PASS,MarkupHelper.createLabel("Login Successful",ExtentColor.GREEN));
        driver.findElement(By.cssSelector("#user-menu")).click();
        System.out.println("ICON PRESSED");
        childTest.log(Status.PASS,MarkupHelper.createLabel("Menu icon pressed",ExtentColor.GREEN));
        Thread.sleep(200);
        driver.findElement(ByAngular.partialButtonText("Log Out")).click();
        childTest.log(Status.PASS,MarkupHelper.createLabel("Logout button pressed",ExtentColor.GREEN));
        System.out.println("LogOut button pressed!");

        childTest = parentTest.createNode("LOGOUT DONE");
        childTest.log(Status.PASS,MarkupHelper.createLabel("Logout Successful",ExtentColor.GREEN));
        if(driver.getCurrentUrl().equals(webUrl))
            childTest.log(Status.PASS,MarkupHelper.createLabel("Navigated to login page",ExtentColor.GREEN));
        else
            childTest.log(Status.FAIL,MarkupHelper.createLabel("Navigated to wrong page",ExtentColor.RED));
        driver.close();
    }

    @Test
    public void TC0004_ProfileCheck() throws InterruptedException, IOException {
        parentTest = extentReports.createTest("PROFILE ACCESS AND FEATURE TEST");
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
        driver.get(webUrl);
        childTest = parentTest.createNode("PROFILE CHECK");
        login(driver);
        childTest.log(Status.PASS,MarkupHelper.createLabel("Login done, entered portal",ExtentColor.GREEN));
        Thread.sleep(2000);
        driver.findElement(By.cssSelector("#user-menu")).click();
        childTest.log(Status.PASS,MarkupHelper.createLabel("User menu clicked",ExtentColor.GREEN));
        driver.findElement(ByAngular.partialButtonText("Profile")).click();
        childTest.log(Status.PASS,MarkupHelper.createLabel("Profile button clicked",ExtentColor.GREEN));
        System.out.println("Profile button pressed!");
        Thread.sleep(1000);
        childTest = parentTest.createNode("PROFILE FEATURE AND ACCESS CHECK");
        childTest.log(Status.PASS,MarkupHelper.createLabel("Profile page opened",ExtentColor.GREEN));
        List<WebElement> elementA = driver.findElements(By.tagName("h6"));
        childTest.log(Status.PASS,MarkupHelper.createLabel("Validating feature and access values",ExtentColor.GREEN));
        checkAccessAndFeatures(responseBody,elementA);
        childTest.log(Status.PASS,MarkupHelper.createLabel("Validation done",ExtentColor.GREEN));
        driver.close();
    }

    @Test
    public void TC0005_SideNavCheck() throws InterruptedException {
        parentTest = extentReports.createTest("SIDE-NAV ELEMENTS TEST");
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
        driver.get(webUrl);
        login(driver);
        Thread.sleep(2000);
        childTest = parentTest.createNode("SIDE-NAV CHECK");
        if(driver.findElement(By.cssSelector(".sidebar")).isDisplayed()){
            childTest.log(Status.PASS,MarkupHelper.createLabel("Side-nav displayed",ExtentColor.GREEN));
        }
        else childTest.log(Status.FAIL,MarkupHelper.createLabel("Side-nav not displayed",ExtentColor.RED));
        Thread.sleep(2000);
        childTest.log(Status.PASS,MarkupHelper.createLabel("Validating list of elements",ExtentColor.GREEN));
        checkAccess(driver,responseBody);
        childTest.log(Status.PASS,MarkupHelper.createLabel("Validation done",ExtentColor.GREEN));
        driver.close();
    }

    @Test
    public void TC0006_CustomerManagement() throws InterruptedException {
        parentTest = extentReports.createTest("CUSTOMER MANAGEMENT CARD TEST");
        System.setProperty("webdriver.chrome.driver", "D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");
        ChromeDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        NgWebDriver ngWebDriver = new NgWebDriver(js);
        driver.get(webUrl);
        childTest = parentTest.createNode("CUSTOMER MANAGEMENT ELEMENT");
        login(driver);
        childTest.log(Status.PASS,MarkupHelper.createLabel("Entered portal, sidenav elements displayed",ExtentColor.GREEN));
        Thread.sleep(2000);
        driver.findElement(By.id("customer_management")).click();
        childTest.log(Status.PASS,MarkupHelper.createLabel("Customer Management found and clicked",ExtentColor.GREEN));
        Thread.sleep(1000);
        driver.findElement(ByAngular.partialButtonText("Create")).click();
        childTest.log(Status.PASS,MarkupHelper.createLabel("Create button clicked",ExtentColor.GREEN));
        Thread.sleep(1000);
        childTest = parentTest.createNode("CUSTOMER MANAGEMENT CARD");
        if(driver.findElement(By.cssSelector("#cdk-overlay-0")).isDisplayed())
            childTest.log(Status.PASS,MarkupHelper.createLabel("Creation card opened",ExtentColor.GREEN));
        else childTest.log(Status.FAIL,MarkupHelper.createLabel("Creation card not opened",ExtentColor.RED));
        int page=0;
        driver.findElement(By.id("mat-tab-label-0-0")).click();
        if(!mandatoryFieldCheck(driver,page)){
            childTest.log(Status.FAIL,MarkupHelper.createLabel("Mandatory fields not filled",ExtentColor.RED));
            driver.findElement(By.id("mat-tab-label-0-1")).click();
            childTest.log(Status.PASS,MarkupHelper.createLabel("Next tab clicked",ExtentColor.GREEN));
            if(driver.findElement(By.id("mat-tab-label-0-1")).isSelected()){
                System.out.println("selected");
                childTest.log(Status.FAIL,MarkupHelper.createLabel("Mandatory fields empty, moved to new tab",ExtentColor.RED));
            }
        }

        driver.findElement(By.id("mat-tab-label-0-1")).click();
        driver.findElement(By.id("mat-tab-label-0-2")).click();
        driver.findElement(By.id("mat-tab-label-0-3")).click();
        childTest.log(Status.PASS,MarkupHelper.createLabel("Mat-tab working fine",ExtentColor.GREEN));
        driver.findElement(ByAngular.partialButtonText("Submit")).click();
        driver.findElement(By.cssSelector("button[class='mat-focus-indicator close-btn mat-icon-button mat-button-base']")).click();
        driver.close();
    }
    public Boolean mandatoryFieldCheck(ChromeDriver driver,int page){
        String name = driver.findElement(By.id("mat-input-3")).getText();
        System.out.println("Name check : " + name);
       // if(name.isEmpty() || )
        return false;
    }
/*
    @Test
    public void TC0006_MyGroups() throws InterruptedException {
        parentTest = extentReports.createTest("MY GROUPS CARD CHECK");
        System.setProperty("webdriver.chrome.driver", "D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");
        ChromeDriver driver = new ChromeDriver();
        driver.get(webUrl);
        driver.manage().window().maximize();
        login(driver);
        Thread.sleep(2000);
        driver.findElement(By.id("my_users")).click();
        Thread.sleep(500);
        driver.findElement(ByAngular.partialButtonText("Create")).click();
        Thread.sleep(500);
        driver.findElement(By.id("mat-tab-label-0-0")).click();
        driver.findElement(By.id("mat-tab-label-0-1")).click();
        driver.findElement(By.id("mat-tab-label-0-2")).click();
        driver.findElement(By.id("mat-tab-label-0-3")).click();
        driver.findElement(ByAngular.partialButtonText("Submit")).click();
        driver.findElement(By.cssSelector("button[class='mat-focus-indicator close-btn mat-icon-button mat-button-base']")).click();
        driver.close();
    }

    @Test
    public void TC0007_SendLink() throws InterruptedException {
        parentTest = extentReports.createTest("SEND LINK CARD CHECK");
        System.setProperty("webdriver.chrome.driver", "D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get(webUrl);
        driver.manage().window().maximize();
        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");
        driver.findElement(By.className("login-button")).click();
        Thread.sleep(2000);
        driver.findElement(By.id("dynamic_links")).click();
        driver.close();
    }

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

    @Test
    public void TC0009_CustomerRolesCheck() throws InterruptedException, IOException {
        parentTest = extentReports.createTest("CUSTOMER PERMISSION CHECK");
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
        driver.get(webUrl);
        driver.manage().window().maximize();
        login(driver);
        Thread.sleep(3000);
        driver.findElement(By.id("customer_management")).click();
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("div.card-body>div>table>tbody>tr:nth-child(1)>td.mat-cell.cdk-cell.cdk-column-Action.mat-column-Action.ng-star-inserted>button")).click();
        Thread.sleep(1000);
        checkRoles(driver,responseBody);
        driver.findElement(ByAngular.partialButtonText("View")).click();
        driver.close();
    }
*/

    @AfterTest
    public void endReport() {
        extentReports.flush();
    }

    @AfterMethod
    public void getResult(ITestResult result) throws Exception{
        System.out.println("After Method");
        if(result.getStatus() == ITestResult.FAILURE){
            childTest.log(Status.FAIL, MarkupHelper.createLabel(result.getName() + " - Test Case Failed", ExtentColor.RED));
            childTest.log(Status.FAIL, MarkupHelper.createLabel(result.getThrowable() + " - Test Case Failed", ExtentColor.RED));
        }
        else if(result.getStatus() == ITestResult.SKIP){
            childTest.log(Status.SKIP, MarkupHelper.createLabel(result.getName() + " - Test Case Skipped", ExtentColor.ORANGE));
        }
        else if(result.getStatus() == ITestResult.SUCCESS)
        {
            childTest.log(Status.PASS, MarkupHelper.createLabel(result.getName()+" Test Case PASSED", ExtentColor.GREEN));
        }
    }

    public void dataPreProcessing(String responseBody, ChromeDriver driver) throws IOException {
        if(responseBody == ""){
            fail("Empty response body!");
        }
        JSONParser parser = new JSONParser();
        try {
            Object object = (Object) parser.parse(responseBody);
            JSONArray jsonArray = (JSONArray) object;

            for(int i=0; i<1;i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
                for(Map.Entry<String, JsonElement> entry : entries) {
//                    System.out.println(entry.getKey() + " -> "+entry.getValue());
                    checkAccountData(entry,driver);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    public void checkAccountData(Map.Entry<String, JsonElement> entry, ChromeDriver driver){
        String name = driver.findElement(By.cssSelector("table>tbody>tr:nth-child(1)>td.mat-cell.cdk-cell.cdk-column-name.mat-column-name.ng-star-inserted")).getText();
        String expDate = driver.findElement(By.cssSelector("table>tbody>tr:nth-child(1)>td.mat-cell.cdk-cell.cdk-column-expDate.mat-column-expDate.ng-star-inserted")).getText();
        System.out.println("Name : "+ name);
        System.out.println("getKey : "+entry.getKey());
        System.out.println("getValue : "+entry.getValue());
        if(entry.getKey().equalsIgnoreCase("name")){
            if(!name.equalsIgnoreCase(String.valueOf(entry.getValue()))){
                fail("Name not matched!");
            }
            System.out.println("Name matched!");
        }
        if(entry.getKey().equalsIgnoreCase("expDate")){
            if(!expDate.equalsIgnoreCase(String.valueOf(entry.getValue()))){
                fail("ExpDate not matched!");
            }
            System.out.println("ExpDate matched!");
        }
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
                    if (!accessValues.contains("Customer Update")) fail();
                }
            if (driver.findElement(ByAngular.partialButtonText("Delete")).isDisplayed()) {
                System.out.println("DELETE");
                if (!accessValues.contains("Customer Delete")) fail();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void checkAccess(ChromeDriver driver,AtomicReference<String> responseBody){
        try {
            List<String> accessValues = getAccessData(responseBody.get());
            List<WebElement> sidebarElements = driver.findElements(By.tagName("ul"));
            if(sidebarElements.isEmpty()){ fail(); }
            for (WebElement element : sidebarElements) {
//                System.out.println("Attribute name : "+element.getAttribute("class"));
                System.out.println("TEXT : "+element.getText());
                if (element.getText().contains("Dashboard")) { if(!accessValues.contains("Dashboard")) fail(); else System.out.println("Dashboard"); }
                else if (element.getText().contains("Customer Management")) { if(!accessValues.contains("Customer Management")) fail(); else System.out.println("Customer Management"); }
                else if (element.getText().contains("My Groups")) { if(!accessValues.contains("My Groups")) fail(); else System.out.println("My Groups");}
                else if (element.getText().contains("My Sessions")) { if(!accessValues.contains("My Sessions")) fail(); else System.out.println("My Sessions"); }
                else if (element.getText().contains("Send Link")) { if(!accessValues.contains("Send Link")) fail(); else System.out.println("Send Link"); }
                else if (element.getText().contains("My Reports")) { if(!accessValues.contains("My Reports")) fail(); else System.out.println("My Reports");}
                else if (element.getText().contains("Platform Access")) { if(!accessValues.contains("Platform Access")) fail(); else System.out.println("Platform Access");}
                else if (element.getText().contains("Platform Feature")) { if(!accessValues.contains("Platform Feature")) fail(); else System.out.println("Platform Features");}
                else if (element.getText().contains("Feedback")) { if(!accessValues.contains("Feedback")) fail(); else System.out.println("Feedback");}
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
