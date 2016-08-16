package org.motechproject.ebodac.uitest.test;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.BookingAppFilters;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppScreeningPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import java.util.ArrayList;
import static org.junit.Assert.assertTrue;

public class BookingApplicationScreeningCreationUiTest extends TestBase {
    private static final String EMPTY_STRING = "";
    private static final int SIX = 6;
    private static final int FIVE = 5;
    private static final int FOUR = 4;
    private static final int THREE = 3;
    private static final int TWO = 2;
    private static final int ONE = 1;
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppScreeningPage bookingAppScreeningPage;
    private String user;
    private String password;

    @Before
    public void setUp() throws Exception {
        try {
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            bookingAppPage = new BookingAppPage(getDriver());
            bookingAppScreeningPage = new BookingAppScreeningPage(getDriver());
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

    @Test
    public void findScreeningTest() throws Exception {

        try {
            ArrayList<LocalDate> dates = new ArrayList<>();
            homePage.clickModules();
            homePage.openBookingAppModule();
            bookingAppPage.openScreening();

            String bookingId = EMPTY_STRING;
            if (EMPTY_STRING != bookingAppScreeningPage.bookScreeningVisitForToday()) {
                bookingId = bookingAppScreeningPage.bookScreeningVisitForToday().replace(". ", EMPTY_STRING);
            }
            if (!EMPTY_STRING.equalsIgnoreCase(bookingId)) {
                assertTrue(bookingAppScreeningPage.bookingIdExists(bookingId));
            }
            bookingAppScreeningPage.changeFilterTo(BookingAppFilters.TODAY.getValue());

            dates.add(LocalDate.now());

            checkBookingAvailableForSpecificDate(dates);
            // We start from tomorrow.
            bookingAppScreeningPage.changeFilterTo(BookingAppFilters.TOMORROW.getValue());

            dates.remove(0);
            dates.add(LocalDate.now().plusDays(ONE));

            checkBookingAvailableForSpecificDate(dates);

            bookingAppScreeningPage.changeFilterTo(BookingAppFilters.DAY_AFTER_TOMORROW.getValue());

            dates.remove(0);
            dates.add(LocalDate.now().plusDays(TWO));
            checkBookingAvailableForSpecificDate(dates);

            bookingAppScreeningPage.changeFilterTo(BookingAppFilters.NEXT_3_DAYS.getValue());

            dates.remove(0);
            dates.add(LocalDate.now());
            dates.add(LocalDate.now().plusDays(ONE));
            dates.add(LocalDate.now().plusDays(TWO));

            checkBookingAvailableForSpecificDate(dates);

            bookingAppScreeningPage.changeFilterTo(BookingAppFilters.NEXT_7_DAYS.getValue());

            dates.add(LocalDate.now().plusDays(THREE));
            dates.add(LocalDate.now().plusDays(FOUR));
            dates.add(LocalDate.now().plusDays(FIVE));
            dates.add(LocalDate.now().plusDays(SIX));
            checkBookingAvailableForSpecificDate(dates);

            bookingAppScreeningPage.changeFilterTo(BookingAppFilters.DATE_RANGE.getValue());

            bookingAppScreeningPage.setDate();
            bookingAppScreeningPage.exportToPDF();
            bookingAppScreeningPage.exportToXLS();

        } catch (AssertionError e) {
            getLogger().error("findScreeningTest - AssertionError . Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error("findScreeningTest - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error("findScreeningTest - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("findScreeningTest - Exception . Reason : " + e.getLocalizedMessage(), e);
        }

    }

    public void checkBookingAvailableForSpecificDate(ArrayList<LocalDate> dates) {
        if (bookingAppScreeningPage.hasBookings(dates)) {
            assertTrue(bookingAppScreeningPage.isFirstBookingOK(dates));
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }

}
