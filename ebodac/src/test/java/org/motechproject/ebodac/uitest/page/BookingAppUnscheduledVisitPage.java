package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import static java.lang.Thread.sleep;


public class BookingAppUnscheduledVisitPage extends AbstractBasePage {

    public static final String URL_PATH = "/#/bookingApp/unscheduledVisit";

    public static final By BOOK_UNSCHEDULED_VISIT_BUTTON = By.xpath("//button[@ng-click='addUnscheduled()']");

    public static final By PARTICIPANT_DROP_DOWN = By.id("select2-chosen-2");

    public static final By FIRST_PARTICIPANT_FROM_DROP_DWON = By.xpath("//*[@id=\"s2id_autogen2_search\"]");

    public static final By UNSCHEDULED_VISIT_DATE_PICKER = By.xpath("//input[@ng-model='form.dto.date']");

    public static final By UNSCHEDULED_VISIT_DATE_PICKER_TODAY_BUTTON = By.xpath("//button[@data-handler='today']");

    public static final By UNSCHEDULED_VISIT_DATE_PICKER_DONE_BUTTON = By.xpath("//button[@data-handler='hide']");

    public static final By UNSCHEDULED_VISIT_START_TIME_INPUT = By.xpath("//input[@placeholder='Click to select start time']");

    public static final By UNSCHEDULED_VISIT_SAVE_BUTTON = By.xpath("//button[@ng-click='saveUnscheduledVisit(false)']");

    public static final By CONFIRM_MODAL_MESSAGE = By.id("popup_message");

    public static final By CONFIRM_MODAL_OK_BUTTON = By.id("popup_ok");

    public static final By SUCCESSFULLY_MODAL_MESSAGE = By.xpath("//div[@class='modal-body ng-binding']");

    public static final By SUCCESSFULLY_MODAL_CLOSE_BUTTON = By.xpath("//button[@data-dismiss='modal']");

    public static final int SLEEP_1000 = 1000;

    public BookingAppUnscheduledVisitPage(WebDriver driver) {
        super(driver);
    }

    public void clickOnBookUnscheduledVisitButton() throws InterruptedException {
        sleep(SLEEP_1000);
        clickWhenVisible(BOOK_UNSCHEDULED_VISIT_BUTTON);
    }

    public void clickOnParticipantIdDropDownAndChooseFirstParticipant() throws InterruptedException {
        clickWhenVisible(PARTICIPANT_DROP_DOWN);
        findElement(FIRST_PARTICIPANT_FROM_DROP_DWON).sendKeys("1");
        sleep(SLEEP_1000);
        findElement(FIRST_PARTICIPANT_FROM_DROP_DWON).sendKeys(Keys.ENTER);
        sleep(SLEEP_1000);
    }

    public void setDatesForUnscheduledVisit() throws InterruptedException {
        clickWhenVisible(UNSCHEDULED_VISIT_DATE_PICKER);
        clickWhenVisible(UNSCHEDULED_VISIT_DATE_PICKER_TODAY_BUTTON);
        clickWhenVisible(UNSCHEDULED_VISIT_DATE_PICKER_DONE_BUTTON);
        findElement(UNSCHEDULED_VISIT_START_TIME_INPUT).sendKeys("23:59");
    }

    public void clickOnButtonToSaveUnscheduledVisit() throws InterruptedException {
        clickWhenVisible(UNSCHEDULED_VISIT_SAVE_BUTTON);
    }

    public String checkIfConfirmModalIsVisible() {
        return findElement(CONFIRM_MODAL_MESSAGE).getText();
    }

    public void clickOnButtonToConfirmBookUnscheduledVisit() throws InterruptedException {
        clickWhenVisible(CONFIRM_MODAL_OK_BUTTON);
    }

    public String checkifVisitIsCorrectlySaved() {
        return findElement(SUCCESSFULLY_MODAL_MESSAGE).getText();
    }

    public void clickOnButtonToCloseModal() throws InterruptedException {
        clickWhenVisible(SUCCESSFULLY_MODAL_CLOSE_BUTTON);
    }

    @Override
    public void goToPage() {

    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }
}
