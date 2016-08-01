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
import static org.junit.Assert.assertTrue;
import org.apache.log4j.Logger;

public class BookingApplicationAddAButtonToCreatePrimeVaccBookingTestUiTest extends TestBase {
    private static final int MAX_COUNTER_VALUE = 10;
    private static Logger log = Logger
            .getLogger(BookingApplicationAddAButtonToCreatePrimeVaccBookingTestUiTest.class.getName());
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
            log.error("setUp - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            log.error("setUp - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test // EBODAC-781
    public void bookingApplicationCapacityInfoTest() throws Exception {
        try {
            homePage.resizePage();
            homePage.clickModules();
            homePage.openBookingAppModule();
            bookingAppPage.openPrimeVaccination();
            bookingAppPrimeVaccinationPage.clickAddPrimeVaccinationButton();

            // bookingAppPrimeVaccinationPage.sleepForMilisec(SLEEP_5000);
            // We Have to wait for a while to have the participant list.
            int counter = 10; // We make sure we do not generate an infinite
                              // loop
            while (!bookingAppPrimeVaccinationPage.isPartincipantIdEnabled() && counter > 0) {
                bookingAppPrimeVaccinationPage.clickFirstParticipantId();
                counter--;
            }
            // After setting the participant id the rest should run step by
            // step.
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
                // After checking the check-box to ignore the EarliestDate we
                // select
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
            bookingAppPrimeVaccinationPage.isEnabledSaveButton();
            bookingAppPrimeVaccinationPage.saveCreatedPrimeVaccination();
            bookingAppPrimeVaccinationPage.confirmAddVisitBookingDetailsAndPrintCard();
            assertTrue(bookingAppPrimeVaccinationPage.closePdfIfIsOpen());

        } catch (AssertionError e) {
            log.error("bookingApplicationCapacityInfoTest - AssertException - Reason : " + e.getLocalizedMessage(), e);

        } catch (NullPointerException e) {
            log.error("bookingApplicationCapacityInfoTest - NullPointerException - Reason : " + e.getLocalizedMessage(),
                    e);

        } catch (Exception e) {
            log.error("bookingApplicationCapacityInfoTest - Exception - Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }

}
