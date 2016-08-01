package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppUnscheduledVisitPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

import static org.junit.Assert.assertEquals;
import org.apache.log4j.Logger;

public class BookingApplicationBookUnscheduledVisit extends TestBase {
    // Object initialization for log
    private static Logger log = Logger.getLogger(BookingApplicationBookUnscheduledVisit.class.getName());
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppUnscheduledVisitPage bookingAppRescheduleVisitPage;
    private String user;
    private String password;
    public static final String CONFIRM_MESSAGE = "Are you sure you want to book Unscheduled visit?";
    public static final String CORRECTLY_UPDATED_MESSAGE = "Visit Booking Details updated successfully.";
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;

    @Before
    public void setUp() {
        try {
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            bookingAppPage = new BookingAppPage(getDriver());
            bookingAppRescheduleVisitPage = new BookingAppUnscheduledVisitPage(getDriver());
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
            homePage.resizePage();
            homePage.openBookingAppModule();
            bookingAppPage.openUnscheduledVisit();
            bookingAppRescheduleVisitPage.clickOnBookUnscheduledVisitButton();
            bookingAppRescheduleVisitPage.clickOnParticipantIdDropDownAndChooseFirstParticipant();
            bookingAppRescheduleVisitPage.setDatesForUnscheduledVisit();
            bookingAppRescheduleVisitPage.clickOnButtonToSaveUnscheduledVisit();
            assertEquals(CONFIRM_MESSAGE, bookingAppRescheduleVisitPage.checkIfConfirmModalIsVisible());
            bookingAppRescheduleVisitPage.clickOnButtonToConfirmBookUnscheduledVisit();
            assertEquals(CORRECTLY_UPDATED_MESSAGE, bookingAppRescheduleVisitPage.checkifVisitIsCorrectlySaved());
            bookingAppRescheduleVisitPage.clickOnButtonToCloseModal();
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
