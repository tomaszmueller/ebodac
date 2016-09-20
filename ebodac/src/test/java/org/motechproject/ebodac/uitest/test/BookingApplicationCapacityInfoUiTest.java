package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.BookingAppAdvancedSettingsPage;
import org.motechproject.ebodac.uitest.page.BookingAppCapacityInfoPage;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.HomePage;

public class BookingApplicationCapacityInfoUiTest extends TestBase {
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private static final long SLEEP_2SEC = 2000;
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
            getLogger().error("setup - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setup - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test
    public void bookingApplicationCapacityInfoTest() throws Exception {
        try {
            homePage.clickModules();
            homePage.sleep(SLEEP_2SEC);
            homePage.openBookingAppModule();
            homePage.sleep(SLEEP_2SEC);
            homePage.resizePage();
            bookingAppPage.sleep(SLEEP_2SEC);
            bookingAppPage.openAdvancedSettings();
            int maxCapacity = Integer.parseInt(bookingAppAdvancedSettingsPage.getMaxCapacity());
            bookingAppPage.openCapacityInfo();
            bookingAppCapacityInfoPage.filterToday();
            bookingAppCapacityInfoPage.sleep(SLEEP_2SEC);
            Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), "" + maxCapacity);
            bookingAppCapacityInfoPage.sleep(SLEEP_2SEC);
            bookingAppCapacityInfoPage.filterTomorrow();
            bookingAppCapacityInfoPage.sleep(SLEEP_2SEC);
            Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), "" + maxCapacity);
            bookingAppCapacityInfoPage.sleep(SLEEP_2SEC);
            bookingAppCapacityInfoPage.filterDayAfterTomorrow();
            bookingAppCapacityInfoPage.sleep(SLEEP_2SEC);
            Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), "" + maxCapacity);
            bookingAppCapacityInfoPage.sleep(SLEEP_2SEC);
            bookingAppCapacityInfoPage.filterNext3Days();
            bookingAppCapacityInfoPage.sleep(SLEEP_2SEC);
            Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), "" + (3 * maxCapacity));
            bookingAppCapacityInfoPage.sleep(SLEEP_2SEC);
            bookingAppCapacityInfoPage.filterNext7Days();
            bookingAppCapacityInfoPage.sleep(SLEEP_2SEC);
            Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), "" + (7 * maxCapacity));
            bookingAppCapacityInfoPage.sleep(SLEEP_2SEC);
            bookingAppCapacityInfoPage.filterDateRange();
            bookingAppCapacityInfoPage.sleep(SLEEP_2SEC);
            Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), "" + (28 * maxCapacity));
        } catch (AssertionError e) {
            getLogger().error("bookingAppTest - AEx. Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error("bookingAppTest - IEx. Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error("bookingAppTest - NPE. Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("bookingAppTest - Exc. Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
