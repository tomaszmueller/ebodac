package org.motechproject.ebodac.uitest.test;

import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

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
import static org.junit.Assert.assertEquals;
import org.apache.log4j.Logger;

public class AdminChangePhoneNumberAndGetParticipantFromZetesUiTest extends TestBase {
    private static final String LOCAL_TEST_MACHINE = "localhost";
    // Object initialization for log
    private static Logger log = Logger
            .getLogger(AdminChangePhoneNumberAndGetParticipantFromZetesUiTest.class.getName());
    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private UserPropertiesHelper userPropertiesHelper;
    private String user;
    private String password;
    private String testNumber = "232000000117";
    private String changedNumber;
    private UITestHttpClientHelper httpClientHelper;
    private String url;
    static final int SLEEP_2000 = 2000;

    @Before
    public void setUp() throws Exception {
        try {
            userPropertiesHelper = new UserPropertiesHelper();
            user = userPropertiesHelper.getAdminUserName();
            password = userPropertiesHelper.getAdminPassword();
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
            log.error("setup - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            log.error("setup - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test // Test for EBODAC-508/EBODAC-509
    public void changePhoneNumberTest() throws Exception {
        try {
            homePage.resizePage();
            homePage.openEBODACModule();
            sleep(SLEEP_2000);
            ebodacPage.showParticipants();
            sleep(SLEEP_2000);
            // Open the participant
            participantPage.openFirstParticipant();
            // Set the phone number.
            participantEditPage.changePhoneNumber(testNumber);
            // New phone number
            changedNumber = participantEditPage.getPhoneNumber();
            log.error("testNumber =*****" + testNumber + "   changedNumber : ******" + changedNumber + "******");
            assertEquals(testNumber, changedNumber);
        } catch (AssertException e) {
            log.error("changePhoneNumberTest - AssertException . Reason : " + e.getLocalizedMessage(), e);
        } catch (NumberFormatException e) {
            log.error("changePhoneNumberTest - NumberFormatException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            log.error("changePhoneNumberTest - Exception . Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
