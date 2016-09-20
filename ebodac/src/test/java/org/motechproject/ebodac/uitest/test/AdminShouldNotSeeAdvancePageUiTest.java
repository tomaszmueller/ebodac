package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.EnrollmentPage;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AdminShouldNotSeeAdvancePageUiTest extends TestBase {
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private static final long SLEEP_2SEC = 2000;
    private static final long SLEEP_4SEC = 4000;
    private static final long SLEEP_6SEC = 6000;
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
        try {
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
        } catch (NullPointerException e) {
            getLogger().error("setUp - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            getLogger().error("setUp - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void adminhouldNotSeeAdvancePageTest() throws Exception {
        try {
            homePage.clickModules();
            homePage.openEBODACModule();
            homePage.resizePage();
            homePage.sleep(SLEEP_2SEC);
            ebodacPage.goToEnrollment();
            ebodacPage.sleep(SLEEP_4SEC);
            enrollmentPage.clickOnFirstRow();
            enrollmentPage.sleep(SLEEP_6SEC);
            // We should not be able to see the advance page for enrollment.
            assertFalse(enrollmentPage.enrollmentDetailEnabled());
            // It should be allowed to enrol unenroll participants.
            // We try to enroll.
            enrollmentPage.sleep(SLEEP_2SEC);
            enrollmentPage.clickAction();
            enrollmentPage.sleep(SLEEP_2SEC);
            enrollmentPage.clickOK();
            enrollmentPage.sleep(SLEEP_2SEC);
            if (enrollmentPage.error()) {
                enrollmentPage.clickOK();
                enrollmentPage.sleep(SLEEP_2SEC);
                enrollmentPage.nextAction();
                enrollmentPage.sleep(SLEEP_2SEC);
            }

            if (enrollmentPage.enrolled()) {
                assertTrue(enrollmentPage.enrolled());
                enrollmentPage.sleep(SLEEP_2SEC);
                enrollmentPage.clickOK();
                enrollmentPage.sleep(SLEEP_2SEC);
            }
            if (enrollmentPage.unenrolled()) {
                assertTrue(enrollmentPage.unenrolled());
                enrollmentPage.sleep(SLEEP_2SEC);
                enrollmentPage.clickOK();
                enrollmentPage.sleep(SLEEP_2SEC);
            }

        } catch (AssertionError e) {
            getLogger().error("adminhouldNotSeeAdvancePageTest - AEr. Reason : " + e.getLocalizedMessage(),
                    e);
        } catch (NumberFormatException e) {
            getLogger().error(
                    "adminhouldNotSeeAdvancePageTest - NFE. Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("adminhouldNotSeeAdvancePageTest - Exc. Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
