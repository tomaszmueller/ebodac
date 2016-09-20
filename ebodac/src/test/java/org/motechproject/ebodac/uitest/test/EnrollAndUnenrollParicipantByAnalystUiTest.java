package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.EnrollmentPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;
import static org.junit.Assert.assertTrue;

public class EnrollAndUnenrollParicipantByAnalystUiTest extends TestBase {
    private static final String ENROLLED = "Enrolled";
    private LoginPage loginPage;
    private HomePage homePage;
    private EnrollmentPage enrollmentPage;
    private EBODACPage ebodacPage;
    private String l1AdminUser;
    private String l1AdminPassword;
    private UITestHttpClientHelper httpClientHelper;
    private String url;
    public static final int SLEEP_3SEC = 3000;
    public static final int BEGIN_LOOP = 0;
    public static final int END_LOOP = 1;
    private static final long SLEEP_2SEC = 2000;

    @Before
    public void setUp() {
        l1AdminUser = getTestProperties().getUserName();
        l1AdminPassword = getTestProperties().getPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        ebodacPage = new EBODACPage(getDriver());
        enrollmentPage = new EnrollmentPage(getDriver());
        url = getServerUrl();
        if (url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant(), l1AdminUser, l1AdminPassword);
        }
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(l1AdminUser, l1AdminPassword);
        }
    }

    @Test // EBODAC-476
    public void enrollAndUnenrollParticipant() throws Exception {
        String idOfParticipant = "";
        String temp = "";
        homePage.openEBODACModule();
        homePage.sleep(SLEEP_2SEC);
        homePage.resizePage();
        homePage.sleep(SLEEP_2SEC);
        ebodacPage.goToEnrollment();
        enrollmentPage.goToPage();
        enrollmentPage.sleep(SLEEP_2SEC);
        for (int i = BEGIN_LOOP; i <= END_LOOP; i++) {
            idOfParticipant = enrollmentPage.getParticipantId();
            enrollmentPage.sleep(SLEEP_2SEC);
            temp = enrollmentPage.getStatusOfFirstParticipantEnrollment();
            enrollmentPage.sleep(SLEEP_2SEC);
            if (ENROLLED.equals(temp)) {
                enrollmentPage.clickOnButtonToUnenrollParticipant(idOfParticipant);
                enrollmentPage.sleep(SLEEP_2SEC);
                enrollmentPage.clickOK();
                enrollmentPage.sleep(SLEEP_2SEC);
                assertTrue(enrollmentPage.checkIfParticipantWasEnrolledOrUnenrolledSuccessfully());
                enrollmentPage.sleep(SLEEP_2SEC);
                enrollmentPage.clickOK();
            } else {
                enrollmentPage.clickOnButtonToEnrollParticipant(idOfParticipant);
                enrollmentPage.sleep(SLEEP_2SEC);
                enrollmentPage.clickOK();
                enrollmentPage.sleep(SLEEP_2SEC);
                assertTrue(enrollmentPage.checkIfParticipantWasEnrolledOrUnenrolledSuccessfully());
                enrollmentPage.sleep(SLEEP_2SEC);
                enrollmentPage.clickOK();
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
