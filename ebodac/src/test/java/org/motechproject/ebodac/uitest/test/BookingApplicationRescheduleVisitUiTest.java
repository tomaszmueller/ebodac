package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.page.*;

import static junit.framework.Assert.assertTrue;


public class BookingApplicationRescheduleVisitUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppRescheduleVisitPage bookingAppRescheduleVisitPage;
    private String user;
    private String password;

    @Before
    public void setUp() {
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        bookingAppPage = new BookingAppPage(driver);
        bookingAppRescheduleVisitPage = new BookingAppRescheduleVisitPage(driver);
        user = properties.getUserName();
        password = properties.getPassword();
        if(homePage.expectedUrlPath() != currentPage().urlPath()) {
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
        loginPage.logOut();
    }
}
