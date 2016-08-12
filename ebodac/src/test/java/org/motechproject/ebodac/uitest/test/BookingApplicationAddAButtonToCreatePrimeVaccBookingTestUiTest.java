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

public class BookingApplicationAddAButtonToCreatePrimeVaccBookingTestUiTest extends TestBase {
    private static final int MAX_COUNTER_VALUE = 10;
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
            getLogger().error("setUp - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            getLogger().error("setUp - Exception - Reason : " + e.getLocalizedMessage(), e);
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

            addParticipantField();
            // After setting the participant id the rest should run step by
            // step.
            // We set the date of the date of the screening date if it is not
            // added before.
            setDateOfScreeningDate();
            // Set the female Child Bear age
            setFemaleChildBearingAge();

            // We click this option to make sure we can select dates for the
            // prime.vaccination
            setEarliestLatestDate();
            bookingAppPrimeVaccinationPage.isEnabledSaveButton();
            bookingAppPrimeVaccinationPage.saveCreatedPrimeVaccination();
            bookingAppPrimeVaccinationPage.confirmAddVisitBookingDetailsAndPrintCard();
            assertTrue(bookingAppPrimeVaccinationPage.closePdfIfIsOpen());

        } catch (AssertionError e) {
            getLogger().error(
                    "bookingApplicationCapacityInfoTest - AssertionError - Reason : " + e.getLocalizedMessage(), e);

        } catch (NullPointerException e) {
            getLogger().error(
                    "bookingApplicationCapacityInfoTest - NullPointerException - Reason : " + e.getLocalizedMessage(),
                    e);

        } catch (Exception e) {
            getLogger().error("bookingApplicationCapacityInfoTest - Exception - Reason : " + e.getLocalizedMessage(),
                    e);
        }

    }

    public void addParticipantField() throws InterruptedException {
        // bookingAppPrimeVaccinationPage.sleepForMilisec(SLEEP_5000);
        // We Have to wait for a while to have the participant list.
        int counter = 10; // We make sure we do not generate an infinite
                          // loop
        while (!bookingAppPrimeVaccinationPage.isPartincipantIdEnabled() && counter > 0) {
            bookingAppPrimeVaccinationPage.clickFirstParticipantId();
            counter--;
        }
    }

    public void setEarliestLatestDate() throws InterruptedException {
        int counter;
        counter = MAX_COUNTER_VALUE; // reset counter;
        // Check the ignore earliest date.
        boolean clickOnIngoreLatesEarliestDate = bookingAppPrimeVaccinationPage.clickOnIngoreLatesEarliestDate();

        while (!bookingAppPrimeVaccinationPage.isEnabledSaveButton() && counter > 0) {
            // After checking the check-box to ignore the EarliestDate we
            // select
            // the dates.
            if (clickOnIngoreLatesEarliestDate) {
                bookingAppPrimeVaccinationPage.setDateOfPrimeVacDateFields();
                bookingAppPrimeVaccinationPage.setTimeOfPrimeVacDateFields();

            } else {
                clickOnIngoreLatesEarliestDate = bookingAppPrimeVaccinationPage.clickOnIngoreLatesEarliestDate();
            }
            // We make sure that the Save Button is enabled.
            if (bookingAppPrimeVaccinationPage.isEnabledSaveButton()) {
                counter = -1;
            } else {
                counter--;
            }
        }

        if (clickOnIngoreLatesEarliestDate) {
            bookingAppPrimeVaccinationPage.setDateOfPrimeVacDateFields();
            bookingAppPrimeVaccinationPage.setTimeOfPrimeVacDateFields();

        }
    }

    public void setDateOfScreeningDate() throws InterruptedException {
        while (bookingAppPrimeVaccinationPage.isPrimeVacDateEmpty()) {
            bookingAppPrimeVaccinationPage.setDateOfScreeningDate();
        }
    }

    public void setFemaleChildBearingAge() throws InterruptedException {
        if (bookingAppPrimeVaccinationPage.isFemailChildBearingAgeEmpty()) {
            bookingAppPrimeVaccinationPage.setFemaleChildBearingAge();
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }

}
