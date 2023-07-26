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

public class LogoutCheck {
    static ExtentTest parentTest;
    static ExtentTest childTest;

    public static void logOut(ChromeDriver driver, ExtentReports extentReports, String loginId, String password, String webUrl) throws InterruptedException {
        parentTest = extentReports.createTest("LOGOUT TEST");
        driver.manage().window().maximize();
        driver.get(webUrl);
        login(driver,loginId,password);
        childTest = parentTest.createNode("LOGOUT CHECK");
        Thread.sleep(3000);
        childTest.log(Status.PASS, MarkupHelper.createLabel("Login Successful", ExtentColor.GREEN));
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
//        driver.close();
    }
    public static void login(ChromeDriver driver, String loginId, String password){
        driver.findElement(By.xpath(".//app-login/div/div/mat-card/mat-card-content/form/mat-form-field[1]/div/div[1]/div/input")).sendKeys(loginId);
        driver.findElement(By.xpath(".//app-login/div/div/mat-card/mat-card-content/form/mat-form-field[2]/div/div[1]/div[1]/input")).sendKeys(password);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("login-button")));
        loginButton.click();
    }
}
