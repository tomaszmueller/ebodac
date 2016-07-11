package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppUnscheduledVisitPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;

import static org.junit.Assert.assertEquals;

public class BookingApplicationBookUnscheduledVisit extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppUnscheduledVisitPage bookingAppRescheduleVisitPage;
    private String user;
    private String password;
    public static final String  CONFIRM_MESSAGE = "Are you sure you want to book Unscheduled visit?";
    public static final String CORRECTLY_UPDATED_MESSAGE = "Visit Booking Details updated successfully.";

    @Before
    public void setUp() {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        bookingAppPage = new BookingAppPage(getDriver());
        bookingAppRescheduleVisitPage = new BookingAppUnscheduledVisitPage(getDriver());
        user = getTestProperties().getUserName();
        password = getTestProperties().getPassword();
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(user, password);
        }
    }

    @Test
    public void bookingApplicationRescheduleVisitTest() throws InterruptedException {
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
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
