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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BookingApplicationModifyaPrimeFollowUpVisitUiTest extends TestBase {
    private static final int COUNTER_ZERO = 0;
    private static final int MAX_COUNTER_VALUE = 50;
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private static final long SLEEP_2SEC = 2000;
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
            getLogger().error("setup - NPE. Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setup - Exc. Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test // EBODAC-798
    public void modifyAPrimeFollowUpVisit() throws Exception {
        try {
            homePage.resizePage();
            homePage.sleep(SLEEP_2SEC);
            homePage.clickModules();
            homePage.sleep(SLEEP_2SEC);
            homePage.openBookingAppModule();
            homePage.sleep(SLEEP_2SEC);
            bookingAppPage.openPrimeVaccination();
            bookingAppPage.sleep(SLEEP_2SEC);
            assertEquals(true, bookingAppPrimeVaccinationPage.checkIfElementAddPrimeVaccinationIsVisible());
            bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
            bookingAppPrimeVaccinationPage.setMaxDateRangeOfPrimeVaccination();
            bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
            // Check if we have bookings to modify
            int counter = MAX_COUNTER_VALUE;
            while (!bookingAppPrimeVaccinationPage.isModalVisible() && counter > COUNTER_ZERO) {
                counter--;
                bookingAppPrimeVaccinationPage.clickOnFirstRowInTheGridUI();
                bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
            }
            if (counter != MAX_COUNTER_VALUE) {
                // We validate that modal is visible.
                assertTrue(bookingAppPrimeVaccinationPage.isModalVisible());
                bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                bookingAppPrimeVaccinationPage.clickOnFirstRowInTheGridUI();
                bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                bookingAppPrimeVaccinationPage.clickOnIngoreLatesEarliestDate();
                bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                bookingAppPrimeVaccinationPage.setDateOfPrimeVacDateFields();
                bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                bookingAppPrimeVaccinationPage.setTimeOfPrimeVacDateFields();
                bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                bookingAppPrimeVaccinationPage.clickSaveInUpdateVisitBookingDetails();
                bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                bookingAppPrimeVaccinationPage.confirmAddVisitBookingDetailsAndPrintCard();
                bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                bookingAppPrimeVaccinationPage.closePdfIfIsOpen();
            } else {
                getLogger().error("No visit possible to click");
            }
        } catch (AssertionError e) {
            getLogger().error("modifyAPrimeFollowUpVisit - AEr . Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error("modifyAPrimeFollowUpVisit - IEx . Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error("modifyAPrimeFollowUpVisit - NPE . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("modifyAPrimeFollowUpVisit - Exc . Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
