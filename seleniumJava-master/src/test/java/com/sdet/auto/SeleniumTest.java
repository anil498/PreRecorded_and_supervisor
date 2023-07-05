package com.sdet.auto;

import com.sdet.auto.PageObjects.*;
import com.sdet.auto.TestHelper.AccessibilityHelper;
import com.sdet.auto.TestHelper.ConfigSettings;
import org.asynchttpclient.proxy.ProxyServer;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v108.network.Network;
import org.openqa.selenium.devtools.v108.network.model.Response;
import org.openqa.selenium.devtools.v114.network.model.ResponseReceived;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import org.openqa.selenium.Proxy;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SeleniumTest extends TestBaseClass{

    @Test
    public void TC0001_SmokeTest() {

        GuiHelper.openWebBrowser();
        Navigation.navToWebPageUnderTest();
//        HomePage.VerifyOnHomePage(testAssert);
        GuiHelper.closeWebBrowser();
    }

    @Test
    public void TC0003_FormAuthentication() {

        final String userId = "mcarbon";
        final String password = "mcarbon";
        final String expectedLoginMsg = "Login successful!";
        final String expectedLogoutMsg = "Logout successful!";

        GuiHelper.openWebBrowser();
        Navigation.navToWebPageUnderTest();
//        HomePage.clickFormAuthentication();
        LoginPage.enterCredentials(userId, password);
        SecureAreaPage.verifyMessage(testAssert, expectedLoginMsg);
        SecureAreaPage.clickLogoutButton();
        LoginPage.verifyMessage(testAssert, expectedLogoutMsg);
        GuiHelper.closeWebBrowser();
    }

    @Test
    public void TC0004_FormAuthenticationBadInfo() {

        final String loginId = "sdetAutomatiom";
        final String password = "pass@word";
        final String expectedMsg = "Invalid username or password!";

        GuiHelper.openWebBrowser();
        Navigation.navToWebPageUnderTest();
//        HomePage.clickFormAuthentication();
        LoginPage.enterCredentials(loginId, password);
        LoginPage.verifyMessage(testAssert, expectedMsg);
        GuiHelper.closeWebBrowser();
    }

    @Test
    public void getLogs() {

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Vatsala\\Desktop\\seleniumJava-master\\src\\main\\resources\\chromedriver");
//        ChromeOptions options = new ChromeOptions();
//        LoggingPreferences logPrefs = new LoggingPreferences();
//        logPrefs.enable(LogType.BROWSER, Level.ALL);
//        options.setCapability("goog:loggingPrefs", logPrefs);
////
        WebDriver driver = new ChromeDriver();

        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        devTools.send(Network.enable(Optional.of(1000000), Optional.empty(), Optional.empty()));

//        devTools.addListener(Network.requestWillBeSent(), request -> {
//            if (request.getRequest().getMethod().equalsIgnoreCase("POST")) {
//                System.out.println("Request URL : " + request.getRequest().getUrl());
//                System.out.println("Request body: " + request.getRequest().getPostData());
//                System.out.println("Request headers: " + request.getRequest().getHeaders().toString());
//            }
//            System.out.println("Request Method : " + request.getRequest().getMethod().equalsIgnoreCase("POST"));
//            System.out.println("Request isPostDataPresent : " + request.getRequest().getHasPostData());
//        });

        devTools.addListener(Network.responseReceived(), response ->
        {
            Response res = response.getResponse();
//            String body = res.getBody().toString();
//            System.out.println("Body : "+);
            System.out.println("Response : " + res);
            System.out.println("Response URL : " + res.getUrl());
            System.out.println("Response Status : " + res.getStatus());
            System.out.println("Response Headers : " + res.getHeaders());
            System.out.println("Response Req Headers : " + res.getRequestHeaders());
            System.out.println("Response Headers : " + res.getStatusText());
        });


        driver.get(ConfigSettings.getWebUrl());
        driver.findElement(By.id("mat-input-0")).sendKeys("mcarbon");
        driver.findElement(By.id("mat-input-1")).sendKeys("mcarbon");
        driver.findElement(By.className("login-button")).click();


        devTools.addListener(Network.responseReceived(), response ->
        {
            Response res = response.getResponse();
//            String body = res.getBody().toString();
//            System.out.println("Body : "+);
            System.out.println("Response 1: " + res);
            System.out.println("Response URL1 : " + res.getUrl());
            System.out.println("Response Status 1: " + res.getStatus());
            System.out.println("Response Headers 1: " + res.getHeaders());
            System.out.println("Response Req Headers 1: " + res.getRequestHeaders());
            System.out.println("Response Headers : 1" + res.getStatusText());
        });


//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//            WebElement liElement = wait.until(ExpectedConditions.elementToBeClickable(By.className("dashboard")));
//            liElement.click();


    }
//        LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
//        for (LogEntry logEntry : logEntries) {
//
//            String message = logEntry.getMessage();
//            if (message.contains("\"method\":\"Network.responseReceived\"")) {
//
//                System.out.println("API response found!");
////                String responseBody = extractResponseBodyFromMessage(message);
//               System.out.println("Response Body: " + message);
//                System.out.println("---");
//            }
//            System.out.println("Response Body: " + message);
//        }
//    }


//    @Test
//    public void TC0002_ForgetPasswordTest() {
//
//        final String email = "sdet.testautomation@gmail.com";
//        final String expectedMsg = "Your e-mail's been sent!";
//
//        GuiHelper.openWebBrowser();
//        Navigation.navToWebPageUnderTest();
////        HomePage.ClickForgetPassword();
//        ForgetPasswordPage.EnterEmail(email);
//        ForgetPasswordPage.ClickRetrieveButton();
//        EmailSentPage.VerifyEmailSent(testAssert, expectedMsg);
//        GuiHelper.closeWebBrowser();
//    }

//    @Test
//    public void TC0005_A11y_Accessibility() {
//
//        GuiHelper.openWebBrowser();
//        Navigation.navToWebPageUnderTest();
//
//        AccessibilityHelper.basicAccessibilityCheck(testAssert);
//
//        GuiHelper.closeWebBrowser();
//    }

//    @Test
//    public void getLogs() {
//
//        // pass the path of the chromedriver location in the second argument
//        System.setProperty("webdriver.chrome.driver", "");
//
//        ChromeOptions options = new ChromeOptions();
//        LoggingPreferences logPrefs = new LoggingPreferences();
//        logPrefs.enable(LogType.BROWSER, Level.ALL);
//        options.setCapability(options.LOGGING_PREFS, logPrefs);
//
//        WebDriver driver = new ChromeDriver(options);
//        driver.get("https://testkru.com/TestUrls/TestConsoleLogs");
//
//        LogEntries entry = driver.manage().logs().get(LogType.BROWSER);
//
//        // Retrieving all logs
//        List<LogEntry> logs = entry.getAll();
//
//        // Printing details separately
//        for (LogEntry e : logs) {
//            System.out.println("Message: " + e.getMessage());
//            System.out.println("Level: " + e.getLevel());
//            System.out.println("Timestamp: "+ e.getTimestamp());
//        }
//
//    }
//@Test
//public void getLogs() {
//
//    // pass the path of the chromedriver location in the second argument
////    System.setProperty("webdriver.chrome.driver", "");
//
//    WebDriver driver = new ChromeDriver();
//    driver.get("https://demo2.progate.mobi/#/");
//
//    LogEntries entry = driver.manage().logs().get(LogType.BROWSER);
//
//    // Retrieving all logs
//    List<LogEntry> logs = entry.getAll();
//
//    // Printing details separately
//    for (LogEntry e : logs) {
//        System.out.println("Message: " + e.getMessage());
//        System.out.println("Level: " + e.getLevel());
//        System.out.println("Timestamp: " + e.getTimestamp());
//    }
//
//}


}
