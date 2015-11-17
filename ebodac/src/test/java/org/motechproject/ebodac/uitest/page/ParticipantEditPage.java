package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;


public class ParticipantEditPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By NAME_FIELD = By.xpath("(//input[@type='text'])[2]");
    static final By HOUSEHOLD_FIELD = By.xpath("(//input[@type='text'])[3]");
    static final By HEAD_OF_HOUSEHOLD_FIELD = By.xpath("(//input[@type='text'])[4]");
    static final By ADDRESS_FIELD = By.xpath("(//input[@type='text'])[6]");
    static final By COMMUNITY_FIELD = By.xpath("(//input[@type='text'])[8]");
    static final By PHONE_NUMBER_FIELD = By.id("phoneNumberForm");
    static final By SAVE_BUTTON = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div[2]/div/button");
    static final By CONFIRMATION_BUTTON = By.xpath("//div[@id='editSubjectModal']/div[2]/div/div[3]/button");
    static final By LANGUAGE_FIELD = By.xpath("(//button[@type='button'])[2]");
    static final String LANGUAGE_PATH = "//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[8]/div/ng-form/div/div/ul/li";
    static final String LANGUAGE_PATH_END = "/a/label";
    static final By DELETE_BUTTON = By.xpath("//div[@id='dataBrowser']/div/div/div/div/button[2]");
    static final By DELETE_CONFIRMATION_BUTTON = By.xpath("//div[@id='deleteInstanceModal']/div[2]/div/div[3]/button");
    public ParticipantEditPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }

    public void changePhoneNumber(String number) throws InterruptedException {
        Long startTime = System.currentTimeMillis();
        while((System.currentTimeMillis() - startTime) < 10000) {
            findElement(PHONE_NUMBER_FIELD).clear();
            changeFocus();
            Thread.sleep(500);
            findElement(PHONE_NUMBER_FIELD).sendKeys(number);
            changeFocus();
            Thread.sleep(500);
            if(findElement(PHONE_NUMBER_FIELD).getText().equals(number)) {
                break;
            }
        }
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


    public void deleteParticipant() throws InterruptedException {
        waitForElement(DELETE_BUTTON);
        Thread.sleep(500);
        findElement(DELETE_BUTTON).sendKeys(Keys.RETURN);
        waitForElement(DELETE_CONFIRMATION_BUTTON);
        clickOn(DELETE_CONFIRMATION_BUTTON);
    }

    public boolean enabled() {
        String nameField = findElement(NAME_FIELD).getAttribute("readonly");
        if(!nameField.contains("true")) {
            return true;
        }
        String householdField = findElement(HOUSEHOLD_FIELD).getAttribute("readonly");
        if(!householdField.contains("true")) {
            return true;
        }
        String headOfHouseHoldField = findElement(HEAD_OF_HOUSEHOLD_FIELD).getAttribute("readonly");
        if(!headOfHouseHoldField.contains("true")) {
            return true;
        }
        String phoneNumberField = findElement(PHONE_NUMBER_FIELD).getAttribute("disabled");
        if(!phoneNumberField.contains("true")) {
            return true;
        }
        String addressField = findElement(ADDRESS_FIELD).getAttribute("readonly");
        if(!addressField.contains("true")) {
            return true;
        }
        try {
            clickWhenVisible(LANGUAGE_FIELD);
            clickOn(By.xpath(LANGUAGE_PATH + "[0]" + LANGUAGE_PATH_END));
            return true;
        } catch(Exception e) {
            ;
        }
        String communityField = findElement(COMMUNITY_FIELD).getAttribute("readonly");
        if(!communityField.contains("true")) {
            return true;
        }
        return false;
    }
}
