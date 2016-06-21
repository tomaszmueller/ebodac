package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class ReportPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";

    static final By MEMISSEDCLINICVISITSREPORT = By.linkText("M&E Missed Clinic Visits Report");

    static final By DAILYCLINICVISITSCHEDULEREPORT = By.linkText("Daily Clinic Visit Schedule Report");

    static final By FOLLOW_UPS_AFTER_PRIME_INJECTION_REPORT = By.linkText("Follow-ups After Prime Injection Report");

    static final By FOLLOW_UPS_MISSED_CLINIC_REPORT = By.linkText("Follow-ups Missed Clinic Visit Report");

    static final By PRIME_VACCINATION_REPORT = By.linkText("Primer Vaccination Report");

    static final By BOOSTER_VACCINATION_REPORT = By.linkText("Booster Vaccination Report");

    static final By CALL_DETAIL_RECORD = By.linkText("Call Detail Record");

    static final By SMS_LOG = By.linkText("SMS Log");

    static final By NUMBER_OF_TIMES_REPORT = By.linkText("Number of times participants listened to each message Report");

    static final By PARTICIPANTS_WHO_OPT_OUT_OF_MESSAGES_REPORT = By.linkText("Participants Who Opt Out of Receiving MOTECH Messages");

    static final By SCREENING_REPORT = By.linkText("Screening Report");

    static final By PRIME_FOLLOW_AND_BOOST_REPORT = By.linkText("Prime Vac 1st Follow-up and Boost Vac Day Report");
    //TABLES
    static final By CALL_DETAIL_REPORT_TABLE_ID = By.id("jqgh_instancesTable_id");

    static final By CALL_DETAIL_REPORT_TABLE_CONFIG_NAME = By.id("instancesTable_configName");

    static final By CALL_DETAIL_REPORT_TABLE_FROM = By.id("instancesTable_configName");

    static final By CALL_DETAIL_REPORT_TABLE_TO = By.id("instancesTable_to");

    static final By CALL_DETAIL_REPORT_TABLE_CALL_DIRECTION = By.id("instancesTable_callDirection");

    static final By CALL_DETAIL_REPORT_TABLE_CALL_STATUS = By.id("instancesTable_callStatus");

    static final By CALL_DETAIL_REPORT_TABLE_TEMPLATE_NAME = By.id("instancesTable_templateName");

    static final By CALL_DETAIL_REPORT_TABLE_PROVIDER_EXTRA_DATA = By.id("instancesTable_providerExtraData");

    static final By CALL_DETAIL_REPORT_TABLE_MOTECH_CALL_ID = By.id("instancesTable_motechCallId");

    static final By CALL_DETAIL_REPORT_TABLE_PROVIDER_CALL_ID = By.id("instancesTable_providerCallId");

    static final By CALL_DETAIL_REPORT_TABLE_MOTEH_TIMESTAMP = By.id("instancesTable_motechTimestamp");

    static final By CALL_DETAIL_REPORT_TABLE_PROVIDER_TIMESTAMP = By.id("instancesTable_providerTimestamp");

    static final By CALL_DETAIL_REPORT_TABLE_CALL_DURATION = By.id("instancesTable_callDuration");

    static final By CALL_DETAIL_REPORT_TABLE_MESSAGE_PERCENT_LISTENED = By.id("instancesTable_messagePercentListened");

    public static final int DEFAULT_VALUE_OF_FAILUIRE_SEARCH = 0;


    public boolean checkIfTableOfCallDetailRecordInstancesIsVisible() throws InterruptedException { //NO CHECKSTYLE CyclomaticComplexity
        int result = DEFAULT_VALUE_OF_FAILUIRE_SEARCH;
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_ID).getText()))) {
            result++;
        }
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_CONFIG_NAME).getText()))) {
            result++;
        }
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_FROM).getText()))) {
            result++;
        }
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_TO).getText()))) {
            result++;
        }
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_CALL_DIRECTION).getText()))) {
            result++;
        }
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_CALL_STATUS).getText()))) {
            result++;
        }
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_TEMPLATE_NAME).getText()))) {
            result++;
        }
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_PROVIDER_EXTRA_DATA).getText()))) {
            result++;
        }
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_MOTECH_CALL_ID).getText()))) {
            result++;
        }
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_PROVIDER_CALL_ID).getText()))) {
            result++;
        }
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_MOTEH_TIMESTAMP).getText()))) {
            result++;
        }
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_PROVIDER_TIMESTAMP).getText()))) {
            result++;
        }
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_CALL_DURATION).getText()))) {
            result++;
        }
        if (!("".equals(findElement(CALL_DETAIL_REPORT_TABLE_MESSAGE_PERCENT_LISTENED).getText()))) {
            result++;
        }
        return (result > DEFAULT_VALUE_OF_FAILUIRE_SEARCH);
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
    }

    public void showDailyClinicVisitReportSchedule() throws InterruptedException {
        clickWhenVisible(DAILYCLINICVISITSCHEDULEREPORT);
    }

    public void showFollowUpsAfterPrimeInjectionReport() throws InterruptedException {
        clickWhenVisible(FOLLOW_UPS_AFTER_PRIME_INJECTION_REPORT);
    }

    public void showCallDetailRecord() throws InterruptedException {
        clickWhenVisible(CALL_DETAIL_RECORD);
    }

    public void showSMSLog() throws InterruptedException {
        clickWhenVisible(SMS_LOG);
    }

    public void showFollowUpsMissedClinicReport() throws InterruptedException {
        clickWhenVisible(FOLLOW_UPS_MISSED_CLINIC_REPORT);
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
}
