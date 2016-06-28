package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import static java.lang.Thread.sleep;


public class BookingAppClinicVisitSchedulePage extends AbstractBasePage {

    public static final String URL_PATH = "/#/bookingApp/capacityInfo/";
    static final By PRIME_VAC_FIRST_FOLLOW_UP_VALUE = By.cssSelector("#main-content > div > div > table > tbody > tr > td:nth-child(4)");
    static final By PARTICIPANT_ID_DROPDOWN = By.id("s2id_subjectId");
    static final By FIRST_PARTICIPANT_IN_DROPDOWN = By.cssSelector("#select2-result-label-3");
    static final String PARTICIPANT_CSS_SELECTOR = ("#select2-result-label-");
    static final By CLEAN_DATE_BUTTON = By.xpath("//*[@id=\"main-content\"]/div/div/button[3]");
    static final By PRIME_VAC_DAY_DATE = By.cssSelector("#primeVacDateInput");
    static final By PRINT_BUTTON = By.xpath("//button[@ng-click='print()']");
    static final By SET_FIRST_DAY = By.linkText("1");
    public static final int SLEEP_1000 = 1000;
    public static final int SLEEP_3000 = 3000;
    public static final int START_INTER = 3;

    public BookingAppClinicVisitSchedulePage(WebDriver driver) {
        super(driver);
    }

    public void clickOnDropDownParticipantId() throws InterruptedException {
        sleep(SLEEP_3000);
        clickWhenVisible(PARTICIPANT_ID_DROPDOWN);
        sleep(SLEEP_1000);
        findElement(FIRST_PARTICIPANT_IN_DROPDOWN).click();
    }

    public void findParticipantWithoutPrimeVacDay() throws InterruptedException {
        sleep(SLEEP_3000);
        clickWhenVisible(PARTICIPANT_ID_DROPDOWN);
        sleep(SLEEP_1000);
        findElement(FIRST_PARTICIPANT_IN_DROPDOWN).click();
        WebElement element = findElement(PRIME_VAC_FIRST_FOLLOW_UP_VALUE);
        String textt = element.getText();
        if (!"".equals(textt)) {
            int temp = START_INTER;
            while (!"".equals(textt)) {
                temp++;
                String intToString = Integer.toString(temp);
                findElement(By.cssSelector(PARTICIPANT_CSS_SELECTOR + intToString));
                textt = element.getText();
            }
        }
    }

    public void clickButtonCleanDate() throws InterruptedException {
        clickWhenVisible(CLEAN_DATE_BUTTON);
        sleep(SLEEP_1000);
    }

    public void clickOnPrimeVacDayDate() throws InterruptedException {
        clickWhenVisible(PRIME_VAC_DAY_DATE);
    }

    public String getPrimeVacDateInput() {
        return findElement(PRIME_VAC_DAY_DATE).getText();
    }

    public void clickOnFirstDayInCalendar() throws InterruptedException {
        clickWhenVisible(SET_FIRST_DAY);
    }

    public String assertIfPrimeVacDayIsEmpty() throws InterruptedException {
        WebElement element = findElement(PRIME_VAC_FIRST_FOLLOW_UP_VALUE);
        String textt = element.getText();
        return textt;
    }

    public void clickOnButtonToPrint() throws InterruptedException {
        clickWhenVisible(PRINT_BUTTON);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;

    }
}
