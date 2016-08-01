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

import com.mchange.util.AssertException;

import org.motechproject.uitest.TestBase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.apache.log4j.Logger;

public class PrimeFollowAndBoostReportUiTest extends TestBase {
    private static final CharSequence TEST_LOCAL_MACHINE = "localhost";
    // Object initialization for log
    private static Logger log = Logger.getLogger(PrimeFollowAndBoostReportUiTest.class.getName());
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
            log.error("setUp - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            log.error("setUp - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test
    public void primeFollowAndBoostReportTest() throws InterruptedException {
        try {
            homePage.openEBODACModule();
            ebodacPage.gotoReports();
            reportPage.showPrimeFollowAndBoostReport();
            assertFalse(primeFollowAndBoostReportPage.isReportEmpty());
            assertTrue(primeFollowAndBoostReportPage.isLookupVisible());
            primeFollowAndBoostReportPage.openLookup();
            primeFollowAndBoostReportPage.openDropdown();
            assertTrue(primeFollowAndBoostReportPage.areLookupsPresent());
            primeFollowAndBoostReportPage.openByVisittypeAndActualVisitDateLookup();
            assertTrue(primeFollowAndBoostReportPage.islookupOpen());
            primeFollowAndBoostReportPage.openVisitType();
            assertTrue(primeFollowAndBoostReportPage.areAllVisitsAvailable());

        } catch (AssertException e) {
            log.error("primeFollowAndBoostReportTest - AssertException - Reason : " + e.getLocalizedMessage(), e);

        } catch (NullPointerException e) {
            log.error("primeFollowAndBoostReportTest - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            log.error("primeFollowAndBoostReportTest - Exception - Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
