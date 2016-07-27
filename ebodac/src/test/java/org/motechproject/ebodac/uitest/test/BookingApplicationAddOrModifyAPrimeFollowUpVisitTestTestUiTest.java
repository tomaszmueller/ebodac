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

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import org.apache.log4j.Logger;

public class BookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest extends TestBase {
    private static Logger log = Logger
            .getLogger(BookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest.class.getName());
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppClinicVisitSchedulePage bookingAppClinicVisitSchedulePage;
    private String user;
    private String password;
    public static final int SLEEP_1SEC = 1000;

    @Before
    public void setUp() {
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
    }

    @Test // EBODAC-800
    public void bookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest() throws InterruptedException {
        try {
            homePage.resizePage();
            homePage.clickModules();
            homePage.openBookingAppModule();
            bookingAppPage.openClinicVisitSchedule();
            bookingAppClinicVisitSchedulePage.clickOnDropDownParticipantId();
            sleep(SLEEP_1SEC);
            String dayBeforeClean = bookingAppClinicVisitSchedulePage.getPrimeVacDateInput();
            bookingAppClinicVisitSchedulePage.clickOnPrimeVacDayDate();
            bookingAppClinicVisitSchedulePage.clickOnFirstDayInCalendar();
            bookingAppClinicVisitSchedulePage.clickButtonCleanDate();
            // Assert to validate the changes.
            assertEquals(dayBeforeClean, bookingAppClinicVisitSchedulePage.assertIfPrimeVacDayIsEmpty());
        } catch (AssertException e) {
            log.error("bookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest - Error Assert : Reason :"
                    + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            log.error("bookingApplicationAddOrModifyAPrimeFollowUpVisitTestTestUiTest - Exception . Reason :"
                    + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
