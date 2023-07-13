package com.sdet.auto.PageObjects;

import com.sdet.auto.SeleniumExtensions.WebDriverBase;
import com.sdet.auto.TestHelper.ConfigSettings;

public class Navigation extends WebDriverBase{

    public void navToWebPageUnderTest(){
        driver.navigate().to(ConfigSettings.getWebUrl());
    }
}
