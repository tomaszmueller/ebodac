package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.FollowupsMissedClinicVisitsReportPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ReportPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FollowedUpsMissedClinicVisitsReportUiTest extends TestBase {
    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ReportPage reportPage;
    private FollowupsMissedClinicVisitsReportPage followupsMissedClinicVisitsReportPage;
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private static final long SLEEP_2SEC = 2000;
    private UITestHttpClientHelper httpClientHelper;
    private String user;
    private String password;

    @Before
    public void setUp() throws Exception {

        try {
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            ebodacPage = new EBODACPage(getDriver());
            reportPage = new ReportPage(getDriver());
            followupsMissedClinicVisitsReportPage = new FollowupsMissedClinicVisitsReportPage(getDriver());
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
            getLogger().error("setup - NPE . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setup - Exc . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test // EBODAC-807
    public void followupsMissedClinicVisitsReportTest() throws Exception {
//        try {
            homePage.openEBODACModule();
            homePage.resizePage();
            homePage.sleep(SLEEP_2SEC);
            ebodacPage.gotoReports();
            ebodacPage.sleep(SLEEP_2SEC);
            reportPage.showFollowUpsMissedClinicReport();
            reportPage.sleep(SLEEP_2SEC);
            assertTrue(followupsMissedClinicVisitsReportPage.existTable());
            assertFalse(followupsMissedClinicVisitsReportPage.isReportEmpty());
            assertTrue(followupsMissedClinicVisitsReportPage.checkColumns());
            reportPage.sleep(SLEEP_2SEC);
//        } catch (AssertionError e) {
//            getLogger().error("boosterVaccinationReportTest - AEr . Reason : " + e.getLocalizedMessage(), e);
//        } catch (InterruptedException e) {
//            getLogger().error("boosterVaccinationReportTest - IEx . Reason : " + e.getLocalizedMessage(), e);
//        } catch (NullPointerException e) {
//            getLogger().error("boosterVaccinationReportTest - NPE . Reason : " + e.getLocalizedMessage(), e);
//        } catch (Exception e) {
//            getLogger().error("boosterVaccinationReportTest - Exc . Reason : " + e.getLocalizedMessage(), e);
//        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
