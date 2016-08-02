package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.page.BookingAppClinicVisitSchedulePage;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;

public class BookingApplicationUiCreationOfTheTabClinicVisitScheduletUiTest extends TestBase {
    // Object initialization for log
    private static Logger log = Logger
            .getLogger(BookingApplicationUiCreationOfTheTabClinicVisitScheduletUiTest.class.getName());
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
            if (homePage.expectedUrlPath() != currentPage().urlPath()) {
                loginPage.goToPage();
                loginPage.login(user, password);
            }
        } catch (NullPointerException e) {
            log.error("setUp - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            log.error("setUp - Exception - Reason : " + e.getLocalizedMessage(), e);
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
        } catch (AssertException e) {
            log.error("bookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest - AssertException - Reason : "
                    + e.getLocalizedMessage(), e);

        } catch (NullPointerException e) {
            log.error(
                    "bookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest - NullPointerException - Reason : "
                            + e.getLocalizedMessage(),
                    e);

        } catch (InterruptedException e) {
            log.error(
                    "bookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest - InterruptedException - Reason : "
                            + e.getLocalizedMessage(),
                    e);
        } catch (Exception e) {
            log.error("bookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest - Exception - Reason : "
                    + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
