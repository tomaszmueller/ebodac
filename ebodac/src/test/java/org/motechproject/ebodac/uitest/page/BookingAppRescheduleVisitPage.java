package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookingAppRescheduleVisitPage extends AbstractBasePage {

    public static final String URL_PATH = "/#/bookingApp/reschedule/";

    static final By PLANNED_DATE_COLUMN = By.xpath("//div[@id='jqgh_visitReschedule_plannedDate']");
    static final By PLANNED_DATE_COLUMN_SORT = By.xpath("//div[@id='jqgh_visitReschedule_plannedDate']/span/span[2]");
    static final By VISIT = By.xpath("//table[@id='visitReschedule']/tbody/tr[2]");
    static final By VISIT_DATE = By.xpath("//table[@id='visitReschedule']/tbody/tr[2]/td[@aria-describedby='visitReschedule_plannedDate']");
    static final By DATE_FIELD = By.xpath("//div[@id='visitRescheduleModal']/div/div/div/div/div/input[@type='text']");
    static final By TIME_FIELD = By.xpath("//input[@mds-time-picker='']");
    static final By TIME_DONE = By.xpath("//button[@data-handler='hide']");
    static final By SAVE_BUTTON = By.xpath("//button[@ng-click='saveVisitReschedule(false)']");
    static final By POPUP_OK = By.id("popup_ok");
    static final By PRINT_CARD = By.xpath("//button[@ng-click='print()']");
    static final By CLOSE_BUTTON = By.xpath("//button[@data-dismiss='modal']");
    static final By IGNORE_EARLIEST_LATEST_DATE = By.xpath("//div[@id='visitRescheduleModal']/div[2]/div/div[2]/div/div[4]/input[@type='checkbox']");
    static final By YEAR_FIELD = By.xpath("//div[@id='ui-datepicker-div']/div/div/select/option[@selected='selected']");
    static final By MONTH_FIELD = By.className("ui-datepicker-month");
    static final By PREV = By.xpath("//div[@id='ui-datepicker-div']/div/a[@title='Prev']");
    static final By NEXT = By.xpath("//div[@id='ui-datepicker-div']/div/a[@title='Next']");
    static final By NEXT_PAGE = By.xpath("//td[@id='next_pager']/span");
    public BookingAppRescheduleVisitPage(WebDriver driver) {
        super(driver);
    }


    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }

    public void sortByPlannedDateColumn() throws InterruptedException {
        clickWhenVisible(PLANNED_DATE_COLUMN);
        Thread.sleep(2000);
        clickWhenVisible(PLANNED_DATE_COLUMN_SORT);
        Thread.sleep(2000);
    }

    public void chooseVisit() throws InterruptedException {
        WebElement visit = findElement(VISIT);
        clickWhenVisible(VISIT);
        visit.sendKeys(Keys.ENTER);
    }

    public boolean visitsExist() {
        try {
            WebElement firstVisitElement = findElement(VISIT);
            if (firstVisitElement == null) {
                return false;
            }
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }


    public void resizePage() {
        driver.manage().window().setSize(new Dimension(1920,1080));

    }

    public boolean rescheduleVisit() throws InterruptedException {
        waitForElement(IGNORE_EARLIEST_LATEST_DATE);
        clickWhenVisible(IGNORE_EARLIEST_LATEST_DATE);
        Thread.sleep(1000);
        waitForElement(DATE_FIELD);
        clickOn(DATE_FIELD);
        Calendar cal1 = Calendar.getInstance();
        Date setDate = cal1.getTime();
        Date myDate = new Date(setDate.getTime() + 5*86400000);
        cal1.setTime(myDate);
        int year = cal1.get(Calendar.YEAR);
        int month = cal1.get(Calendar.MONTH);
        int day = cal1.get(Calendar.DAY_OF_MONTH);
        int selectedYear = Integer.parseInt(findElement(YEAR_FIELD).getText());
        int selectedMonth = getMonthNumber(findElement(MONTH_FIELD).getText());
        while(year != selectedYear || month != selectedMonth) {
            selectedYear = Integer.parseInt(findElement(YEAR_FIELD).getText());
            selectedMonth = getMonthNumber(findElement(MONTH_FIELD).getText());
            if(year < selectedYear || (year == selectedYear && month < selectedMonth)) {
                waitForElement(PREV);
                clickOn(PREV);
            } else {
                waitForElement(NEXT);
                clickOn(NEXT);
            }
        }
        clickWhenVisible(By.linkText("" + day));
        setTextToFieldNoEnter(TIME_FIELD, "12:00");
        clickWhenVisible(TIME_DONE);
        Thread.sleep(1000);
        clickWhenVisible(SAVE_BUTTON);
        waitForElement(POPUP_OK);
        clickWhenVisible(POPUP_OK);
        clickWhenVisible(POPUP_OK);
        waitForElement(CLOSE_BUTTON);
        clickWhenVisible(CLOSE_BUTTON);
        Thread.sleep(1000);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String finalDate = dateFormat.format(myDate);
        boolean finalDateExists = false;
        int counter = 0;
        while(findElement(NEXT_PAGE).isEnabled()) {
            if(counter > 10) {
                break;
            }
            try {
                if(findElement(By.xpath("//table[@id='visitReschedule']/tbody/tr/td[contains(text(),'"+finalDate+"')]")) != null) {
                    finalDateExists = true;
                    break;
                } else {
                    clickOn(NEXT_PAGE);
                }
            } catch(Exception e) {
                clickOn(NEXT_PAGE);
            }
            counter++;
        }
        return finalDateExists;
    }

    public void printCard() throws InterruptedException {
        Thread.sleep(1000);
        waitForElement(PRINT_CARD);
        clickWhenVisible(PRINT_CARD);
    }

    /**
     * <p>Checks if two dates are on the same day ignoring time.</p>
     * @param date1  the first date, not altered, not null
     * @param date2  the second date, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either date is <code>null</code>
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    /**
     * <p>Checks if two calendars represent the same day ignoring time.</p>
     * @param cal1  the first calendar, not altered, not null
     * @param cal2  the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either calendar is <code>null</code>
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static int getMonthNumber(String monthName) {
        switch(monthName.toLowerCase()) {
            case "january":
                return 0;
            case "february":
                return 1;
            case "march":
                return 2;
            case "april":
                return 3;
            case "may":
                return 4;
            case "june":
                return 5;
            case "july":
                return 6;
            case "august":
                return 7;
            case "september":
                return 8;
            case "october":
                return 9;
            case "november":
                return 10;
            case "december":
                return 11;
            default:
                return -1;
        }

    }
}
