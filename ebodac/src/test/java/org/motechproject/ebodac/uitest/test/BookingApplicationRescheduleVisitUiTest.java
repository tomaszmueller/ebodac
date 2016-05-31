package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppRescheduleVisitPage;
import org.motechproject.ebodac.uitest.page.HomePage;

import static org.junit.Assert.assertTrue;

public class BookingApplicationRescheduleVisitUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppRescheduleVisitPage bookingAppRescheduleVisitPage;
    private String user;
    private String password;

    @Before
    public void setUp() {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        bookingAppPage = new BookingAppPage(getDriver());
        bookingAppRescheduleVisitPage = new BookingAppRescheduleVisitPage(getDriver());
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
        homePage.openBookingAppModule();
        bookingAppPage.openRescheduleVisit();
        bookingAppRescheduleVisitPage.resizePage();
        bookingAppRescheduleVisitPage.sortByPlannedDateColumn();
        assertTrue(bookingAppRescheduleVisitPage.visitsExist());
        bookingAppRescheduleVisitPage.chooseVisit();
        assertTrue(bookingAppRescheduleVisitPage.rescheduleVisit());
        bookingAppRescheduleVisitPage.printCard();
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
