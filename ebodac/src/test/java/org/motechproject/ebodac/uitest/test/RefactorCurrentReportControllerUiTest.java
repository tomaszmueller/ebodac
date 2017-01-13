package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.DailyClinicVisitScheduleReportPage;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ReportPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;


public class RefactorCurrentReportControllerUiTest extends TestBase {
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private static final long SLEEP_2SEC = 2000;
    private static final long SLEEP_4SEC = 4000;
    private static final long SLEEP_6SEC = 6000;
    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ReportPage reportPage;
    private DailyClinicVisitScheduleReportPage dailyClinicVisitScheduleReportPage;
    private String user;
    private String password;
    private UITestHttpClientHelper httpClientHelper;
    private UserPropertiesHelper userPropertiesHelper;
    private String url;

    @Before
    public void setUp() throws Exception {
        try {
            url = getServerUrl();
            // We log in the EBODAC
            userPropertiesHelper = new UserPropertiesHelper();
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            loginPage = new LoginPage(getDriver());
            // We load homepage and the rest of pages.
            homePage = new HomePage(getDriver());
            // The ebodac page and the rest pages we load.
            ebodacPage = new EBODACPage(getDriver());
            reportPage = new ReportPage(getDriver());
            dailyClinicVisitScheduleReportPage = new DailyClinicVisitScheduleReportPage(getDriver());
            // We try to log in Ebodac.
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
            getLogger().error("setUp - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            getLogger().error("setUp - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void refactorCurrentReportControllerTest() throws Exception {
        homePage.openEBODACModule();
        homePage.resizePage();
        ebodacPage.gotoReports();
        reportPage.showDailyClinicVisitReportSchedule();
        dailyClinicVisitScheduleReportPage.openLookup();
        dailyClinicVisitScheduleReportPage.findByParticipantName("John");
        dailyClinicVisitScheduleReportPage.openLookup();
        dailyClinicVisitScheduleReportPage.findByType("Screening");
        dailyClinicVisitScheduleReportPage.openLookup();
        dailyClinicVisitScheduleReportPage.findByParticipantId("111");
        dailyClinicVisitScheduleReportPage.openLookup();
        dailyClinicVisitScheduleReportPage.findByParticipantAddress("CIRCLE");
        dailyClinicVisitScheduleReportPage.openLookup();
        dailyClinicVisitScheduleReportPage.findByPlannedVisitDateRange("2015-07-28", "");
        dailyClinicVisitScheduleReportPage.openLookup();
        dailyClinicVisitScheduleReportPage.findByPlannedVisitDateRangeAndType("2015-07-28", "", "Prime Vaccination Day");


    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
