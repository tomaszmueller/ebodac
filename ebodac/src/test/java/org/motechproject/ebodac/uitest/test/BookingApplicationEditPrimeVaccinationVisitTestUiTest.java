package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppPrimeVaccinationPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

public class BookingApplicationEditPrimeVaccinationVisitTestUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;

    private BookingAppPrimeVaccinationPage bookingAppPrimeVaccinationPage;
    private String user;
    private String password;
    static final int SLEEP_500 = 500;
    static final int SLEEP_2000 = 2000;

    @Before
    public void setUp() {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        bookingAppPage = new BookingAppPage(getDriver());
        bookingAppPrimeVaccinationPage = new BookingAppPrimeVaccinationPage(getDriver());
        user = getTestProperties().getUserName();
        password = getTestProperties().getPassword();
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(user, password);
        }
    }

    @Test //EBODAC-781
    public void bookingApplicationEditPrimeVaccinationVisitTest() throws InterruptedException {
        homePage.resizePage();
        homePage.clickModules();
        homePage.openBookingAppModule();
        bookingAppPage.openPrimeVaccination();
        assertEquals(true, bookingAppPrimeVaccinationPage.checkIfElementAddPrimeVaccinationIsVisible());
        bookingAppPrimeVaccinationPage.changeDateRangeFromToday();
        sleep(SLEEP_2000);
        bookingAppPrimeVaccinationPage.sortTableByPrimeVacDate();
        sleep(SLEEP_2000);
        bookingAppPrimeVaccinationPage.sortTableByPrimeVacDate();
        sleep(SLEEP_2000);
        String participantId = bookingAppPrimeVaccinationPage.firstParticipantId();
        String participantVacDate = bookingAppPrimeVaccinationPage.firstParticipantPrimeVacDay();
        bookingAppPrimeVaccinationPage.clickOnFirstRowInTheGridUI();
        sleep(SLEEP_500);
        bookingAppPrimeVaccinationPage.changeDates();
        sleep(SLEEP_500);
        bookingAppPrimeVaccinationPage.saveAndConfirmChanges();
        bookingAppPrimeVaccinationPage.findParticipantInLookup(participantId);
        String participantVadDateAfterChange = bookingAppPrimeVaccinationPage.firstParticipantPrimeVacDay();
        assertEquals(false, participantVacDate.equals(participantVadDateAfterChange));
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
