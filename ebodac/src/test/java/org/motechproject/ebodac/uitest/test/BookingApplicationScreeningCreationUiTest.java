package org.motechproject.ebodac.uitest.test;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppScreeningPage;
import org.motechproject.ebodac.uitest.page.HomePage;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class BookingApplicationScreeningCreationUiTest extends TestBase {
    private static final int SIX = 6;
    private static final int FIVE = 5;
    private static final int FOUR = 4;
    private static final int THREE = 3;
    private static final int TWO = 2;
    private static final int ONE = 1;
    private static final int ZERO = 0;
    private static final String DOT = ". ";
    private static final String EMPTY = "";
    private static final String NEXT_7_DAYS = "Next 7 days";
    private static final String NEXT_3_DAYS = "Next 3 days";
    private static final String DAY_AFTER_TOMORROW = "Day after tomorrow";
    private static final String TOMORROW = "Tomorrow";
    private static final String TODAY = "Today";
    private static final String DATE_RANGE = "Date range";
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
            getLogger().error("setup - NPE . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setup - Exc . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test
    public void bAScreeningVisitCreationTest() throws Exception {
        String bookingId = EMPTY;
        try {
            ArrayList<LocalDate> dates = new ArrayList<>();
            homePage.clickModules();
            homePage.openBookingAppModule();
            bookingAppPage.openScreening();
            bookingAppScreeningPage.changeFilterTo(DATE_RANGE);
            bookingId = bookingAppScreeningPage.bookScreeningVisit().replace(DOT, EMPTY);

            if (EMPTY != bookingId) {
                assertTrue(bookingAppScreeningPage.bookingIdExists(bookingId));
            }

            bookingAppScreeningPage.changeFilterTo(TODAY);
            dates.add(LocalDate.now());
            assertVisitTest(dates);

            bookingAppScreeningPage.changeFilterTo(TOMORROW);
            dates.remove(ZERO);
            dates.add(LocalDate.now().plusDays(ONE));
            assertVisitTest(dates);

            bookingAppScreeningPage.changeFilterTo(DAY_AFTER_TOMORROW);
            dates.remove(ZERO);
            dates.add(LocalDate.now().plusDays(TWO));
            assertVisitTest(dates);

            bookingAppScreeningPage.changeFilterTo(NEXT_3_DAYS);
            dates.remove(ZERO);
            dates.add(LocalDate.now());
            dates.add(LocalDate.now().plusDays(ONE));
            dates.add(LocalDate.now().plusDays(TWO));
            assertVisitTest(dates);

            bookingAppScreeningPage.changeFilterTo(NEXT_7_DAYS);
            dates.add(LocalDate.now().plusDays(THREE));
            dates.add(LocalDate.now().plusDays(FOUR));
            dates.add(LocalDate.now().plusDays(FIVE));
            dates.add(LocalDate.now().plusDays(SIX));
            assertVisitTest(dates);

            bookingAppScreeningPage.changeFilterTo(DATE_RANGE);
            bookingAppScreeningPage.setDate();
            bookingAppScreeningPage.exportToPDF();
            bookingAppScreeningPage.exportToXLS();

        } catch (AssertException e) {
            getLogger().error("bAScreeningVisitCreationTest - AEX . Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error("bAScreeningVisitCreationTest - IEX . Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error("bAScreeningVisitCreationTest - NPE . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("bAScreeningVisitCreationTest - Exc . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    public void assertVisitTest(ArrayList<LocalDate> dates) {
        if (bookingAppScreeningPage.hasVisits()) {
            assertTrue(bookingAppScreeningPage.isFirstBookingOK(dates));
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
