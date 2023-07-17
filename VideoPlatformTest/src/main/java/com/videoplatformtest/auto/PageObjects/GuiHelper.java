package com.videoplatformtest.auto.PageObjects;

import com.videoplatformtest.auto.SeleniumExtensions.WebDriverBase;
import com.videoplatformtest.auto.TestHelper.ConfigSettings;
import org.openqa.selenium.WebDriver;

import static com.videoplatformtest.auto.SeleniumExtensions.WebDriverBase.getWebDriver;

public class GuiHelper {

    public static void openWebBrowser(){
        getWebDriver(ConfigSettings.getWebBrowser());
    }

    public static void openWebBrowser(WebDriver chrome){
        getWebDriver(chrome);
    }

    public static void closeWebBrowser(){
        WebDriverBase.driver.close();
        WebDriverBase.driver.quit();
    }
}