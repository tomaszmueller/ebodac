package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ParticipantEditPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final int SMALL_TIMEOUT = 500;
    static final int TIMEOUT_BORDER = 10000;
    static final By PHONE_NUMBER_FIELD = By.id("phoneNumberForm");
    static final By SAVE_BUTTON = By.xpath("//button[@ng-click='addEntityInstance()']");
    static final By CONFIRMATION_BUTTON = By.xpath("//button[@ng-click='addEntityInstanceDefault()']");
    static final By LANGUAGE_FIELD = By.xpath("(//button[@type='button'])[2]");
    static final String LANGUAGE_PATH = "//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[8]/div/ng-form/div/div/ul/li";
    static final String LANGUAGE_PATH_END = "/a/label";
    static final By DELETE_BUTTON = By.xpath("//div[@id='dataBrowser']/div/div/div/div/button[2]");
    static final By DELETE_CONFIRMATION_BUTTON = By.xpath("//div[@id='deleteInstanceModal']/div[2]/div/div[3]/button");
    static final By DATE = By.linkText("1");
    static final By POPUP_OK = By.id("popup_ok");
    static final By PICK_DATA = By.cssSelector(".ui-state-active");
    static final By POPUP_CONTENT = By.id("popup_content");
    static final By DATE_TABLE = By.xpath("//div[3]/div/ng-form/div/input");
    static final By VISIT_TABLE = By.xpath("//div[@ng-include='loadEditValueForm(field)']/table");
    static final By PLANNED_VISITS_DATE = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[3]/div/ng-form/div/input");
    static final By NAME = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[2]/div/ng-form/div/input[@type='text']");
    static final By HOUSEHOLD_NAME = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[3]/div/ng-form/div/input[@type='text']");
    static final By HEAD_OF_HOUSEHOLD = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[4]/div/ng-form/div/input[@type='text']");
    static final By BUTTON = By.xpath("//div[@ng-include='loadEditValueForm(field)']/table/tbody/tr/*/button");

    public ParticipantEditPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public void changePhoneNumber(String number) throws InterruptedException {
        setPhoneNumber(number);
        clickOn(SAVE_BUTTON);
        clickWhenVisible(CONFIRMATION_BUTTON);
    }

    public boolean setPhoneNumber(String number) throws InterruptedException {
            findElement(PHONE_NUMBER_FIELD).clear();
            changeFocus();
            Thread.sleep(SMALL_TIMEOUT);
            findElement(PHONE_NUMBER_FIELD).sendKeys(number);
            changeFocus();
            Thread.sleep(SMALL_TIMEOUT);
            if (findElement(PHONE_NUMBER_FIELD).getText().equals(number)) {
                return true;
            }
        return false;
    }

    private void changeFocus() {
        findElement(By.className("form-control")).click();
        findElement(By.className("form-control")).sendKeys("");
    }

    public String changeLanguage(String languagePos) throws InterruptedException {
        clickWhenVisible(LANGUAGE_FIELD);
        String language = chooseLanguage(languagePos);
        Thread.sleep(SMALL_TIMEOUT);
        clickOn(SAVE_BUTTON);
        clickWhenVisible(CONFIRMATION_BUTTON);
        return language;
    }

    public String chooseLanguage(String languagePos) {
        try {
            clickOn(By.xpath(LANGUAGE_PATH + "[" + languagePos + "]" + LANGUAGE_PATH_END));
            Thread.sleep(TIMEOUT_BORDER);
            return findElement(By.xpath(LANGUAGE_PATH + "[" + languagePos + "]" + LANGUAGE_PATH_END + "/input")).getText();
        } catch (Exception e) {
            throw new AssertionError("No language at chosen position");
        }
    }

    public void deleteParticipant() throws InterruptedException {
        waitForElement(DELETE_BUTTON);
        Thread.sleep(SMALL_TIMEOUT);
        findElement(DELETE_BUTTON).sendKeys(Keys.RETURN);
        waitForElement(DELETE_CONFIRMATION_BUTTON);
        clickOn(DELETE_CONFIRMATION_BUTTON);
    }

    public void changeVisit() {
        clickOn(SAVE_BUTTON);
        clickOn(POPUP_OK);
    }

    public void clickPlannedVisitDate() throws InterruptedException {
        Thread.sleep(SMALL_TIMEOUT);
        clickWhenVisible(PLANNED_VISITS_DATE);
        Thread.sleep(SMALL_TIMEOUT);
        clickOn(DATE);
    }

    public void clickOK() throws InterruptedException {
        Thread.sleep(SMALL_TIMEOUT);
        clickOn(POPUP_OK);
    }

    public boolean dateEnroll() {
        try {
            return findElement(POPUP_CONTENT).getText().contains("Error occurred during re-enrolling: Cannot re-enroll Participant for that Visit, because motech projected date wasnt changed");
        } catch (Exception ex) {
            return false;
        }
    }

    public String getChoosenData() {
        return findElement(PICK_DATA).getText();
    }

    public void enter() throws InterruptedException {
        int  day = 1;
        do {
            day++;
            Thread.sleep(SMALL_TIMEOUT);
            clickOn(DATE_TABLE);
            WebElement date  = findElement(By.linkText("" + day + ""));
            date.click();
            clickOn(SAVE_BUTTON);
            clickOn(POPUP_OK);
        }
        while (dateEnroll());
    }

    public boolean isNameEditable() {
        WebElement nameElement = findElement(NAME);
        try {
            if (nameElement.getAttribute("readonly").contains("readonly") || nameElement.getAttribute("readonly").contains("true")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isHouseholdNameEditable() {
        WebElement nameElement = findElement(HOUSEHOLD_NAME);
        try {
            if (nameElement.getAttribute("readonly").contains("readonly") || nameElement.getAttribute("readonly").contains("true")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isHeadOfHouseholdEditable() {
        WebElement nameElement = findElement(HEAD_OF_HOUSEHOLD);
        try {
            if (nameElement.getAttribute("readonly").contains("readonly") || nameElement.getAttribute("readonly").contains("true")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean checkButtons() {
        try {
            if (findElement(BUTTON) != null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isTable() {
        try {
            waitForElement(VISIT_TABLE);
            if (findElement(VISIT_TABLE) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
