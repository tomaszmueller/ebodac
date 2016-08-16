package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.EnrollmentPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertTrue;

public class EnrollAndUnenrollParicipantByAnalystUiTest extends TestBase {
    private LoginPage loginPage;
    private HomePage homePage;
    private EnrollmentPage enrollmentPage;
    private EBODACPage ebodacPage;
    private ParticipantEditPage participantEditPage;
    private String l1AdminUser;
    private String l1AdminPassword;
    private UITestHttpClientHelper httpClientHelper;
    private String url;
    public static final int SLEEP_3000 = 3000;
    public static final int BEGIN_LOOP = 0;
    public static final int END_LOOP = 1;


    @Before
    public void setUp() {
        l1AdminUser = getTestProperties().getUserName();
        l1AdminPassword = getTestProperties().getPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        ebodacPage = new EBODACPage(getDriver());
        enrollmentPage = new EnrollmentPage(getDriver());
        participantEditPage = new ParticipantEditPage(getDriver());
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
    @Test  //EBODAC-476
    public void enrollAndUnenrollParticipant() throws Exception {
        homePage.resizePage();
        homePage.openEBODACModule();
        ebodacPage.goToEnrollment();
        sleep(SLEEP_3000);
        for (int i = BEGIN_LOOP; i <= END_LOOP; i++) {
            String idOfParticipant = enrollmentPage.getParticipantId();
            String temp = enrollmentPage.getStatusOfFirstParticipantEnrollment();
            if ("Enrolled".equals(temp)) {
                enrollmentPage.clickOnButtonToUnenrollParticipant(idOfParticipant);
                enrollmentPage.clickOK();
                assertTrue(enrollmentPage.checkIfParticipantWasEnrolledOrUnenrolledSuccessfully());
                enrollmentPage.clickOK();
            } else {
                enrollmentPage.clickOnButtonToEnrollParticipant(idOfParticipant);
                enrollmentPage.clickOK();
                assertTrue(enrollmentPage.checkIfParticipantWasEnrolledOrUnenrolledSuccessfully());
                enrollmentPage.clickOK();
            }
        }
    }
    @After
    public void tearDown() throws Exception {
        logout();
    }
}
