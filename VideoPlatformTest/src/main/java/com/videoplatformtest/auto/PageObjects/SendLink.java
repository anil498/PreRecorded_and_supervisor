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
import java.util.stream.Collectors;

import static org.junit.Assert.fail;

public class SendLink {
    static ExtentTest parentTest;
    static ExtentTest childTest;

    public static void sendLinkCheck(ChromeDriver driver, ExtentReports extentReports, String loginId, String password, String webUrl) throws InterruptedException, IOException {
        parentTest = extentReports.createTest("SEND LINK TEST");
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
        driver.manage().window().maximize();
        login(driver, loginId, password);
        Thread.sleep(3000);
        childTest = parentTest.createNode("CUSTOMER ROLES CHECK");
        driver.findElement(By.id("dynamic_links")).click();
        if(driver.getCurrentUrl().equals(webUrl+"app/dynamic_links"))
            childTest.log(Status.PASS, MarkupHelper.createLabel("Send Link clicked, navigated to dynamic link page", ExtentColor.GREEN));
        else
            childTest.log(Status.FAIL, MarkupHelper.createLabel("Send Link clicked, not navigated to dynamic link page", ExtentColor.RED));

        if(checkPermissions(driver,responseBody)){
            childTest.log(Status.PASS, MarkupHelper.createLabel("All permission displayed are given to user", ExtentColor.GREEN));
        }
        else {
            childTest.log(Status.FAIL, MarkupHelper.createLabel("Permission displayed are not given to user", ExtentColor.RED));
        }
//        driver.close();
    }

    public static Boolean checkPermissions(ChromeDriver driver, AtomicReference<String> responseBody) throws IOException {

        List<WebElement> list = driver.findElements(By.xpath(".//mat-tab-group/mat-tab-header/div/div/div")).stream().filter(row -> {
            if (row.findElements(By.className("mat-tab-label-content")) == null)
                return false;
            return true;
        }).collect(Collectors.toList());
        List<String> accessData = getAccessData(responseBody);
        for(WebElement row : list){
            if (row.getText().contains("SMS")) {
                if(!accessData.contains("SMS")){
                    fail(); return false;
                }
            }
            if (row.getText().contains("Whatsapp")) {
                if(!accessData.contains("Whatsapp")){
                    fail(); return false;
                }
            }
            if (row.getText().contains("Call on App")) {
                if(!accessData.contains("Send Notification")){
                    fail(); return false;
                }
            }
            System.out.println(row.getText());
        }
        return true;
    }

//            List<String> accessValues = getAccessData(responseBody.get());
//            if (driver.findElement(ByAngular.partialButtonText("Edit")).isDisplayed()) {
//                System.out.println("EDIT");
//                if (!accessValues.contains("Customer Update")) {fail(); return false;}
//            }
//            if (driver.findElement(ByAngular.partialButtonText("Delete")).isDisplayed()) {
//                System.out.println("DELETE");
//                if (!accessValues.contains("Customer Delete")) {fail(); return false;}
//            }
//      /  }
//        catch (IOException e) {
//            e.printStackTrace();
//        }

    public static List<String> getAccessData(AtomicReference<String> responseBody) throws IOException {
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(String.valueOf(responseBody),JsonObject.class);
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

    public static void login(ChromeDriver driver, String loginId, String password) {
        driver.findElement(By.xpath(".//app-login/div/div/mat-card/mat-card-content/form/mat-form-field[1]/div/div[1]/div/input")).sendKeys(loginId);
        driver.findElement(By.xpath(".//app-login/div/div/mat-card/mat-card-content/form/mat-form-field[2]/div/div[1]/div[1]/input")).sendKeys(password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-button")));
        loginButton.click();
    }
}
