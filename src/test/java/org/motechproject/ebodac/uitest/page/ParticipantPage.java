package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class ParticipantPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By PARTICIPANT = By.xpath("//table[@id='instancesTable']/tbody/tr");
    static final String PARTICIPANT_STRING = "//table[@id='instancesTable']/tbody/tr/td[text()='";
    static final String PARTICIPANT_STRING_END = "']";
    static final By PHONE_NUMBER = By.xpath("//table[@id='instancesTable']/tbody/tr[2]/td[@aria-describedby='instancesTable_phoneNumber']");
    static final By LANGUAGE = By.xpath("//table[@id='instancesTable']/tbody/tr[2]/td[@aria-describedby='instancesTable_language']");
    static final By NEXT = By.cssSelector("span.ui-icon.ui-icon-seek-next");
    public ParticipantPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }

    public void openFirstParticipant() throws InterruptedException {
        try {
            Thread.sleep(500);
            findElement(PARTICIPANT);
        } catch(Exception e) {
            throw new AssertionError("No participants present");
        }
        clickWhenVisible(PARTICIPANT);
    }

    public void openParticipant(String id) {

        do {
            if(findParticipantOnPage(id)) {
                openParticipantOnPage(id);
                break;
            }
            clickOn(NEXT);
        } while (true);
    }

    public void openParticipantOnPage(String id) {
        clickOn(By.xpath(PARTICIPANT_STRING + id + PARTICIPANT_STRING_END));
    }

    public boolean findParticipant(String id) {
        try {
            do {
                if(findParticipantOnPage(id)) {
                    return true;
                }
                clickOn(NEXT);
            } while (true);
        } catch(Exception e) {
            return false;
        }
    }

    public boolean findParticipantOnPage(String id) {
        try {
            if(findElement(By.xpath(PARTICIPANT_STRING + id + PARTICIPANT_STRING_END)) != null) {
                return true;
            }
            return false;
        } catch(Exception e) {
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
