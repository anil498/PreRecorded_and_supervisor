package com.videoplatformtest.auto;


import com.videoplatformtest.auto.TestHelper.IoLibrary;
import com.videoplatformtest.auto.TestHelper.TestAssert;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class TestBaseClass {

    public TestAssert testAssert;

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            System.out.println(" ");
            System.out.println("--------------------------------------------");
            System.out.println("Starting Test: " + description.getMethodName());
            System.out.println("--------------------------------------------");
            IoLibrary.setTestName(description.getMethodName());
        }
    };


}
