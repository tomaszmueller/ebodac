package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;

import static java.lang.Thread.sleep;


public class BookingAppPrimeVaccinationPage extends AbstractBasePage {

    public static final String URL_PATH = "/#/bookingApp/capacityInfo/";
    static final By ADD_PRIME_VACCINATION_BUTTON = By.xpath("//*[text()[contains(.,'Add Prime Vaccination')]]");
    static final By FIRST_ROW_IN_THE_GRID_UI = By.id("1");
    static final By PARTICIPANT_SELECT = By.id("s2id_subjectIdSelect");
    static final By PARTICIPANT_ID_INPUT = By.xpath("//*[@id=\"s2id_subjectIdSelect\"]/a");
    static final By SCHEDULER_DAY_OF_MONTH = By.linkText("28");
    static final By SAVE_BUTTON_UPDATE_VISIT_BOOKING_DAY = By.xpath("//*[text()[contains(.,'Save')]]");
    static final By PRIME_VAC_DATE_FIELD_NAME = By.xpath("//*[text()[contains(.,'Prime Vac. Date')]]");
    static final By CONFIRM_UPDATE_VISIT_BOOKING_DETAILS = By.id("popup_ok");
    static final By CHECK_IS_VISIBLE_PRINT_CARD_WINDOW = By.xpath("//*[text()[contains(.,'Visit Booking Details updated successfully.')]]");
    static final By IGNORE_LATES_EARLEST_DATE_CHECKBOX = By.xpath("//*[@id=\"primeVaccinationScheduleModal\"]/div[2]/div/div[2]/div[1]/div[7]/input");
    static final By IGNOTE_LATES_EARLIEST_DATE_CHECKBOX = By.xpath("//input[@ng-model='form.dto.ignoreDateLimitation']");
    static final By PRIME_VAC_DATE_FIELD = By.xpath("//input[@booking-app-date-picker='']");
    static final By PRIME_VAC_TIME_FIELD = By.xpath("//input[@mds-time-picker='']");
    static final By PRIME_VAC_TIME_CLOSE_BUTTON = By.xpath("//*[@id=\"ui-datepicker-div\"]/div[3]/button[2]");
    static final By FEMALE_CHILD_BEARING_AGE_DROPDOWN = By.cssSelector("#select2-chosen-4");
    static final By FEMALE_CHILD_BEARING_AGE_YES_OPTION = By.id("s2id_autogen4_search");
    static final By NEXT_MONTH_BUTTON = By.cssSelector("#ui-datepicker-div > div.ui-datepicker-header.ui-widget-header.ui-helper-clearfix.ui-corner-all > a.ui-datepicker-next.ui-corner-all > span");
    static final By TODAY_BUTTON_DATE_PICKER = By.xpath("//*[@id=\"ui-datepicker-div\"]/div[2]/button[1]");
    static final By SAVE_BUTTON_ADD_VISIT_BOOKING_DETAILS = By.xpath("//*[@id=\"primeVaccinationScheduleModal\"]/div[2]/div/div[2]/div[2]/div/button[1]");
    static final By CONFIRM_ADD_VISIT_BOOKING_DETAILS_BUTTON = By.cssSelector("#popup_ok");
    static final By PRINT_CARD_VISIT_BOOKING_DETAILS_BUTTON = By.xpath("//*[@id=\"primeVaccinationScheduleModal\"]/div[2]/div/div[2]/div[2]/div/button[1]");
    static final By SCREENING_DATE_PICKER = By.xpath("//input[@ng-model='form.dto.bookingScreeningActualDate']");
    static final By SCREENING_DATE_PICKER_DONE_BUTTON = By.xpath("//*[@id=\"ui-datepicker-div\"]/div[2]/button[2]");
    static final By PRIME_VAC_DAY_DATE_RANGE_DROP_DOWN = By.xpath("//*[@id=\"main-content\"]/div/div/div[1]/div/button[2]");
    static final By PRIME_VAC_DAY_DATE_RANGE_DROP_DOWN_NEXT_7_DAYS_CHOOSE = By.xpath("//*[@id=\"main-content\"]/div/div/div[1]/div[1]/ul/li[5]/a");
    static final By PRIME_VAC_DAY_DATE_RANGE_DROP_DOWN_DATE_RANGE_CHOOSE = By.xpath("//*[@id=\"main-content\"]/div/div/div[1]/div/ul/li[6]/a");
    static final By SECOND_CONFIRM_ADD_VISIT_BOOKING_DETAILS_BUTTON = By.xpath("//*[text()[contains(.,'Confirm visit booking details update')]]");
    static final By PRIME_VACCINATION_DATE_RANGE_FROM = By.xpath("//input[@ng-model='selectedFilter.startDate']");
    static final int SLEEP_500 = 500;
    static final int SLEEP_2000 = 2000;
    static final By CLINIC_LOCATION = By.id("jqgh_primeVaccinationSchedule_location");
    static final By PARTICIPANT_ID = By.id("jqgh_primeVaccinationSchedule_participantId");
    static final By PARTICIPANT_NAME = By.id("jqgh_primeVaccinationSchedule_participantName");
    static final By FEMALE_CHILD_BEARING_AGE = By.id("jqgh_primeVaccinationSchedule_femaleChildBearingAge");
    static final By ACTUAL_SCREENING_DATE = By.id("jqgh_primeVaccinationSchedule_bookingScreeningActualDate");
    static final By PRIME_VAC_DATE = By.id("jqgh_primeVaccinationSchedule_date");
    static final By PRIME_VAC_TIME = By.id("jqgh_primeVaccinationSchedule_startTime");
    static final By CLOSE_BUTTON = By.xpath("//button[text()='Close']");

    public BookingAppPrimeVaccinationPage(WebDriver driver) {
        super(driver);
    }

    public void clickAddPrimeVaccinationButton() throws InterruptedException {
        sleep(SLEEP_500);
        clickWhenVisible(ADD_PRIME_VACCINATION_BUTTON);
    }

    public void clickFirstParticipantId() throws InterruptedException {
        clickWhenVisible(PARTICIPANT_SELECT);
        System.out.println("klikniete raz");
        sleep(5000);
        sleep(SLEEP_500);
        clickWhenVisible(PARTICIPANT_ID_INPUT);
        System.out.println("klikniete dwa");
        findElement(PARTICIPANT_ID_INPUT).sendKeys(Keys.ENTER);
    }

    public void clickOnFirstRowInTheGridUI() throws InterruptedException {
        sleep(SLEEP_2000);
        clickWhenVisible(FIRST_ROW_IN_THE_GRID_UI);
    }

    public void clickOnIngoreLatesEarliestDate() throws InterruptedException {
        findElement(IGNOTE_LATES_EARLIEST_DATE_CHECKBOX).click();
    }

    public void clickSaveInUpdateVisitBookingDetails() throws InterruptedException {
        clickWhenVisible(SAVE_BUTTON_UPDATE_VISIT_BOOKING_DAY);
    }

    public void setDateOfPrimeVacDateFields() throws InterruptedException {
        sleep(SLEEP_500);
        clickWhenVisible(IGNOTE_LATES_EARLIEST_DATE_CHECKBOX);
        findElement(PRIME_VAC_DATE_FIELD).click();
        clickWhenVisible(NEXT_MONTH_BUTTON);
        clickWhenVisible(TODAY_BUTTON_DATE_PICKER);
        clickWhenVisible(SCHEDULER_DAY_OF_MONTH);

    }

    public void setTimeOfPrimeVacDateFields() throws InterruptedException {
        findElement(PRIME_VAC_TIME_FIELD).sendKeys("10:29");
        findElement(PRIME_VAC_TIME_FIELD).sendKeys(Keys.ENTER);
        clickWhenVisible(PRIME_VAC_TIME_CLOSE_BUTTON);
    }

    public void setMaxDateRangeOfPrimeVaccination() throws InterruptedException {
        sleep(SLEEP_2000);
        clickWhenVisible(PRIME_VAC_DAY_DATE_RANGE_DROP_DOWN);
        sleep(SLEEP_2000);
        clickWhenVisible(PRIME_VAC_DAY_DATE_RANGE_DROP_DOWN_NEXT_7_DAYS_CHOOSE);
        sleep(SLEEP_500);
        clickWhenVisible(PRIME_VAC_DAY_DATE_RANGE_DROP_DOWN);
        sleep(SLEEP_500);
        clickWhenVisible(PRIME_VAC_DAY_DATE_RANGE_DROP_DOWN_DATE_RANGE_CHOOSE);

    }

    public void setFemaleChildBearingAge() throws InterruptedException {
        boolean exists;

        try {
            exists = null != findElement(FEMALE_CHILD_BEARING_AGE_DROPDOWN);
        } catch (Exception e) {
            exists = false;
        }

        if (exists) {
            clickWhenVisible(FEMALE_CHILD_BEARING_AGE_DROPDOWN);
            findElement(FEMALE_CHILD_BEARING_AGE_YES_OPTION).sendKeys("Yes");
            findElement(FEMALE_CHILD_BEARING_AGE_YES_OPTION).sendKeys(Keys.ENTER);
        }
    }

    public void saveCreatedPrimeVaccination() throws InterruptedException {
        clickWhenVisible(SAVE_BUTTON_ADD_VISIT_BOOKING_DETAILS);
    }

    public void confirmAddVisitBookingDetailsAndPrintCard() throws InterruptedException {
        sleep(SLEEP_2000);
        boolean exists;
        clickWhenVisible(CONFIRM_ADD_VISIT_BOOKING_DETAILS_BUTTON);
        try {
            exists = null != findElement(SECOND_CONFIRM_ADD_VISIT_BOOKING_DETAILS_BUTTON);
        } catch (Exception e) {
            exists = false;
        }

        if (exists) {
            clickWhenVisible(CONFIRM_ADD_VISIT_BOOKING_DETAILS_BUTTON);
        }
        sleep(SLEEP_500);
        clickWhenVisible(PRINT_CARD_VISIT_BOOKING_DETAILS_BUTTON);
        clickWhenVisible(CLOSE_BUTTON);
        ArrayList<String> tabs2 = new ArrayList<String>(getDriver().getWindowHandles());
        getDriver().switchTo().window(tabs2.get(1));
        getDriver().close();
        getDriver().switchTo().window(tabs2.get(0));
        sleep(SLEEP_500);
        clickWhenVisible(By.linkText("EBODAC"));
    }

    public void setDateOfScreeningDate() throws InterruptedException {
        clickWhenVisible(SCREENING_DATE_PICKER);
        clickWhenVisible(SCHEDULER_DAY_OF_MONTH);
        clickWhenVisible(SCREENING_DATE_PICKER_DONE_BUTTON);
    }


    //Asserts
    public boolean checkVisibleUpdatedVisitBookingDetails() {
        WebElement element = findElement(CHECK_IS_VISIBLE_PRINT_CARD_WINDOW);
        String name = element.getText();
        return "Visit Booking Details updated successfully.".equals(name);
    }

    public boolean checkConfirmWindowIsVisible() {
        WebElement element = findElement(CONFIRM_UPDATE_VISIT_BOOKING_DETAILS);
        String name = element.getText();
        return "OK".equals(name);
    }

    public boolean checkIfElementAddPrimeVaccinationIsVisible() {
        WebElement element = findElement(ADD_PRIME_VACCINATION_BUTTON);
        String name = element.getText();
        return "Add Prime Vaccination".equals(name);
    }

    public boolean checkIfPrimeVacDateIsVisible() {
        WebElement element = findElement(PRIME_VAC_DATE_FIELD_NAME);
        String name = element.getText();
        return "Prime Vac. Date".equals(name);

    }

    public boolean checkTable() {
        try {
            if (findElement(CLINIC_LOCATION) == null) {
                return false;
            }
            if (findElement(PARTICIPANT_ID) == null) {
                return false;
            }
            if (findElement(PARTICIPANT_NAME) == null) {
                return false;
            }
            if (findElement(FEMALE_CHILD_BEARING_AGE) == null) {
                return false;
            }
            if (findElement(ACTUAL_SCREENING_DATE) == null) {
                return false;
            }
            if (findElement(PRIME_VAC_DATE) == null) {
                return false;
            }
            if (findElement(PRIME_VAC_TIME) == null) {
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
}
