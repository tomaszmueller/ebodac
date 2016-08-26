package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BookingAppClinicVisitSchedulePage extends AbstractBasePage {
    private static final String NO_MATCHES_FOUND = "No matches found";
    private static final String INNER_HTML = "innerHTML";
    private static final By LIST_PARTICIPANTS = By.xpath("//*[@id='main-content']/div/div/table/tbody/tr/td[1]");
    private static final String URL_PATH = "/#/bookingApp/capacityInfo/";
    private static final By PRIME_VAC_FIRST_FOLLOW_UP_VALUE = By
            .cssSelector("#main-content > div > div > table > tbody > tr > td:nth-child(4)");
    private static final By PARTICIPANT_ID_DROPDOWN = By.id("s2id_subjectId");
    private static final By FIRST_PARTICIPANT_IN_DROPDOWN = By.cssSelector("#select2-result-label-3");
    private static final String PARTICIPANT_CSS_SELECTOR = ("#select2-result-label-");
    private static final By CLEAN_DATE_BUTTON = By.xpath("//*[@id=\"main-content\"]/div/div/button[3]");
    private static final By PROME_VAC_FIRST_FOLLOW_UP = By
            .xpath("//*[@id=\"main-content\"]/div/div/table/tbody/tr/td[4]");
    private static final By PRIME_VAC_DAY_DATE = By.cssSelector("#primeVacDateInput");
    private static final By PRINT_BUTTON = By.xpath("//button[@ng-click='print()']");
    private static final By SET_FIRST_DAY = By.linkText("1");
    private static final int SLEEP_1SEC = 1000;
    private static final int SLEEP_3SEC = 3000;
    private static final int START_INTER = 3;

    public BookingAppClinicVisitSchedulePage(WebDriver driver) {
        super(driver);
    }

    public void clickOnDropDownParticipantId() throws InterruptedException {
        sleep(SLEEP_3SEC);
        clickWhenVisible(PARTICIPANT_ID_DROPDOWN);
        sleep(SLEEP_1SEC);
        clickWhenVisible(FIRST_PARTICIPANT_IN_DROPDOWN);

    }

    public boolean findParticipantWithoutPrimeVacDay() throws InterruptedException {
        boolean status = false;
        try {
            waitForElement(LIST_PARTICIPANTS);
            sleep(SLEEP_3SEC);
            if (!findElement(LIST_PARTICIPANTS).getAttribute(INNER_HTML).contains(NO_MATCHES_FOUND)) {
                clickWhenVisible(PARTICIPANT_ID_DROPDOWN);
                sleep(SLEEP_1SEC);
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
                        status = true;
                    }
                }
            }

        } catch (NullPointerException e) {
            getLogger().error("findParticipantWithoutPrimeVacDay - NPE . Reason :" + e.getLocalizedMessage(), e);
            status = false;
        } catch (InterruptedException e) {
            getLogger().error("findParticipantWithoutPrimeVacDay - IE . Reason :" + e.getLocalizedMessage(), e);
            status = false;
        } catch (Exception e) {
            getLogger().error("findParticipantWithoutPrimeVacDay - Exc . Reason :" + e.getLocalizedMessage(), e);
            status = false;
        }

        return status;
    }

    public void clickButtonCleanDate() throws InterruptedException {
        clickWhenVisible(CLEAN_DATE_BUTTON);
        sleep(SLEEP_1SEC);
    }

    public void clickOnPrimeVacDayDate() throws InterruptedException {
        clickWhenVisible(PRIME_VAC_DAY_DATE);
    }

    public String getPrimeVacDateInput() {
        return findElement(PROME_VAC_FIRST_FOLLOW_UP).getText();
    }

    public boolean clickOnFirstDayInCalendar() throws InterruptedException {
        boolean status = false;
        try {
            clickWhenVisible(SET_FIRST_DAY);
            status = true;
        } catch (NullPointerException e) {
            getLogger().error("clickOnFirstDayInCalendar - NPE . Reason :" + e.getLocalizedMessage(), e);
            status = false;
        } catch (InterruptedException e) {
            getLogger().error("clickOnFirstDayInCalendar - IEx . Reason :" + e.getLocalizedMessage(), e);
            status = false;
        } catch (Exception e) {
            getLogger().error("clickOnFirstDayInCalendar - Exc . Reason :" + e.getLocalizedMessage(), e);
            status = false;
        }

        return status;
    }

    public String assertIfPrimeVacDayIsEmpty() throws InterruptedException {
        WebElement element = findElement(PRIME_VAC_FIRST_FOLLOW_UP_VALUE);
        String textt = element.getText();
        return textt;
    }

    public boolean clickOnButtonToPrint() throws InterruptedException {
        boolean status = false;
        try {
            clickWhenVisible(PRINT_BUTTON);
            status = true;
        } catch (NullPointerException e) {
            getLogger().error("clickOnButtonToPrint - NullPointerException . Reason :" + e.getLocalizedMessage(), e);
            status = false;
        } catch (InterruptedException e) {
            getLogger().error("clickOnButtonToPrint - InterruptedException . Reason :" + e.getLocalizedMessage(), e);
            status = false;
        } catch (Exception e) {
            getLogger().error("clickOnButtonToPrint - Exception . Reason :" + e.getLocalizedMessage(), e);
            status = false;
        }

        return status;

    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;

    }

    public void sleep(long timeout) throws InterruptedException {
        Thread.sleep(timeout);

    }
}
