package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppPrimeVaccinationPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

import static org.junit.Assert.assertTrue;
import org.apache.log4j.Logger;

public class BookingApplicationAddAButtonToCreatePrimeVaccBookingTestUiTest extends TestBase {
    private static final int MAX_COUNTER_VALUE = 10;
    private static Logger log = Logger
            .getLogger(BookingApplicationAddAButtonToCreatePrimeVaccBookingTestUiTest.class.getName());
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

    @Test // EBODAC-781
    public void bookingApplicationCapacityInfoTest() throws InterruptedException {
        homePage.resizePage();
        homePage.clickModules();
        homePage.openBookingAppModule();
        bookingAppPage.openPrimeVaccination();
        bookingAppPrimeVaccinationPage.clickAddPrimeVaccinationButton();

        // bookingAppPrimeVaccinationPage.sleepForMilisec(SLEEP_5000);
        // We Have to wait for a while to have the participant list.
        int counter = 10; // We make sure we do not generate an infinite loop
        while (!bookingAppPrimeVaccinationPage.isPartincipantIdEnabled() && counter > 0) {
            bookingAppPrimeVaccinationPage.clickFirstParticipantId();
            counter--;
        }
        // After setting the participant id the rest should run step by step.
        bookingAppPrimeVaccinationPage.setDateOfScreeningDate();
        bookingAppPrimeVaccinationPage.setFemaleChildBearingAge();
        // We click this option to make sure we can select dates for the
        // prime.vaccination
        counter = MAX_COUNTER_VALUE; // reset counter;
        // Check the ignore earliest date.
        boolean clickOnIngoreLatesEarliestDate = bookingAppPrimeVaccinationPage.clickOnIngoreLatesEarliestDate();
        // log.error("clickOnIngoreLatesEarliestDate = " +
        // clickOnIngoreLatesEarliestDate);
        while (!bookingAppPrimeVaccinationPage.isEnabledSaveButton() && counter > 0) {
            // After checking the check-box to ignore the EarliestDate we select
            // the dates.
            if (clickOnIngoreLatesEarliestDate) {
                bookingAppPrimeVaccinationPage.setDateOfPrimeVacDateFields();
                bookingAppPrimeVaccinationPage.setTimeOfPrimeVacDateFields();

            }
            // We make sure that the Save Button is enabled.
            if (bookingAppPrimeVaccinationPage.isEnabledSaveButton()) {
                counter = -1;
            } else {
                counter--;
            }
        }
        // We Validate that the Button is Enabled.
        try {
            assertTrue(bookingAppPrimeVaccinationPage.isEnabledSaveButton());
        } catch (AssertException e) {
            log.error("bookingApplicationCapacityInfoTest - Assert Exception :" + e.getLocalizedMessage(), e);
        }
        bookingAppPrimeVaccinationPage.saveCreatedPrimeVaccination();
        bookingAppPrimeVaccinationPage.confirmAddVisitBookingDetailsAndPrintCard();
        bookingAppPrimeVaccinationPage.closePdfIfIsOpen();

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }

}
