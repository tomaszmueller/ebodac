package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class ReportPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";

    static final By MEMISSEDCLINICVISITSREPORT = By.linkText("M&E Missed Clinic Visits Report");
    public ReportPage(WebDriver driver) {
        super(driver);
    }

    public void showMEMissedClinicVisitsReport() throws InterruptedException {
        clickWhenVisible(MEMISSEDCLINICVISITSREPORT);
    }
    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }
}
