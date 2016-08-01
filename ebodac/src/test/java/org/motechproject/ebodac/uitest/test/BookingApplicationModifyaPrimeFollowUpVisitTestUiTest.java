package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppPrimeVaccinationPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

import org.apache.log4j.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BookingApplicationModifyaPrimeFollowUpVisitTestUiTest extends TestBase {
    private static final int COUNTER_ZERO = 0;
    private static final int MAX_COUNTER_VALUE = 50;
    // Object initialization for log
    private static Logger log = Logger.getLogger(BookingApplicationModifyaPrimeFollowUpVisitTestUiTest.class.getName());
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;

    private BookingAppPrimeVaccinationPage bookingAppPrimeVaccinationPage;
    private String user;
    private String password;

    @Before
    public void setUp() throws Exception {
        try {
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            bookingAppPage = new BookingAppPage(getDriver());
            bookingAppPrimeVaccinationPage = new BookingAppPrimeVaccinationPage(getDriver());
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            url = getServerUrl();
            if (url.contains(LOCAL_TEST_MACHINE)) {
                httpClientHelper = new UITestHttpClientHelper(url);
                httpClientHelper.addParticipant(new TestParticipant(), user, password);
                loginPage.goToPage();
                loginPage.login(user, password);
            } else if (homePage.expectedUrlPath() != currentPage().urlPath()) {
                loginPage.goToPage();
                loginPage.login(user, password);
            }
        } catch (NullPointerException e) {
            log.error("setup - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            log.error("setup - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test // EBODAC-798
    public void modifyAPrimeFollowUpVisit() throws Exception {
        try {
            homePage.resizePage();
            homePage.clickModules();
            homePage.openBookingAppModule();
            bookingAppPage.openPrimeVaccination();
            assertEquals(true, bookingAppPrimeVaccinationPage.checkIfElementAddPrimeVaccinationIsVisible());
            bookingAppPrimeVaccinationPage.setMaxDateRangeOfPrimeVaccination();
            // Check if we have bookings to modify
            int counter = MAX_COUNTER_VALUE;
            while (!bookingAppPrimeVaccinationPage.isModalVisible() && counter > COUNTER_ZERO) {
                counter--;
                bookingAppPrimeVaccinationPage.clickOnFirstRowInTheGridUI();
            }
            if (counter != MAX_COUNTER_VALUE) {
                // We validate that modal is visible.
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
        } catch (AssertException e) {
            log.error("modifyAPrimeFollowUpVisit - AssertException . Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            log.error("modifyAPrimeFollowUpVisit - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            log.error("modifyAPrimeFollowUpVisit - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            log.error("modifyAPrimeFollowUpVisit - Exception . Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
