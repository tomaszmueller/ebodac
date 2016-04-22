package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class DailyClinicVisitScheduleReportPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By RECORD = By.xpath("//table[@id='reportTable']/tbody/tr[2]");

    public DailyClinicVisitScheduleReportPage(WebDriver driver) {
        super(driver);
    }

    public boolean isReportEmpty() {
        try {
            if (findElement(RECORD) != null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }
}
