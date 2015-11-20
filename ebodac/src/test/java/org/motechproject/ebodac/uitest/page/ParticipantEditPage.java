package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class ParticipantEditPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By PHONE_NUMBER_FIELD = By.id("phoneNumberForm");
    static final By SAVE_BUTTON = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div[2]/div/button");
    static final By CONFIRMATION_BUTTON = By.xpath("//div[@id='editSubjectModal']/div[2]/div/div[3]/button");
    static final By LANGUAGE_FIELD = By.xpath("(//button[@type='button'])[2]");
    static final String LANGUAGE_PATH = "//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[8]/div/ng-form/div/div/ul/li";
    static final String LANGUAGE_PATH_END = "/a/label";
    static final By DATE = By.linkText("1");
    static final By POPUP_OK = By.id("popup_ok");
    static final By PICK_DATA = By.cssSelector(".ui-state-active");
    static final By POPUP_CONTENT = By.id("popup_content");
    static final By DATE_TABLE = By.xpath("//div[3]/div/ng-form/div/input");
    static final By PLANNED_VISITS_DATE = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[3]/div/ng-form/div/input");

    public ParticipantEditPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }

    public void changePhoneNumber(String number) throws InterruptedException {
        findElement(PHONE_NUMBER_FIELD).clear();
        changeFocus();
        Thread.sleep(500);
        findElement(PHONE_NUMBER_FIELD).sendKeys(number);
        changeFocus();
        Thread.sleep(500);
        clickOn(SAVE_BUTTON);
        clickWhenVisible(CONFIRMATION_BUTTON);
    }

    private void changeFocus() {
        findElement(By.className("form-control")).click();
        findElement(By.className("form-control")).sendKeys("");
    }

    public String changeLanguage(String languagePos) throws InterruptedException  {
        clickWhenVisible(LANGUAGE_FIELD);
        String language = chooseLanguage(languagePos);
        Thread.sleep(500);
        clickOn(SAVE_BUTTON);
        clickWhenVisible(CONFIRMATION_BUTTON);
        return language;
    }

    private String chooseLanguage(String languagePos) {
        try {
            clickOn(By.xpath(LANGUAGE_PATH + "[" + languagePos + "]" + LANGUAGE_PATH_END));
            return findElement(By.xpath(LANGUAGE_PATH + "[" + languagePos + "]" + LANGUAGE_PATH_END + "/input")).getText();
        } catch(Exception e) {
            throw new AssertionError("No language at chosen position");
        }
    }

    public void chageVisit() {
        clickOn(SAVE_BUTTON);
        clickOn(POPUP_OK);
    }
    public void clickPlannedVisitDate() throws InterruptedException{
        Thread.sleep(500);
        clickWhenVisible(PLANNED_VISITS_DATE);
        Thread.sleep(500);
        clickOn(DATE);
    }
    public void clickOK() throws InterruptedException {
        Thread.sleep(500);
        clickOn(POPUP_OK);
    }
    public boolean dateEnroll() {
        try {
            return driver.findElement(POPUP_CONTENT).getText().contains("Error occurred during re-enrolling: Cannot re-enroll Participant for that Visit, because motech projected date wasnt changed");
        } catch (Exception ex) {
            return false;
        }
    }
    public String getChoosenData() {
        return driver.findElement(PICK_DATA).getText();
    }
    public void enter() throws InterruptedException{
        int  day = 1;
        do {
            day++;
            Thread.sleep(500);
            clickOn(DATE_TABLE);
            WebElement date  = findElement(By.linkText(""+day+""));
            date.click();
            clickOn(SAVE_BUTTON);
            clickOn(POPUP_OK);
        }
        while (dateEnroll());
    }
}
