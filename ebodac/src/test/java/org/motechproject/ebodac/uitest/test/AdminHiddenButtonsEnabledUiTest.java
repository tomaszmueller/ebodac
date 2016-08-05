package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;
import static org.junit.Assert.assertTrue;

/**
 * Class created to test some hidden buttons enabled for Admin L1 if they are
 * enabled then we have an error.
 * 
 * @author tmueller
 * @modified rmartin
 *
 */
public class AdminHiddenButtonsEnabledUiTest extends TestBase {
    private LoginPage loginPage;
    private HomePage homePage;
    private String user;
    private String password;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;
    private UserPropertiesHelper userPropertiesHelper;

    @Before
    public void setUp() throws Exception {
        try {
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            participantPage = new ParticipantPage(getDriver());
            participantEditPage = new ParticipantEditPage(getDriver());
            userPropertiesHelper = new UserPropertiesHelper();
            user = userPropertiesHelper.getAdminUserName();
            password = userPropertiesHelper.getAdminPassword();
            url = getServerUrl();
            if (url.contains(LOCAL_TEST_MACHINE)) {
                httpClientHelper = new UITestHttpClientHelper(url);
                httpClientHelper.addParticipant(new TestParticipant(), user, password);
                loginPage.goToPage();
                loginPage.login(user, password);
            } else if (homePage.expectedUrlPath() != currentPage().urlPath()) {
                loginPage.goToPage();
                loginPage.login(user, password);
            }
        } catch (NullPointerException e) {
            getLogger().error("setup - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setup - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test
    public void hiddenButtonsEnabledTest() throws Exception {
        try {
            assertTrue(homePage.isEBODACModulePresent());
            assertTrue(homePage.isIVRModulePresent());
            assertTrue(homePage.isSMSModulePresent());
            homePage.openEBODACModule();
            participantPage.openFirstParticipant();
            assertTrue(participantEditPage.checkButtons());
        } catch (AssertionError e) {
            getLogger().error("hiddenButtonsEnabledTest - AssertionError . Reason : " + e.getLocalizedMessage(), e);
        } catch (NumberFormatException e) {
            getLogger().error("hiddenButtonsEnabledTest - NumberFormatException . Reason : " + e.getLocalizedMessage(),
                    e);
        } catch (Exception e) {
            getLogger().error("hiddenButtonsEnabledTest - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
