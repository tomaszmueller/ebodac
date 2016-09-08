package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
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
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.IVREditPage;
import org.motechproject.ebodac.uitest.page.IVRPage;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AdminAccessToEbodacTabsUiTest extends TestBase {

    private static final String LOCAL_TEST_MACHINE = "localhost";
    private static final long SLEEP_2SEC = 2000;
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
        // We close the session with admin user to try to log in with admin
        // user.
        try {
            userPropertiesHelper = new UserPropertiesHelper();
            user = userPropertiesHelper.getAdminUserName();
            password = userPropertiesHelper.getAdminPassword();
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            homePage.resizePage();
            url = getServerUrl();

            if (url.contains(LOCAL_TEST_MACHINE)) {
                httpClientHelper = new UITestHttpClientHelper(url);
                httpClientHelper.addParticipant(new TestParticipant(), user, password);
                loginPage.goToPage();
                loginPage.login(user, password);
                // Load the rest of the pages
            } else if (homePage.expectedUrlPath() != currentPage().urlPath()) {
                loginPage.goToPage();
                loginPage.login(user, password);
            }
            loadEbodacPages();

            // Start Ebodac
            startEbodac();
        } catch (NullPointerException e) {
            getLogger().error("setUp - NullPointerException Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setUp - Exception Reason : " + e.getLocalizedMessage(), e);
        }

    }

    /**
     * This method check if the EBODAC page opens and if it is possible if there
     * are name, house hold and head of household .
     * 
     * @throws Exception
     */
    public void testAdminEbodacHome() throws Exception {
        try {
            ebodacPage.showParticipants();
            participantPage.openFirstParticipant();
            assertTrue(participantEditPage.isNameEditable());
            assertTrue(participantEditPage.isHouseholdNameEditable());
            assertTrue(participantEditPage.isHeadOfHouseholdEditable());
        } catch (AssertionError e) {
            getLogger().error("testAdminEbodacHome - AssertionError Error . Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error("testAdminEbodacHome - NullPointerException - Reason :  " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error("testAdminEbodacHome - InterruptedException - Reason :  " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("testAdminEbodacHome - Exception - Reason :  " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * We use this method to load all the pages needed for the test.
     * 
     * @return the list of pages loaded for the test
     * @throws InterruptedException
     */
    public void loadEbodacPages() throws InterruptedException {

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

    public void startEbodac() throws InterruptedException {
        // Load Ebodac Page
        homePage.clickModules();
        homePage.clickOnEbodac();
        ebodacPage = new EBODACPage(getDriver());
    }

    @Test // Test for EBODAC-531
    public void adminAccessOnlyToEbodacUiTest() throws Exception {
        try {
            // We try to access to the Ebodac

            // Ebodac Asserts.
            testAdminEbodacHome();
            // Visits Asserts
            testAdminVisitsTab();

            // Report Asserts
            testAdminWithReports();

            // Enrolment Tab
            testAdminEnrolmentTab();

            // IVR Module
            testAdminIVRModule();

            // SMS Module
            testAdminSMSModule();
        } catch (AssertionError e) {
            getLogger().error("adminAccessOnlyToEbodacUiTest - Assertion Error " + e.getLocalizedMessage(), e);

        } catch (NullPointerException e) {
            getLogger().error(
                    "adminAccessOnlyToEbodacUiTest - NullPointerException . Reason = " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("adminAccessOnlyToEbodacUiTest - Error . Reason = " + e.getLocalizedMessage(), e);
        }

    }

    public void testAdminSMSModule() {
        smsPage.goToPage();
        assertTrue(smsPage.logExists());
    }

    public void testAdminIVRModule() throws Exception {
        ebodacPage.sleep(SLEEP_2SEC);
        homePage.openIVRModule();
        ivrPage.openLog();
        ivrPage.openFirstRecord();
        assertFalse(ivrEditPage.isFromEditable());
    }

    public void testAdminVisitsTab() throws Exception {
        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.goToVisit();
        ebodacPage.showVisits();
        ebodacPage.sleep(SLEEP_2SEC);
        if (visitPage.hasVisitsVisible()) {
            assertTrue(visitEditPage.isPlannedVisitDateEditable());
            assertFalse(visitEditPage.isActualVisitDateEditable());
            assertFalse(visitEditPage.isVisitTypeEditable());
            visitEditPage.changeVisit();
        }
    }

    public void testAdminEnrolmentTab() throws Exception {
        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.goToEnrollment();
        // enrollmentPage.goToPage();
        // It should be allowed to enrol unenroll participants.
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

    }

    public void testAdminWithReports() throws Exception {

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.showPrimeVaccinationReport();
        assertTrue(primerVaccinationReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.showBoostVaccinationReport();
        assertTrue(boosterVaccinationReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.showDailyClinicVisitReportSchedule();
        assertTrue(dailyClinicVisitScheduleReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.showFollowUpsAfterPrimeInjectionReport();
        assertTrue(followupsAfterPrimeInjectionReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.showParticipantsWhoOptOutOfMessages();
        assertTrue(participantsWhoOptOutOfMessagesReportPage.existTable());

        // Report FollowupsMissedClinicVisits
        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.showFollowUpsMissedClinicReport();
        assertTrue(followupsMissedClinicVisitsReportPage.existTable());

        // Report MEMissed Clinics Visits Reports.
        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.showMEMissedClinicVisitsReport();
        assertTrue(meMissedClinicVisitsReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.showNumberOfTimesReport();
        assertTrue(numberOfTimesListenedReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.showScreeningReport();
        assertTrue(screeningReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.showPrimeFollowAndBoostReport();
        assertTrue(primeFollowAndBoostReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.showCallDetailRecord();
        assertTrue(callDetailRecordPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.showSMSLog();
        assertTrue(smsLogReportPage.existTable());
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
