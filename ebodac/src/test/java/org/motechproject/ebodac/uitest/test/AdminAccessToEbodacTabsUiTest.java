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
    private static final long SLEEP_6SEC = 6000;
    private static final long SLEEP_4SEC = 4000;
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
            ebodacPage = new EBODACPage(getDriver());

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
            homePage.openEBODACModule();
            homePage.resizePage();
            
            ebodacPage.showParticipants();
            ebodacPage.sleep(SLEEP_2SEC);
            participantPage.openFirstParticipant();
            participantPage.sleep(SLEEP_6SEC);
            assertTrue(participantEditPage.isNameEditable());
            participantEditPage.sleep(SLEEP_2SEC);
            assertTrue(participantEditPage.isHouseholdNameEditable());
            participantEditPage.sleep(SLEEP_2SEC);
            assertTrue(participantEditPage.isHeadOfHouseholdEditable());
            participantEditPage.sleep(SLEEP_2SEC);
        } catch (AssertionError e) {
            getLogger().error("testAdminEbodacHome - AEr. Error . Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error("testAdminEbodacHome - NPE. Reason :  " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error("testAdminEbodacHome - IEx. - Reason :  " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("testAdminEbodacHome - Exc. - Reason :  " + e.getLocalizedMessage(), e);
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
        visitPage.sleep(SLEEP_6SEC);
        if (visitPage.hasVisitsVisible()) {
            visitPage.clickVisit();
            visitEditPage.isPlannedVisitDateEditable();
            visitEditPage.isActualVisitDateEditable();
            assertFalse(visitEditPage.isVisitTypeEditable());
            visitEditPage.changeVisit();
        }
    }

    public void testAdminEnrolmentTab() throws Exception {
        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.goToEnrollment();
        // It should be allowed to enrol unenroll participants.
        enrollmentPage.clickAction();
        enrollmentPage.clickOK();
        enrollmentPage.sleep(SLEEP_2SEC);
        if (enrollmentPage.error()) {
            enrollmentPage.clickOK();
            enrollmentPage.sleep(SLEEP_2SEC);
            enrollmentPage.nextAction();
        }
        enrollmentPage.sleep(SLEEP_2SEC);
        if (enrollmentPage.enrolled()) {
            assertTrue(enrollmentPage.enrolled());
            enrollmentPage.sleep(SLEEP_2SEC);
            enrollmentPage.clickOK();
        }
        enrollmentPage.sleep(SLEEP_2SEC);
        if (enrollmentPage.unenrolled()) {
            assertTrue(enrollmentPage.unenrolled());
            enrollmentPage.sleep(SLEEP_2SEC);
            enrollmentPage.clickOK();
        }

    }

    public void testAdminWithReports() throws Exception {

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.sleep(SLEEP_2SEC);
        reportPage.showPrimeVaccinationReport();
        primerVaccinationReportPage.sleep(SLEEP_4SEC);
        assertTrue(primerVaccinationReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.sleep(SLEEP_2SEC);
        reportPage.showBoostVaccinationReport();
        boosterVaccinationReportPage.sleep(SLEEP_4SEC);
        assertTrue(boosterVaccinationReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.sleep(SLEEP_2SEC);
        reportPage.showDailyClinicVisitReportSchedule();
        dailyClinicVisitScheduleReportPage.sleep(SLEEP_4SEC);
        assertTrue(dailyClinicVisitScheduleReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.sleep(SLEEP_2SEC);
        reportPage.showFollowUpsAfterPrimeInjectionReport();
        followupsAfterPrimeInjectionReportPage.sleep(SLEEP_4SEC);
        assertTrue(followupsAfterPrimeInjectionReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.sleep(SLEEP_2SEC);
        reportPage.showParticipantsWhoOptOutOfMessages();
        participantsWhoOptOutOfMessagesReportPage.sleep(SLEEP_4SEC);
        assertTrue(participantsWhoOptOutOfMessagesReportPage.existTable());

        // Report FollowupsMissedClinicVisits
        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.sleep(SLEEP_2SEC);
        reportPage.showFollowUpsMissedClinicReport();
        followupsMissedClinicVisitsReportPage.sleep(SLEEP_4SEC);
        assertTrue(followupsMissedClinicVisitsReportPage.existTable());

        // Report MEMissed Clinics Visits Reports.
        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.sleep(SLEEP_2SEC);
        reportPage.showMEMissedClinicVisitsReport();
        meMissedClinicVisitsReportPage.sleep(SLEEP_4SEC);
        assertTrue(meMissedClinicVisitsReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.sleep(SLEEP_2SEC);
        reportPage.showNumberOfTimesReport();
        numberOfTimesListenedReportPage.sleep(SLEEP_4SEC);
        assertTrue(numberOfTimesListenedReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.sleep(SLEEP_2SEC);
        reportPage.showScreeningReport();
        screeningReportPage.sleep(SLEEP_4SEC);
        assertTrue(screeningReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.sleep(SLEEP_2SEC);
        reportPage.showPrimeFollowAndBoostReport();
        primeFollowAndBoostReportPage.sleep(SLEEP_4SEC);
        assertTrue(primeFollowAndBoostReportPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.sleep(SLEEP_2SEC);
        reportPage.showCallDetailRecord();
        callDetailRecordPage.sleep(SLEEP_2SEC);
        assertTrue(callDetailRecordPage.existTable());

        ebodacPage.sleep(SLEEP_2SEC);
        ebodacPage.gotoReports();
        reportPage.sleep(SLEEP_2SEC);
        reportPage.showSMSLog();
        smsLogReportPage.sleep(SLEEP_4SEC);
        assertTrue(smsLogReportPage.existTable());
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
