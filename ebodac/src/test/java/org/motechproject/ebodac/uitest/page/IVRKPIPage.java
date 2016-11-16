package org.motechproject.ebodac.uitest.page;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertTrue;

public class IVRKPIPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/ebodac/subjects";

    static final By POPUP_OK = By.id("popup_ok");
    static final By POPUP_CONTENT = By.id("popup_content");
    static final By IVR_GRAPHS = By.linkText("IVR Graphs");
    static final By IVR_KPIS = By.linkText("IVR KPIs");
    static final By SMS_KPIS = By.linkText("SMS KPIs");
    static final By STAT_PERIOD_BUTTON = By.xpath("(//button[@type='button'])[4]");
    static final By GRAPH_PERIOD_BUTTON = By.xpath("(//button[@type='button'])[6]");
    static final By LAST_30_DAYS = By.linkText("Last 30 days");
    static final By DATE_RANGE = By.linkText("Date Range");
    static final By START_DATE = By.xpath("//input[@ng-model='selectedFilter.startDate']");
    static final By END_DATE = By.xpath("//input[@ng-model='selectedFilter.endDate']");
    static final String YYYY_MM_DD = "yyyy-MM-dd";
    static final By DATE_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Date']");
    static final By TOTAL_CALLS_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Total Calls']");
    static final By TOTAL_SMS_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Total SMS Sent']");
    static final By TOTAL_PENDING_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Total Pending']");
    static final By TOTAL_FAILED_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Total Failed']");
    static final By TOTAL_SUCCEEDED_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Total Succeed']");
    static final By CALL_TO_MEN_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Call To Men']");
    static final By CALL_TO_WOMEN_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Call To Women']");
    static final By SUCCESSFULL_CALL_TO_MEN_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Successful call To Men']");
    static final By SUCCESSFULL_CALL_TO_WOMEN_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Successful call To Women']");
    static final By SMS_TO_MEN_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='SMS To Men']");
    static final By SMS_TO_WOMEN_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='SMS To Women']");
    static final By SUCCESSFULL_SMS_TO_MEN_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Successful SMS To Men']");
    static final By SUCCESSFULL_SMS_TO_WOMEN_COLUMN = By.xpath("//th[@ng-repeat='header in tableHeaders'][text()='Successful SMS To Women']");
    static final int SLEEP_3SEC = 3000;
    static final By STATUS_GRAPH = By.id("statsGraph");
    static final By GENDER_GRAPH = By.id("genderGraph");
    static final By SUCCESSFUL_GENDER_GRAPH = By.id("successfulGenderGraph");
    public IVRKPIPage(WebDriver driver) {
        super(driver);
    }

    public void showIVRKPIs() throws InterruptedException {
        clickWhenVisible(IVR_KPIS);
    }

    public void showSMSKPIs() throws InterruptedException {
        clickWhenVisible(SMS_KPIS);
    }

    public void showIVRGraphs() throws InterruptedException {
        clickWhenVisible(IVR_GRAPHS);
    }

    public void showStatsFromLast30Days() throws InterruptedException {
        clickWhenVisible(STAT_PERIOD_BUTTON);
        clickWhenVisible(LAST_30_DAYS);
    }

    public void showStatsFromLastYear() throws InterruptedException {
        clickWhenVisible(GRAPH_PERIOD_BUTTON);
        clickWhenVisible(DATE_RANGE);
        findElement(START_DATE).sendKeys("2016-01-01");
        while (error()) {
            clickWhenVisible(POPUP_OK);
        }
        sleep(SLEEP_3SEC);
        findElement(END_DATE).sendKeys(LocalDate.now().toString(DateTimeFormat.forPattern(YYYY_MM_DD)));
        while (error()) {
            clickWhenVisible(POPUP_OK);
        }

    }

    public boolean checkGraphs() {
        try {
            if (findElement(STATUS_GRAPH) == null) {
                return false;
            }
            if (findElement(GENDER_GRAPH) == null) {
                return false;
            }
            if (findElement(SUCCESSFUL_GENDER_GRAPH) == null) {
                return false;
            }
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public void checkIVRColumns() {
        assertTrue(checkColumn(DATE_COLUMN));
        assertTrue(checkColumn(TOTAL_CALLS_COLUMN));
        assertTrue(checkColumn(TOTAL_PENDING_COLUMN));
        assertTrue(checkColumn(TOTAL_FAILED_COLUMN));
        assertTrue(checkColumn(TOTAL_SUCCEEDED_COLUMN));
        assertTrue(checkColumn(CALL_TO_MEN_COLUMN));
        assertTrue(checkColumn(CALL_TO_WOMEN_COLUMN));
        assertTrue(checkColumn(SUCCESSFULL_CALL_TO_MEN_COLUMN));
        assertTrue(checkColumn(SUCCESSFULL_CALL_TO_WOMEN_COLUMN));
    }

    public void checkSMSColumns() {
        assertTrue(checkColumn(DATE_COLUMN));
        assertTrue(checkColumn(TOTAL_SMS_COLUMN));
        assertTrue(checkColumn(TOTAL_PENDING_COLUMN));
        assertTrue(checkColumn(TOTAL_FAILED_COLUMN));
        assertTrue(checkColumn(TOTAL_SUCCEEDED_COLUMN));
        assertTrue(checkColumn(SMS_TO_MEN_COLUMN));
        assertTrue(checkColumn(SMS_TO_WOMEN_COLUMN));
        assertTrue(checkColumn(SUCCESSFULL_SMS_TO_MEN_COLUMN));
        assertTrue(checkColumn(SUCCESSFULL_SMS_TO_WOMEN_COLUMN));
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

    public boolean error() {
        try {
            if (findElement(POPUP_CONTENT) != null) {
                return true;
            }
            return false;
        } catch (Exception ex) {
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
