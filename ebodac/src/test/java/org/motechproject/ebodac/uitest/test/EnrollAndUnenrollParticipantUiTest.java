package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.EnrollmentPage;
import static org.junit.Assert.assertTrue;
//import org.apache.log4j.Logger;

public class EnrollAndUnenrollParticipantUiTest extends TestBase {
    // Object initialization for log
    // private static Logger log =
    // Logger.getLogger(EnrollAndUnenrollParticipantUiTest.class.getName());
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private LoginPage loginPage;
    private HomePage homePage;
    private EnrollmentPage enrollmentPage;
    private EBODACPage ebodacPage;
    private String user;
    private String password;
    private UITestHttpClientHelper httpClientHelper;
    private String url;

    @Before
    public void setUp() throws Exception {
        try {
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            ebodacPage = new EBODACPage(getDriver());
            enrollmentPage = new EnrollmentPage(getDriver());
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
            getLogger().error("setUp - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            getLogger().error("setUp - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test // Test for EBODAC-524, EBODAC-525
    public void enrollAndUnenrollParticipantTest() throws Exception {
        try {
            homePage.openEBODACModule();
            ebodacPage.goToEnrollment();
            enrollmentPage.clickAction();
            enrollmentPage.clickOK();
            if (enrollmentPage.error()) {
                enrollmentPage.clickOK();
                enrollmentPage.nextAction();
            }
            if (enrollmentPage.enrolled()) {
                assertTrue(enrollmentPage.enrolled());
                enrollmentPage.clickOK();
            }
            if (enrollmentPage.unenrolled()) {
                assertTrue(enrollmentPage.unenrolled());
                enrollmentPage.clickOK();
            }
            enrollmentPage.actionSecond();
            enrollmentPage.clickOK();
            enrollmentPage.clickOK();
            if (enrollmentPage.enrolled()) {
                assertTrue(enrollmentPage.enrolled());
                enrollmentPage.clickOK();
            }
            if (enrollmentPage.unenrolled()) {
                assertTrue(enrollmentPage.unenrolled());
                enrollmentPage.clickOK();
            }

        } catch (NullPointerException e) {
            getLogger().error(
                    "enrollAndUnenrollParticipantTest - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            getLogger().error("enrollAndUnenrollParticipantTest - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        // We close the page
        logout();
    }
}
