package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppPrimeVaccinationPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;
import org.apache.log4j.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BookingApplicationModifyaPrimeFollowUpVisitTestUiTest extends TestBase {
    private static final int COUNTER_ZERO = 0;
    private static final int MAX_COUNTER_VALUE = 50;
    // Object initialization for log
    private static Logger log = Logger.getLogger(BookingApplicationModifyaPrimeFollowUpVisitTestUiTest.class.getName());
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

    @Test // EBODAC-798
    public void modifyAPrimeFollowUpVisit() throws InterruptedException {
        homePage.resizePage();
        homePage.clickModules();
        homePage.openBookingAppModule();
        bookingAppPage.openPrimeVaccination();
        assertEquals(true, bookingAppPrimeVaccinationPage.checkIfElementAddPrimeVaccinationIsVisible());
        bookingAppPrimeVaccinationPage.setMaxDateRangeOfPrimeVaccination();
        // Check if we have bookings to modify
        int counter = MAX_COUNTER_VALUE;
        while (!bookingAppPrimeVaccinationPage.isModalVisible() && counter > 0) {
            counter--;
            bookingAppPrimeVaccinationPage.clickOnFirstRowInTheGridUI();
        } 
        if (counter != MAX_COUNTER_VALUE) {
            //We validate that modal is visible.
            assertTrue(bookingAppPrimeVaccinationPage.isModalVisible());
            
            bookingAppPrimeVaccinationPage.clickOnFirstRowInTheGridUI();
            
            bookingAppPrimeVaccinationPage.clickOnIngoreLatesEarliestDate();
            
            bookingAppPrimeVaccinationPage.setDateOfPrimeVacDateFields();
            
            bookingAppPrimeVaccinationPage.setTimeOfPrimeVacDateFields();
           
            bookingAppPrimeVaccinationPage.clickSaveInUpdateVisitBookingDetails();
            
            bookingAppPrimeVaccinationPage.confirmAddVisitBookingDetailsAndPrintCard();
            bookingAppPrimeVaccinationPage.closePdfIfIsOpen();
        } else {
            
            log.error("No visit possible to click");
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
