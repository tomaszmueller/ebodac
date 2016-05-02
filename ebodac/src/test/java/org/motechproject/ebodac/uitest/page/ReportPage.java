package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
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

    public void showCallDetailRecord() throws InterruptedException  {
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
        return URL_ROOT + URL_PATH;
    }
}
