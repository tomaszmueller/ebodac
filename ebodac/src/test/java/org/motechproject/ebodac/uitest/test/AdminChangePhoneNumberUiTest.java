package org.motechproject.ebodac.uitest.test;

import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;
import java.lang.Exception;
import static java.lang.Thread.sleep;

public class AdminChangePhoneNumberUiTest extends TestBase {
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private UserPropertiesHelper userPropertiesHelper;
    private String user;
    private String password;
    private String testNumber = "232000000117";
    private UITestHttpClientHelper httpClientHelper;
    private String url;
    static final int SLEEP_2SEC = 2000;

    @Before
    public void setUp() throws Exception {
        try {
            // Admin user / password
            userPropertiesHelper = new UserPropertiesHelper();
            user = userPropertiesHelper.getAdminUserName();
            password = userPropertiesHelper.getAdminPassword();

            // Init build constructors pages.
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            ebodacPage = new EBODACPage(getDriver());
            participantPage = new ParticipantPage(getDriver());
            participantEditPage = new ParticipantEditPage(getDriver());

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

    @Test // Test for EBODAC-508/EBODAC-509
    public void changePhoneNumberTest() throws Exception {
        try {
            homePage.clickModules();
            sleep(SLEEP_2SEC);
            homePage.resizePage();
            sleep(SLEEP_2SEC);
           
            homePage.clickOnEbodac();
            sleep(SLEEP_2SEC);
            homePage.openEBODACModule();
            homePage.resizePage();
            sleep(SLEEP_2SEC);
            //homePage.clickOnEbodac();
            sleep(SLEEP_2SEC);
            ebodacPage.showParticipants();
            sleep(SLEEP_2SEC);
            // Open the participant
            participantPage.goToPage();
            participantPage.openFirstParticipant();
            sleep(SLEEP_2SEC);
            // Set the phone number.
            participantEditPage.changePhoneNumber(testNumber);

        } catch (NullPointerException e) {
            getLogger().error("changePhoneNumberTest - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (NumberFormatException e) {
            getLogger().error("changePhoneNumberTest - NumberFormatException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("changePhoneNumberTest - Exception . Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
