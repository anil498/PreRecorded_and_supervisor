package com.videoplatformtest.auto.PageObjects;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class SendLink {
    static ExtentTest parentTest;
    static ExtentTest childTest;

    public static void sendLinkCheck(ChromeDriver driver, ExtentReports extentReports, String loginId, String password, String webUrl) throws InterruptedException {
        parentTest = extentReports.createTest("SEND LINK CARD CHECK");
        driver.get(webUrl);
        driver.manage().window().maximize();
        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");
        driver.findElement(By.className("login-button")).click();
        Thread.sleep(2000);
        driver.findElement(By.id("dynamic_links")).click();
        driver.close();
    }
}
