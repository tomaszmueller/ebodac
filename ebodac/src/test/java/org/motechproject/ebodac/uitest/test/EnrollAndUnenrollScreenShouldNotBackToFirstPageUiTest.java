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
import static org.junit.Assert.assertEquals;

public class EnrollAndUnenrollScreenShouldNotBackToFirstPageUiTest extends TestBase {
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
    @Test  //EBODAC-828
    public void enrollAndUnenrollParticipantFromSecondPageTest() throws Exception {
        homePage.resizePage();
        homePage.openEBODACModule();
        ebodacPage.goToEnrollment();
        sleep(SLEEP_3000);
        if (!"1".equals(enrollmentPage.getAmmountPagesOfTable())) {
            enrollmentPage.goToNextPageInTable();
        } else {
            enrollmentPage.changeAmmountOfResultsShownOnPage();
            enrollmentPage.goToNextPageInTable();
        }
        sleep(SLEEP_3000);
        String pagingInfo = enrollmentPage.getNumberOfActualPage();
        String idOfParticipant = enrollmentPage.getParticipantId();
        String temp = enrollmentPage.getStatusOfFirstParticipantEnrollment();

        if ("Enrolled".equals(temp)) {
            enrollmentPage.clickOnButtonToUnenrollParticipant(idOfParticipant);
            enrollmentPage.clickOK();
            enrollmentPage.clickOK();
        } else {
            enrollmentPage.clickOnButtonToEnrollParticipant(idOfParticipant);
            enrollmentPage.clickOK();
            enrollmentPage.clickOK();
        }
        assertEquals(pagingInfo, enrollmentPage.getNumberOfActualPage());
    }
    @After
    public void tearDown() throws Exception {
        logout();
    }
}
