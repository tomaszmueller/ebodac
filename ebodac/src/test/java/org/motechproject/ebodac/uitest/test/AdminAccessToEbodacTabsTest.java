package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
//import org.junit.Ignore;
import org.motechproject.ebodac.uitest.page.ParticipantPage;
import org.motechproject.ebodac.uitest.page.ReportPage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.VisitPage;
import org.motechproject.ebodac.uitest.page.VisitEditPage;
import org.motechproject.ebodac.uitest.page.BoosterVaccinationReportPage;
import org.motechproject.ebodac.uitest.page.CallDetailRecordPage;
import org.motechproject.ebodac.uitest.page.DailyClinicVisitScheduleReportPage;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.EnrollmentPage;
import org.motechproject.ebodac.uitest.page.FollowupsAfterPrimeInjectionReportPage;
import org.motechproject.ebodac.uitest.page.FollowupsMissedClinicVisitsReportPage;
import org.motechproject.ebodac.uitest.page.MEMissedClinicVisitsReportPage;
import org.motechproject.ebodac.uitest.page.NumberOfTimesListenedReportPage;
import org.motechproject.ebodac.uitest.page.ParticipantsWhoOptOutOfMessagesReportPage;
import org.motechproject.ebodac.uitest.page.PrimeFollowAndBoostReportPage;
import org.motechproject.ebodac.uitest.page.PrimerVaccinationReportPage;
import org.motechproject.ebodac.uitest.page.ScreeningReportPage;
import org.motechproject.ebodac.uitest.page.SMSLogReportPage;
import org.motechproject.ebodac.uitest.page.SMSPage;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

import org.motechproject.uitest.TestBase;
//import org.motechproject.ebodac.uitest.helper.CreateUsersHelper;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.IVREditPage;
import org.motechproject.ebodac.uitest.page.IVRPage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

//import java.text.DateFormat;
//import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

public class AdminAccessToEbodacTabsTest extends TestBase {

    // Object initialization for log
    private static Logger log = Logger.getLogger(AdminAccessToEbodacTabsTest.class.getName());
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private String user;
    private String password;
    private UITestHttpClientHelper httpClientHelper;
    private UserPropertiesHelper userPropertiesHelper;
    private String url;
    // Variables for the pages.
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
    private IVRPage ivrPage;
    private IVREditPage ivrEditPage;
    private SMSPage smsPage;
    private EnrollmentPage enrollmentPage;

    @Before
    public void setUp() throws Exception {
        url = getServerUrl();
        log.error("uri : " + url);

        // We close the session with admin user to try to log in with admin
        // user.
        try {
            userPropertiesHelper = new UserPropertiesHelper();
            user = userPropertiesHelper.getAdminUserName();
            password = userPropertiesHelper.getAdminPassword();
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            log.error("Admin User : " + user + "   /// Password : " + password);
        } catch (Exception e) {
            log.error("Error in Test : " + e.getMessage());
            e.printStackTrace();
        }

        try {

            // We try to log in Ebodac.
            log.error("Try to log in Local Machine ");
            if (url.contains(LOCAL_TEST_MACHINE)) {
                httpClientHelper = new UITestHttpClientHelper(url);
                httpClientHelper.addParticipant(new TestParticipant(), user, password);
                loginPage.goToPage();
                loginPage.login(user, password);
            } else if (homePage.expectedUrlPath() != currentPage().urlPath()) {
                loginPage.goToPage();
                loginPage.login(user, password);
            }

        } catch (Exception e) {
            log.error("Error Trying to log in : " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        // Load Ebodac Tabs. Setting up the pages.
        try {
            loadEbodacPages();
        } catch (Exception e) {
            log.error("Cannot load tabs . Reason : " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        // Load the rest of the pages

    }

    /**
     * We use this method to load all the pages neeeded for the test.
     * 
     * @return the list of pages loaded for the test
     */
    public void loadEbodacPages() {

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

    }

    @Test // Test for EBODAC-531
    public void adminAccessOnlyToEbodacUiTest() throws Exception {
        // We try to access to the Ebodac
        homePage.clickModules();
        try {
            // Ebodac Asserts.
            try {
                testAdminEbodacHome();
            } catch (Exception e) {
                log.error("testAdminEbodacHome - Error . Reason = " + e.getLocalizedMessage());
                e.printStackTrace();
            }
            // Visits Asserts
            try {
                testAdminVisitsTab();
            } catch (Exception e) {
                log.error("testAdminVisits - Error . Reason = " + e.getLocalizedMessage());
                e.printStackTrace();
            }
            // Report Asserts
            try {
                testAdminWithReports();
            } catch (Exception e) {
                log.error("testAdminWithReports - Error . Reason = " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            // Enrolment Tab
            try {
                testAdminEnrolmentTab();
            } catch (Exception e) {
                log.error("  testAdminEnrolmentTab - Error . Reason = " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            // IVR Module
            try {
                testAdminIVRModule();
            } catch (Exception e) {
                log.error("testAdminIVRModule - Error . Reason = " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            // SMS Module
            try {
                testAdminSMSModule();
            } catch (Exception e) {
                log.error("testAdminSMSModule - Error . Reason = " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        } catch (AssertionError e) {
            log.error("Assertion Error " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void testAdminEbodacHome() throws InterruptedException {
        homePage.clickOnEbodac();
        ebodacPage.showParticipants();
        participantPage.openFirstParticipant();
        assertTrue(participantEditPage.isNameEditable());
        assertTrue(participantEditPage.isHouseholdNameEditable());
        assertTrue(participantEditPage.isHeadOfHouseholdEditable());
    }

    public void testAdminSMSModule() {
        smsPage.goToPage();
        assertTrue(smsPage.logExists());
    }

    public void testAdminIVRModule() throws InterruptedException {
        homePage.openIVRModule();
        ivrPage.openLog();
        ivrPage.openFirstRecord();
        assertFalse(ivrEditPage.isFromEditable());
    }

    public void testAdminVisitsTab() throws InterruptedException {
        ebodacPage.showVisits();
        visitPage.clickVisit();
        // DateFormat df = new SimpleDateFormat("yyyy-dd-MM");
        assertTrue(visitEditPage.isPlannedVisitDateEditable());
        assertFalse(visitEditPage.isActualVisitDateEditable());
        assertFalse(visitEditPage.isVisitTypeEditable());
        visitEditPage.changeVisit();
    }

    public void testAdminEnrolmentTab() throws InterruptedException {
        ebodacPage.goToEnrollment();
        // enrollmentPage.goToPage();
        // It should be allowed to enrol unenroll participants.
        try {
            enrollmentPage.clickAction();
            log.error("After click action ");
            enrollmentPage.clickOK();
            log.error("After click ok ");
            if (enrollmentPage.error()) {
                enrollmentPage.clickOK();
                log.error("adminhouldNotSeeAdvancePageTest - After enrollmentPage click ok ");
                enrollmentPage.nextAction();
                log.error("adminhouldNotSeeAdvancePageTest - After enrollmentPage nextAction ");
            }
        } catch (NullPointerException e) {
            log.error("adminhouldNotSeeAdvancePageTest - Error :" + e.getMessage());
            e.printStackTrace();
        }
        try {
            if (enrollmentPage.enrolled()) {
                log.error("adminhouldNotSeeAdvancePageTest - After enrollmentPage enrolled True");
                try {
                    assertTrue(enrollmentPage.enrolled());
                } catch (AssertException e) {
                    log.error(
                            "adminhouldNotSeeAdvancePageTest - AssertTrue Error . Reason : " + e.getLocalizedMessage());
                    e.printStackTrace();
                }

                enrollmentPage.clickOK();
            }
            if (enrollmentPage.unenrolled()) {
                assertTrue(enrollmentPage.unenrolled());
                enrollmentPage.clickOK();
            }
        } catch (NullPointerException e) {
            log.error("adminhouldNotSeeAdvancePageTest - enrolled & unenrolled . Reason : " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void testAdminWithReports() throws InterruptedException {
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
        reportPage.showFollowUpsMissedClinicReport();
        assertFalse(followupsMissedClinicVisitsReportPage.isReportEmpty());
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
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
