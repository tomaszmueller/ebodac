package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.CreateUsersHelper;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.HomePage;

public class AdminAccessOnlyToEbodacUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private String l1AdminUser;
    private String l1AdminPassword;
    private UITestHttpClientHelper httpClientHelper;
    private UserPropertiesHelper userPropertiesHelper;
    private CreateUsersHelper createUsersHelper;
    private String url;

    @Before
    public void setUp() throws Exception {
        url = getServerUrl();
        if (url.contains("localhost")) {
            createUsersHelper = new CreateUsersHelper(getDriver());
            createUsersHelper.createUsersWithLogin(getTestProperties());
            logout();
        }
        userPropertiesHelper = new UserPropertiesHelper();
        l1AdminUser = userPropertiesHelper.getAdminUserName();
        l1AdminPassword = userPropertiesHelper.getAdminPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        if (url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant() , l1AdminUser , l1AdminPassword);
        }
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.login(l1AdminUser, l1AdminPassword);
        }
    }

    @Ignore
    @Test //Test for EBODAC-531
    public void adminAccessOnlyToEbodacUiTest() throws Exception {
        homePage.clickModules();
        Assert.assertFalse(homePage.isElementPresent(homePage.DATA_SERVICES));
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
