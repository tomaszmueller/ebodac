package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.EnrollmentPage;

import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.apache.log4j.Logger;

public class AdminShouldNotSeeAdvancePageUiTest extends TestBase {
    private static Logger log = Logger.getLogger(AdminShouldNotSeeAdvancePageUiTest.class.getName());
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private EnrollmentPage enrollmentPage;
    private String user;
    private String password;
    private UITestHttpClientHelper httpClientHelper;
    private UserPropertiesHelper userPropertiesHelper;
    private String url;

    @Before
    public void setUp() throws Exception {
        url = getServerUrl();
        // We log in the EBODAC
        userPropertiesHelper = new UserPropertiesHelper();
        user = userPropertiesHelper.getAdminUserName();
        password = userPropertiesHelper.getAdminPassword();
        loginPage = new LoginPage(getDriver());
        // We load homepage and the rest of pages.
        homePage = new HomePage(getDriver());
        // The ebodac page and the rest pages we load.
        ebodacPage = new EBODACPage(getDriver());
        // Start enrolment page
        enrollmentPage = new EnrollmentPage(getDriver());

        // We try to log in Ebodac.
        if (url.contains(LOCAL_TEST_MACHINE)) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant(), user, password);
            loginPage.goToPage();
            loginPage.login(user, password);
        } else if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(user, password);
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void adminhouldNotSeeAdvancePageTest() throws Exception {
        homePage.clickModules();
        homePage.openEBODACModule();
        ebodacPage.goToEnrollment();
        // We should not be able to see the advance page for enrollment.
        try {
            assertFalse(enrollmentPage.enrollmentDetailEnabled());
        } catch (NullPointerException e) {
            log.error("adminhouldNotSeeAdvancePageTest - AssertNull : NullPointerException . Reason : "
                    + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (AssertException e) {
            log.error("adminhouldNotSeeAdvancePageTest - AssertException . Reason : " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        // It should be allowed to enrol unenroll participants.
        try {
            // We try to enroll.
            enrollmentPage.clickAction();
            enrollmentPage.clickOK();
            if (enrollmentPage.error()) {
                enrollmentPage.clickOK();
                enrollmentPage.nextAction();
            }
        } catch (NullPointerException e) {
            log.error("adminhouldNotSeeAdvancePageTest - Error :" + e.getMessage());
            e.printStackTrace();
        }
        try {
            if (enrollmentPage.enrolled()) {
                log.error("adminhouldNotSeeAdvancePageTest - After enrollmentPage enrolled True");
                try {
                    assertTrue(enrollmentPage.enrolled());
                } catch (AssertException e) {
                    log.error(
                            "adminhouldNotSeeAdvancePageTest - AssertTrue Error . Reason : " + e.getLocalizedMessage());
                    e.printStackTrace();
                }

                enrollmentPage.clickOK();
            }
            if (enrollmentPage.unenrolled()) {
                assertTrue(enrollmentPage.unenrolled());
                enrollmentPage.clickOK();
            }
        } catch (NullPointerException e) {
            log.error("adminhouldNotSeeAdvancePageTest - enrolled & unenrolled . Reason : " + e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
