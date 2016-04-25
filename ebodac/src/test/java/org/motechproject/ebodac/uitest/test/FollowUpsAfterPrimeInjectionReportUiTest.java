package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.helper.CreateUsersHelper;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ReportPage;

/**
 * Created by serwis on 05.01.16.
 */
public class FollowUpsAfterPrimeInjectionReportUiTest extends TestBase {
    private String l1AdminUser;
    private String l1AdminPassword;
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
        l1AdminUser = properties.getUserName();
        l1AdminPassword = properties.getPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        ebodacPage = new EBODACPage(driver);
        url = properties.getWebAppUrl();
    }

    @Test //Test for EBODAC-806

    public void followUpsAfterPrimeInjectionReportTest() throws InterruptedException {
        httpClientHelper = new UITestHttpClientHelper(url);
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.login(l1AdminUser, l1AdminPassword);
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
