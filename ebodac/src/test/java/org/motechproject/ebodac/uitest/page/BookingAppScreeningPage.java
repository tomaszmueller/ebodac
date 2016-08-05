package org.motechproject.ebodac.uitest.page;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

public class BookingAppScreeningPage extends AbstractBasePage {

    private static final String KAMBIA_I = "Kambia I";

    private static final String EMPTY_ID = "";

    private static final String HOUR_12 = "12:00";

    private static final String FORMAT_DATE = "yyyy-MM-dd";

    private static final String TITLE = "title";
    public static final String URL_PATH = "/#/bookingApp/screening";
    static final By SCREENING_BUTTON = By.xpath("//div[@id='main-content']/div/div/div/button[1]");
    static final By SCREENING_BUTTON_NG_CLICK = By.xpath("//button[@ng-click='addScreening()']");
    // static final By DATE_FIELD =
    // By.xpath("//div[@class='modal-body']/div/input[@type='text']");
    static final By SCREENING_MODAL_PATH = By.xpath("//*[@id='screeningModal']");
    static final String MODAL_CLASS_VISIBLE = "modal fade ng-scope in";
    static final By DATE_FIELD = By.xpath("//*[@id='screeningModal']/div[2]/div/div[2]/div[1]/div[1]/input");
    // static final By DATE_VALIDATOR_CLASS =
    // By.xpath("//*[@class='input-group-addon validator alert-success'");
    static final By TODAY_BUTTON = By.xpath("//*[@id='ui-datepicker-div']/div[2]/button[1]");
    static final By SAVE_DATE_BUTTON_MODAL = By.xpath("//*[@id='ui-datepicker-div']/div[2]/button[2]");
    // static final By TODAY_BUTTON =
    // By.xpath("//button[@data-handler='today']");
    // static final By DAY = By.linkText("13");
    static final By TIME_FIELD = By.xpath("//input[@mds-time-picker='']");
    // static final By TIME_FIELD = By.xpath("//*[@id='dp1470299468393']");
    static final By TIME_DONE = By.xpath("//button[@data-handler='hide']");
    // static final By CLINIC_LOCATION = By.xpath("//div[@class='booking-app
    // input-group'][3]");
    static final By CLINIC_LOCATION = By.xpath("//*[@id='clinicSelect']");
    // static final By CLINIC =
    // By.xpath("(//div[@class='select2-result-label'])[2]");
    static final By CLINIC = By.xpath("//*[@id='select2-result-label-51']");
    // static final By SAVE_BUTTON =
    // By.xpath("//button[@ng-click='saveScreening(false)']");
    static final By SAVE_BUTTON = By.xpath("//*[@id='screeningModal']/div[2]/div/div[2]/div[2]/div/button[1]");
    // static final By POPUP_OK = By.id("popup_ok");
    static final By POPUP_OK = By.xpath("//*[@id='popup_ok']");
    static final By PRINT_CARD = By.xpath("//button[@ng-click='printRow(-1)']");
    static final By CLOSE_BUTTON = By.xpath("//button[@data-dismiss='modal']");
    static final By CLOSE_BUTTON_TEXT = By.xpath("//button[contains(text(),'Close')]");
    static final By EXPORT_BUTTON = By.xpath("(//button[@type='button'])[5]");
    static final By CONFIRM_EXPORT = By.xpath("//div[@id='exportBookingAppInstanceModal']/div[2]/div/div[3]/button");
    static final By FILTER = By.xpath("//div[@class='btn-group']/button[@data-toggle='dropdown']");
    static final By BOOKING_STRING = By.xpath("//div[@id='screeningModal']/div[2]/div/div[2]/div");
    static final By FIRST_VISIT_DATE = By.xpath("//table[@id='screenings']/tbody/tr[2]/td[4]");
    static final By FORMAT = By.linkText("PDF");
    static final By XLS = By.xpath("(//a[contains(text(),'XLS')])[2]");
    static final By START_DATE = By.xpath("//input[@ng-model='selectedFilter.startDate']");
    static final By END_DATE = By.xpath("//input[@ng-model='selectedFilter.endDate']");
    static final By FIRST_DAY = By.linkText("1");
    static final By LAST_DAY = By.linkText("28");
    static final By NEXT_PAGE = By.xpath("//td[@id='next_pager']/span");
    static final By DATE_FIELD_INPUT = By.xpath("//input[@ng-model='form.dto.date']");
    static final By DONE_BUTTON = By.xpath("//button[@data-handler='hide']");
    static final By START_TIME_PICKER = By.xpath("//input[@ng-model='form.dto.startTime']");
    static final By NOW_BUTTON = By.xpath("//button[@data-handler='today']");
    static final By CLINIC_LOCATION_DROP_DOWN = By.xpath("//span[contains(text(), '- Please Choose -')]");
    static final By CLINIC_LOCACATION_KAMBIA_I = By.xpath("//div[contains(text(), 'Kambia I')]");
    static final By SAVE_BUTTON_NG_CLICK = By.xpath("//button[@ng-click='saveScreening(false)']");
    static final By CONFIRM_BUTTON = By.id("popup_ok");
    static final int SLEEP_4SEC = 4000;
    static final long WAIT_2SEC = 2000;
    static final int SLEEP_1SEC = 1000;
    static final int SLEEP_500 = 500;
    static final int TEXTPOINT = 52;
    static final int TIMEOUT = 1000;
    static final int MAX_PAGES = 10;
    static final By TABLE_SCREENING = By.className("ui-jqgrid-bdiv");
    private ScreeningCardPage screeningCardPage;

    public BookingAppScreeningPage(WebDriver driver) {
        super(driver);
    }

    public String bookScreeningVisitForToday() throws InterruptedException {
        boolean status = true;
        String bookingString = "";
        clickWhenVisible(SCREENING_BUTTON);

        sleep(TIMEOUT);
        if (findElement(SCREENING_MODAL_PATH).getAttribute("class").equalsIgnoreCase(MODAL_CLASS_VISIBLE)) {
            clickWhenVisible(DATE_FIELD);
            if (findElement(TODAY_BUTTON).isDisplayed()) {
                clickWhenVisible(TODAY_BUTTON);
                if (findElement(SAVE_DATE_BUTTON_MODAL).isDisplayed()) {
                    clickWhenVisible(SAVE_DATE_BUTTON_MODAL);
                } else {
                    status = false; // We should not find next elements.
                }
            } else {
                status = false; // We should not find next elements.
            }

        } else {
            status = false; // We should not find next elements.
        }

        if (status) {

            // Set the time for the visit
            setTextToFieldNoEnter(TIME_FIELD, HOUR_12);
            clickWhenVisible(TIME_DONE);

            waitForElement(CLINIC_LOCATION);
            sleep(TIMEOUT);
            selectFrom(CLINIC_LOCATION, KAMBIA_I); // We select the clinic
                                                   // Kambia 1

        }

        if (findElement(SAVE_BUTTON).isEnabled()) {

            waitForElement(SAVE_BUTTON);
            clickWhenVisible(SAVE_BUTTON);

            waitForElement(POPUP_OK);
            clickWhenVisible(POPUP_OK);

            sleep(TIMEOUT);
            clickWhenVisible(POPUP_OK);

            // sleep(TIMEOUT);
            waitForElement(BOOKING_STRING);
            bookingString = findElement(BOOKING_STRING).getText().substring(TEXTPOINT).replace(".", EMPTY_ID)
                    .replace(" ", EMPTY_ID);
            waitForElement(PRINT_CARD);
            clickWhenVisible(PRINT_CARD);

            sleep(TIMEOUT);
            ArrayList<String> tabs2 = new ArrayList<String>(getDriver().getWindowHandles());
            getDriver().switchTo().window(tabs2.get(1));
            screeningCardPage = new ScreeningCardPage(getDriver());
            String screeningBookingId = screeningCardPage.getBookingId();
            assertEquals(screeningBookingId, bookingString);
            getDriver().close();
            getDriver().switchTo().window(tabs2.get(0));

            sleep(TIMEOUT);
            waitForElement(CLOSE_BUTTON);
            clickOn(CLOSE_BUTTON);
            clickWhenVisible(CLOSE_BUTTON);
        }

        return bookingString;
    }

    public String bookScreeningVisitForSpecificDay(String date) throws InterruptedException {
        boolean status = true;
        String bookingString = "";
        clickWhenVisible(SCREENING_BUTTON);

        sleep(TIMEOUT);
        if (findElement(SCREENING_MODAL_PATH).getAttribute("class").equalsIgnoreCase(MODAL_CLASS_VISIBLE)) {
            clickWhenVisible(DATE_FIELD);
            if (findElement(TODAY_BUTTON).isDisplayed()) {
                //Add the date here.
                if (findElement(SAVE_DATE_BUTTON_MODAL).isDisplayed()) {
                    clickWhenVisible(SAVE_DATE_BUTTON_MODAL);
                } else {
                    status = false; // We should not find next elements.
                }
            } else {
                status = false; // We should not find next elements.
            }

        } else {
            status = false; // We should not find next elements.
        }

        if (status) {

            // Set the time for the visit
            setTextToFieldNoEnter(TIME_FIELD, HOUR_12);
            clickWhenVisible(TIME_DONE);

            waitForElement(CLINIC_LOCATION);
            sleep(TIMEOUT);
            selectFrom(CLINIC_LOCATION, KAMBIA_I); // We select the clinic
                                                   // Kambia 1

        }

        if (findElement(SAVE_BUTTON).isEnabled()) {

            waitForElement(SAVE_BUTTON);
            clickWhenVisible(SAVE_BUTTON);

            waitForElement(POPUP_OK);
            clickWhenVisible(POPUP_OK);

            sleep(TIMEOUT);
            clickWhenVisible(POPUP_OK);

            waitForElement(BOOKING_STRING);
            bookingString = findElement(BOOKING_STRING).getText().substring(TEXTPOINT).replace(".", EMPTY_ID)
                    .replace(" ", EMPTY_ID);
            waitForElement(PRINT_CARD);
            clickWhenVisible(PRINT_CARD);

            sleep(TIMEOUT);
            ArrayList<String> tabs2 = new ArrayList<String>(getDriver().getWindowHandles());
            getDriver().switchTo().window(tabs2.get(1));
            screeningCardPage = new ScreeningCardPage(getDriver());
            String screeningBookingId = screeningCardPage.getBookingId();
            assertEquals(screeningBookingId, bookingString);
            getDriver().close();
            getDriver().switchTo().window(tabs2.get(0));

            sleep(TIMEOUT);
            waitForElement(CLOSE_BUTTON);
            clickOn(CLOSE_BUTTON);
            clickWhenVisible(CLOSE_BUTTON);
        }
        return bookingString;
    }

    public void bookVisitForScreening() throws InterruptedException {
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
            getLogger().error("clickOnButtonToCloseScheduleScreening - InterruptedException . Reason : "
                    + e.getLocalizedMessage(), e);
            status = false;
        } catch (NullPointerException e) {
            getLogger().error("clickOnButtonToCloseScheduleScreening - NullPointerException . Reason : "
                    + e.getLocalizedMessage(), e);
            status = false;
        } catch (Exception e) {
            getLogger().error("clickOnButtonToCloseScheduleScreening - Exception . Reason : " + e.getLocalizedMessage(),
                    e);
            status = false;
        }
        return status;

    }

    public void changeFilterTo(String filter) throws InterruptedException {

        waitForElement(FILTER);
        clickOn(FILTER);
        sleep(WAIT_2SEC);
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
            int counter = 0;
            while (findElement(NEXT_PAGE).isEnabled()) {
                if (counter > MAX_PAGES) {
                    break;
                }
                if (!EMPTY_ID.equalsIgnoreCase(id.trim()) && bookingIdExistsOnPage(id)) {
                    exists = true;
                    break;
                } else {
                    clickOn(NEXT_PAGE);
                }
                counter++;
            }

        } catch (TimeoutException e) {
            exists = false;
            getLogger().error(
                    "bookingIdExists - TimeoutException for id = " + id + "  . Reason : " + e.getLocalizedMessage(), e);

        } catch (NullPointerException e) {
            exists = false;
            getLogger().error(
                    "bookingIdExists - NullPointerException for id = " + id + "  . Reason : " + e.getLocalizedMessage(),
                    e);

        } catch (Exception e) {
            exists = false;
            getLogger().error("bookingIdExists - Exception for id = " + id + " . Reason : " + e.getLocalizedMessage(),
                    e);

        }
        return exists;

    }

    public boolean bookingIdExistsOnPage(String id) {
        boolean status = false;
        try {
            By elementBookingId = By.cssSelector("td[title=\"" + id + "\"]");
            sleep(TIMEOUT);

            waitForElementToBeEnabled(elementBookingId);
            // waitForElement(elementBookingId);
            WebElement element = findElement(elementBookingId);
            if (element.isDisplayed()) {
                status = true;
            } else {
                status = false;
            }

        } catch (TimeoutException e) {
            status = false;
            getLogger().error("bookingIdExistsOnPage - TimeoutException for id = " + id + " . Reason : "
                    + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            status = false;
            getLogger().error("bookingIdExistsOnPage - NullPointerException for id = " + id + " . Reason : "
                    + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            status = false;
            getLogger().error(
                    "bookingIdExistsOnPage - Exception for id = " + id + "  . Reason : " + e.getLocalizedMessage(), e);
        }
        return status;
    }

    public boolean isFirstBookingOK(ArrayList<LocalDate> dates) {
        boolean status = false;
        try {
            if (hasBookings(dates)) {
                WebElement firstVisitElement = findElement(FIRST_VISIT_DATE);
                if (firstVisitElement == null) {
                    status = false;
                } else {
                    LocalDate date = LocalDate.parse((firstVisitElement.getAttribute(TITLE)).toString(),
                            DateTimeFormat.forPattern(FORMAT_DATE));
                    if (dates.contains(date)) {
                        status = true;
                    } else {
                        status = false;
                    }
                }
            }
        } catch (NullPointerException e) {
            status = false;
            getLogger().error("isFirstBookingOK - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (TimeoutException e) {
            status = false;
            getLogger().error("isFirstBookingOK - TimeoutException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            status = false;
            getLogger().error("isFirstBookingOK - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
        getLogger().error("isFirstBookingOK - status :" + status);
        return status;
    }

    public boolean hasBookings(ArrayList<LocalDate> dates) {
        boolean status = false;
        try {
            String html = findElement(TABLE_SCREENING).getAttribute("innerHTML");
            if (html.contains("Active") || html.contains("Canceled")) {
                status = true;
            }
        } catch (NullPointerException e) {
            status = false;
            getLogger().error(
                    "hasBookings " + dates.toString() + " - NullPointerException . Reason : " + e.getLocalizedMessage(),
                    e);
        } catch (Exception e) {
            status = false;
            getLogger().error("hasBookings " + dates.toString() + " - Exception . Reason : " + e.getLocalizedMessage(),
                    e);
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

}
