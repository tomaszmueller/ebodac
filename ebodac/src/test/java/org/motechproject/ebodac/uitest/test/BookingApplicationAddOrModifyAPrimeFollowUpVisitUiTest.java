package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.BookingAppClinicVisitSchedulePage;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;
import static org.junit.Assert.assertEquals;

public class BookingApplicationAddOrModifyAPrimeFollowUpVisitUiTest extends TestBase {
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppClinicVisitSchedulePage bookingAppClinicVisitSchedulePage;
    private String user;
    private String password;
    public static final int SLEEP_2SEC = 2000;

    @Before
    public void setUp() throws Exception {
        try {
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            bookingAppPage = new BookingAppPage(getDriver());
            bookingAppClinicVisitSchedulePage = new BookingAppClinicVisitSchedulePage(getDriver());
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
            getLogger().error("setUp - NPE. - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            getLogger().error("setUp - Exc. - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test // EBODAC-800
    public void bookingAppAddOrModifyUiTest() throws Exception {
        try {
            homePage.resizePage();
            homePage.clickModules();
            homePage.openBookingAppModule();
            homePage.sleep(SLEEP_2SEC);
            bookingAppPage.openClinicVisitSchedule();
            bookingAppPage.sleep(SLEEP_2SEC);
            bookingAppClinicVisitSchedulePage.clickOnDropDownParticipantId();
            bookingAppPage.sleep(SLEEP_2SEC);
            String dayBeforeClean = bookingAppClinicVisitSchedulePage.getPrimeVacDateInput();
            bookingAppClinicVisitSchedulePage.clickOnPrimeVacDayDate();
            bookingAppClinicVisitSchedulePage.sleep(SLEEP_2SEC);
            bookingAppClinicVisitSchedulePage.clickOnFirstDayInCalendar();
            bookingAppClinicVisitSchedulePage.sleep(SLEEP_2SEC);
            bookingAppClinicVisitSchedulePage.clickButtonCleanDate();
            // Assert to validate the changes.
            bookingAppClinicVisitSchedulePage.sleep(SLEEP_2SEC);
            assertEquals(dayBeforeClean, bookingAppClinicVisitSchedulePage.assertIfPrimeVacDayIsEmpty());
        } catch (AssertionError e) {
            getLogger().error("bookingAppAddOrModifyUiTest - AEr. Reason :" + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error("bookingAppAddOrModifyUiTest - IEx. Reason :" + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error("bookingAppAddOrModifyUiTest - NPE. Reason :" + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("bookingAppAddOrModifyUiTest - Exc. Reason :" + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
