package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.page.BookingAppAdvancedSettingsPage;
import org.motechproject.ebodac.uitest.page.BookingAppCapacityInfoPage;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.HomePage;

public class BookingApplicationCapacityInfoUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppCapacityInfoPage bookingAppCapacityInfoPage;
    private BookingAppAdvancedSettingsPage bookingAppAdvancedSettingsPage;
    private String user;
    private String password;

    @Before
    public void setUp() {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        bookingAppPage = new BookingAppPage(getDriver());
        bookingAppCapacityInfoPage = new BookingAppCapacityInfoPage(getDriver());
        bookingAppAdvancedSettingsPage = new BookingAppAdvancedSettingsPage(getDriver());
        user = getTestProperties().getUserName();
        password = getTestProperties().getPassword();
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(user, password);
        }
    }

    @Test
    public void bookingApplicationCapacityInfoTest() throws InterruptedException {
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

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
