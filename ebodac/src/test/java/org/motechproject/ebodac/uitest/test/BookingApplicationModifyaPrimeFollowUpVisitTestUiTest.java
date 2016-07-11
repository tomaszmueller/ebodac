package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppPrimeVaccinationPage;
import org.motechproject.ebodac.uitest.page.BookingAppRescheduleVisitPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;

import static org.junit.Assert.assertEquals;

public class BookingApplicationModifyaPrimeFollowUpVisitTestUiTest extends TestBase {

    private BookingAppRescheduleVisitPage bookingAppRescheduleVisitPage;

    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;

    private BookingAppPrimeVaccinationPage bookingAppPrimeVaccinationPage;
    private String user;
    private String password;

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

    @Test//EBODAC-798
    public void modifyAPrimeFollowUpVisit() throws InterruptedException {
        homePage.resizePage();
        homePage.clickModules();
        homePage.openBookingAppModule();
        bookingAppPage.openPrimeVaccination();
        assertEquals(true, bookingAppPrimeVaccinationPage.checkIfElementAddPrimeVaccinationIsVisible());
        bookingAppPrimeVaccinationPage.setMaxDateRangeOfPrimeVaccination();
        bookingAppPrimeVaccinationPage.clickOnFirstRowInTheGridUI();
        bookingAppPrimeVaccinationPage.clickOnIngoreLatesEarliestDate();
        bookingAppPrimeVaccinationPage.setDateOfPrimeVacDateFields();
        bookingAppPrimeVaccinationPage.setTimeOfPrimeVacDateFields();
        bookingAppPrimeVaccinationPage.clickSaveInUpdateVisitBookingDetails();
        bookingAppPrimeVaccinationPage.confirmAddVisitBookingDetailsAndPrintCard();
        bookingAppPrimeVaccinationPage.closePdfIfIsOpen();
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
