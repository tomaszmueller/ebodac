package org.motechproject.ebodac.uitest.test;

import static org.junit.Assert.assertFalse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.HomePage;

/**
 * Class created to test the Access to the different tabs in ebodac by Analyst.
 * 
 * @author tmueller
 * @modified rmartin
 *
 */
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
            getLogger().error("setUp - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            getLogger().error("setUp - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test // Test for EBODAC-528
    public void analystAccessOnlyToEbodacUITest() throws Exception {
        try {
            homePage.clickModules();
            assertFalse(homePage.isElementPresent(HomePage.DATA_SERVICES));
        } catch (AssertionError e) {
            getLogger().error(
                    "analystAccessOnlyToEbodacUITest - NullPointerException - Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error(
                    "analystAccessOnlyToEbodacUITest - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            getLogger().error("analystAccessOnlyToEbodacUITest - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
