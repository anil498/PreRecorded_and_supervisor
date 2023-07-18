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

public class CustomerManagement {
    static ExtentTest parentTest;
    static ExtentTest childTest;

    public static void customerManagementCheck(ChromeDriver driver, ExtentReports extentReports, String loginId, String password, String webUrl) throws InterruptedException {
        parentTest = extentReports.createTest("CUSTOMER MANAGEMENT CARD TEST");
        driver.manage().window().maximize();
        driver.get(webUrl);
        childTest = parentTest.createNode("CUSTOMER MANAGEMENT ELEMENT");
        login(driver,loginId,password);
        childTest.log(Status.PASS, MarkupHelper.createLabel("Entered portal, sidenav elements displayed", ExtentColor.GREEN));
        Thread.sleep(3000);
        driver.findElement(By.id("customer_management")).click();
        childTest.log(Status.PASS, MarkupHelper.createLabel("Customer Management found and clicked", ExtentColor.GREEN));
        Thread.sleep(1000);
        driver.findElement(ByAngular.partialButtonText("Create")).click();
        childTest.log(Status.PASS, MarkupHelper.createLabel("Create button clicked", ExtentColor.GREEN));
        Thread.sleep(1000);

        List<WebElement> list = driver.findElements(By.xpath(".//mat-tab-group/mat-tab-header/div/div/div")).stream().filter(row -> {
            if (row.findElements(By.className("mat-tab-label-content")) == null)
                return false;
            return true;
        }).collect(Collectors.toList());

        for (WebElement row : list) {
            System.out.println(row.getText());
        }

        childTest = parentTest.createNode("CUSTOMER MANAGEMENT CARD");
        if (driver.findElement(By.xpath("/html/body/div[3]/div[2]/div/mat-dialog-container")).isDisplayed())
            childTest.log(Status.PASS, MarkupHelper.createLabel("Creation card opened", ExtentColor.GREEN));
        else childTest.log(Status.FAIL, MarkupHelper.createLabel("Creation card not opened", ExtentColor.RED));
        int page = 0;
        driver.findElement(By.xpath(".//app-create-account/div/mat-tab-group/mat-tab-header/div/div/div/div[1]")).click();
        System.out.println("Account Clicked!");
        if (!custMandatoryFieldCheck(driver, page)) {
            childTest.log(Status.FAIL, MarkupHelper.createLabel("Mandatory fields not filled", ExtentColor.RED));
            driver.findElement(By.xpath(".//app-create-account/div/mat-tab-group/mat-tab-header/div/div/div/div[2]")).click();
            childTest.log(Status.PASS, MarkupHelper.createLabel("Next tab clicked", ExtentColor.GREEN));
            if (driver.findElement(By.xpath(".//app-create-account/div/mat-tab-group/mat-tab-header/div/div/div/div[2]")).getAttribute("class").contains("mat-tab-label-active")) {
                System.out.println("Account Setting tab opened");
                childTest.log(Status.FAIL, MarkupHelper.createLabel("Mandatory fields empty, moved to new tab", ExtentColor.RED));
            }
        }

        driver.findElement(By.xpath(".//app-create-account/div/mat-tab-group/mat-tab-header/div/div/div/div[1]")).click();
        if (!custMandatoryFieldCheck(driver, page)) {
            driver.findElement(ByAngular.partialButtonText("Next")).click();
            childTest.log(Status.PASS, MarkupHelper.createLabel("Mandatory fields empty, Next button clicked", ExtentColor.GREEN));
            if (driver.findElement(By.xpath(".//app-create-account/div/mat-tab-group/mat-tab-header/div/div/div/div[2]")).getAttribute("class").contains("mat-tab-label-active")) {
                System.out.println("Account Setting tab opened");
                childTest.log(Status.FAIL, MarkupHelper.createLabel("Mandatory fields empty, next button working", ExtentColor.RED));
            } else {
                childTest.log(Status.PASS, MarkupHelper.createLabel("Mandatory fields empty, Next button not working", ExtentColor.GREEN));
                System.out.println("Mandatory fields empty, Next button not working");
            }
        }
        driver.findElement(By.xpath(".//app-create-account/div/div/h4/button/span[1]/mat-icon")).click();
        driver.close();
    }

    public static void customerRolesCheck(ChromeDriver driver, ExtentReports extentReports, String loginId, String password, String webUrl) throws InterruptedException {
        parentTest = extentReports.createTest("CUSTOMER PERMISSION TEST");
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
        login(driver,loginId,password);
        Thread.sleep(2000);
        childTest = parentTest.createNode("CUSTOMER ROLES CHECK");
        driver.findElement(By.id("customer_management")).click();
        childTest.log(Status.PASS, MarkupHelper.createLabel("Customer Management clicked", ExtentColor.GREEN));
        Thread.sleep(500);
        driver.findElement(By.cssSelector("div.card-body>div>table>tbody>tr:nth-child(1)>td.mat-cell.cdk-cell.cdk-column-Action.mat-column-Action.ng-star-inserted>button")).click();
        childTest.log(Status.PASS, MarkupHelper.createLabel("Mat icon clicked, roles displayed", ExtentColor.GREEN));
        Thread.sleep(1000);
        if(checkRoles(driver,responseBody)){
            childTest.log(Status.PASS, MarkupHelper.createLabel("Roles displayed and present in customer's permission", ExtentColor.GREEN));
        }else childTest.log(Status.FAIL, MarkupHelper.createLabel("Roles displayed but not present in customer's permission", ExtentColor.RED));
        driver.close();
    }

    public static Boolean checkRoles(ChromeDriver driver, AtomicReference<String> responseBody) {
        try {
            List<String> accessValues = getAccessData(responseBody.get());
            if (driver.findElement(ByAngular.partialButtonText("Edit")).isDisplayed()) {
                System.out.println("EDIT");
                if (!accessValues.contains("Customer Update")) {fail(); return false;}
            }
            if (driver.findElement(ByAngular.partialButtonText("Delete")).isDisplayed()) {
                System.out.println("DELETE");
                if (!accessValues.contains("Customer Delete")) {fail(); return false;}
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
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
        public static Boolean custMandatoryFieldCheck(ChromeDriver driver, int page) {
        String name = driver.findElement(By.id("name")).getText();
        String address = driver.findElement(By.id("address")).getText();
        String maxUser = driver.findElement(By.id("max_user")).getText();

        System.out.println("Name check : " + name);
        if (page == 0)
            if (name.isEmpty() || address.isEmpty()) {
                return false;
            }
        return true;
    }

    public static void login(ChromeDriver driver, String loginId, String password) {
        driver.findElement(By.id("mat-input-0")).sendKeys(loginId);
        driver.findElement(By.id("mat-input-1")).sendKeys(password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-button")));
        loginButton.click();
    }
}
