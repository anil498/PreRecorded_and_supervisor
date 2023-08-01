package com.videoplatformtest.auto.PageObjects;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paulhammant.ngwebdriver.ByAngular;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v108.network.Network;
import org.openqa.selenium.devtools.v108.network.model.RequestId;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.fail;

public class ProfileCheck {
    static ExtentTest parentTest;
    static ExtentTest childTest;

    public static void profileCheck(ChromeDriver driver, ExtentReports extentReports, String loginId, String password, String webUrl) throws InterruptedException {
        parentTest = extentReports.createTest("PROFILE ACCESS AND FEATURE TEST");
        AtomicReference<String> responseBody= new AtomicReference<>("");
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
        driver.get(webUrl);
        childTest = parentTest.createNode("PROFILE CHECK");
        login(driver,loginId,password);
        childTest.log(Status.PASS, MarkupHelper.createLabel("Login done, entered portal", ExtentColor.GREEN));
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
//        driver.close();
    }
    public static void login(ChromeDriver driver, String loginId, String password){
        driver.findElement(By.xpath(".//app-login/div/div/mat-card/mat-card-content/form/mat-form-field[1]/div/div[1]/div/input")).sendKeys(loginId);
        driver.findElement(By.xpath(".//app-login/div/div/mat-card/mat-card-content/form/mat-form-field[2]/div/div[1]/div[1]/input")).sendKeys(password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-button")));
        loginButton.click();
    }
    private static void checkAccessAndFeatures(AtomicReference<String> responseBody, List<WebElement> elements) {
        try {
            List<String> accessValues = getAccessData(responseBody.get());
            List<String> featureValues = getFeatureData(responseBody.get());
            if (elements.isEmpty()) {
                fail();
            }
            for (WebElement element : elements) {
                if (element.getText().equalsIgnoreCase("dashboard")) {
                    if (!accessValues.contains("Dashboard")) fail();
                }
                else if (element.getText().equalsIgnoreCase("Customer Management")) { if (!accessValues.contains("Customer Management")) fail(); }
                else if (element.getText().equalsIgnoreCase("My Groups")) { if (!accessValues.contains("My Groups")) fail(); }
                else if (element.getText().equalsIgnoreCase("My Sessions")) { if (!accessValues.contains("My Sessions")) fail(); }
                else if (element.getText().equalsIgnoreCase("Send Link")) { if (!accessValues.contains("Send Link")) fail(); }
                else if (element.getText().equalsIgnoreCase("MY Reports")) { if (!accessValues.contains("My Reports")) fail(); }
                else if (element.getText().equalsIgnoreCase("Platform Access")) { if (!accessValues.contains("Platform Access")) fail(); }
                else if (element.getText().equalsIgnoreCase("Platform Feature")) { if (!accessValues.contains("Platform Feature")) fail();}
                else if (element.getText().equalsIgnoreCase("Customer Creation")) { if (!accessValues.contains("Customer Creation")) fail();}
                else if (element.getText().equalsIgnoreCase("Customer Update")) { if (!accessValues.contains("Customer Update")) fail();}
                else if (element.getText().equalsIgnoreCase("Customer Delete")) { if (!accessValues.contains("Customer Delete")) fail();}
                else if (element.getText().equalsIgnoreCase("Group Creation")) { if (!accessValues.contains("Group Creation")) fail();}
                else if (element.getText().equalsIgnoreCase("Group Delete")) { if (!accessValues.contains("Group Delete")) fail(); }
                else if (element.getText().equalsIgnoreCase("Session Create")) { if (!accessValues.contains("Session Create")) fail(); }
                else if (element.getText().equalsIgnoreCase("Whatsapp")) { if (!accessValues.contains("Whatsapp")) fail(); }
                else if (element.getText().equalsIgnoreCase("Send Notification")) { if (!accessValues.contains("Send Notification")) fail(); }
                else if (element.getText().equalsIgnoreCase("SMS")) { if (!accessValues.contains("SMS")) fail(); }
                else if (element.getText().equalsIgnoreCase("Report Review")) { if (!accessValues.contains("Report Review")) fail(); }
                else if (element.getText().equalsIgnoreCase("Report Download")) { if (!accessValues.contains("Report Download")) fail(); }
                else if (element.getText().equalsIgnoreCase("Recording")) { if (!featureValues.contains("Recording")) fail(); }
                else if (element.getText().equalsIgnoreCase("Screen Share")) { if (!featureValues.contains("Screen Share")) fail(); }
                else if (element.getText().equalsIgnoreCase("Chat")) { if (!featureValues.contains("Chat")) fail(); }
                else if (element.getText().equalsIgnoreCase("Pre Recorded Video")) { if (!featureValues.contains("Pre Recorded Video")) fail(); }
                else if (element.getText().equalsIgnoreCase("Participant Ticker")) { if (!featureValues.contains("Participant Ticker")) fail(); }
                else if (element.getText().equalsIgnoreCase("Customer Info To Agent")) { if (!featureValues.contains("Customer info to Agent")) fail(); }
                else if (element.getText().equalsIgnoreCase("Customised Layout")) { if (!featureValues.contains("Customised Layout")) fail(); }
                else if (element.getText().equalsIgnoreCase("Add Supervisor")) { if (!featureValues.contains("Add Supervisor")) fail(); }
                else if (element.getText().equalsIgnoreCase("Session Timer")) { if (!featureValues.contains("Session Timer")) fail(); }
                else if (element.getText().equalsIgnoreCase("Session Activities")) { if (!featureValues.contains("Session Activities")) fail(); }
                else if (element.getText().equalsIgnoreCase("Session Participants")) { if (!featureValues.contains("Session Participants")) fail(); }
                else if (element.getText().equalsIgnoreCase("Agent Landing Page URL")) { if (!featureValues.contains("Agent Landing Page URL")) fail(); }
                else if (element.getText().equalsIgnoreCase("Participant Landing Page URL")) { if (!featureValues.contains("Participant Landing Page URL")) fail(); }
                else if (element.getText().equalsIgnoreCase("In Call Data Collection")) { if (!featureValues.contains("In Call Data Collection")) fail(); }
            }
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
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
    public static List<String> getFeatureData(String responseBody) throws IOException {
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(responseBody,JsonObject.class);
//        System.out.println("Params : "+params);
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

}
