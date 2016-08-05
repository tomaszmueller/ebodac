package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.page.MEMissedClinicVisitsReportPage;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ReportPage;
import static org.junit.Assert.assertTrue;

/**
 * Class created to test Missed Clinic Visit Report.
 * 
 * @author tmueller
 * @modified rmartin
 *
 */
public class MEMissedClinicVisitsReportUiTest extends TestBase {
    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ReportPage reportPage;
    private MEMissedClinicVisitsReportPage mEMissedClinicVisitsReportPage;
    private String user;
    private String password;
    private static final String TEST_LOCAL_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;
    private String url;

    @Before
    public void setUp() throws Exception {
        try {
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            ebodacPage = new EBODACPage(getDriver());
            reportPage = new ReportPage(getDriver());
            mEMissedClinicVisitsReportPage = new MEMissedClinicVisitsReportPage(getDriver());
            url = getServerUrl();
            if (url.contains(TEST_LOCAL_MACHINE)) {
                httpClientHelper = new UITestHttpClientHelper(url);
                httpClientHelper.addParticipant(new TestParticipant(), user, password);
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
    public void mEMissedClinicVisitsReportTest() throws Exception {
        try {
            homePage.openEBODACModule();
            ebodacPage.gotoReports();
            reportPage.showMEMissedClinicVisitsReport();
            assertTrue(mEMissedClinicVisitsReportPage.existTable());
        } catch (AssertionError e) {
            getLogger().error("mEMissedClinicVisitsReportTest - AssertionError - Reason : " + e.getLocalizedMessage(),
                    e);

        } catch (NullPointerException e) {
            getLogger().error(
                    "mEMissedClinicVisitsReportTest - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (InterruptedException e) {
            getLogger().error(
                    "mEMissedClinicVisitsReportTest - InterruptedException - Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("mEMissedClinicVisitsReportTest - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
