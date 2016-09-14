package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CallDetailRecordPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By TABLE = By.xpath("//table[@class='ui-jqgrid-htable']");

    public CallDetailRecordPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public boolean existTable() {
        boolean status = false;
        try {
            if (!findElement(TABLE).getAttribute("innerHTML").isEmpty()) {
                status = true;
            }
        } catch (Exception e) {
            getLogger().error("existTable - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
        return status;
    }

    public boolean isReportEmpty() {
        boolean status = false;
        try {
            if (findElement(By.xpath("//*[@id='pageReportTable_left']/div")).getAttribute("innerHTML")
                    .contains("No records to view")) {
                status = true;
            }
        } catch (Exception e) {
            getLogger().error("isReportEmpty -  Exc. Reason : " + e.getLocalizedMessage(), e);
            status = false;
        }
        return status;
    }

    public void sleep(long timeout) throws InterruptedException {
       Thread.sleep(timeout);
        
    }
}
