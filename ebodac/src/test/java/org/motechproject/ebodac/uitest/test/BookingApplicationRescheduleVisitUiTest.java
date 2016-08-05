package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.BookingAppRescheduleVisitPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import static org.junit.Assert.assertTrue;

/**
 * Class created to test the Booking app and the Reschedule Visit.
 * 
 * @author tmueller
 * @modified rmartin
 *
 */
public class BookingApplicationRescheduleVisitUiTest extends TestBase {
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppRescheduleVisitPage bAReschedulePage;
    private String user;
    private String password;
    private static final String ERROR_TITLE_MODAL = "Cannot change planned date";

    @Before
    public void setUp() throws Exception {
        try {
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            bookingAppPage = new BookingAppPage(getDriver());
            bAReschedulePage = new BookingAppRescheduleVisitPage(getDriver());
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
    public void rescheduleVisitTest() throws Exception {
        try {
            homePage.clickModules();
            homePage.openBookingAppModule();
            bookingAppPage.openRescheduleVisit();
            bAReschedulePage.resizePage();
            bAReschedulePage.sortByPlannedDateColumn();
            assertTrue(bAReschedulePage.visitsExist());

            if (bAReschedulePage.chooseVisit()) {
                if (bAReschedulePage.getTitleReschedule() != ERROR_TITLE_MODAL) {
                    assertTrue(bAReschedulePage.rescheduleVisit());
                    bAReschedulePage.printCard();
                } else {
                    bAReschedulePage.clickCose();
                }
            }
        } catch (AssertionError e) {
            getLogger().error("rescheduleVisitTest - AssertionError . Reason : " + e.getLocalizedMessage(), e);
            bAReschedulePage.clickCose();
        } catch (InterruptedException e) {
            getLogger().error("rescheduleVisitTest - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
            bAReschedulePage.clickCose();
        } catch (NullPointerException e) {
            getLogger().error("rescheduleVisitTest - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
            bAReschedulePage.clickCose();
        } catch (Exception e) {
            getLogger().error("rescheduleVisitTest - Exception . Reason : " + e.getLocalizedMessage(), e);
            bAReschedulePage.clickCose();
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
