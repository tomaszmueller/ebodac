package org.motechproject.ebodac.uitest.test;

import org.junit.*;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
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
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        bookingAppPage = new BookingAppPage(driver);
        bookingAppCapacityInfoPage = new BookingAppCapacityInfoPage(driver);
        bookingAppAdvancedSettingsPage = new BookingAppAdvancedSettingsPage(driver);
        user = properties.getUserName();
        password = properties.getPassword();
        if(homePage.expectedUrlPath() != currentPage().urlPath()) {
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
        Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), ""+(3*maxCapacity));
        bookingAppCapacityInfoPage.filterNext7Days();
        Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), ""+(7*maxCapacity));
        bookingAppCapacityInfoPage.filterDateRange();
        Assert.assertEquals(bookingAppCapacityInfoPage.getMaxCapacity(), ""+(28*maxCapacity));

    }

    @After
    public void tearDown() throws Exception {
        loginPage.logOut();
    }
}
