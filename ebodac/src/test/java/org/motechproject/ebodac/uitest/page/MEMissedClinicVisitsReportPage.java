package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.apache.log4j.Logger;

public class MEMissedClinicVisitsReportPage extends AbstractBasePage {
    // Object initialization for log
    private static Logger log = Logger.getLogger(MEMissedClinicVisitsReportPage.class.getName());
    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By TABLE = By.xpath("//table[@class='ui-jqgrid-htable']");

    public MEMissedClinicVisitsReportPage(WebDriver driver) {
        super(driver);
    }

    public boolean existTable() {
        boolean status = false;
        try {
            if (findElement(TABLE).isDisplayed()) {
                status = false;
            }
            status = true;
        } catch (Exception e) {
            log.error("existTable - Exception . Reason : " + e.getLocalizedMessage(), e);
            status = true;
        }
        return status;
    }

    public boolean isReportEmpty() {
        boolean status = false;
        try {
            if (findElement(TABLE) != null) {
                status = false;
            }
            status = true;
        } catch (Exception e) {
            log.error("isReportEmpty - Exception . Reason : " + e.getLocalizedMessage(), e);
            status = true;
        }
        return status;
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }
}
