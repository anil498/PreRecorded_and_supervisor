package com.videoplatformtest.auto.PageObjects;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

public class Dashboard {
    static ExtentTest parentTest;
    static ExtentTest childTest;

    public static void dashboard(ChromeDriver driver, ExtentReports extentReports,String loginId, String password, String webUrl) throws InterruptedException, IOException {
        parentTest = extentReports.createTest("DASHBOARD TEST");
        driver.manage().window().maximize();
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
            requestIds[0] = responseReceived.getRequestId();
            String url = responseReceived.getResponse().getUrl();
            if(url.contains("/VPService/v1/User/login")) {
                responseBody.set(finalDevTools.send(Network.getResponseBody(requestIds[0])).getBody());
            }
        });
        driver.get(webUrl);
        login(driver,loginId,password);
        Thread.sleep(2000);
        childTest = parentTest.createNode("ROW ONE CARDS");
        if(accessCheck(driver,responseBody))
            childTest.log(Status.PASS, MarkupHelper.createLabel("All Cards displayed according to user's permission", ExtentColor.GREEN));
        else childTest.log(Status.FAIL, MarkupHelper.createLabel("User don't have permission but card displayed", ExtentColor.RED));

//        getDashboardData(responseBody);
        driver.close();
    }
    public static Boolean accessCheck(ChromeDriver driver, AtomicReference<String> responseBody) throws IOException {
        List<String> accessData = getAccessData(responseBody);
        List<WebElement> webElements = driver.findElements(By.xpath("/html/body/app-root/app-admin-layout/div[1]/div[2]/app-dashboard/div/div/div[1]"));
        for(WebElement element : webElements){
            if(!(element.getText().contains("Account") && accessData.contains("Customer Management"))){
                fail(); return false;
            }
            if(!(element.getText().contains("Users") && accessData.contains("My Groups"))){
                fail(); return false;
            }
            if(!(element.getText().contains("Sessions") && accessData.contains("My Sessions"))){
                fail(); return false;
            }
        }
        return true;
    }
    public static Boolean countCheck(ChromeDriver driver, AtomicReference<String> responseBody) throws IOException {

        return true;
    }
        public static void login(ChromeDriver driver, String loginId, String password) {
        driver.findElement(By.id("mat-input-0")).sendKeys(loginId);
        driver.findElement(By.id("mat-input-1")).sendKeys(password);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-button")));
        loginButton.click();
    }

    public static List<String> getAccessData(AtomicReference<String> responseBody) throws IOException {
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(String.valueOf(responseBody),JsonObject.class);
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
    public static List<String> getDashboardData(AtomicReference<String> responseBody) throws IOException {
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(String.valueOf(responseBody),JsonObject.class);
//        System.out.println("Params : "+params);
        ObjectMapper objectMapper=new ObjectMapper();
        if(params.isJsonNull()) return null;

        List<String>name = new ArrayList<>();
        if(!params.get("Dashboard").isJsonNull()){
            List<HashMap> dataAsMap = objectMapper.readValue(params.get("Dashboard").toString(), List.class);
            System.out.println(dataAsMap.toString());
            for(Map<String,String> map : dataAsMap){
                System.out.println(map);
//                name.add(map.get("name"));
            }
        }
        return name;
    }
}
