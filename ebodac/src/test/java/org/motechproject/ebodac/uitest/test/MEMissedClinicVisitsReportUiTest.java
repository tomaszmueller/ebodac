package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.page.MEMissedClinicVisitsReportPage;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ReportPage;

import static org.junit.Assert.assertFalse;


public class MEMissedClinicVisitsReportUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ReportPage reportPage;
    private MEMissedClinicVisitsReportPage mEMissedClinicVisitsReportPage;
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
        mEMissedClinicVisitsReportPage = new MEMissedClinicVisitsReportPage(getDriver());
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(user, password);
        }
    }

    @Test
    public void mEMissedClinicVisitsReportTest() throws InterruptedException {
        homePage.openEBODACModule();
        ebodacPage.gotoReports();
        reportPage.showMEMissedClinicVisitsReport();
        assertFalse(mEMissedClinicVisitsReportPage.isReportEmpty());
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
