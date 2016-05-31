package org.motechproject.ebodac.uitest.test;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppScreeningPage;
import org.motechproject.ebodac.uitest.page.HomePage;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class BookingApplicationScreeningCreationUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppScreeningPage bookingAppScreeningPage;
    private String user;
    private String password;

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
            loginPage.login(user , password);
        }
    }

    @Test
    public void bookingApplicationScreeningCreationTest() throws InterruptedException {
        ArrayList<LocalDate> dates = new ArrayList<>();
        homePage.clickModules();
        homePage.openBookingAppModule();
        bookingAppPage.openScreening();
        bookingAppScreeningPage.changeFilterTo("Date range");
        String bookingId = bookingAppScreeningPage.bookScreeningVisit().replace(". ", "");
        assertTrue(bookingAppScreeningPage.bookingIdExists(bookingId));
        bookingAppScreeningPage.changeFilterTo("Today");
        dates.add(LocalDate.now());
        assertTrue(bookingAppScreeningPage.isFirstBookingOK(dates));
        bookingAppScreeningPage.changeFilterTo("Tomorrow");
        dates.remove(0);
        dates.add(LocalDate.now().plusDays(1));
        assertTrue(bookingAppScreeningPage.isFirstBookingOK(dates));
        bookingAppScreeningPage.changeFilterTo("Day after tomorrow");
        dates.remove(0);
        dates.add(LocalDate.now().plusDays(2));
        assertTrue(bookingAppScreeningPage.isFirstBookingOK(dates));
        bookingAppScreeningPage.changeFilterTo("Next 3 days");
        dates.remove(0);
        dates.add(LocalDate.now());
        dates.add(LocalDate.now().plusDays(1));
        dates.add(LocalDate.now().plusDays(2));
        assertTrue(bookingAppScreeningPage.isFirstBookingOK(dates));
        bookingAppScreeningPage.changeFilterTo("Next 7 days");
        dates.add(LocalDate.now().plusDays(3));
        dates.add(LocalDate.now().plusDays(4));
        dates.add(LocalDate.now().plusDays(5));
        dates.add(LocalDate.now().plusDays(6));
        assertTrue(bookingAppScreeningPage.isFirstBookingOK(dates));
        bookingAppScreeningPage.changeFilterTo("Date range");
        bookingAppScreeningPage.setDate();
        bookingAppScreeningPage.exportToPDF();
        bookingAppScreeningPage.exportToXLS();
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
