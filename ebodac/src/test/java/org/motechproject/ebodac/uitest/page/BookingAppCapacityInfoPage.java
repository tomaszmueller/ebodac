package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class BookingAppCapacityInfoPage extends AbstractBasePage {

    public static final String URL_PATH = "/#/bookingApp/capacityInfo/";

    static final By FILTER_BUTTON = By.xpath("(//button[@type='button'])[2]");

    static final By TODAY = By.linkText("Today");
    static final By TOMORROW = By.linkText("Tomorrow");
    static final By DAY_AFTER_TOMORROW = By.linkText("Day after tomorrow");
    static final By NEXT_3_DAYS = By.linkText("Next 3 days");
    static final By NEXT_7_DAYS = By.linkText("Next 7 days");
    static final By DATE_RANGE = By.linkText("Date range");
    static final By MAX_CAPACITY = By.xpath("//td[@aria-describedby='capacityInfo_maxCapacity']");
    static final By START_DATE = By.xpath("//input[@ng-model='selectedFilter.startDate']");
    static final By END_DATE = By.xpath("//input[@ng-model='selectedFilter.endDate']");
    static final By FIRST_DAY = By.linkText("1");
    static final By LAST_DAY = By.linkText("28");
    static final int SLEEP_6500 = 6500;

    public BookingAppCapacityInfoPage(WebDriver driver) {
        super(driver);
    }

    public void filterToday() throws InterruptedException {
        clickWhenVisible(FILTER_BUTTON);
        clickWhenVisible(TODAY);
        Thread.sleep(SLEEP_6500);
    }

    public void filterTomorrow() throws InterruptedException {
        clickWhenVisible(FILTER_BUTTON);
        clickWhenVisible(TOMORROW);
        Thread.sleep(SLEEP_6500);
    }

    public void filterDayAfterTomorrow() throws InterruptedException {
        clickWhenVisible(FILTER_BUTTON);
        clickWhenVisible(DAY_AFTER_TOMORROW);
        Thread.sleep(SLEEP_6500);
    }

    public void filterNext3Days() throws InterruptedException {
        clickWhenVisible(FILTER_BUTTON);
        clickWhenVisible(NEXT_3_DAYS);
        Thread.sleep(SLEEP_6500);
    }

    public void filterNext7Days() throws InterruptedException {
        clickWhenVisible(FILTER_BUTTON);
        clickWhenVisible(NEXT_7_DAYS);
        Thread.sleep(SLEEP_6500);
    }

    public void filterDateRange() throws InterruptedException {
        clickWhenVisible(FILTER_BUTTON);
        clickWhenVisible(DATE_RANGE);
        clickWhenVisible(START_DATE);
        clickWhenVisible(FIRST_DAY);
        clickWhenVisible(END_DATE);
        clickWhenVisible(LAST_DAY);
        Thread.sleep(SLEEP_6500);
    }

    public String getMaxCapacity() {
        WebElement element = findElement(MAX_CAPACITY);
        String value = element.getAttribute("title");
        return value;
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }
}
