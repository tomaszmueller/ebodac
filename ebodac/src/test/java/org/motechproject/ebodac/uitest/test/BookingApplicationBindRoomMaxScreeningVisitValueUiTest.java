package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppScreeningPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;

public class BookingApplicationBindRoomMaxScreeningVisitValueUiTest extends TestBase {
    // Object initialization for log
    private static Logger log = Logger
            .getLogger(BookingApplicationBindRoomMaxScreeningVisitValueUiTest.class.getName());
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppScreeningPage bookingAppScreeningPage;
    private String user;
    private String password;
    static final int START_LOOP = 0;
    static final int END_LOOP = 5;

    @Before
    public void setUp() {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        bookingAppPage = new BookingAppPage(getDriver());
        bookingAppScreeningPage = new BookingAppScreeningPage(getDriver());
        user = getTestProperties().getUserName();
        password = getTestProperties().getPassword();
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(user, password);
        }
    }

    @Test // EBODAC-718
    public void bindRoomMaxScreeningVisitValue() throws InterruptedException {
        try {
            homePage.resizePage();
            homePage.clickModules();
            homePage.openBookingAppModule();
            bookingAppPage.openScreening();
            for (int i = START_LOOP; i < END_LOOP; i++) {
                bookingAppScreeningPage.bookVisitForScreening();
                bookingAppScreeningPage.confirmBookVistiForScreening();
                bookingAppScreeningPage.clickOnButtonToAddAnotherScreening();
                bookingAppScreeningPage.confirmBookVistiForScreening();
                assertTrue(bookingAppScreeningPage.clickOnButtonToCloseScheduleScreening());
            }

        } catch (AssertException e) {
            log.error("bindRoomMaxScreeningVisitValue - AssertException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            log.error("bindRoomMaxScreeningVisitValue - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
