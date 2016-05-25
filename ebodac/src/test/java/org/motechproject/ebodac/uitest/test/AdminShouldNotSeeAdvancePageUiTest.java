package org.motechproject.ebodac.uitest.test;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.CreateUsersHelper;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;
import org.motechproject.ebodac.uitest.page.ReportPage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.VisitPage;
import org.motechproject.ebodac.uitest.page.VisitEditPage;
import org.motechproject.ebodac.uitest.page.BoosterVaccinationReportPage;
import org.motechproject.ebodac.uitest.page.CallDetailRecordPage;
import org.motechproject.ebodac.uitest.page.DailyClinicVisitScheduleReportPage;
import org.motechproject.ebodac.uitest.page.FollowupsAfterPrimeInjectionReportPage;
import org.motechproject.ebodac.uitest.page.FollowupsMissedClinicVisitsReportPage;
import org.motechproject.ebodac.uitest.page.MEMissedClinicVisitsReportPage;
import org.motechproject.ebodac.uitest.page.NumberOfTimesListenedReportPage;
import org.motechproject.ebodac.uitest.page.ParticipantsWhoOptOutOfMessagesReportPage;
import org.motechproject.ebodac.uitest.page.PrimeFollowAndBoostReportPage;
import org.motechproject.ebodac.uitest.page.PrimerVaccinationReportPage;
import org.motechproject.ebodac.uitest.page.ScreeningReportPage;
import org.motechproject.ebodac.uitest.page.SMSLogReportPage;
import org.motechproject.ebodac.uitest.page.EnrollmentPage;
import org.motechproject.ebodac.uitest.page.IVRPage;
import org.motechproject.ebodac.uitest.page.IVREditPage;
import org.motechproject.ebodac.uitest.page.SMSPage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AdminShouldNotSeeAdvancePageUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ReportPage reportPage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private VisitPage visitPage;
    private VisitEditPage visitEditPage;
    private BoosterVaccinationReportPage boosterVaccinationReportPage;
    private CallDetailRecordPage callDetailRecordPage;
    private DailyClinicVisitScheduleReportPage dailyClinicVisitScheduleReportPage;
    private FollowupsAfterPrimeInjectionReportPage followupsAfterPrimeInjectionReportPage;
    private FollowupsMissedClinicVisitsReportPage followupsMissedClinicVisitsReportPage;
    private MEMissedClinicVisitsReportPage meMissedClinicVisitsReportPage;
    private NumberOfTimesListenedReportPage numberOfTimesListenedReportPage;
    private ParticipantsWhoOptOutOfMessagesReportPage participantsWhoOptOutOfMessagesReportPage;
    private PrimeFollowAndBoostReportPage primeFollowAndBoostReportPage;
    private PrimerVaccinationReportPage primerVaccinationReportPage;
    private ScreeningReportPage screeningReportPage;
    private SMSLogReportPage smsLogReportPage;
    private EnrollmentPage enrollmentPage;
    private IVRPage ivrPage;
    private IVREditPage ivrEditPage;
    private SMSPage smsPage;
    private String l1adminUser;
    private String l1adminPassword;
    private UITestHttpClientHelper httpClientHelper;
    private UserPropertiesHelper userPropertiesHelper;
    private CreateUsersHelper createUsersHelper;
    private String url;

    @Before
    public void setUp() throws Exception {
        url = getServerUrl();
        if (url.contains("localhost")) {
            createUsersHelper = new CreateUsersHelper(getDriver());
            createUsersHelper.createUsersWithLogin(getTestProperties());
            logout();
        }
        userPropertiesHelper = new UserPropertiesHelper();
        l1adminUser = userPropertiesHelper.getAdminUserName();
        l1adminPassword = userPropertiesHelper.getAdminPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        ebodacPage = new EBODACPage(getDriver());
        reportPage = new ReportPage(getDriver());
        enrollmentPage = new EnrollmentPage(getDriver());
        boosterVaccinationReportPage = new BoosterVaccinationReportPage(getDriver());
        callDetailRecordPage = new CallDetailRecordPage(getDriver());
        dailyClinicVisitScheduleReportPage = new DailyClinicVisitScheduleReportPage(getDriver());
        followupsAfterPrimeInjectionReportPage = new FollowupsAfterPrimeInjectionReportPage(getDriver());
        followupsMissedClinicVisitsReportPage = new FollowupsMissedClinicVisitsReportPage(getDriver());
        meMissedClinicVisitsReportPage = new MEMissedClinicVisitsReportPage(getDriver());
        numberOfTimesListenedReportPage = new NumberOfTimesListenedReportPage(getDriver());
        primeFollowAndBoostReportPage = new PrimeFollowAndBoostReportPage(getDriver());
        participantsWhoOptOutOfMessagesReportPage = new ParticipantsWhoOptOutOfMessagesReportPage(getDriver());
        primerVaccinationReportPage = new PrimerVaccinationReportPage(getDriver());
        screeningReportPage = new ScreeningReportPage(getDriver());
        smsLogReportPage = new SMSLogReportPage(getDriver());
        participantPage = new ParticipantPage(getDriver());
        participantEditPage = new ParticipantEditPage(getDriver());
        smsPage = new SMSPage(getDriver());
        ivrPage = new IVRPage(getDriver());
        ivrEditPage = new IVREditPage(getDriver());
        visitPage = new VisitPage(getDriver());
        visitEditPage = new VisitEditPage(getDriver());
        if (url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant(), l1adminUser, l1adminPassword);
            loginPage.goToPage();
            loginPage.login(l1adminUser, l1adminPassword);
        }
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(l1adminUser, l1adminPassword);
        }
    }


    @Test
    public void adminhouldNotSeeAdvancePageTest() throws Exception {
        homePage.clickModules();
        homePage.openEBODACModule();
        ebodacPage.gotoReports();
        reportPage.showPrimeVaccinationReport();
        assertFalse(primerVaccinationReportPage.isReportEmpty());
        ebodacPage.gotoReports();
        reportPage.showBoostVaccinationReport();
        assertFalse(boosterVaccinationReportPage.isReportEmpty());
        ebodacPage.gotoReports();
        reportPage.showDailyClinicVisitReportSchedule();
        assertFalse(dailyClinicVisitScheduleReportPage.isReportEmpty());
        ebodacPage.gotoReports();
        reportPage.showFollowUpsAfterPrimeInjectionReport();
        assertFalse(followupsAfterPrimeInjectionReportPage.isReportEmpty());
        ebodacPage.gotoReports();
        reportPage.showMEMissedClinicVisitsReport();
        assertFalse(meMissedClinicVisitsReportPage.isReportEmpty());
        ebodacPage.gotoReports();
        reportPage.showParticipantsWhoOptOutOfMessages();
        assertFalse(participantsWhoOptOutOfMessagesReportPage.isReportEmpty());
        ebodacPage.gotoReports();
        reportPage.showNumberOfTimesReport();
        assertFalse(numberOfTimesListenedReportPage.isReportEmpty());
        ebodacPage.gotoReports();
        reportPage.showScreeningReport();
        assertFalse(screeningReportPage.isReportEmpty());
        ebodacPage.gotoReports();
        reportPage.showCallDetailRecord();
        assertFalse(callDetailRecordPage.isReportEmpty());
        ebodacPage.gotoReports();
        reportPage.showPrimeFollowAndBoostReport();
        assertFalse(primeFollowAndBoostReportPage.isReportEmpty());
        ebodacPage.gotoReports();
        reportPage.showSMSLog();
        assertFalse(smsLogReportPage.isReportEmpty());
        ebodacPage.showParticipants();
        participantPage.openFirstParticipant();
        assertTrue(participantEditPage.isNameEditable());
        assertTrue(participantEditPage.isHouseholdNameEditable());
        assertTrue(participantEditPage.isHeadOfHouseholdEditable());
        ebodacPage.showVisits();
        visitPage.clickVisit();
        DateFormat df = new SimpleDateFormat("yyyy-dd-MM");
        assertTrue(visitEditPage.isPlannedVisitDateEditable());
        assertFalse(visitEditPage.isActualVisitDateEditable());
        assertFalse(visitEditPage.isVisitTypeEditable());
        visitEditPage.changeVisit();
        ebodacPage.goToEnrollment();
        assertFalse(enrollmentPage.enrollmentDetailEnabled());
        enrollmentPage.clickAction();
        enrollmentPage.clickOK();
        if (enrollmentPage.error()) {
            enrollmentPage.clickOK();
            enrollmentPage.nextAction();
        }

        if (enrollmentPage.enrolled()) {
            assertTrue(enrollmentPage.enrolled());
            enrollmentPage.clickOK();
        }
        if (enrollmentPage.unenrolled()) {
            assertTrue(enrollmentPage.unenrolled());
            enrollmentPage.clickOK();
        }
        homePage.openIVRModule();
        ivrPage.openLog();
        ivrPage.openFirstRecord();
        assertFalse(ivrEditPage.isFromEditable());
        assertTrue(smsPage.logExists());
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
