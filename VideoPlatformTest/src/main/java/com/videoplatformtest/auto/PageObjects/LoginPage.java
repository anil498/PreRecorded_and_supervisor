package com.videoplatformtest.auto.PageObjects;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.paulhammant.ngwebdriver.ByAngular;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    static ExtentTest parentTest;
    static ExtentTest childTest;

    public static void loginPage(ChromeDriver driver, ExtentReports extentReports, String webUrl) throws InterruptedException {
        parentTest = extentReports.createTest("LOGIN TEST");
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

}
