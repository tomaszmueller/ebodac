package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ReportPage;
import org.motechproject.ebodac.uitest.page.PrimeFollowAndBoostReportPage;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PrimeFollowAndBoostReportUiTest extends TestBase {
    private static final int SLEEP_2SEC = 2000;
    private static final CharSequence TEST_LOCAL_MACHINE = "localhost";
    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ReportPage reportPage;
    private PrimeFollowAndBoostReportPage primeFollowAndBoostReportPage;
    private String user;
    private String password;
    private UITestHttpClientHelper httpClientHelper;

    @Before
    public void setUp() throws Exception {
        try {
            String url = getServerUrl();
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            ebodacPage = new EBODACPage(getDriver());
            reportPage = new ReportPage(getDriver());
            primeFollowAndBoostReportPage = new PrimeFollowAndBoostReportPage(getDriver());
            if (url.contains(TEST_LOCAL_MACHINE)) {
                httpClientHelper = new UITestHttpClientHelper(url);
                httpClientHelper.addParticipant(new TestParticipant(), user, password);
            }
            if (homePage.expectedUrlPath() != currentPage().urlPath()) {
                loginPage.goToPage();
                loginPage.login(user, password);
            } else if (homePage.expectedUrlPath() != currentPage().urlPath()) {
                loginPage.goToPage();
                loginPage.login(user, password);
            }
        } catch (NullPointerException e) {
            getLogger().error("setUp - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            getLogger().error("setUp - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test
    public void primeFollowAndBoostReportTest() throws Exception {
        try {
            homePage.openEBODACModule();

            ebodacPage.gotoReports();
            reportPage.showPrimeFollowAndBoostReport();
            primeFollowAndBoostReportPage.sleep(SLEEP_2SEC);
            assertFalse(primeFollowAndBoostReportPage.isReportEmpty());
            primeFollowAndBoostReportPage.sleep(SLEEP_2SEC);
            assertTrue(primeFollowAndBoostReportPage.isLookupVisible());
            primeFollowAndBoostReportPage.sleep(SLEEP_2SEC);
            primeFollowAndBoostReportPage.openLookup();
            primeFollowAndBoostReportPage.sleep(SLEEP_2SEC);
            primeFollowAndBoostReportPage.openDropdown();
            primeFollowAndBoostReportPage.sleep(SLEEP_2SEC);
            assertTrue(primeFollowAndBoostReportPage.areLookupsPresent());
            primeFollowAndBoostReportPage.sleep(SLEEP_2SEC);
            primeFollowAndBoostReportPage.openByVisittypeAndActualVisitDateLookup();
            primeFollowAndBoostReportPage.sleep(SLEEP_2SEC);
            assertTrue(primeFollowAndBoostReportPage.islookupOpen());
            primeFollowAndBoostReportPage.sleep(SLEEP_2SEC);
            primeFollowAndBoostReportPage.openVisitType();
            assertTrue(primeFollowAndBoostReportPage.areAllVisitsAvailable());

        } catch (AssertionError e) {
            getLogger().error("primeFollowAndBoostReportTest - AssertionError - Reason : " + e.getLocalizedMessage(),
                    e);

        } catch (InterruptedException e) {
            getLogger().error(
                    "primeFollowAndBoostReportTest - InterruptedException - Reason : " + e.getLocalizedMessage(), e);

        } catch (NullPointerException e) {
            getLogger().error(
                    "primeFollowAndBoostReportTest - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            getLogger().error("primeFollowAndBoostReportTest - Exception - Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
