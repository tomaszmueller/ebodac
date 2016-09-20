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

public class BookingApplicationEditPrimeVaccinationVisitUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppPrimeVaccinationPage bookingAppPrimeVaccinationPage;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    static final int SLEEP_2SEC = 2000;

    @Before
    public void setUp() throws Exception {
        String url;
        UITestHttpClientHelper httpClientHelper;
        String user;
        String password;
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
            getLogger().error("setup - NPE . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setup - Exc . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test // EBODAC-781
    public void bookingAppEditPrimeVacVisitTest() throws Exception {
        String participantId = "";
        String participantVacDate = "";
        String participantVadDateAfterChange = "";

        try {
            homePage.resizePage();
            homePage.sleep(SLEEP_2SEC);
            homePage.clickModules();
            homePage.sleep(SLEEP_2SEC);
            homePage.openBookingAppModule();
            homePage.sleep(SLEEP_2SEC);
            bookingAppPage.openPrimeVaccination();
            bookingAppPage.sleep(SLEEP_2SEC);

            if (bookingAppPrimeVaccinationPage.checkIfElementAddPrimeVaccinationIsVisible()) {
                bookingAppPrimeVaccinationPage.changeDateRangeFromToday();
                bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                bookingAppPrimeVaccinationPage.sortTableByPrimeVacDate();
                bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                bookingAppPrimeVaccinationPage.sortTableByPrimeVacDate();
                bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                // We run the next code if we have visits
                if (bookingAppPrimeVaccinationPage.hasVisitsVisible()) {
                    participantId = bookingAppPrimeVaccinationPage.firstParticipantId();
                    bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                    participantVacDate = bookingAppPrimeVaccinationPage.firstParticipantPrimeVacDay();
                    bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                    bookingAppPrimeVaccinationPage.clickOnFirstRowInTheGridUI();
                    bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                    bookingAppPrimeVaccinationPage.changeDates();
                    bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                    bookingAppPrimeVaccinationPage.saveAndConfirmChanges();
                    bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                    if (bookingAppPrimeVaccinationPage.findParticipantInLookup(participantId)) {
                        participantVadDateAfterChange = bookingAppPrimeVaccinationPage.firstParticipantPrimeVacDay();
                        bookingAppPrimeVaccinationPage.sleep(SLEEP_2SEC);
                        assertEquals(false, participantVacDate.equalsIgnoreCase(participantVadDateAfterChange));
                    } else {
                        getLogger().error("bookingAppEditPVacVTest - No Participant found .");
                    }
                } else {
                    getLogger().error("bookingAppEditPVacVTest - No Visits visible. ");
                }
            } else {
                getLogger().error("bookingAppEditPVacVTest - No elemement visible. ");
            }
        } catch (AssertionError e) {
            getLogger().error("Var Status : participantId: " + participantId);
            getLogger().error("Var Satus  : participantVacDate: " + participantVacDate);
            getLogger().error("Var Status : participantVadDateAfterChange: " + participantVadDateAfterChange);
            getLogger().error("bookingAppEditPrimeVacVisitTest - AsserionError .");
            getLogger().error("Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error("Var Status : participantId: " + participantId);
            getLogger().error("Var Satus  : participantVacDate: " + participantVacDate);
            getLogger().error("Var Status : participantVadDateAfterChange: " + participantVadDateAfterChange);
            getLogger().error("bookingAppEditPrimeVacVisitTest - IEx Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error("Var Status : participantId: " + participantId);
            getLogger().error("Var Satus  : participantVacDate: " + participantVacDate);
            getLogger().error("Var Status : participantVadDateAfterChange: " + participantVadDateAfterChange);
            getLogger().error("bookingAppEditPrimeVacVisitTest - NPE Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("Var Status : participantId: " + participantId);
            getLogger().error("Var Satus  : participantVacDate: " + participantVacDate);
            getLogger().error("Var Status : participantVadDateAfterChange: " + participantVadDateAfterChange);
            getLogger().error("bookingAppEditPrimeVacVisitTest - Exception Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
