package org.motechproject.ebodac.uitest.page;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

public class BookingAppScreeningPage extends AbstractBasePage {

    private static final By XPATH_FIND_BUTTON = By.xpath("//*[@id='lookup-dialog']/div[2]/div[3]/div/button");
    private static final By XPATH_LOOKUP_FIELD = By.xpath("//*[@id='lookup-dialog']/div[2]/div[2]/div/input");
    private static final By XPATH_LINK_TEXT = By.linkText("Find By Booking Id");
    private static final By XPATH_LOOKUP_BUTTON = By.xpath("//*[@id='lookup-dialog']/div[2]/div[1]/div/button");
    private static final By XPATH_FILTER_BUTTON = By.xpath("//*[@id='lookupDialogButton']");
    private static final String TITLE = "title";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String INNER_HTML = "innerHTML";
    private static final String SPACE = " ";
    private static final String DOT = ".";
    private static final String NO_RECORDS_TO_VIEW = "No records to view";
    private static final By TABLE_SCREENING_LIST = By.xpath("//*[@id='pager_left']/div");
    private static final int ZERO = 0;
    private static final String EMPTY = "";
    private static final String MODAL = "//*[@id='screeningModal']";
    private static final String URL_PATH = "/#/bookingApp/screening";
    private static final int TEXTPOINT = 52;
    private static final int TIMEOUT = 1000;
    private static final int MAX_PAGES = 10;
    private static final By SCREENING_BUTTON = By.xpath("//div[@id='main-content']/div/div/div/button");
    private static final By SCREENING_BUTTON_NG_CLICK = By.xpath("//button[@ng-click='addScreening()']");
    private static final By DATE_FIELD = By.xpath("//div[@class='modal-body']/div/input[@type='text']");
    private static final By TIME_FIELD = By.xpath("//input[@mds-time-picker='']");
    private static final By TIME_DONE = By.xpath("//button[@data-handler='hide']");
    private static final By CLINIC_LOCATION = By.xpath("//div[@class='booking-app input-group'][3]");
    private static final By CLINIC = By.xpath("(//div[@class='select2-result-label'])[2]");
    private static final By SAVE_BUTTON = By.xpath("//button[@ng-click='saveScreening(false)']");
    private static final By POPUP_OK = By.id("popup_ok");
    private static final By PRINT_CARD = By.xpath("//button[@ng-click='printRow(-1)']");
    private static final By CLOSE_BUTTON = By.xpath("//button[@data-dismiss='modal']");
    private static final By CLOSE_BUTTON_TEXT = By.xpath("//button[contains(text(),'Close')]");
    private static final By EXPORT_BUTTON = By.xpath("(//button[@type='button'])[5]");
    private static final By CONFIRM_EXPORT = By
            .xpath("//div[@id='exportBookingAppInstanceModal']/div[2]/div/div[3]/button");
    private static final By FILTER = By.xpath("//div[@class='btn-group']/button[@data-toggle='dropdown']");
    private static final By BOOKING_STRING = By.xpath("//div[@id='screeningModal']/div[2]/div/div[2]/div");
    private static final By FIRST_VISIT = By.xpath("//table[@id='screenings']/tbody/tr[2]/td[4]");
    private static final By FORMAT = By.linkText("PDF");
    private static final By XLS = By.xpath("(//a[contains(text(),'XLS')])[2]");
    private static final By START_DATE = By.xpath("//input[@ng-model='selectedFilter.startDate']");
    private static final By END_DATE = By.xpath("//input[@ng-model='selectedFilter.endDate']");
    private static final By FIRST_DAY = By.linkText("1");
    private static final By LAST_DAY = By.linkText("28");
    private static final By NEXT_PAGE = By.xpath("//td[@id='next_pager']/span");
    private static final By DATE_FIELD_INPUT = By.xpath("//input[@ng-model='form.dto.date']");
    private static final By TODAY_BUTTON = By.xpath("//button[@data-handler='today']");
    private static final By DONE_BUTTON = By.xpath("//button[@data-handler='hide']");
    private static final By START_TIME_PICKER = By.xpath("//input[@ng-model='form.dto.startTime']");
    private static final By NOW_BUTTON = By.xpath("//button[@data-handler='today']");
    private static final By CLINIC_LOCATION_DROP_DOWN = By.xpath("//span[contains(text(), '- Please Choose -')]");
    private static final By CLINIC_LOCACATION_KAMBIA_I = By.xpath("//div[contains(text(), 'Kambia I')]");
    private static final By SAVE_BUTTON_NG_CLICK = By.xpath("//button[@ng-click='saveScreening(false)']");
    private static final By CONFIRM_BUTTON = By.id("popup_ok");
    private static final int SLEEP_1SEC = 1000;
    private static final int SLEEP_2SEC = 2000;
    private static final int SLEEP_4SEC = 4000;
    private ScreeningCardPage screeningCardPage;

    public BookingAppScreeningPage(WebDriver driver) {
        super(driver);
    }

    public String bookScreeningVisit() throws InterruptedException {
        String bookingString = EMPTY;
        try {
            waitForElement(SCREENING_BUTTON);
            clickOn(SCREENING_BUTTON);
            clickWhenVisible(SCREENING_BUTTON);
            sleep(SLEEP_2SEC);
            clickWhenVisible(DATE_FIELD);
            clickWhenVisible(TODAY_BUTTON);
            setTextToFieldNoEnter(TIME_FIELD, new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime()));
            clickWhenVisible(TIME_DONE);
            waitForElement(CLINIC_LOCATION);
            sleep(SLEEP_2SEC);
            clickWhenVisible(CLINIC_LOCATION);
            clickWhenVisible(CLINIC);
            waitForElement(SAVE_BUTTON);
            clickWhenVisible(SAVE_BUTTON);
            waitForElement(POPUP_OK);
            clickWhenVisible(POPUP_OK);
            sleep(TIMEOUT);
            clickWhenVisible(POPUP_OK);
            sleep(TIMEOUT);
            if (findElement(By.xpath(MODAL)).isDisplayed()) {
                waitForElement(BOOKING_STRING);
                bookingString = findElement(BOOKING_STRING).getText().substring(TEXTPOINT).replace(DOT, EMPTY)
                        .replace(SPACE, EMPTY);

                waitForElement(PRINT_CARD);
                clickWhenVisible(PRINT_CARD);
                sleep(TIMEOUT);
                ArrayList<String> tabs2 = new ArrayList<String>(getDriver().getWindowHandles());
                getDriver().switchTo().window(tabs2.get(1));
                screeningCardPage = new ScreeningCardPage(getDriver());
                String screeningBookingId = screeningCardPage.getBookingId();
                assertEquals(screeningBookingId, bookingString);
                getDriver().close();
                getDriver().switchTo().window(tabs2.get(ZERO));
                sleep(TIMEOUT);
                waitForElement(CLOSE_BUTTON);
                clickOn(CLOSE_BUTTON);
                clickWhenVisible(CLOSE_BUTTON);
            }
        } catch (Exception e) {
            getLogger().error("bookScreeningVisit - Exc . Reason : " + e.getLocalizedMessage(), e);

        }
        return bookingString;
    }

    public void bookVisitForScreening() throws InterruptedException {
        try {
            sleep(SLEEP_1SEC);
            clickWhenVisible(SCREENING_BUTTON_NG_CLICK);
            clickWhenVisible(DATE_FIELD_INPUT);
            clickWhenVisible(TODAY_BUTTON);
            clickWhenVisible(DONE_BUTTON);
            clickWhenVisible(START_TIME_PICKER);
            clickWhenVisible(NOW_BUTTON);
            clickWhenVisible(DONE_BUTTON);
            clickWhenVisible(CLINIC_LOCATION_DROP_DOWN);
            sleep(SLEEP_1SEC);
            clickWhenVisible(CLINIC_LOCACATION_KAMBIA_I);
            clickWhenVisible(SAVE_BUTTON_NG_CLICK);
        } catch (Exception e) {
            getLogger().error("bookVisitForScreening - Exc . Reason : " + e.getLocalizedMessage(), e);

        }
    }

    public void confirmBookVistiForScreening() throws InterruptedException {
        clickWhenVisible(CONFIRM_BUTTON);
    }

    public void clickOnButtonToAddAnotherScreening() throws InterruptedException {
        clickWhenVisible(SCREENING_BUTTON_NG_CLICK);
    }

    public boolean clickOnButtonToCloseScheduleScreening() throws InterruptedException {
        boolean status = false;
        try {
            sleep(SLEEP_1SEC);
            clickWhenVisible(CLOSE_BUTTON_TEXT);
            sleep(SLEEP_4SEC);
            status = true;
        } catch (InterruptedException e) {
            getLogger().error("clickOnButtonToCloseScheduleScreening - Iex . Reason : " + e.getLocalizedMessage(), e);
            status = false;
        } catch (Exception e) {
            getLogger().error("clickOnButtonToCloseScheduleScreening - Exc . Reason : " + e.getLocalizedMessage(), e);
            status = false;
        }
        return status;

    }

    public void changeFilterTo(String filter) throws InterruptedException {
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

    public void setDate() throws InterruptedException {
        clickWhenVisible(START_DATE);
        clickWhenVisible(FIRST_DAY);
        clickWhenVisible(END_DATE);
        clickWhenVisible(LAST_DAY);
    }

    public boolean bookingIdExists(String id) {
        boolean exists = false;
        try {
            clickWhenClickable(XPATH_FILTER_BUTTON);
            sleep(SLEEP_2SEC);
            clickWhenClickable(XPATH_LOOKUP_BUTTON);
            sleep(SLEEP_2SEC);
            clickWhenClickable(XPATH_LINK_TEXT);
            sleep(SLEEP_2SEC);
            findElement(XPATH_LOOKUP_FIELD).sendKeys(id);
            sleep(SLEEP_2SEC);
            clickWhenVisible(XPATH_FIND_BUTTON);
            if (hasVisits()) {
                exists = true;
            }

        } catch (Exception e) {
            getLogger().error("bookingIdExists - Exc . Reason : " + e.getLocalizedMessage(), e);
            exists = false;
        }
        return exists;
    }

    public boolean bookingIdExistsOnPage(String id) {
        boolean status = false;
        try {
            By elementBookingId = By.cssSelector("td[title=\"" + id + "\"]");
            sleep(TIMEOUT);
            waitForElement(elementBookingId);
            WebElement element = findElement(elementBookingId);
            if (element != null) {
                status = true;
            }

        } catch (Exception e) {
            getLogger().error("bookingIdExistsOnPage - Exc . Reason : " + e.getLocalizedMessage(), e);
            status = false;
        }

        return status;
    }

    public boolean isFirstBookingOK(ArrayList<LocalDate> dates) {
        boolean status = false;
        try {
            if (hasVisits()) {
                String firstVisit = (findElement(FIRST_VISIT).getAttribute(TITLE)).toString();
                sleep(SLEEP_2SEC);
                LocalDate date = LocalDate.parse(firstVisit, DateTimeFormat.forPattern(YYYY_MM_DD));
                if (dates.contains(date)) {
                    status = true;
                } else {
                    getLogger().error("isFBOK - No contains the Date: " + dates.toString());
                }
            } else {
                getLogger().error("isFBOK - No visits for Date: " + dates.toString());
            }

        } catch (Exception e) {
            getLogger().error("isFirstBookingOK - Exc . Reason : " + e.getLocalizedMessage(), e);
            status = false;
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

    public boolean hasVisits() {
        boolean status = false;
        try {
            waitForElement(TABLE_SCREENING_LIST);
            status = !findElement(TABLE_SCREENING_LIST).getAttribute(INNER_HTML).trim().contains(NO_RECORDS_TO_VIEW);
        } catch (NullPointerException e) {
            getLogger().error("hasVisits - NPE . Reason : " + e.getLocalizedMessage(), e);
            status = false;
        } catch (Exception e) {
            getLogger().error("hasVisits - Exc . Reason : " + e.getLocalizedMessage(), e);
            status = false;
        }
        return status;
    }

    public void sleep(long timeout) throws InterruptedException {
        Thread.sleep(timeout);
        
    }
}
