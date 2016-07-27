package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.page.BookingAppAdvancedSettingsPage;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;

public class BookingApplicationAdvanceSettingsTestUiTest extends TestBase {
    // Object initialization for log
    private static Logger log = Logger.getLogger(BookingApplicationAdvanceSettingsTestUiTest.class.getName());
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppAdvancedSettingsPage bookingAppAdvancedSettingsPage;
    private String user;
    private String password;

    @Before
    public void setUp() {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        bookingAppPage = new BookingAppPage(getDriver());
        bookingAppAdvancedSettingsPage = new BookingAppAdvancedSettingsPage(getDriver());
        user = getTestProperties().getUserName();
        password = getTestProperties().getPassword();
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(user, password);
        }
    }

    @Test // EBODAC-802
    public void bookingAppAdvanceSettingsTest() throws InterruptedException {
        try {
            homePage.resizePage();
            homePage.clickModules();
            homePage.openBookingAppModule();
            bookingAppPage.openAdvancedSettings();
            bookingAppAdvancedSettingsPage.clickKambiaIRowAndShowMore();
            bookingAppAdvancedSettingsPage.removeTextFromInputMaxPrimeVisitsPasteOtherValue();
            assertTrue(bookingAppAdvancedSettingsPage.clickSaveAfterEditKambiaI());

        } catch (AssertException e) {
            log.error("bookingAppAdvanceSettingsTest - AssertException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            log.error("bookingAppAdvanceSettingsTest - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
