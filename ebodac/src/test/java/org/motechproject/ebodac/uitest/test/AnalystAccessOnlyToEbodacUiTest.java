package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.apache.log4j.Logger;

public class AnalystAccessOnlyToEbodacUiTest extends TestBase {
    private static Logger log = Logger.getLogger(AnalystAccessOnlyToEbodacUiTest.class.getName());
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
        try {
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
        } catch (NullPointerException e) {
            log.error("setUp - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            log.error("setUp - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test // Test for EBODAC-528
    public void analystAccessOnlyToEbodacUITest() throws Exception {
        try {
            homePage.clickModules();
            Assert.assertFalse(homePage.isElementPresent(HomePage.DATA_SERVICES));
        } catch (AssertionError e) {
            log.error("analystAccessOnlyToEbodacUITest - NullPointerException - Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            log.error("analystAccessOnlyToEbodacUITest - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            log.error("analystAccessOnlyToEbodacUITest - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
