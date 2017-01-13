package org.motechproject.ebodac.uitest.page;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertTrue;

public class DailyClinicVisitScheduleReportPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By TABLE = By.xpath("//table[@class='ui-jqgrid-htable']");
    static final By LOOKUP = By.id("lookupDialogButton");
    static final By SELECT = By.xpath("//div[@id='lookup-dialog']/div[2]/div/div/button");
    static final By TYPE_SELECT = By.xpath("//select[@ng-model='lookupBy[buildLookupFieldName(field)]']");
    static final By FIND = By.xpath("//button[@ng-click='filterInstancesByLookup()']");
    static final By PARTICIPANT_NAME = By.linkText("Find By Participant Name");
    static final By TYPE = By.linkText("Find By Type");
    static final By PARTICIPANT_ID = By.linkText("Find By Participant Id");
    static final By PARTICIPANT_ADDRESS = By.linkText("Find By Participant Address");
    static final By PLANNED_VISIT_DATE_RANGE = By.linkText("Find By Planned Visit Date Range");
    static final By PLANNED_VISIT_DATE_RANGE_AND_TYPE = By.linkText("Find By Planned Visit Date Range And Type");
    static final By RECORD_NAME = By.xpath("//table[@id='reportTable']/tbody/tr[@id='1']/td[4]");
    static final By RECORD_TYPE = By.xpath("//table[@id='reportTable']/tbody/tr[@id='1']/td[7]");
    static final By RECORD_ID = By.xpath("//table[@id='reportTable']/tbody/tr[@id='1']/td[3]");
    static final By RECORD_ADDRESS = By.xpath("//table[@id='reportTable']/tbody/tr[@id='1']/td[6]");
    static final By RECORD_DATE = By.xpath("//table[@id='reportTable']/tbody/tr[@id='1']/td[2]");
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final int SLEEP_1000 = 1000;
    private static final int SLEEP_2500 = 2500;
    private static final int VAL_2 = 2;
    private static final int VAL_3 = 3;
    private static final int VAL_4 = 4;
    private static final int VAL_5 = 5;
    private static final int VAL_6 = 6;
    private static final int VAL_7 = 7;
    private static final int VAL_8 = 8;
    private static final int VAL_9 = 9;
    private static final int VAL_10 = 10;
    private static final int VAL_11 = 11;
    private static final int VAL_12 = 12;
    private static final int VAL_13 = 13;
    private static final int VAL_14 = 14;
    private static final int VAL_15 = 15;
    private static final int VAL_16 = 16;
    private static final int VAL_17 = 17;
    private static final int DEFAULT_VAL = -1;
    public DailyClinicVisitScheduleReportPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public void openLookup() throws InterruptedException {
        clickOn(LOOKUP);
        clickOn(SELECT);
    }

    public void findByParticipantName(String name) throws InterruptedException {
        clickWhenVisible(PARTICIPANT_NAME);
        Thread.sleep(SLEEP_1000);
        findElement(By.xpath("//input[@type='text']")).sendKeys(name);
        clickWhenVisible(FIND);
        Thread.sleep(SLEEP_1000);
        assertTrue(findElement(RECORD_NAME).getText().contains(name));
    }

    public void findByType(String type) throws InterruptedException {
        clickWhenVisible(TYPE);
        clickWhenVisible(TYPE_SELECT);
        clickOn(By.xpath("//select[@ng-model='lookupBy[buildLookupFieldName(field)]']/option[" + visitOption(type) + "]"));
        clickWhenVisible(FIND);
        Thread.sleep(SLEEP_1000);
        assertTrue(findElement(RECORD_TYPE).getText().contains(type));
    }

    public void findByParticipantId(String id) throws InterruptedException {
        clickWhenVisible(PARTICIPANT_ID);
        findElement(By.xpath("//input[@type='text']")).sendKeys(id);
        clickWhenVisible(FIND);
        Thread.sleep(SLEEP_1000);
        assertTrue(findElement(RECORD_ID).getText().contains(id));
    }

    public void findByParticipantAddress(String address) throws InterruptedException {
        clickWhenVisible(PARTICIPANT_ADDRESS);
        findElement(By.xpath("//input[@type='text']")).sendKeys(address);
        clickWhenVisible(FIND);
        Thread.sleep(SLEEP_1000);
        assertTrue(findElement(RECORD_ADDRESS).getText().contains(address));
    }

    public void findByPlannedVisitDateRange(String from, String to) throws InterruptedException {
        clickWhenVisible(PLANNED_VISIT_DATE_RANGE);
        if (!from.isEmpty()) {
            findElement(By.xpath("//input[@placeholder='From']")).sendKeys(from);
            findElement(By.xpath("//input[@placeholder='From']")).sendKeys(Keys.ENTER);
            clickOn(By.xpath("//button[text()='Done']"));
        }
        if (!to.isEmpty()) {
            findElement(By.xpath("//input[@placeholder='To']")).sendKeys(to);
            findElement(By.xpath("//input[@placeholder='To']")).sendKeys(Keys.ENTER);
            clickOn(By.xpath("//button[text()='Done']"));
        }
        clickWhenVisible(FIND);
        Thread.sleep(SLEEP_1000);
        assertTrue(isDateBetween(from, to, LocalDate.parse(findElement(RECORD_DATE).getText(), DateTimeFormat.forPattern(YYYY_MM_DD))));
    }

    public void findByPlannedVisitDateRangeAndType(String from, String to, String type) throws InterruptedException {
        clickWhenVisible(PLANNED_VISIT_DATE_RANGE_AND_TYPE);
        if (!from.isEmpty()) {
            findElement(By.xpath("//input[@placeholder='From']")).sendKeys(from);
            findElement(By.xpath("//input[@placeholder='From']")).sendKeys(Keys.ENTER);
            clickOn(By.xpath("//button[text()='Done']"));
        }
        if (!to.isEmpty()) {
            findElement(By.xpath("//input[@placeholder='To']")).sendKeys(to);
            findElement(By.xpath("//input[@placeholder='To']")).sendKeys(Keys.ENTER);
            clickOn(By.xpath("//button[text()='Done']"));
        }
        clickWhenVisible(TYPE);
        clickWhenVisible(TYPE_SELECT);
        clickOn(By.xpath("//select[@ng-model='lookupBy[buildLookupFieldName(field)]']/option[" + visitOption(type) + "]"));
        clickWhenVisible(FIND);
        Thread.sleep(SLEEP_2500);
        assertTrue(isDateBetween(from, to, LocalDate.parse(findElement(RECORD_DATE).getText(), DateTimeFormat.forPattern(YYYY_MM_DD))));
        assertTrue(findElement(RECORD_TYPE).getText().contains(type));
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
            getLogger().error("isReportEmpty -  Exception . Reason : " + e.getLocalizedMessage(), e);
            status = false;
        }
        return status;
    }

    public void sleep(long timeout) throws InterruptedException {
        Thread.sleep(timeout);
        
    }

    private boolean isDateBetween(String from, String to, LocalDate date) {
        boolean isNotToLate = true;
        boolean isNotToEarly = true;
        LocalDate.parse(from, DateTimeFormat.forPattern(YYYY_MM_DD));
        try {
            if (date.isBefore(LocalDate.parse(from, DateTimeFormat.forPattern(YYYY_MM_DD)))) {
                isNotToEarly = false;
            }
        } catch (Exception e) {
            isNotToEarly = true;
        }
        try {
            if (date.isAfter(LocalDate.parse(to, DateTimeFormat.forPattern(YYYY_MM_DD)))) {
                isNotToLate = false;
            }
        } catch (Exception e) {
            isNotToLate = true;
        }
        return (isNotToLate & isNotToEarly);
    }

    private Integer visitOption(String type) {
        switch(type) {
            case "Screening":
                return VAL_5;
            case "Prime Vaccination Day":
                return VAL_14;
            default:
                return DEFAULT_VAL;
        }
    }
}
