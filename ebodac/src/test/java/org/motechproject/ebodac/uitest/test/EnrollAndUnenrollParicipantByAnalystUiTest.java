package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.EnrollmentPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;
import static org.junit.Assert.assertTrue;

public class EnrollAndUnenrollParicipantByAnalystUiTest extends TestBase {

    private static final String UNENROLLED = "Unenrolled";
    private static final String ENROLLED = "Enrolled";
    private LoginPage loginPage;
    private HomePage homePage;
    private EnrollmentPage enrollmentPage;
    private EBODACPage ebodacPage;
    private String l1AnalystUser;
    private String l1AnalystPassword;
    private UITestHttpClientHelper httpClientHelper;
    private String url;
    public static final int SLEEP_3SEC = 3000;
    public static final int BEGIN_LOOP = 1;
    private static final long SLEEP_2SEC = 2000;
    private UserPropertiesHelper userPropertiesHelper;

    @Before
    public void setUp() {
        userPropertiesHelper = new UserPropertiesHelper();
        l1AnalystUser = userPropertiesHelper.getAnalystUserName();
        l1AnalystPassword = userPropertiesHelper.getAnalystPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        ebodacPage = new EBODACPage(getDriver());
        enrollmentPage = new EnrollmentPage(getDriver());
        url = getServerUrl();
        if (url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant(), l1AnalystUser, l1AnalystPassword);
        }
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(l1AnalystUser, l1AnalystPassword);
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
        int endLoop = Integer.parseInt(enrollmentPage.getNumberOfEnrollments());
        boolean enrollunenrollsuccessfull = false;
        for (int i = BEGIN_LOOP+1; i <= endLoop; i++) {
            idOfParticipant = enrollmentPage.getCurrentParticipantId(""+i);
            enrollmentPage.sleep(SLEEP_2SEC);
            temp = enrollmentPage.getStatusOfCurrentParticipantEnrollment(""+i);
            enrollmentPage.sleep(SLEEP_2SEC);
            if (ENROLLED.equals(temp)) {
                boolean success = unenrollment(idOfParticipant);
                if(success) {
                    enrollment(idOfParticipant);
                    enrollunenrollsuccessfull = true;
                    break;
                }
            } else if(UNENROLLED.equals(temp)){
                boolean success = enrollment(idOfParticipant);
                if(success) {
                    unenrollment(idOfParticipant);
                    enrollunenrollsuccessfull = true;
                    break;
                }
            }
        }
        if(!enrollunenrollsuccessfull) {
            getLogger().error("No enrollment/unenrollment was performed");
        }
    }

    private boolean enrollment(String idOfParticipant) throws InterruptedException {
        enrollmentPage.clickOnButtonToEnrollParticipant(idOfParticipant);
        enrollmentPage.sleep(SLEEP_2SEC);
        enrollmentPage.clickOK();
        enrollmentPage.sleep(SLEEP_2SEC);
        if (enrollmentPage.error()) {
            enrollmentPage.clickOK();
            return false;
        } else {
            assertTrue(enrollmentPage.checkIfParticipantWasEnrolledOrUnenrolledSuccessfully());
            enrollmentPage.sleep(SLEEP_2SEC);
            enrollmentPage.clickOK();
            return true;
        }
    }

    private boolean unenrollment(String idOfParticipant) throws InterruptedException {
        enrollmentPage.clickOnButtonToUnenrollParticipant(idOfParticipant);
        enrollmentPage.sleep(SLEEP_2SEC);
        enrollmentPage.clickOK();
        enrollmentPage.sleep(SLEEP_2SEC);
        if (enrollmentPage.error()) {
            enrollmentPage.clickOK();
            return false;
        } else {
            assertTrue(enrollmentPage.checkIfParticipantWasEnrolledOrUnenrolledSuccessfully());
            enrollmentPage.sleep(SLEEP_2SEC);
            enrollmentPage.clickOK();
            return true;
        }
    }
    @After
    public void tearDown() throws Exception {
        logout();
    }
}
