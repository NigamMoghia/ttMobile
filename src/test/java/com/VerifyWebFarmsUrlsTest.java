package com;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class VerifyWebFarmsUrlsTest {

    @BeforeMethod
    public void setUp() {
    }

    @AfterMethod
    public void tearDown() {
    }

    @Test
    public void testVerifyWebFarmPages() throws Exception {
        VerifyWebFarmsUrls webFarmsUrls= new    VerifyWebFarmsUrls();
        webFarmsUrls.verifyWebFarmPages("Production", "PR");
    }
}