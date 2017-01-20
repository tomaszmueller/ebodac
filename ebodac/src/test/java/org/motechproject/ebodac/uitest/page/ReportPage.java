package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ReportPage extends AbstractBasePage {

    private static final String ID_PAGE_INSTANCES_TABLE_LEFT_DIV = "//*[@id='pageInstancesTable_left']/div";

    private static final String NO_RECORDS_TO_VIEW = "No records to view";

    public static final String URL_PATH = "/home#/mds/dataBrowser";

    static final By FOLLOW_UPS_MISSED_CLINIC_REPORT = By
            .xpath("//*[@id='main-content']/div/div/table/tbody/tr[5]/td/a");
    // linkText("Follow-ups Missed Clinic Visit Report");

    static final By MEMISSEDCLINICVISITSREPORT = By.xpath("//*[@id='main-content']/div/div/table/tbody/tr[7]/td/a");
    // linkText("M&E Missed Clinic Visits Report");

    static final By DAILYCLINICVISITSCHEDULEREPORT = By.linkText("Daily Clinic Visit Schedule Report");

    static final By FOLLOW_UPS_AFTER_PRIME_INJECTION_REPORT = By.linkText("Follow-ups After Prime Injection Report");

    static final By PRIME_VACCINATION_REPORT = By.linkText("Primer Vaccination Report");

    static final By BOOSTER_VACCINATION_REPORT = By.linkText("Booster Vaccination Report");

    static final By CALL_DETAIL_RECORD = By.linkText("Call Detail Record");

    static final By SMS_LOG = By.linkText("SMS Log");

    static final By NUMBER_OF_TIMES_REPORT = By
            .linkText("Number of times participants listened to each message Report");

    static final By PARTICIPANTS_WHO_OPT_OUT_OF_MESSAGES_REPORT = By
            .linkText("Participants Who Opt Out of Receiving MOTECH Messages");

    static final By SCREENING_REPORT = By.linkText("Screening Report");

    static final By PRIME_FOLLOW_AND_BOOST_REPORT = By.linkText("Prime Vac 1st Follow-up and Boost Vac Day Report");

    static final By NUMBER_OF_TIMES_LISTENED_REPORT = By.linkText("Number of times participants listened to each message Report");

    public static final int DEFAULT_VALUE_OF_FAILUIRE_SEARCH = 0;

    private static final long WAIT_2SEC = 2000;

    public boolean checkIfIVRTableHistoryContainsRows() throws InterruptedException {
        boolean status = false;
        try {
            return !findElement(By.xpath(ID_PAGE_INSTANCES_TABLE_LEFT_DIV)).getAttribute("innerHTML")
                    .contains(NO_RECORDS_TO_VIEW);
        } catch (NullPointerException e) {
            status = false;
            getLogger().error("checkIfTableOfCallDetailRecordInstancesIsVisible - NullPointerException . Reason : "
                    + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            status = false;
            getLogger().error("checkIfTableOfCallDetailRecordInstancesIsVisible - Exception . Reason : "
                    + e.getLocalizedMessage(), e);
        }
        return status;
    }

    public ReportPage(WebDriver driver) {
        super(driver);
    }

    public void showPrimeVaccinationReport() throws InterruptedException {
        clickWhenVisible(PRIME_VACCINATION_REPORT);
    }

    public void showBoostVaccinationReport() throws InterruptedException {
        clickWhenVisible(BOOSTER_VACCINATION_REPORT);
    }

    public void showMEMissedClinicVisitsReport() throws InterruptedException {
        clickWhenVisible(MEMISSEDCLINICVISITSREPORT);
        sleep(WAIT_2SEC);
    }

    public void showDailyClinicVisitReportSchedule() throws InterruptedException {
        clickWhenVisible(DAILYCLINICVISITSCHEDULEREPORT);
    }

    public void showFollowUpsAfterPrimeInjectionReport() throws InterruptedException {
        clickWhenVisible(FOLLOW_UPS_AFTER_PRIME_INJECTION_REPORT);
    }

    public void showNumberOfTimesListenedReport() throws InterruptedException {
        clickWhenVisible(NUMBER_OF_TIMES_LISTENED_REPORT);
    }

    public void showCallDetailRecord() throws InterruptedException {
        clickWhenVisible(CALL_DETAIL_RECORD);
    }

    public void showSMSLog() throws InterruptedException {
        clickWhenVisible(SMS_LOG);
    }

    public void showFollowUpsMissedClinicReport() throws InterruptedException {
        clickWhenVisible(FOLLOW_UPS_MISSED_CLINIC_REPORT);
        sleep(WAIT_2SEC);
    }

    public void showNumberOfTimesReport() throws InterruptedException {
        clickWhenVisible(NUMBER_OF_TIMES_REPORT);
    }

    public void showParticipantsWhoOptOutOfMessages() throws InterruptedException {
        clickWhenVisible(PARTICIPANTS_WHO_OPT_OUT_OF_MESSAGES_REPORT);
    }

    public void showScreeningReport() throws InterruptedException {
        clickWhenVisible(SCREENING_REPORT);
    }

    public void showPrimeFollowAndBoostReport() throws InterruptedException {
        clickWhenVisible(PRIME_FOLLOW_AND_BOOST_REPORT);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public void sleep(long sleep) throws InterruptedException {
        Thread.sleep(sleep);

    }

}
