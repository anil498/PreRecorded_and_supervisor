package com.videoplatformtest.auto.PageObjects;


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

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.devtools.v108.network.Network;
import org.openqa.selenium.devtools.v108.network.model.RequestId;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.fail;

public class SideNavCheck  {
    static ExtentTest parentTest;
    static ExtentTest childTest;

    public static void sideNavCheck(ChromeDriver driver, ExtentReports extentReports, String loginId, String password, String webUrl) throws InterruptedException {
        parentTest = extentReports.createTest("SIDE-NAV ELEMENTS TEST");
        AtomicReference<String> responseBody= new AtomicReference<>("");
//        File file = new File("D:\\VideoPlatformBackend\\videoPlatform\\seleniumJava-master\\src\\main\\resources\\chromedriver");
//        ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(file).usingAnyFreePort().build();
//        ChromeOptions options = new ChromeOptions().addArguments("--incognito");
//        ChromeDriver driver = new ChromeDriver(service, options);
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
        Thread.sleep(4000);
        childTest = parentTest.createNode("SIDE-NAV CHECK");
        if(driver.findElement(By.cssSelector(".sidebar")).isDisplayed()){
            childTest.log(Status.PASS, MarkupHelper.createLabel("Side-nav displayed", ExtentColor.GREEN));
        }
        else childTest.log(Status.FAIL,MarkupHelper.createLabel("Side-nav not displayed",ExtentColor.RED));
        Thread.sleep(2000);
        childTest.log(Status.PASS,MarkupHelper.createLabel("Validating list of elements",ExtentColor.GREEN));
        checkAccess(driver,responseBody);
        childTest.log(Status.PASS,MarkupHelper.createLabel("Validation done",ExtentColor.GREEN));
        driver.close();
    }
    public static void login(ChromeDriver driver){
        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-button")));
        loginButton.click();
    }
    public static void checkAccess(ChromeDriver driver, AtomicReference<String> responseBody){
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

    public static List<String> getAccessData(String responseBody) throws IOException {
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(responseBody,JsonObject.class);
//        System.out.println("Params : "+params);
        ObjectMapper objectMapper=new ObjectMapper();
        if(params.isJsonNull()) return null;

        List<String>name = new ArrayList<>();
        if(!params.get("Access").isJsonNull()){
            List<HashMap> dataAsMap = objectMapper.readValue(params.get("Access").toString(), List.class);
            for(Map<String,String> map : dataAsMap){
                name.add(map.get("name"));
            }
        }
        return name;
    }
}
