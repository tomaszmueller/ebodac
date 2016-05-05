package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.helper.CreateUsersHelper;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.HomePage;

public class AnalystAccessOnlyToEbodacUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private String l1analystUser;
    private String l1analystpassword;
    private UserPropertiesHelper userPropertiesHelper;
    private UITestHttpClientHelper httpClientHelper;
    private CreateUsersHelper createUsersHelper;
    private String url;

    @Before
    public void setUp() throws  Exception {
        url = properties.getWebAppUrl();
        if (url.contains("localhost")) {
            createUsersHelper = new CreateUsersHelper(driver);
            createUsersHelper.createUsersWithLogin(properties);
        }
        userPropertiesHelper = new UserPropertiesHelper();
        l1analystUser = userPropertiesHelper.getAnalystUserName();
        l1analystpassword = userPropertiesHelper.getAnalystPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        if (url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant() , l1analystUser , l1analystpassword);
        }
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.login(l1analystUser , l1analystpassword);
        }
    }

    @Ignore
    @Test //Test fo EBODAC-528
    public void analystAccessOnlyToEbodacUITest() throws Exception {
        homePage.clickModules();
        Assert.assertFalse(homePage.isElementPresent(homePage.DATA_SERVICES));
    }

    @After
    public void tearDown() throws Exception {
        loginPage.logOut();
    }
}

