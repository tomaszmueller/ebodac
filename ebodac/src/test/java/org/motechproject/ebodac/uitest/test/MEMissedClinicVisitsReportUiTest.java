package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
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
        user = properties.getUserName();
        password = properties.getPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        ebodacPage = new EBODACPage(driver);
        reportPage = new ReportPage(driver);
        mEMissedClinicVisitsReportPage = new MEMissedClinicVisitsReportPage(driver);
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
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
        loginPage.logOut();
    }
}
