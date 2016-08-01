package org.motechproject.ebodac.uitest.test;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.BookingAppAdvancedSettingsPage;
import org.motechproject.ebodac.uitest.page.BookingAppCapacityInfoPage;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.HomePage;

public class BookingApplicationCapacityInfoUiTest extends TestBase {
    // Object initialization for log
    private static Logger log = Logger.getLogger(BookingApplicationCapacityInfoUiTest.class.getName());
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;

    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppCapacityInfoPage bookingAppCapacityInfoPage;
    private BookingAppAdvancedSettingsPage bookingAppAdvancedSettingsPage;
    private String user;
    private String password;

    @Before
    public void setUp() throws Exception {
        try {
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            bookingAppPage = new BookingAppPage(getDriver());
            bookingAppCapacityInfoPage = new BookingAppCapacityInfoPage(getDriver());
            bookingAppAdvancedSettingsPage = new BookingAppAdvancedSettingsPage(getDriver());
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
    public void bookingApplicationCapacityInfoTest() throws Exception {
        try {
            homePage.clickModules();
            homePage.openBookingAppModule();
            bookingAppPage.openAdvancedSettings();
            int maxCapacity = Integer.parseInt(bookingAppAdvancedSettingsPage.getMaxCapacity());
            bookingAppPage.openCapacityInfo();
            bookingAppCapacityInfoPage.filterToday();
            Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), "" + maxCapacity);
            bookingAppCapacityInfoPage.filterTomorrow();
            Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), "" + maxCapacity);
            bookingAppCapacityInfoPage.filterDayAfterTomorrow();
            Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), "" + maxCapacity);
            bookingAppCapacityInfoPage.filterNext3Days();
            Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), "" + (3 * maxCapacity));
            bookingAppCapacityInfoPage.filterNext7Days();
            Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), "" + (7 * maxCapacity));
            bookingAppCapacityInfoPage.filterDateRange();
            Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), "" + (28 * maxCapacity));
        } catch (AssertException e) {
            log.error("bookingApplicationCapacityInfoTest - AssertException . Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            log.error("bookingApplicationCapacityInfoTest - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            log.error("bookingApplicationCapacityInfoTest - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            log.error("bookingApplicationCapacityInfoTest - Exception . Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
