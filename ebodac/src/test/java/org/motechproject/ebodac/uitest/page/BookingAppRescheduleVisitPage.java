
package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidSelectorException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookingAppRescheduleVisitPage extends AbstractBasePage {

    static final int DEFAULT_VALUE = -1;
    static final int JANUARY = 0;
    static final int FEBRUARY = 1;
    static final int MARCH = 2;
    static final int APRIL = 3;
    static final int MAY = 4;
    static final int JUNE = 5;
    static final int JULY = 6;
    static final int AUGUST = 7;
    static final int SEPTEMBER = 8;
    static final int OCTOBER = 9;
    static final int NOVEMBER = 10;
    static final int DECEMBER = 11;

    public static final String URL_PATH = "/#/bookingApp/reschedule/";

    static final By PLANNED_DATE_COLUMN = By.xpath("//div[@id='jqgh_visitReschedule_plannedDate']");
    static final By PLANNED_DATE_COLUMN_SORT = By.xpath("//div[@id='jqgh_visitReschedule_plannedDate']/span/span[2]");
    static final By VISIT = By.xpath("//table[@id='visitReschedule']/tbody/tr[2]");
    static final By VISIT_DATE = By
            .xpath("//table[@id='visitReschedule']/tbody/tr[2]/td[@aria-describedby='visitReschedule_plannedDate']");
    static final By DATE_FIELD = By.xpath("//div[@id='visitRescheduleModal']/div/div/div/div/div/input[@type='text']");
    static final By TIME_FIELD = By.xpath("//input[@mds-time-picker='']");
    static final By TIME_DONE = By.xpath("//button[@data-handler='hide']");
    static final By SAVE_BUTTON = By.xpath("//button[@ng-click='saveVisitReschedule(false)']");
    static final By POPUP_OK = By.id("popup_ok");
    static final By PRINT_CARD = By.xpath("//button[@ng-click='print()']");
    static final By CLOSE_BUTTON = By.xpath("//button[@data-dismiss='modal']");
    static final By IGNORE_EARLIEST_LATEST_DATE = By
            .xpath("//div[@id='visitRescheduleModal']/div[2]/div/div[2]/div/div[4]/input[@type='checkbox']");
    static final By YEAR_FIELD = By.xpath("//div[@id='ui-datepicker-div']/div/div/select/option[@selected='selected']");
    static final By MONTH_FIELD = By.className("ui-datepicker-month");
    static final By PREV = By.xpath("//div[@id='ui-datepicker-div']/div/a[@title='Prev']");
    static final By NEXT = By.xpath("//div[@id='ui-datepicker-div']/div/a[@title='Next']");
    static final By NEXT_PAGE = By.xpath("//td[@id='next_pager']/span");
    static final By DIALOG_TEXT = By.xpath("//div[@class='modal-dialog']/div/div[2]/div");
    static final By TITLE_MODAL_SCREEN = By.xpath("//*[@id='visitRescheduleModal']/div[2]/div/div[1]/h4");
    static final By CLOSE_ERROR_RESCHEDULE_BUTTON = By
            .xpath("//*[@id='visitRescheduleModal']/div[2]/div/div[2]/div[2]/div/button");
    static final int BIG_TIMEOUT = 2000;
    static final int TIMEOUT = 1000;
    static final int WIDTH = 1920;
    static final int HEIGHT = 1080;
    static final int SECONDS_IN_DAY = 86400000;
    static final int NUMDER_OF_DAYS = 5;
    static final int MAX_PAGES = 10;

    private Map<String, Integer> months = new HashMap<String, Integer>() {
        /**
         * 
         */
        private static final long serialVersionUID = DEFAULT_VALUE;

        {
            put("default", new Integer(DEFAULT_VALUE));
            put("january", new Integer(JANUARY));
            put("february", new Integer(FEBRUARY));
            put("march", new Integer(MARCH));
            put("april", new Integer(APRIL));
            put("may", new Integer(MAY));
            put("june", new Integer(JUNE));
            put("july", new Integer(JULY));
            put("august", new Integer(AUGUST));
            put("september", new Integer(SEPTEMBER));
            put("october", new Integer(OCTOBER));
            put("november", new Integer(NOVEMBER));
            put("december", new Integer(DECEMBER));

        }
    };

    public BookingAppRescheduleVisitPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public void sortByPlannedDateColumn() throws InterruptedException {
        clickWhenVisible(PLANNED_DATE_COLUMN);
        Thread.sleep(BIG_TIMEOUT);
        clickWhenVisible(PLANNED_DATE_COLUMN_SORT);
        Thread.sleep(BIG_TIMEOUT);
    }

    public boolean chooseVisit() throws InterruptedException {
        boolean status = false;
        try {
            WebElement visit = findElement(VISIT);
            clickWhenVisible(VISIT);
            visit.sendKeys(Keys.ENTER);
            // if we could select the visit we have the status = true;
            status = true;
        } catch (NullPointerException e) {
            getLogger().error("chooseVisit - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("chooseVisit - Exception . Reason : " + e.getLocalizedMessage(), e);
        }

        return status;
    }

    public boolean visitsExist() {
        try {
            WebElement firstVisitElement = findElement(VISIT);
            if (firstVisitElement == null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void resizePage() {
        getDriver().manage().window().setSize(new Dimension(WIDTH, HEIGHT));
    }

    public boolean rescheduleVisit() throws Exception {
        return checkVisit();

    }

    public String getTitleReschedule() {
        return findElement(TITLE_MODAL_SCREEN).getAttribute("innerHTML").trim();

    }

    public String getModalText() {
        return findElement(DIALOG_TEXT).getAttribute("innerHTML").trim();

    }

    public boolean checkVisit() throws InterruptedException {
        boolean status = false;
        String text;
        try {
            waitForElement(IGNORE_EARLIEST_LATEST_DATE);
            clickWhenVisible(IGNORE_EARLIEST_LATEST_DATE);
            Thread.sleep(TIMEOUT);
            waitForElement(DATE_FIELD);
            clickOn(DATE_FIELD);
            Calendar cal1 = Calendar.getInstance();
            Date setDate = cal1.getTime();
            Date myDate = new Date(setDate.getTime() + NUMDER_OF_DAYS * SECONDS_IN_DAY);
            cal1.setTime(myDate);
            int year = cal1.get(Calendar.YEAR);
            int month = cal1.get(Calendar.MONTH);
            int day = cal1.get(Calendar.DAY_OF_MONTH);
            int selectedYear = Integer.parseInt(findElement(YEAR_FIELD).getText());
            int selectedMonth = months.get(findElement(MONTH_FIELD).getText().toLowerCase()).intValue();
            getLogger().error("selectedMonth :" + selectedMonth);
            while (year != selectedYear || month != selectedMonth) {
                selectedYear = Integer.parseInt(findElement(YEAR_FIELD).getText());
                selectedMonth = months.get(findElement(MONTH_FIELD).getText().toLowerCase()).intValue();
                if (year < selectedYear || (year == selectedYear && month < selectedMonth)) {
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
            Thread.sleep(TIMEOUT);
            clickWhenVisible(SAVE_BUTTON);
            waitForElement(POPUP_OK);
            clickWhenVisible(POPUP_OK);
            Thread.sleep(BIG_TIMEOUT);
            clickWhenVisible(POPUP_OK);
            text = findElement(DIALOG_TEXT).getText();
            Thread.sleep(BIG_TIMEOUT);
            waitForElement(CLOSE_BUTTON);
            Thread.sleep(BIG_TIMEOUT);
            clickWhenVisible(CLOSE_BUTTON);
            getLogger().error("text :" + text);
            if (text.contains("Visit Planned Date updated successfully.")) {
                return status;
            }
        } catch (InterruptedException e) {
            status = false;
            getLogger().error("rescheduleVisit - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            status = false;
            getLogger().error("rescheduleVisit - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            status = false;
            getLogger().error("rescheduleVisit - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
        return status;
    }

    public void printCard() throws InterruptedException {
        Thread.sleep(BIG_TIMEOUT);
        waitForElement(PRINT_CARD);
        clickWhenVisible(PRINT_CARD);
    }

    /**
     * <p>
     * Checks if two dates are on the same day ignoring time.
     * </p>
     * 
     * @param date1
     *            the first date, not altered, not null
     * @param date2
     *            the second date, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException
     *             if either date is <code>null</code>
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
     * <p>
     * Checks if two calendars represent the same day ignoring time.
     * </p>
     * 
     * @param cal1
     *            the first calendar, not altered, not null
     * @param cal2
     *            the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException
     *             if either calendar is <code>null</code>
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public boolean clickCose() throws InterruptedException {
        boolean status = false;
        try {
            Thread.sleep(TIMEOUT);
            clickOn(CLOSE_ERROR_RESCHEDULE_BUTTON);
            status = true;
        } catch (InvalidSelectorException e) {
            status = false;
            getLogger().error("clickCose - InvalidSelectorException . Reason : " + e.getLocalizedMessage(), e);

        } catch (NullPointerException e) {
            status = false;
            getLogger().error("clickCose - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            status = false;
            getLogger().error("clickCose - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
        return status;
    }
}
