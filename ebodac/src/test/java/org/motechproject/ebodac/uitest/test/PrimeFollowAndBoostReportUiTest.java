package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ReportPage;
import org.motechproject.ebodac.uitest.page.PrimeFollowAndBoostReportPage;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class PrimeFollowAndBoostReportUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ReportPage reportPage;
    private PrimeFollowAndBoostReportPage primeFollowAndBoostReportPage;
    private String user;
    private String password;

    @Before
    public void setUp() {
        user = getTestProperties().getUserName();
        password = getTestProperties().getPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        ebodacPage = new EBODACPage(getDriver());
        reportPage = new ReportPage(getDriver());
        primeFollowAndBoostReportPage = new PrimeFollowAndBoostReportPage(getDriver());
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(user, password);
        }
    }

    @Test
    public void primeFollowAndBoostReportTest() throws InterruptedException {
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
        
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
