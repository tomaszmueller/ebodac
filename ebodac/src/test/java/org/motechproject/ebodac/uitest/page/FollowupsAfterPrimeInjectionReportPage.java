package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class FollowupsAfterPrimeInjectionReportPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By TABLE = By.xpath("//table[@class='ui-jqgrid-htable']");

    public FollowupsAfterPrimeInjectionReportPage(WebDriver driver) {
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
            getLogger().error("existTable - Exception . Reason : " + e.getLocalizedMessage(), e);
            status = true;
        }
        return status;
    }
    
    public boolean isReportEmpty() {
        boolean status = true;
        try {
            if (findElement(By.xpath("//*[@id='pageReportTable_left']/div")).getAttribute("innerHTML")
                    .contains("No records to view")) {
                status = true;
            } else {
                status = false;
            }
        } catch (Exception e) {
            getLogger().error("isReportEmpty - Exception . Reason : " + e.getLocalizedMessage(), e);
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
