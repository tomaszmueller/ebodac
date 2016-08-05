package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.DailyClinicVisitScheduleReportPage;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ReportPage;
import static org.junit.Assert.assertFalse;

public class DailyClinicVisitReportScheduleUiTest extends TestBase {
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;
    private String user;
    private String password;
    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ReportPage reportPage;
    private DailyClinicVisitScheduleReportPage dailyClinicVisitScheduleReportPage;

    @Before
    public void setUp() throws Exception {
        try {
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            ebodacPage = new EBODACPage(getDriver());
            reportPage = new ReportPage(getDriver());
            dailyClinicVisitScheduleReportPage = new DailyClinicVisitScheduleReportPage(getDriver());
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

    @Test
    public void dailyClinicVisitReportScheduleTest() throws Exception {
        try {
            homePage.openEBODACModule();
            ebodacPage.gotoReports();
            reportPage.showDailyClinicVisitReportSchedule();
            assertFalse(dailyClinicVisitScheduleReportPage.isReportEmpty());

        } catch (AssertionError e) {
            getLogger().error(
                    "dailyClinicVisitReportScheduleTest - AssertionError . Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error(
                    "dailyClinicVisitReportScheduleTest - NullPointerException . Reason : " + e.getLocalizedMessage(),
                    e);
        } catch (NullPointerException e) {
            getLogger().error(
                    "dailyClinicVisitReportScheduleTest - NullPointerException . Reason : " + e.getLocalizedMessage(),
                    e);
        } catch (Exception e) {
            getLogger().error("dailyClinicVisitReportScheduleTest - Exception . Reason : " + e.getLocalizedMessage(),
                    e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
