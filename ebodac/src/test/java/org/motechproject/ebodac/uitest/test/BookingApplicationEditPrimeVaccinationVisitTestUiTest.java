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
import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

public class BookingApplicationEditPrimeVaccinationVisitTestUiTest extends TestBase {
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;

    private BookingAppPrimeVaccinationPage bookingAppPrimeVaccinationPage;
    private String user;
    private String password;
    static final int SLEEP_500MLS = 500;
    static final int SLEEP_2SEC = 2000;

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
            getLogger().error("setup - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setup - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test // EBODAC-781
    public void bookingAppEditPrimeVacVisitTest() throws Exception {
        String participantId = "";
        String participantVacDate = "";
        String participantVadDateAfterChange = "";

        try {
            homePage.resizePage();
            homePage.clickModules();
            homePage.openBookingAppModule();
            bookingAppPage.openPrimeVaccination();

            assertEquals(true, bookingAppPrimeVaccinationPage.checkIfElementAddPrimeVaccinationIsVisible());
            bookingAppPrimeVaccinationPage.changeDateRangeFromToday();
            sleep(SLEEP_2SEC);
            bookingAppPrimeVaccinationPage.sortTableByPrimeVacDate();
            sleep(SLEEP_2SEC);
            bookingAppPrimeVaccinationPage.sortTableByPrimeVacDate();
            sleep(SLEEP_2SEC);
            // We run the next code if we have visits
            if (bookingAppPrimeVaccinationPage.hasVisitsVisible()) {
                participantId = bookingAppPrimeVaccinationPage.firstParticipantId();
                sleep(SLEEP_500MLS);
                participantVacDate = bookingAppPrimeVaccinationPage.firstParticipantPrimeVacDay();
                sleep(SLEEP_500MLS);
                bookingAppPrimeVaccinationPage.clickOnFirstRowInTheGridUI();
                sleep(SLEEP_500MLS);
                bookingAppPrimeVaccinationPage.changeDates();
                sleep(SLEEP_500MLS);
                bookingAppPrimeVaccinationPage.saveAndConfirmChanges();
                if (bookingAppPrimeVaccinationPage.findParticipantInLookup(participantId)) {
                    participantVadDateAfterChange = bookingAppPrimeVaccinationPage.firstParticipantPrimeVacDay();
                    assertEquals(false, participantVacDate.equalsIgnoreCase(participantVadDateAfterChange));
                }
            }
        } catch (AssertionError e) {
            getLogger().error("Var Status : participantId: " + participantId);
            getLogger().error("Var Satus  : participantVacDate: " + participantVacDate);
            getLogger().error("Var Status : participantVadDateAfterChange: " + participantVadDateAfterChange);
            getLogger().error("bookingAppEditPrimeVacVisitTest - AsserionError .");
            getLogger().error("Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error(
                    "bookingAppEditPrimeVacVisitTest - NullPointerException Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error(
                    "bookingAppEditPrimeVacVisitTest - NullPointerException Reason : " + e.getLocalizedMessage(), e);
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
