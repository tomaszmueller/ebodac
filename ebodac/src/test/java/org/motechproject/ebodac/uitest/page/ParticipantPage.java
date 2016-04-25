package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class ParticipantPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final int SMALL_TIMEOUT = 500;
    static final int BIG_TIMEOUT = 1000;
    static final By PARTICIPANT = By.xpath("//table[@id='instancesTable']/tbody/tr[2]");
    static final By PHONE_NUMBER = By.xpath("//table[@id='instancesTable']/tbody/tr[2]/td[@aria-describedby='instancesTable_phoneNumber']");
    static final By LANGUAGE = By.xpath("//table[@id='instancesTable']/tbody/tr[2]/td[@aria-describedby='instancesTable_language']");
    static final By LOOKUP_DIALOG_BUTTON = By.id("lookupDialogButton");
    static final By SELECT_LOOKUP_BUTTON = By.xpath("//div[@id='lookup-dialog']/div[2]/div/div/button");
    static final By FIND_UNIQUE_PARTICIPANT_BY_PARTICIPANT_ID = By.linkText("Find unique Participant By ParticipantId");
    static final By ID_FIELD = By.xpath("//input[@type='text']");
    static final By FIND_BUTTON = By.xpath("//button[@ng-click='filterInstancesByLookup()']");
    public ParticipantPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }

    public void openFirstParticipant() throws InterruptedException {
        try {
            Thread.sleep(SMALL_TIMEOUT);
            findElement(PARTICIPANT);
        } catch (Exception e) {
            throw new AssertionError("No participants present");
        }
        clickWhenVisible(PARTICIPANT);
    }

    public boolean findParticipant(String id) throws InterruptedException {
        clickWhenVisible(LOOKUP_DIALOG_BUTTON);
        clickWhenVisible(SELECT_LOOKUP_BUTTON);
        clickWhenVisible(FIND_UNIQUE_PARTICIPANT_BY_PARTICIPANT_ID);
        findElement(ID_FIELD).clear();
        findElement(ID_FIELD).sendKeys(id);
        Thread.sleep(BIG_TIMEOUT);
        clickWhenVisible(FIND_BUTTON);
        Thread.sleep(BIG_TIMEOUT);
        try {
            Thread.sleep(SMALL_TIMEOUT);
            findElement(PARTICIPANT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public String getFirstParticipantNumber() {
        return findElement(PHONE_NUMBER).getText();
    }


    public String getFirstParticipantLanguage() {
        return findElement(LANGUAGE).getText();
    }
}
