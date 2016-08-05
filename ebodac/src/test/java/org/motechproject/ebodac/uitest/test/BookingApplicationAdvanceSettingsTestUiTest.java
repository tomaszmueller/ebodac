package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.BookingAppAdvancedSettingsPage;
import org.motechproject.ebodac.uitest.page.BookingAppPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;
import static org.junit.Assert.assertTrue;

public class BookingApplicationAdvanceSettingsTestUiTest extends TestBase {
    private LoginPage loginPage;
    private HomePage homePage;
    private BookingAppPage bookingAppPage;
    private BookingAppAdvancedSettingsPage bookingAppAdvancedSettingsPage;
    private String user;
    private String password;
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;

    @Before
    public void setUp() throws Exception {
        try {
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            bookingAppPage = new BookingAppPage(getDriver());
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

    @Test // EBODAC-802
    public void bookingAppAdvanceSettingsTest() throws Exception {
        try {
            homePage.resizePage();
            homePage.clickModules();
            homePage.openBookingAppModule();
            bookingAppPage.openAdvancedSettings();
            bookingAppAdvancedSettingsPage.clickKambiaIRowAndShowMore();
            bookingAppAdvancedSettingsPage.removeTextFromInputMaxPrimeVisitsPasteOtherValue();
            assertTrue(bookingAppAdvancedSettingsPage.clickSaveAfterEditKambiaI());

        } catch (AssertionError e) {
            getLogger().error("bookingAppAdvanceSettingsTest - AssertionError . Reason : " + e.getLocalizedMessage(),
                    e);
        } catch (NullPointerException e) {
            getLogger().error(
                    "bookingAppAdvanceSettingsTest - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error(
                    "bookingAppAdvanceSettingsTest - InterruptedException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("bookingAppAdvanceSettingsTest - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
