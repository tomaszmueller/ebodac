package org.motechproject.ebodac.uitest.test;

import org.junit.*;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.helper.CreateUsersHelper;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.page.*;

import java.lang.Exception;


/**
 * Created by serwis on 05.01.16.
 */

public class FollowUpsAfterPrimeInjectionReportUiTest extends TestBase {
    private String L1adminUser;
    private String UserName;
    private String Password;
    private String L1adminpassword;
    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private UITestHttpClientHelper httpClientHelper;
    private String url;
    private CreateUsersHelper createUsersHelper;
    private UserPropertiesHelper userPropertiesHelper;
    private ReportPage reportPage;

    @Before
    public void setUp() throws Exception {
        reportPage = new ReportPage(driver);
        userPropertiesHelper = new UserPropertiesHelper();
        L1adminUser = properties.getUserName();
        L1adminpassword = properties.getPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        ebodacPage = new EBODACPage(driver);
        url = properties.getWebAppUrl();
    }

    @Test //Test for EBODAC-806

    public void followUpsAfterPrimeInjectionReportTest() throws InterruptedException {
        httpClientHelper = new UITestHttpClientHelper(url);
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.login(L1adminUser, L1adminpassword);
            homePage.openEBODACModule();
            ebodacPage.gotoReports();
            reportPage.showFollowUpsAfterPrimeInjectionReport();
            Thread.sleep(500);
        }


    }

    @After
    public void tearDown() throws Exception {
        loginPage.logOut();
    }
}


