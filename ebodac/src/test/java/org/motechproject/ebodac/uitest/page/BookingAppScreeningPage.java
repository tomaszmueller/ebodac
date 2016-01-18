package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;


public class BookingAppScreeningPage extends AbstractBasePage {

    public BookingAppScreeningPage(WebDriver driver) {
        super(driver);
    }

    public static final String URL_PATH = "/#/bookingApp/screening";

    static final By SCREENING_BUTTON = By.xpath("//div[@id='main-content']/div/div/div/button");
    static final By DATE_FIELD = By.xpath("//div[@class='modal-body']/div/input[@type='text']");
    static final By DAY = By.linkText("13");
    static final By TIME_FIELD = By.xpath("//input[@mds-time-picker='']");
    static final By TIME_DONE = By.xpath("//button[@data-handler='hide']");
    static final By CLINIC_LOCATION = By.xpath("//div[@class='booking-app input-group'][3]");
    static final By CLINIC = By.xpath("(//div[@class='select2-result-label'])[2]");
    static final By SAVE_BUTTON = By.xpath("//button[@ng-click='saveScreening(false)']");
    static final By POPUP_OK = By.id("popup_ok");
    static final By PRINT_CARD = By.xpath("//button[@ng-click='printRow(-1)']");
    static final By CLOSE_BUTTON = By.xpath("//button[@data-dismiss='modal']");
    static final By EXPORT_BUTTON = By.xpath("(//button[@type='button'])[5]");
    static final By CONFIRM_EXPORT = By.xpath("//div[@id='exportBookingAppInstanceModal']/div[2]/div/div[3]/button");
    static final By FILTER = By.xpath("//div[@class='btn-group']/button[@data-toggle='dropdown']");
    static final By BOOKING_STRING = By.xpath("//div[@id='screeningModal']/div[2]/div/div[2]/div");
    static final By FIRST_VISIT = By.xpath("//table[@id='screenings']/tbody/tr[2]/td[3]");
    static final By FORMAT = By.linkText("PDF");
    static final By XLS = By.xpath("(//a[contains(text(),'XLS')])[2]");
    static final By START_DATE = By.xpath("//input[@ng-model='selectedFilter.startDate']");
    static final By END_DATE = By.xpath("//input[@ng-model='selectedFilter.endDate']");
    static final By FIRST_DAY = By.linkText("1");
    static final By LAST_DAY = By.linkText("28");

    private ScreeningCardPage screeningCardPage;

    public String bookScreeningVisit() throws InterruptedException {
        waitForElement(SCREENING_BUTTON);
        clickOn(SCREENING_BUTTON);
        clickWhenVisible(SCREENING_BUTTON);
        findElement(SCREENING_BUTTON).sendKeys(Keys.ENTER);
        clickWhenVisible(DATE_FIELD);
        clickWhenVisible(DAY);
        setTextToFieldNoEnter(TIME_FIELD, "12:00");
        clickWhenVisible(TIME_DONE);
        waitForElement(CLINIC_LOCATION);
        Thread.sleep(1000);
        clickWhenVisible(CLINIC_LOCATION);
        clickWhenVisible(CLINIC);
        clickWhenVisible(SAVE_BUTTON);
        waitForElement(POPUP_OK);
        clickWhenVisible(POPUP_OK);
        Thread.sleep(1000);
        clickWhenVisible(POPUP_OK);
        Thread.sleep(1000);
        waitForElement(BOOKING_STRING);
        String bookingString = findElement(BOOKING_STRING).getText().substring(52).replace(".", "").replace(" ","");
        waitForElement(PRINT_CARD);
        clickWhenVisible(PRINT_CARD);
        ArrayList<String> tabs2 = new ArrayList<String> (driver.getWindowHandles());
        driver.switchTo().window(tabs2.get(1));
        screeningCardPage = new ScreeningCardPage(driver);
        String screeningBookingId = screeningCardPage.getBookingId();
        assertEquals(screeningBookingId, bookingString);
        driver.close();
        driver.switchTo().window(tabs2.get(0));
        Thread.sleep(1000);
        waitForElement(CLOSE_BUTTON);
        clickOn(CLOSE_BUTTON);
        clickWhenVisible(CLOSE_BUTTON);
        return bookingString;
    }

    public void changeFilterTo(String filter) throws InterruptedException  {
        waitForElement(FILTER);
        clickOn(FILTER);
        clickWhenVisible(FILTER);
        waitForElement(By.linkText(filter));
        clickOn(By.linkText(filter));
        clickWhenVisible(By.linkText(filter));
    }

    public void exportToPDF() throws InterruptedException {
        clickWhenVisible(EXPORT_BUTTON);
        clickWhenVisible(CONFIRM_EXPORT);
    }


    public void exportToXLS() throws InterruptedException {
        clickWhenVisible(EXPORT_BUTTON);
        clickWhenVisible(FORMAT);
        clickWhenVisible(XLS);
        clickWhenVisible(CONFIRM_EXPORT);
    }

    public void setDate() throws InterruptedException  {
        clickWhenVisible(START_DATE);
        clickWhenVisible(FIRST_DAY);
        clickWhenVisible(END_DATE);
        clickWhenVisible(LAST_DAY);
    }

    public boolean bookingIdExists(String id) {
        try {
            By ELEMENT_BOOKING_ID = By.cssSelector("td[title=\"" + id + "\"]");
            Thread.sleep(1000);
            waitForElement(ELEMENT_BOOKING_ID);
            WebElement element = findElement(ELEMENT_BOOKING_ID);
            if (element != null) {
                return true;
            }
            return false;
        } catch(Exception e) {
            return false;
        }
    }

    public boolean isFirstBookingOK(ArrayList<Date> dates) {
        try {
            WebElement firstVisitElement = findElement(FIRST_VISIT);
            if (firstVisitElement == null) {
                return true;
            } else {
                long dateparse = Date.parse((firstVisitElement.getAttribute("title")).toString());
                Date date = new Date(dateparse);
                if(containsDate(dates,date)) {
                    return true;
                }
                    return false;
            }
        }
        catch(Exception e) {
            return true;
        }
    }


    public static boolean containsDate(ArrayList<Date> dates, Date date) {
        for(Date thisDate : dates) {
            if(isSameDay(thisDate, date)) {
                return true;
            }
        }
        return false;
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
    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }
}
