package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
        System.out.println("tekst wizyty=" + visit.getText());
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

    public Date getVisitDate() {
        String date = findElement(VISIT_DATE).getText();
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd");//, Locale.ENGLISH);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public boolean rescheduleVisit() throws InterruptedException {
        clickWhenVisible(DATE_FIELD);
        Date setDate = new Date(new Date().getDate() + 5);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(setDate);
        int day = cal1.get(Calendar.DAY_OF_MONTH);
        clickWhenVisible(By.linkText("" + day));
        setTextToFieldNoEnter(TIME_FIELD, "12:00");
        clickWhenVisible(TIME_DONE);
        clickWhenVisible(SAVE_BUTTON);
        waitForElement(POPUP_OK);
        clickWhenVisible(POPUP_OK);
        Date resultDate = getVisitDate();
        return isSameDay(resultDate,setDate);
    }

    public void printCard() throws InterruptedException {
        Thread.sleep(1000);
        waitForElement(PRINT_CARD);
        clickWhenVisible(PRINT_CARD);
        Thread.sleep(1000);
        waitForElement(CLOSE_BUTTON);
        clickWhenVisible(CLOSE_BUTTON);
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
}
