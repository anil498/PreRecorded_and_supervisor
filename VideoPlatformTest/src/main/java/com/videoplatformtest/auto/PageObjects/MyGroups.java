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

public class MyGroups {
    static ExtentTest parentTest;
    static ExtentTest childTest;

    public static void myGroupsCheck(ChromeDriver driver, ExtentReports extentReports, String loginId, String password, String webUrl) throws InterruptedException {
        parentTest = extentReports.createTest("MY GROUPS CARD TEST");
        driver.manage().window().maximize();
        driver.get(webUrl);
        login(driver,loginId,password);
        Thread.sleep(3000);
        childTest = parentTest.createNode("MY GROUPS ELEMENT CHECK");
        driver.findElement(By.id("my_users")).click();
        childTest.log(Status.PASS, MarkupHelper.createLabel("My Groups found and clicked",ExtentColor.GREEN));
        childTest = parentTest.createNode("MY GROUPS CARD CHECK");
        driver.findElement(ByAngular.partialButtonText("Create")).click();
        childTest.log(Status.PASS,MarkupHelper.createLabel("Create button clicked", ExtentColor.GREEN));
        if(driver.findElement(By.xpath("/html/body/div[3]/div[2]/div/mat-dialog-container")).isDisplayed())
            childTest.log(Status.PASS,MarkupHelper.createLabel("Creation card opened",ExtentColor.GREEN));
        else childTest.log(Status.FAIL,MarkupHelper.createLabel("Creation card not opened",ExtentColor.RED));
        Thread.sleep(500);
        driver.findElement(By.xpath(".//app-form-dialog/div/mat-tab-group/mat-tab-header/div/div/div/div[1]")).click();
        if(!groupMandatoryFieldCheck(driver)){
            driver.findElement(By.xpath(".//app-form-dialog/div/mat-tab-group/mat-tab-header/div/div/div/div[2]")).click();
            childTest.log(Status.PASS,MarkupHelper.createLabel("Next tab clicked",ExtentColor.GREEN));
            if(driver.findElement(By.xpath(".//app-form-dialog/div/mat-tab-group/mat-tab-header/div/div/div/div[2]")).getAttribute("class").contains("mat-tab-label-active")){
                System.out.println("Security tab opened");
                childTest.log(Status.FAIL,MarkupHelper.createLabel("Mandatory fields empty, moved to new tab",ExtentColor.RED));
            }
            else childTest.log(Status.PASS, MarkupHelper.createLabel("Mandatory fields empty, can't switch to next tab", ExtentColor.GREEN));
        }
        driver.findElement(By.xpath(".//app-form-dialog/div/mat-tab-group/mat-tab-header/div/div/div/div[1]")).click();
        if(!groupMandatoryFieldCheck(driver)){
            driver.findElement(By.xpath(".//mat-dialog-container/app-form-dialog/div/div[2]/button/span[1]")).click();
            childTest.log(Status.PASS,MarkupHelper.createLabel("Next button clicked",ExtentColor.GREEN));
            if(driver.findElement(By.xpath(".//app-form-dialog/div/mat-tab-group/mat-tab-header/div/div/div/div[2]")).getAttribute("class").contains("mat-tab-label-active")){
                System.out.println("Security tab opened");
                childTest.log(Status.FAIL,MarkupHelper.createLabel("Mandatory fields empty, moved to new tab",ExtentColor.RED));
            }
            else childTest.log(Status.PASS, MarkupHelper.createLabel("Mandatory fields empty, can't switch to next tab", ExtentColor.GREEN));
        }

//        driver.close();
    }

    public static Boolean groupMandatoryFieldCheck(ChromeDriver driver) {
        String fName = driver.findElement(By.id("user_fname")).getText();
        String lName = driver.findElement(By.id("user_lname")).getText();
        String mobile = driver.findElement(By.id("mobile")).getText();
        String email = driver.findElement(By.id("email")).getText();
        String loginId = driver.findElement(By.id("login_id")).getText();

        if (fName.isEmpty() || lName.isEmpty() || mobile.isEmpty() || email.isEmpty() || loginId.isEmpty()) {
            return false;
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
}
