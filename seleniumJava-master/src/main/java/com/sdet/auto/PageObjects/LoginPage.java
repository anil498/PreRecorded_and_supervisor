package com.sdet.auto.PageObjects;

import com.sdet.auto.SeleniumExtensions.WebDriverExtensions;
import com.sdet.auto.TestHelper.LoggingLibrary;
import com.sdet.auto.TestHelper.TestAssert;

public class LoginPage extends WebDriverExtensions {

    private final static String txtUsername = "#mat-input-0";
    private final static String txtPassword = "#mat-input-1";
    private final static String btnLogin = ".login-button";
    private final static String lblMessage = ".mat-simple-snack-bar-content";

    public static void enterCredentials(String loginId, String password){

        getElementBySelector(txtUsername).sendKeys(loginId);
        getElementBySelector(txtPassword).sendKeys(password);
        getElementBySelector(btnLogin).click();
    }

    public static void verifyMessage(TestAssert testAssert, String expectedMsg){

        testAssert.setPass(LoggingLibrary.CompareResultContains(getElementBySelector(lblMessage).getText(), expectedMsg));
    }
}
