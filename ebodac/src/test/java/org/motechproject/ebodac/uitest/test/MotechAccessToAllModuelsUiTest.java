package org.motechproject.ebodac.uitest.test;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.HomePage;

import static org.junit.Assert.assertTrue;
import org.apache.log4j.Logger;

public class MotechAccessToAllModuelsUiTest extends TestBase {

    // Object initialization for log
    private static Logger log = Logger.getLogger(MotechAccessToAllModuelsUiTest.class.getName());
    private LoginPage loginPage;
    private HomePage homePage;
    private String user;
    private String password;
    private static final String TEST_LOCAL_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;
    private String url;

    @Before
    public void setUp() throws Exception {
        try {
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            url = getServerUrl();
            if (url.contains(TEST_LOCAL_MACHINE)) {
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

    @Test
    public void motechAccessToAllModulesUiTest() throws Exception {
        try {
            assertTrue(homePage.isDataServicesModulePresent());
            assertTrue(homePage.isEBODACModulePresent());
            assertTrue(homePage.isEmailModulePresent());
            assertTrue(homePage.isMessageCampaignModulePresent());
            assertTrue(homePage.isIVRModulePresent());
            assertTrue(homePage.isSMSModulePresent());
            assertTrue(homePage.isSchedulerModulePresent());
            assertTrue(homePage.isTasksModulePresent());
        } catch (AssertException e) {
            log.error("motechAccessToAllModulesUiTest - AssertException - Reason : " + e.getLocalizedMessage(), e);

        } catch (NullPointerException e) {
            log.error("motechAccessToAllModulesUiTest - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (InterruptedException e) {
            log.error("motechAccessToAllModulesUiTest - InterruptedException - Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            log.error("motechAccessToAllModulesUiTest - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    public void tearDown() throws Exception {
        logout();
    }
}
