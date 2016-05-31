package org.motechproject.ebodac.uitest.test;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.HomePage;

import static org.junit.Assert.assertTrue;

public class MotechAccessToAllModuelsUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private String user;
    private String password;
    private UITestHttpClientHelper httpClientHelper;
    private String url;

    @Before
    public void setUp() {
        user = getTestProperties().getUserName();
        password = getTestProperties().getPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        url = getServerUrl();
        if (url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant(), user, password);
        }
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(user, password);
        }
    }

    @Test
    public void motechAccessToAllModulesUiTest() throws Exception {
        assertTrue(homePage.isDataServicesModulePresent());
        assertTrue(homePage.isEBODACModulePresent());
        assertTrue(homePage.isEmailModulePresent());
        assertTrue(homePage.isMessageCampaignModulePresent());
        assertTrue(homePage.isIVRModulePresent());
        assertTrue(homePage.isSMSModulePresent());
        assertTrue(homePage.isSchedulerModulePresent());
        assertTrue(homePage.isTasksModulePresent());
    }

    public void tearDown() throws Exception {
        logout();
    }
}
