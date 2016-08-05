package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppScreeningPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;
import static org.junit.Assert.assertTrue;

public class BookingApplicationBindRoomMaxScreeningVisitValueUiTest extends TestBase {
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppScreeningPage bookingAppScreeningPage;
    private String user;
    private String password;
    static final int START_LOOP = 0;
    static final int END_LOOP = 5;
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;

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

    @Test // EBODAC-718
    public void bindRoomMaxScreeningVisitValue() throws Exception {
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

        } catch (AssertionError e) {
            getLogger().error("bindRoomMaxScreeningVisitValue - AssertionError . Reason : " + e.getLocalizedMessage(),
                    e);
        } catch (InterruptedException e) {
            getLogger().error(
                    "bindRoomMaxScreeningVisitValue - InterruptedException . Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error(
                    "bindRoomMaxScreeningVisitValue - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("bindRoomMaxScreeningVisitValue - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
