package org.motechproject.ebodac.uitest.test;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
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
        url = properties.getWebAppUrl();
        if (url.contains("localhost")) {
            createUsersHelper = new CreateUsersHelper(driver);
            createUsersHelper.createUsersWithLogin(properties);
        }
        userPropertiesHelper = new UserPropertiesHelper();
        l1adminUser = userPropertiesHelper.getAdminUserName();
        l1adminPassword = userPropertiesHelper.getAdminPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        ebodacPage = new EBODACPage(driver);
        reportPage = new ReportPage(driver);
        enrollmentPage = new EnrollmentPage(driver);
        boosterVaccinationReportPage = new BoosterVaccinationReportPage(driver);
        callDetailRecordPage = new CallDetailRecordPage(driver);
        dailyClinicVisitScheduleReportPage = new DailyClinicVisitScheduleReportPage(driver);
        followupsAfterPrimeInjectionReportPage = new FollowupsAfterPrimeInjectionReportPage(driver);
        followupsMissedClinicVisitsReportPage = new FollowupsMissedClinicVisitsReportPage(driver);
        meMissedClinicVisitsReportPage = new MEMissedClinicVisitsReportPage(driver);
        numberOfTimesListenedReportPage = new NumberOfTimesListenedReportPage(driver);
        primeFollowAndBoostReportPage = new PrimeFollowAndBoostReportPage(driver);
        participantsWhoOptOutOfMessagesReportPage = new ParticipantsWhoOptOutOfMessagesReportPage(driver);
        primerVaccinationReportPage = new PrimerVaccinationReportPage(driver);
        screeningReportPage = new ScreeningReportPage(driver);
        smsLogReportPage = new SMSLogReportPage(driver);
        participantPage = new ParticipantPage(driver);
        participantEditPage = new ParticipantEditPage(driver);
        smsPage = new SMSPage(driver);
        ivrPage = new IVRPage(driver);
        ivrEditPage = new IVREditPage(driver);
        visitPage = new VisitPage(driver);
        visitEditPage = new VisitEditPage(driver);
        if (url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant(), l1adminUser, l1adminPassword);
        }
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
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
        loginPage.logOut();
    }
}
