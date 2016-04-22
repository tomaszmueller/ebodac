package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.EnrollmentPage;
import static junit.framework.Assert.assertTrue;

public class EnrollAndUnenrollParticipantUiTest extends TestBase {
    private LoginPage loginPage;
    private HomePage homePage;
    private EnrollmentPage enrollmentPage;
    private EBODACPage ebodacPage;
    private ParticipantEditPage participantEditPage;
    private String l1AdminUser;
    private String l1AdminPassword;
    private UITestHttpClientHelper httpClientHelper;
    private String url;
    @Before
    public void setUp() {
        l1AdminUser = properties.getUserName();
        l1AdminPassword = properties.getPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        ebodacPage = new EBODACPage(driver);
        enrollmentPage = new EnrollmentPage(driver);
        participantEditPage = new ParticipantEditPage(driver);
        url = properties.getWebAppUrl();
        if (url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant(),l1AdminUser,l1AdminPassword);
        }
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.login(l1AdminUser , l1AdminPassword);
        }
    }
    @Test  //Test for EBODAC-524, EBODAC-525
    public void enrollAndUnenrollParticipantTest() throws Exception {
        homePage.openEBODACModule();
        ebodacPage.goToEnrollment();
        enrollmentPage.clickAction();
        enrollmentPage.clickOK();
        if (enrollmentPage.error()) {
            enrollmentPage.clickOK();
            enrollmentPage.nextAction();
            enrollmentPage.clickOK();
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
    }
    @After
    public void tearDown() throws Exception {
        logout();
    }
}
