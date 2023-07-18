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

import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SeleniumTest extends TestBaseClass{

    String relativePath = "src/main/resources/chromedriver";
    File file = new File(relativePath);
    String absolutePath = file.getAbsolutePath();

    public String webUrl = "https://video.progate.mobi/#/";
    public ExtentReports extentReports;
    public ExtentSparkReporter extentSparkReporter;

    @BeforeClass
    public void beforeClass(){
        System.out.println("Before class!");
        extentReports = new ExtentReports();
        extentSparkReporter = new ExtentSparkReporter("VideoPlatformTestReport_video.html");
        extentReports.setSystemInfo("Environment", "Dev");
        extentReports.setSystemInfo("User Name", "mCarbon");
        extentSparkReporter.config().setDocumentTitle("Video Platform Test Report");
        extentSparkReporter.config().setReportName("Video Platform Test Report");
        extentSparkReporter.config().setTheme(Theme.STANDARD);
        extentSparkReporter.config().setTimelineEnabled(true);
    }

    @BeforeMethod
    public void startReport() {
        extentReports.attachReporter(extentSparkReporter);
    }

    @AfterTest
    public void endReport() { extentReports.flush(); }

    @Test
    public void TC0001_LoginTest() throws Exception {
        System.setProperty("webdriver.chrome.driver", absolutePath);
        ChromeDriver driver = new ChromeDriver();
        LoginPage.loginPage(driver,extentReports,webUrl);
    }

    @Test
    public void TC0002_LogOutTest() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", absolutePath);
        ChromeDriver driver = new ChromeDriver();
        LogoutCheck.logOut(driver,extentReports,"mcarbon","mcarbon",webUrl);
    }

    @Test
    public void TC0004_ProfileCheck() throws InterruptedException, IOException {
        System.setProperty("webdriver.chrome.driver", absolutePath);
        ChromeDriver driver = new ChromeDriver();
        ProfileCheck.profileCheck(driver,extentReports,"mcarbon","mcarbon",webUrl);
    }

    @Test
    public void TC0005_SideNavCheck() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", absolutePath);
        ChromeDriver driver = new ChromeDriver();
        SideNavCheck.sideNavCheck(driver,extentReports,"mcarbon","mcarbon",webUrl);
    }

    @Test
    public void TC0006_CustomerManagement() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", absolutePath);
        ChromeDriver driver = new ChromeDriver();
        CustomerManagement.customerManagementCheck(driver,extentReports,"mcarbon","mcarbon",webUrl);
    }
    @Test
    public void TC0006_MyGroups() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", absolutePath);
        ChromeDriver driver = new ChromeDriver();
        MyGroups.myGroupsCheck(driver,extentReports,"mcarbon","mcarbon",webUrl);
    }

    @Test
    public void TC0007_SendLink() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", absolutePath);
        ChromeDriver driver = new ChromeDriver();
        SendLink.sendLinkCheck(driver,extentReports,"mcarbon","mcarbon",webUrl);
    }

    @Test
    public void TC0008_CustomerRolesCheck() throws InterruptedException, IOException {
        System.setProperty("webdriver.chrome.driver", absolutePath);
        ChromeDriver driver = new ChromeDriver();
        CustomerManagement.customerRolesCheck(driver,extentReports,"mcarbon","mcarbon",webUrl);
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
//    public List<String> getAccessData(String responseBody) throws IOException {
//        Gson gson=new Gson();
//        JsonObject params=gson.fromJson(responseBody,JsonObject.class);
////        System.out.println("Params : "+params);
//        ObjectMapper objectMapper=new ObjectMapper();
//        if(params.isJsonNull()) return null;
//
//       List<String>name = new ArrayList<>();
//        if(!params.get("Access").isJsonNull()){
//            List<HashMap> dataAsMap = objectMapper.readValue(params.get("Access").toString(), List.class);
//            for(Map<String,String>map : dataAsMap){
//                name.add(map.get("name"));
//            }
//        }
//        return name;
//    }
//    public List<String> getFeatureData(String responseBody) throws IOException {
//        Gson gson=new Gson();
//        JsonObject params=gson.fromJson(responseBody,JsonObject.class);
//        System.out.println("Params : "+params);
//        ObjectMapper objectMapper=new ObjectMapper();
//        if(params.isJsonNull()) return null;
//        List<String>name = new ArrayList<>();
//        if(!params.get("Features").isJsonNull()){
//            List<HashMap> dataAsMap = objectMapper.readValue(params.get("Features").toString(), List.class);
//            for(Map<String,String>map : dataAsMap){
//                name.add(map.get("name"));
//            }
//        }
//        return name;
//    }


//    public void checkAccess(ChromeDriver driver,AtomicReference<String> responseBody){
//        try {
//            List<String> accessValues = getAccessData(responseBody.get());
//            List<WebElement> sidebarElements = driver.findElements(By.tagName("ul"));
//            if(sidebarElements.isEmpty()){ fail(); }
//            for (WebElement element : sidebarElements) {
////                System.out.println("Attribute name : "+element.getAttribute("class"));
//                System.out.println("TEXT : "+element.getText());
//                if (element.getText().contains("Dashboard")) { if(!accessValues.contains("Dashboard")) fail(); else System.out.println("Dashboard"); }
//                else if (element.getText().contains("Customer Management")) { if(!accessValues.contains("Customer Management")) fail(); else System.out.println("Customer Management"); }
//                else if (element.getText().contains("My Groups")) { if(!accessValues.contains("My Groups")) fail(); else System.out.println("My Groups");}
//                else if (element.getText().contains("My Sessions")) { if(!accessValues.contains("My Sessions")) fail(); else System.out.println("My Sessions"); }
//                else if (element.getText().contains("Send Link")) { if(!accessValues.contains("Send Link")) fail(); else System.out.println("Send Link"); }
//                else if (element.getText().contains("My Reports")) { if(!accessValues.contains("My Reports")) fail(); else System.out.println("My Reports");}
//                else if (element.getText().contains("Platform Access")) { if(!accessValues.contains("Platform Access")) fail(); else System.out.println("Platform Access");}
//                else if (element.getText().contains("Platform Feature")) { if(!accessValues.contains("Platform Feature")) fail(); else System.out.println("Platform Features");}
//                else if (element.getText().contains("Feedback")) { if(!accessValues.contains("Feedback")) fail(); else System.out.println("Feedback");}
//                }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            fail("Unexpected error");
//        }
//    }


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


