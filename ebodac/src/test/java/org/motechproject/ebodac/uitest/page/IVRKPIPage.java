package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertTrue;

public class IVRKPIPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/ebodac/subjects";

    static final By IVR_KPIS = By.linkText("IVR KPIs");
    static final By STAT_PERIOD_BUTTON = By.xpath("(//button[@type='button'])[4]");
    static final By LAST_30_DAYS = By.linkText("Last 30 days");
    static final By DATE_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Date']");
    static final By TOTAL_CALLS_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Total Calls']");
    static final By TOTAL_PENDING_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Total Pending']");
    static final By TOTAL_FAILED_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Total Failed']");
    static final By TOTAL_SUCCEEDED_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Total Succeed']");
    static final By CALL_TO_MEN_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Call To Men']");
    static final By CALL_TO_WOMEN_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Call To Women']");
    static final By SUCCESSFULL_CALL_TO_MEN_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Successful call to Men']");
    static final By SUCCESSFULL_CALL_TO_WOMEN_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Successful call to Women']");

    public IVRKPIPage(WebDriver driver) {
        super(driver);
    }

    public void showIVRKPIs() throws InterruptedException {
        clickWhenVisible(IVR_KPIS);
    }

    public void showStatsFromLast30Days() throws InterruptedException {
        clickWhenVisible(STAT_PERIOD_BUTTON);
        clickWhenVisible(LAST_30_DAYS);
    }

    public void checkColumns() {
            assertTrue(checkColumn(DATE_COLUMN));
            assertTrue(checkColumn(TOTAL_CALLS_COLUMN));
            assertTrue(checkColumn(TOTAL_PENDING_COLUMN));
            assertTrue(checkColumn(TOTAL_FAILED_COLUMN));
            assertTrue(checkColumn(TOTAL_SUCCEEDED_COLUMN));
            assertTrue(checkColumn(CALL_TO_MEN_COLUMN));
            assertTrue(checkColumn(CALL_TO_WOMEN_COLUMN));
    }
    public boolean checkColumn(By column) {
        try {
            if (findElement(column) == null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }

    }
    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }


}
