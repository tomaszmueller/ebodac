package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class ReportPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";

    static final By MEMISSEDCLINICVISITSREPORT = By.linkText("M&E Missed Clinic Visits Report");

    static final By DAILYCLINICVISITSCHEDULEREPORT = By.linkText("Daily Clinic Visit Schedule Report");

    static final By FOLLOW_UPS_AFTER_PRIME_INJECTION_REPORT = By.linkText("Follow-ups After Prime Injection Report");

    public ReportPage(WebDriver driver) {
        super(driver);
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

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }
}
