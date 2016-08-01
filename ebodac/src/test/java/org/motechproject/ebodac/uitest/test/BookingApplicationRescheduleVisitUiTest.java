package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppRescheduleVisitPage;
import org.motechproject.ebodac.uitest.page.HomePage;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;

public class BookingApplicationRescheduleVisitUiTest extends TestBase {
    // Object initialization for log
    private static Logger log = Logger.getLogger(BookingApplicationRescheduleVisitUiTest.class.getName());
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppRescheduleVisitPage bookingAppRescheduleVisitPage;
    private String user;
    private String password;

    @Before
    public void setUp() throws Exception {
        try {
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            bookingAppPage = new BookingAppPage(getDriver());
            bookingAppRescheduleVisitPage = new BookingAppRescheduleVisitPage(getDriver());
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

    @Test
    public void bookingApplicationRescheduleVisitTest() throws Exception {
        try {
            homePage.clickModules();
            homePage.openBookingAppModule();
            bookingAppPage.openRescheduleVisit();
            bookingAppRescheduleVisitPage.resizePage();
            bookingAppRescheduleVisitPage.sortByPlannedDateColumn();
            assertTrue(bookingAppRescheduleVisitPage.visitsExist());
            bookingAppRescheduleVisitPage.chooseVisit();
            assertTrue(bookingAppRescheduleVisitPage.rescheduleVisit());
            bookingAppRescheduleVisitPage.printCard();
        } catch (AssertException e) {
            log.error("bookingApplicationRescheduleVisitTest - AssertException . Reason : " + e.getLocalizedMessage(),
                    e);
        } catch (InterruptedException e) {
            log.error("bookingApplicationRescheduleVisitTest - NullPointerException . Reason : "
                    + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            log.error("bookingApplicationRescheduleVisitTest - NullPointerException . Reason : "
                    + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            log.error("bookingApplicationRescheduleVisitTest - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
