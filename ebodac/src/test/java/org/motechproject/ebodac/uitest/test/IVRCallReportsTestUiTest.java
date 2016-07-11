package org.motechproject.ebodac.uitest.test;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.DailyClinicVisitScheduleReportPage;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;
import org.motechproject.ebodac.uitest.page.ReportPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;

import static org.junit.Assert.assertEquals;


public class IVRCallReportsTestUiTest extends TestBase {
    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ReportPage reportPage;
    private DailyClinicVisitScheduleReportPage dailyClinicVisitScheduleReportPage;
    private String user;
    private String password;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private UserPropertiesHelper userPropertiesHelper;

    @Before
    public void setUp() {
        user = getTestProperties().getUserName();
        password = getTestProperties().getPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        participantPage = new ParticipantPage(getDriver());
        participantEditPage = new ParticipantEditPage(getDriver());
        if (!StringUtils.equals(homePage.expectedUrlPath(), currentPage().urlPath())) {
            loginPage.goToPage();
            loginPage.login(user , password);
        }
    }

    @Test//EBODAC-811
    public void iVRCallReportsTestUiTest() throws InterruptedException {
        homePage.resizePage();
        ebodacPage = homePage.openEBODACModule();
        reportPage = ebodacPage.gotoReports();
        reportPage.showCallDetailRecord();
        reportPage.checkIfTableOfCallDetailRecordInstancesIsVisible();
        assertEquals(true, reportPage.checkIfTableOfCallDetailRecordInstancesIsVisible());
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
