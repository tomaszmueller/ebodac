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
import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
//import org.apache.log4j.Logger;

public class BookingApplicationEditPrimeVaccinationVisitTestUiTest extends TestBase {
    // private static Logger log =
    // Logger.getLogger(BookingApplicationEditPrimeVaccinationVisitTestUiTest.class.getName());
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;

    private BookingAppPrimeVaccinationPage bookingAppPrimeVaccinationPage;
    private String user;
    private String password;
    static final int SLEEP_500 = 500;
    static final int SLEEP_2000 = 2000;

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
    public void bookingApplicationEditPrimeVaccinationVisitTest() throws Exception {
        try {
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
        } catch (AssertException e) {
            getLogger().error("bookingApplicationEditPrimeVaccinationVisitTest - AssertException . Reason : "
                    + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error("bookingApplicationEditPrimeVaccinationVisitTest - NullPointerException . Reason : "
                    + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error("bookingApplicationEditPrimeVaccinationVisitTest - NullPointerException . Reason : "
                    + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error(
                    "bookingApplicationEditPrimeVaccinationVisitTest - Exception . Reason : " + e.getLocalizedMessage(),
                    e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
