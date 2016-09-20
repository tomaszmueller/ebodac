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
import static org.junit.Assert.assertTrue;

public class BookingApplicationUiCreationOfTheTabClinicVisitScheduletUiTest extends TestBase {
    private static final int SLEEP_2SEC = 2000;
    private static final long SLEEP_4SEC = 4000;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppClinicVisitSchedulePage bookingAppClinicVisitSchedulePage;
    private String user;
    private String password;

    @Before
    public void setUp() throws Exception {
        try {
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            bookingAppPage = new BookingAppPage(getDriver());
            bookingAppClinicVisitSchedulePage = new BookingAppClinicVisitSchedulePage(getDriver());
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            String url = getServerUrl();
            if (url.contains(LOCAL_TEST_MACHINE)) {
                UITestHttpClientHelper httpClientHelper = new UITestHttpClientHelper(url);
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

    @Test // EBODAC-710
    public void modifyAPrimeFollowUpVisitTest() throws Exception {
        try {
            homePage.resizePage();
            homePage.clickModules();
            homePage.sleep(SLEEP_2SEC);
            homePage.openBookingAppModule();
            bookingAppPage.sleep(SLEEP_2SEC);
            bookingAppPage.openClinicVisitSchedule();
            bookingAppPage.sleep(SLEEP_4SEC);

            if (bookingAppClinicVisitSchedulePage.findParticipantWithoutPrimeVacDay()) {
                bookingAppClinicVisitSchedulePage.sleep(SLEEP_2SEC);
                bookingAppClinicVisitSchedulePage.clickOnPrimeVacDayDate();
                bookingAppClinicVisitSchedulePage.sleep(SLEEP_2SEC);
                bookingAppClinicVisitSchedulePage.clickOnFirstDayInCalendar();
                bookingAppClinicVisitSchedulePage.sleep(SLEEP_2SEC);
                // We assert that this step works
                assertTrue(bookingAppClinicVisitSchedulePage.clickOnButtonToPrint());
            } else {
                getLogger().error("modifyAPrimeFollowUpVisitTest - No participant found ");
            }
        } catch (AssertionError e) {
            getLogger().error("modifyAPrimeFollowUpVisitTest - AEr - Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error("modifyAPrimeFollowUpVisitTest - NPE - Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error("modifyAPrimeFollowUpVisitTest - IEx - Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("modifyAPrimeFollowUpVisitTest - Exc - Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
