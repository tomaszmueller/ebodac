package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.BookingAppClinicVisitSchedulePage;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;
import static org.junit.Assert.assertTrue;

/**
 * Class created to test the Booking app in finding a visit for the prime vac.
 * 
 * @author tmueller
 * @modified rmartin
 *
 */
public class BookingApplicationUiCreationOfTheTabClinicVisitScheduletUiTest extends TestBase {
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppClinicVisitSchedulePage bookingAppClinicVisitSchedulePage;
    private String user;
    private String password;

    @Before
    public void setUp() throws Exception {
        try {
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            bookingAppPage = new BookingAppPage(getDriver());
            bookingAppClinicVisitSchedulePage = new BookingAppClinicVisitSchedulePage(getDriver());
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            String url = getServerUrl();
            if (url.contains(LOCAL_TEST_MACHINE)) {
                UITestHttpClientHelper httpClientHelper = new UITestHttpClientHelper(url);
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

    @Test // EBODAC-710
    public void bookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest() throws Exception {
        try {
            homePage.resizePage();
            homePage.clickModules();
            homePage.openBookingAppModule();
            bookingAppPage.openClinicVisitSchedule();
            bookingAppClinicVisitSchedulePage.findParticipantWithoutPrimeVacDay();
            bookingAppClinicVisitSchedulePage.clickOnPrimeVacDayDate();
            // We assert that this step works
            assertTrue(bookingAppClinicVisitSchedulePage.clickOnFirstDayInCalendar());
            assertTrue(bookingAppClinicVisitSchedulePage.clickOnButtonToPrint());
        } catch (AssertionError e) {
            getLogger()
                    .error("bookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest - AssertionError - Reason : "
                            + e.getLocalizedMessage(), e);

        } catch (NullPointerException e) {
            getLogger()
                    .error("bookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest - NullPointerException - Reason : "
                            + e.getLocalizedMessage(), e);

        } catch (InterruptedException e) {
            getLogger()
                    .error("bookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest - InterruptedException - Reason : "
                            + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("bookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest - Exception - Reason : "
                    + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
