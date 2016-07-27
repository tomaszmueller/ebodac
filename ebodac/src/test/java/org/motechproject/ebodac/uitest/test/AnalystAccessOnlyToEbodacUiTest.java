package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
//import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.HomePage;

public class AnalystAccessOnlyToEbodacUiTest extends TestBase {

    private static final String LOCAL_MACHINE = "localhost";
    private LoginPage loginPage;
    private HomePage homePage;
    private String user;
    private String password;
    private UserPropertiesHelper userPropertiesHelper;
    private UITestHttpClientHelper httpClientHelper;
    private String url;

    @Before
    public void setUp() throws Exception {
        url = getServerUrl();
        userPropertiesHelper = new UserPropertiesHelper();
        user = userPropertiesHelper.getAnalystUserName();
        password = userPropertiesHelper.getAnalystPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        if (url.contains(LOCAL_MACHINE)) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant(), user, password);
            loginPage.goToPage();
            loginPage.login(user, password);
        } else if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(user, password);
        }
    }

    @Test // Test for EBODAC-528
    public void analystAccessOnlyToEbodacUITest() throws Exception {
        homePage.clickModules();
        Assert.assertFalse(homePage.isElementPresent(HomePage.DATA_SERVICES));
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
