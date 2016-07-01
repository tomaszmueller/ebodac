package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static java.lang.Thread.sleep;

public class EnrollmentPage extends AbstractBasePage {
    public static final String URL_PATH = "home#/ebodac/enrollment";
    static final By ACTION = By.xpath("//table[@id='enrollmentTable']/tbody/tr[2]/td[6]/button");
    static final By POPUP_OK = By.id("popup_ok");
    static final By POPUP_CONTENT = By.id("popup_content");
    static final int SLEEP_500 = 500;
    static final By ENROLLMENT_RECORD = By.xpath("//table[@id='enrollmentTable']/tbody/tr[2]");
    static final By ENROLLMENT_ADVANCED = By.id("enrollmentAdvanced");
    static final int LAST_ENROLL = 2;
    private int lastEnroll;
    static final int SMALL_TIMEOUT = 500;
    static final int BIG_TIMEOUT = 2000;
    static final By AMMOUNT_OF_PAGES = By.id("sp_1_pageEnrollmentTable");
    static final By NUBMER_OF_ACTUAL_PAGE = By.className("ui-paging-info");
    static final By POPUP_MESSAGE = By.id("popup_message");
    static final By NEXT_PAGE_BUTTON = By.id("next_pageEnrollmentTable");
    static final By AMMOUNT_OF_RESULTS = By.xpath("//select[@title='Records per Page']");
    static final By FIRST_PARTICIPANT_STATUS = By.xpath("//*[@id=\"1\"]/td[5]");
    static final By FIRST_PARTICIPANT_ID = By.xpath("//*[@id=\"1\"]/td[2]");

    public EnrollmentPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }
    public String getPopupMessage() {
        WebElement popupElement = findElement(POPUP_MESSAGE);
        String popupMessage = popupElement.getText();
        return popupMessage;
    }

    public void clickAction() throws InterruptedException {
        sleep(SLEEP_500);
        clickWhenVisible(ACTION);
    }

    public void clickOK() throws InterruptedException {
        sleep(SMALL_TIMEOUT);
        clickWhenVisible(POPUP_OK);
    }
    public boolean error() {
        try {
            WebElement popUpContent = findElement(POPUP_CONTENT);
            if (popUpContent.getText().contains("Error occurred during enrolling Participant: Cannot enroll Participant")
                    || popUpContent.getText().contains("Error occurred during unenrolling Participant: Cannot unenroll Participant")) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }
    public boolean enrolled() {
        try {
            return findElement(POPUP_CONTENT).getText().contains("Participant was enrolled successfully.");
        } catch (Exception ex) {
            return false;
        }
    }
    public boolean unenrolled() {
        try {
            return findElement(POPUP_CONTENT).getText().contains("Participant was unenrolled successfully.");
        } catch (Exception ex) {
            return false;
        }
    }
    public void nextAction() throws InterruptedException {
        lastEnroll = LAST_ENROLL;
        do {
            lastEnroll++;
            try {
                actionSecond();
                clickOn(POPUP_OK);
            } catch (Exception e) {
                clickOn(POPUP_OK);
            }
        }
        while (error());
    }
    public void actionSecond() {
        WebElement action = findElement(By.xpath("//table[@id='enrollmentTable']/tbody/tr[" + LAST_ENROLL + "]/td[6]/button"));
        action.click();
    }

    public boolean enrollmentDetailEnabled() throws InterruptedException {
        clickWhenVisible(ENROLLMENT_RECORD);
        sleep(BIG_TIMEOUT);
        try {
            if (findElement(ENROLLMENT_ADVANCED) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkEnroll() throws InterruptedException {
        sleep(BIG_TIMEOUT);
        findElement(By.xpath("//tr[@id='1']/td[2]")).click();
        try {
            findElement(By.cssSelector("td[title=\"Boost Vaccination Third Follow-up visit\"]")).click();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String getAmmountPagesOfTable() {
        return findElement(AMMOUNT_OF_PAGES).getText();
    }

    public String getNumberOfActualPage() {
        return findElement(NUBMER_OF_ACTUAL_PAGE).getText();
    }

    public void goToNextPageInTable() throws InterruptedException {
        clickWhenVisible(NEXT_PAGE_BUTTON);
    }

    public void changeAmmountOfResultsShownOnPage() throws InterruptedException {
        Select dropdown = new Select(findElement(AMMOUNT_OF_RESULTS));
        dropdown.selectByVisibleText("10");
    }

    public String getStatusOfFirstParticipantEnrollment() {
        return findElement(FIRST_PARTICIPANT_STATUS).getAttribute("title");
    }

    public String getParticipantId() {
        return findElement(FIRST_PARTICIPANT_ID).getText();
    }

    public void clickOnButtonToEnrollParticipant(String idOfParticipant) {
        findElement(By.xpath("//button[@ng-click='enroll(\"" + idOfParticipant + "\")']")).click();
    }

    public void clickOnButtonToUnenrollParticipant(String idOfParticipant) {
        findElement(By.xpath("//button[@ng-click='unenroll(\"" + idOfParticipant + "\")']")).click();
    }
}
