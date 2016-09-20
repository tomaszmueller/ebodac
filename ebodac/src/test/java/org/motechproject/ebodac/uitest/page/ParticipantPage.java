package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ParticipantPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final int SLEEP_500MILISEC = 500;
    static final int SLEEP_1SEC = 1000;
    private static final long SLEEP_2SEC = 2000;
    static final By PARTICIPANT = By.xpath("//table[@id='instancesTable']/tbody/tr[2]");
    static final By PHONE_NUMBER = By
            .xpath("//table[@id='instancesTable']/tbody/tr[2]/td[@aria-describedby='instancesTable_phoneNumber']");
    static final By LANGUAGE = By
            .xpath("//table[@id='instancesTable']/tbody/tr[2]/td[@aria-describedby='instancesTable_language']");
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
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public void openFirstParticipant() throws InterruptedException {
        try {
            sleep(SLEEP_1SEC);
            clickWhenVisible(PARTICIPANT);
        } catch (Exception e) {
            throw new AssertionError("No participants present");
        }

    }

    public void sleep(long timeout) throws InterruptedException {
        Thread.sleep(timeout);

    }

    public boolean findParticipant(String id) throws InterruptedException {
        boolean status = false;
        try {
            clickWhenVisible(LOOKUP_DIALOG_BUTTON);
            clickWhenVisible(SELECT_LOOKUP_BUTTON);
            clickWhenVisible(FIND_UNIQUE_PARTICIPANT_BY_PARTICIPANT_ID);
            findElement(ID_FIELD).clear();
            sleep(SLEEP_2SEC);
            findElement(ID_FIELD).sendKeys(id);
            sleep(SLEEP_2SEC);
            clickWhenVisible(FIND_BUTTON);
            sleep(SLEEP_2SEC);
            findElement(PARTICIPANT);
            status = true;
            return true;
        } catch (Exception e) {
            status = false;
            getLogger().error("findParticipant - Exc. Reason :" + e.getLocalizedMessage(), e);
        }
        return status;
    }

    public String getFirstParticipantNumber() {
        return findElement(PHONE_NUMBER).getText();
    }

    public String getFirstParticipantLanguage() {
        return findElement(LANGUAGE).getText();
    }
}
