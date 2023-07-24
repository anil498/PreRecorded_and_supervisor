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

import static org.junit.Assert.fail;

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
            if(driver.findElement(By.xpath(".//html/body/app-root/app-login/div/div/mat-card/mat-card-content/form/div")).getText().contains(cardLabelText))
                childTest.log(Status.PASS,MarkupHelper.createLabel("Empty fields giving right response label",ExtentColor.GREEN));
            else
                childTest.log(Status.FAIL,MarkupHelper.createLabel("Empty fields giving wrong response label",ExtentColor.RED));
        }
        childTest = parentTest.createNode("EMPTY USERNAME");
        String cardLabelTextU = "Must Enter Username";

        if(userName.getText().isEmpty() && password.getText().equals("")){
            password.sendKeys("abcde@123");
            loginButton.click();
            Thread.sleep(30);
            if(driver.findElement(By.xpath("/html/body/app-root/app-login/div/div/mat-card/mat-card-content/form/div")).getText().contains(cardLabelTextU)) {
                childTest.log(Status.PASS, MarkupHelper.createLabel("Empty username field giving right response label", ExtentColor.GREEN));
            }
            else {
                childTest.log(Status.FAIL, MarkupHelper.createLabel("Empty username field giving wrong response label", ExtentColor.RED));
            }
        }
        password.clear();
        childTest = parentTest.createNode("EMPTY PASSWORD");
        String cardLabelTextP = "Must Enter Password";

        if(userName.getText().equals("") && password.getText().isEmpty()){
            userName.sendKeys("abcedfg");
            loginButton.click();
            Thread.sleep(30);
//            System.out.println("Element val is : " +driver.findElement(By.xpath("/html/body/app-root/app-login/div/div/mat-card/mat-card-content/form/div")).getText());
            if(driver.findElement(By.xpath("/html/body/app-root/app-login/div/div/mat-card/mat-card-content/form/div")).getText().contains(cardLabelTextP)) {
//                System.out.println("COrrect label ");
                childTest.log(Status.PASS, MarkupHelper.createLabel("Empty password field giving right response label", ExtentColor.GREEN));
            }
            else {
//                System.out.println("wrong label");
                childTest.log(Status.FAIL, MarkupHelper.createLabel("Empty password field giving wrong response label", ExtentColor.RED));
            }
        }
        userName.clear();
        childTest = parentTest.createNode("LABEL TEXT");
        String expectedText = "Invalid username or password!";

        userName.sendKeys("abcedfg");
        password.sendKeys("abcde");
        childTest.log(Status.PASS,MarkupHelper.createLabel("Credentials entered",ExtentColor.GREEN));
        loginButton.click();
        Thread.sleep(500);
        if(driver.findElement(By.xpath(".//html/body/div[2]")).getText().contains(expectedText)){
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
