package com.sdet.auto.PageObjects;

import com.sdet.auto.SeleniumExtensions.WebDriverExtensions;
import com.sdet.auto.TestHelper.LoggingLibrary;
import com.sdet.auto.TestHelper.TestAssert;

public class SecureAreaPage extends WebDriverExtensions {

    private final static String lblMessage = ".mat-simple-snack-bar-content";
    private final static String btnLogout = ".mat-focus-indicator.btn-action.mat-menu-item.ng-tns-c60-2";

    public static void verifyMessage(TestAssert testAssert, String expectedMsg){

        testAssert.setPass(LoggingLibrary.CompareResultContains(getElementBySelector(lblMessage).getText(), expectedMsg));
    }

    public static void clickLogoutButton(){

        getElementBySelector(btnLogout).click();
    }
}
