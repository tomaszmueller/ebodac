package org.motechproject.ebodac.uitest.test;

import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class LoginLogoutUiTest extends TestBase {
    private LoginPage loginPage;

    @Before
    public void setUp() {
        loginPage = new LoginPage(driver);
        login();
    }

    @Test
    public void loginLogoutTest() {

    }

    @After
    public void tearDown() throws Exception {
        loginPage.logOut();
    }
}
