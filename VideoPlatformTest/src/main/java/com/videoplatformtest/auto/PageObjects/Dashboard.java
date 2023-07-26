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
            childTest.log(Status.PASS, MarkupHelper.createLabel("All cards displayed according to user's permission", ExtentColor.GREEN));
        else childTest.log(Status.FAIL, MarkupHelper.createLabel("User don't have permission but card displayed", ExtentColor.RED));
        if(countCheck(driver,responseBody)){
            childTest.log(Status.PASS, MarkupHelper.createLabel("All cards representing correct counts", ExtentColor.GREEN));
        }
        else childTest.log(Status.FAIL, MarkupHelper.createLabel("Wrong count displayed", ExtentColor.RED));
//        driver.close();
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
        String totalParticipant  = driver.findElement(By.xpath(".//app-dashboard/div/div/div[1]/div[4]/div/div[1]/h3")).getText();
        HashMap<String,Object> dashboardDetails = getDashboardData(responseBody);
        Integer participants = (Integer) dashboardDetails.get("participants");
        System.out.println("Total participants : "+participants+","+" Participants : "+totalParticipant);
        if(!(totalParticipant.contains(participants.toString()))){
            fail("Total participant value didn't matched !"); return false;
        }

        Map<String,Object> usersData = (Map<String, Object>) dashboardDetails.get("users");
        Integer totalUsers = (Integer) usersData.get("totalUsers");
        Integer activeUsers = (Integer) usersData.get("activeUsers");
        String aUsers = driver.findElement(By.xpath(".//app-dashboard/div/div/div[1]/div[2]/div/div[1]/h3")).getText();
        List<String> listU = Arrays.asList(aUsers.split("/"));
        if((totalUsers.toString().equals(listU.get(1)))){
            System.out.println("Checking user data.");
            if(!(activeUsers.toString().equals(listU.get(0)))){
                fail("Wrong user count !"); return false;
            }
        }

        Map<String,Object> sessionData = (Map<String, Object>) dashboardDetails.get("sessions");
        String aSessions = driver.findElement(By.xpath(".//app-dashboard/div/div/div[1]/div[3]/div/div[1]/h3")).getText();
        Integer activeSessions = (Integer) sessionData.get("activeSessions");
        Integer totalSessions = (Integer) sessionData.get("totalSessions");
        List<String> listS = Arrays.asList(aSessions.split("/"));
        if((totalSessions.toString().equals(listS.get(1)))){
            System.out.println("Checking session data.");
            if(!(activeSessions.toString().equals(listS.get(0)))){
                fail("Wrong session count !"); return false;
            }
        }

        Map<String,Object> accountData = (Map<String, Object>) dashboardDetails.get("accounts");
        String aAccounts = driver.findElement(By.xpath(".//app-dashboard/div/div/div[1]/div[1]/div/div[1]/h3")).getText();
        Integer activeAccounts = (Integer) accountData.get("activeAccounts");
        Integer totalAccounts = (Integer) accountData.get("totalAccounts");
        List<String> listA = Arrays.asList(aAccounts.split("/"));
        if((totalAccounts.toString().equals(listA.get(1)))){
            System.out.println("Checking account data.");
            if(!(activeAccounts.toString().equals(listA.get(0)))){
                fail("Wrong account count !"); return false;
            }
        }

        return true;
    }
        public static void login(ChromeDriver driver, String loginId, String password) {
            driver.findElement(By.xpath(".//app-login/div/div/mat-card/mat-card-content/form/mat-form-field[1]/div/div[1]/div/input")).sendKeys(loginId);
            driver.findElement(By.xpath(".//app-login/div/div/mat-card/mat-card-content/form/mat-form-field[2]/div/div[1]/div[1]/input")).sendKeys(password);

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
    public static HashMap<String,Object> getDashboardData(AtomicReference<String> responseBody) throws IOException {
        Gson gson=new Gson();
        JsonObject params=gson.fromJson(String.valueOf(responseBody),JsonObject.class);
        ObjectMapper objectMapper=new ObjectMapper();
        if(params.isJsonNull()) return null;

        HashMap<String,Object> dataAsMap = new HashMap<>();
        if(!params.get("Dashboard").isJsonNull()){
            dataAsMap = objectMapper.readValue(params.get("Dashboard").toString(), HashMap.class);
            System.out.println(dataAsMap);
        }
        return dataAsMap;
    }
}
