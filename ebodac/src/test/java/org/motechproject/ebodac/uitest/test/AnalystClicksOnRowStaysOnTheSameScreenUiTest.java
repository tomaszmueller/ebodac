package org.motechproject.ebodac.uitest.test;


import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.helper.CreateUsersHelper;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.*;

import static org.junit.Assert.assertFalse;

public class AnalystClicksOnRowStaysOnTheSameScreenUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private EnrollmentsPage enrollmentsPage;
    private String L1analystUser;
    private String L1analystpassword;
    private UserPropertiesHelper userPropertiesHelper;
    private UITestHttpClientHelper httpClientHelper;
    private CreateUsersHelper createUsersHelper;
    private String url;

    @Before
    public void setUp() throws  Exception {
        url = properties.getWebAppUrl();
        if(url.contains("localhost")) {
            createUsersHelper = new CreateUsersHelper(driver);
            createUsersHelper.createUsersWithLogin(properties);
        }
        ebodacPage = new EBODACPage(driver);
        enrollmentsPage = new EnrollmentsPage(driver);
        userPropertiesHelper = new UserPropertiesHelper();
        L1analystUser = userPropertiesHelper.getAnalystUserName();
        L1analystpassword = userPropertiesHelper.getAnalystPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        if(url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
        }
        if(homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.login(L1analystUser, L1analystpassword);
        }
    }

    @Test
    public void analystClicksOnRowStaysOnTheSameScreenTest() throws InterruptedException {
        homePage.openEBODACModule();
        ebodacPage.showEnrollments();
        assertFalse(enrollmentsPage.checkEnroll());
    }
}
